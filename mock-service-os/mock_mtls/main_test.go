package main

import (
	"bytes"
	"io"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

type roundTripperFunc func(*http.Request) (*http.Response, error)

func (f roundTripperFunc) RoundTrip(r *http.Request) (*http.Response, error) { return f(r) }

// captureLogs routes the default slog logger into a buffer for the duration of the test.
func captureLogs(t *testing.T) *bytes.Buffer {
	t.Helper()
	var buf bytes.Buffer
	previous := slog.Default()
	slog.SetDefault(slog.New(slog.NewJSONHandler(&buf, nil)))
	t.Cleanup(func() { slog.SetDefault(previous) })
	return &buf
}

func assertNotLogged(t *testing.T, logs string, values ...string) {
	t.Helper()
	for _, v := range values {
		if strings.Contains(logs, v) {
			t.Errorf("logs contain %q:\n%s", v, logs)
		}
	}
}

func TestIntrospectLogsOnlyActiveAndClientID(t *testing.T) {
	logs := captureLogs(t)

	// introspect builds its client without a transport, so it picks up this stub.
	previousTransport := http.DefaultTransport
	http.DefaultTransport = roundTripperFunc(func(r *http.Request) (*http.Response, error) {
		return &http.Response{
			StatusCode: http.StatusOK,
			Header:     http.Header{"Content-Type": []string{"application/json"}},
			Body: io.NopCloser(strings.NewReader(`{"active":true,"client_id":"client-one","sub":"subject-12345678900",` +
				`"scope":"openid accounts consent:urn:raidiambank:consent:abc-123"}`)),
			Request: r,
		}, nil
	})
	t.Cleanup(func() { http.DefaultTransport = previousTransport })

	req := httptest.NewRequest(http.MethodGet, "/accounts", nil)
	if _, err := introspect(req, "the-access-token"); err != nil {
		t.Fatalf("introspect: %v", err)
	}

	got := logs.String()
	if !strings.Contains(got, `"active":true`) || !strings.Contains(got, `"client_id":"client-one"`) {
		t.Errorf("expected active and client_id to be logged:\n%s", got)
	}
	assertNotLogged(t, got, "subject-12345678900", "urn:raidiambank:consent:abc-123", "the-access-token")
}

func TestMiddlewareDoesNotLogCredentials(t *testing.T) {
	next := http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {})

	t.Run("client certificate", func(t *testing.T) {
		logs := captureLogs(t)

		req := httptest.NewRequest(http.MethodGet, "/accounts", nil)
		req.Header.Set(HeaderClientCert, "-----BEGIN CERTIFICATE----- MIIBcertificatebody -----END CERTIFICATE-----")
		middleware(next).ServeHTTP(httptest.NewRecorder(), req)

		assertNotLogged(t, logs.String(), "MIIBcertificatebody")
	})

	t.Run("basic authorization header", func(t *testing.T) {
		logs := captureLogs(t)

		req := httptest.NewRequest(http.MethodGet, "/accounts", nil)
		req.SetBasicAuth("some-client", "some-secret")
		rec := httptest.NewRecorder()
		middleware(next).ServeHTTP(rec, req)

		if rec.Code != http.StatusUnauthorized {
			t.Errorf("status = %d, want %d", rec.Code, http.StatusUnauthorized)
		}
		assertNotLogged(t, logs.String(), req.Header.Get("Authorization"))
	})
}
