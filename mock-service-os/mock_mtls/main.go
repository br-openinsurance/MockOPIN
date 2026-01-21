package main

import (
	"context"
	"crypto/sha256"
	"crypto/tls"
	"crypto/x509"
	"encoding/base64"
	"encoding/json"
	"encoding/pem"
	"errors"
	"fmt"
	"io"
	"log/slog"
	"net/http"
	"net/http/httputil"
	"net/url"
	"os"
	"regexp"
	"runtime/debug"
	"strings"
	"time"

	"github.com/go-jose/go-jose/v4"
	"github.com/go-jose/go-jose/v4/jwt"
	"github.com/google/uuid"
)

type ContextKey string

const (
	CtxKeyCorrelationID ContextKey = "correlation_id"
	CtxKeyInteractionID ContextKey = "interaction_id"
)

var (
	APIHost  = envValue("API_HOST", "http://mockapi:8080")
	AuthHost = envValue("AUTH_HOST", "http://auth:3000")
)

const (
	HeaderClientCert       = "BANK-TLS-Certificate"
	HeaderClientCertVerify = "X-BANK-Certificate-Verify"
	HeaderClientCertDN     = "X-BANK-Certificate-DN"
)

type Environment string

const (
	EnvironmentLocal Environment = "LOCAL"
)

var (
	Env = envValue("ENV", EnvironmentLocal)
)

const (
	IntrospectionClientID     = "introspection-client"
	IntrospectionClientSecret = "introspection-client-secret"
)

// Constants for local development.
const (
	CACertFilePath            = "certs/ca.crt"
	ServerCertFilePath        = "certs/mtls.crt"
	ServerKeyFilePath         = "certs/mtls.key"
	ClientOnePublicJWKSPath   = "certs/client_one_pub.jwks"
	ClientTwoPublicJWKSPath   = "certs/client_two_pub.jwks"
	ParticipantsFilePath      = "mocks/participants.json"
	SoftwareStatementFilePath = "mocks/software_statement.json"
)

var certRegex = regexp.MustCompile(`(?:-----(?:BEGIN|END) CERTIFICATE-----|\s)`)

func main() {
	l := logger()
	slog.SetDefault(l)

	mux := http.NewServeMux()
	authHandler := authHandler()
	apiHandler := apiHandler()

	if Env == EnvironmentLocal {
		mux.Handle("directory/", directoryHandler())
	}

	mux.Handle("/health", http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		slog.Info("health check request")
		_, _ = w.Write([]byte("OK"))
	}))

	mux.Handle("/", http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		host := r.Header.Get("X-Forwarded-Host")
		if strings.HasPrefix(host, "api.") || strings.HasPrefix(host, "matls-api.") {
			slog.Info("api request", slog.String("host", host))
			apiHandler.ServeHTTP(w, r)
			return
		}

		slog.Info("defaulting to auth request", slog.String("host", host))
		authHandler.ServeHTTP(w, r)
	}))

	handler := middleware(mux)

	if Env == EnvironmentLocal {
		tlsConfig := tlsConfiguration()
		tlsServer := http.Server{
			Addr:      ":443",
			Handler:   middlewareLocal(handler),
			ErrorLog:  slog.NewLogLogger(l.Handler(), slog.LevelError),
			TLSConfig: tlsConfig,
		}
		go func() {
			slog.Info("tls server starting")
			if err := tlsServer.ListenAndServeTLS("", ""); err != nil && err != http.ErrServerClosed {
				slog.Error("tls server error", slog.String("err", err.Error()))
				os.Exit(1)
			}
			slog.Info("tls server shutdown")
		}()
	}

	server := &http.Server{
		Addr:              ":80",
		Handler:           handler,
		ReadTimeout:       5 * time.Second,
		WriteTimeout:      10 * time.Second,
		IdleTimeout:       120 * time.Second,
		ReadHeaderTimeout: 2 * time.Second,
	}
	slog.Info("http server starting")
	if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
		slog.Error("server error", slog.String("err", err.Error()))
		os.Exit(1)
	}
	slog.Info("http server shutdown")
}

func authHandler() http.Handler {
	authHost, err := url.Parse(AuthHost)
	if err != nil {
		slog.Error("unable to upstream url", slog.String("err", err.Error()))
		os.Exit(1)
	}
	proxy := httputil.NewSingleHostReverseProxy(authHost)
	// Rewrite Host header to match the target.
	originalDirector := proxy.Director
	proxy.Director = func(req *http.Request) {
		originalDirector(req)
		req.Host = authHost.Host
	}
	return proxy
}

func apiHandler() http.Handler {
	apiHost, err := url.Parse(APIHost)
	if err != nil {
		slog.Error("unable to upstream url", slog.String("err", err.Error()))
		os.Exit(1)
	}

	proxy := httputil.NewSingleHostReverseProxy(apiHost)
	// Rewrite Host header to match the target.
	originalDirector := proxy.Director
	proxy.Director = func(req *http.Request) {
		originalDirector(req)
		req.Host = apiHost.Host
	}
	return accessTokenMiddleware(proxy)
}

func logger() *slog.Logger {
	return slog.New(&logCtxHandler{
		Handler: slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{
			Level: slog.LevelDebug,
			// Make sure time is logged in UTC.
			ReplaceAttr: func(groups []string, attr slog.Attr) slog.Attr {
				if attr.Key == slog.TimeKey {
					now := time.Now().UTC()
					return slog.Attr{Key: slog.TimeKey, Value: slog.StringValue(now.String())}
				}
				return attr
			},
		}),
	})
}

type logCtxHandler struct {
	slog.Handler
}

func (h *logCtxHandler) Handle(ctx context.Context, r slog.Record) error {
	if correlationID, ok := ctx.Value(CtxKeyCorrelationID).(string); ok {
		r.AddAttrs(slog.String("correlation_id", correlationID))
	}

	if interactionID, ok := ctx.Value(CtxKeyInteractionID).(string); ok {
		r.AddAttrs(slog.String("interaction_id", interactionID))
	}

	return h.Handler.Handle(ctx, r)
}

func middleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if strings.HasPrefix(r.Header.Get("Authorization"), "Basic ") {
			slog.Error("basic authentication is not supported", "authorization", r.Header.Get("Authorization"))
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}

		ctx := r.Context()
		ctx = context.WithValue(ctx, CtxKeyCorrelationID, uuid.NewString())
		if fapiID := r.Header.Get("X-Fapi-Interaction-Id"); fapiID != "" {
			ctx = context.WithValue(ctx, CtxKeyInteractionID, fapiID)
		}
		slog.InfoContext(ctx, "request received", "method", r.Method, "path", r.URL.Path)

		if cert := r.Header.Get(HeaderClientCert); cert != "" {
			cert = normalizeCertificate(cert)
			r.Header.Set(HeaderClientCert, cert)
			slog.InfoContext(ctx, "client certificate", slog.String("cert", cert))
		}

		start := time.Now().UTC()
		rw := &responseWriter{ResponseWriter: w, statusCode: http.StatusOK}
		defer func() {
			if rec := recover(); rec != nil {
				slog.Error("panic recovered", "error", rec, "stack", string(debug.Stack()))
				http.Error(w, "internal error", http.StatusInternalServerError)
			}
			slog.InfoContext(ctx, "request completed", slog.Duration("duration", time.Since(start)), slog.Int("status", rw.statusCode))
		}()

		r = r.WithContext(ctx)
		next.ServeHTTP(rw, r)
	})
}

func envValue[T ~string](key, fallback T) T {
	if value, exists := os.LookupEnv(string(key)); exists {
		return T(value)
	}
	return fallback
}

type responseWriter struct {
	http.ResponseWriter
	statusCode int
}

func (rw *responseWriter) WriteHeader(code int) {
	rw.statusCode = code
	rw.ResponseWriter.WriteHeader(code)
}

func middlewareLocal(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		r.Header.Set("X-Forwarded-Proto", "https")
		r.Header.Set("X-Forwarded-Host", r.Host)

		// Extract and set the client's certificate and DN.
		if r.TLS != nil && len(r.TLS.PeerCertificates) > 0 {
			// The TLS Block Ensures that the correct ordering has taken place and that the leaf certificate will be at block 0.
			clientCert := r.TLS.PeerCertificates[0]
			certPEM := string(pem.EncodeToMemory(&pem.Block{
				Type:  "CERTIFICATE",
				Bytes: clientCert.Raw,
			}))
			certPEM = strings.ReplaceAll(certPEM, "\n", " ")
			r.Header.Set(HeaderClientCert, certPEM)
			r.Header.Set(HeaderClientCertDN, clientCert.Subject.String())
			r.Header.Set(HeaderClientCertVerify, "SUCCESS")
		}
		next.ServeHTTP(w, r)
	})
}

func accessTokenMiddleware(next http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		accessToken := strings.TrimPrefix(r.Header.Get("Authorization"), "Bearer ")
		if accessToken == "" {
			slog.ErrorContext(r.Context(), "no authorization header", "status", http.StatusUnauthorized)
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}

		tokenInfo, err := introspect(r, accessToken)
		if err != nil {
			slog.ErrorContext(r.Context(), "introspection failed", slog.String("error", err.Error()))
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}

		r.Header.Set("access_token", tokenInfo)
		next.ServeHTTP(w, r)
	})
}

func introspect(r *http.Request, token string) (string, error) {
	introspectionURL := AuthHost + "/token/introspection"

	data := url.Values{}
	data.Set("token", token)

	req, err := http.NewRequestWithContext(r.Context(), http.MethodPost, introspectionURL, strings.NewReader(data.Encode()))
	if err != nil {
		return "", fmt.Errorf("failed to create introspection request: %w", err)
	}

	req.SetBasicAuth(IntrospectionClientID, IntrospectionClientSecret)
	req.Header.Set("Content-Type", "application/x-www-form-urlencoded")

	httpClient := &http.Client{Timeout: 3 * time.Second}
	resp, err := httpClient.Do(req)
	if err != nil {
		return "", fmt.Errorf("failed to introspect token: %w", err)
	}
	defer resp.Body.Close()

	if resp.StatusCode != http.StatusOK {
		body, err := io.ReadAll(resp.Body)
		if err != nil {
			return "", fmt.Errorf("token introspection returned non-200 status: %s. %w", resp.Status, err)
		}
		slog.ErrorContext(r.Context(), "introspection failed", slog.String("status", resp.Status), slog.String("body", string(body)))
		return "", fmt.Errorf("token introspection returned non-200 status: %s", resp.Status)
	}

	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return "", fmt.Errorf("failed to read introspection response body: %w", err)
	}

	var introspectionResponse map[string]any
	if err := json.Unmarshal(body, &introspectionResponse); err != nil {
		return "", fmt.Errorf("failed to unmarshal introspection response: %w", err)
	}

	slog.InfoContext(r.Context(), "introspection response", slog.Any("response", introspectionResponse))

	if active, ok := introspectionResponse["active"].(bool); !ok || !active {
		return "", fmt.Errorf("token is not active")
	}

	// Check x5t#S256 against client certificate's SHA-256 thumbprint.
	if cnf, ok := introspectionResponse["cnf"].(map[string]any); ok {
		if x5tS256, ok := cnf["x5t#S256"].(string); ok {
			if err := verifyCertificateThumbprint(r, x5tS256); err != nil {
				return "", fmt.Errorf("client certificate thumbprint verification failed: %w", err)
			}
		}
	}

	return string(body), nil
}

func verifyCertificateThumbprint(r *http.Request, x5tS256 string) error {
	cert := r.Header.Get(HeaderClientCert)
	if cert == "" {
		return errors.New("no client certificate found")
	}
	cert = certRegex.ReplaceAllString(cert, "")

	certData, err := base64.StdEncoding.DecodeString(cert)
	if err != nil {
		return fmt.Errorf("failed to decode base64 certificate data: %w", err)
	}

	hash := sha256.Sum256(certData)
	certThumbprint := base64.RawURLEncoding.EncodeToString(hash[:])
	if certThumbprint != x5tS256 {
		return fmt.Errorf("client certificate thumbprint verification failed. want: %s, got: %s", x5tS256, certThumbprint)
	}
	return nil
}

// normalizeCertificate handles certificates that may come URL-encoded or with spaces.
func normalizeCertificate(cert string) string {
	if cert == "" {
		return cert
	}
	if strings.Contains(cert, "%") {
		if decoded, err := url.QueryUnescape(cert); err == nil {
			cert = decoded
		}
	}
	// Normalize newlines to spaces to keep consistent format
	cert = strings.ReplaceAll(cert, "\n", " ")
	return cert
}

func directoryHandler() http.Handler {
	var jwk jose.JSONWebKey
	if err := json.Unmarshal([]byte(`{
		"p": "4ZwqFaaF6CyPzkiURyECv7nb3QBH7tkfn3drjiY1ZaHKiyprdoANagibeha0rbgZIGhkkErBosKwecuLQ8yQz-9mTeHQ1FgOlmxSJL9Eod2s_WLqxUr3WDUbOr724i5VfOaUo8fZYA867y_MomhwFyO5BzFfbECztwCmj-zH61E",
		"kty": "RSA",
		"q": "ww4Ur8cD54BgnHSx6yQHN8S0ArzI_2NdF-d267Xtb0HUyqSbiaRcvAqIrRXkgz_bD-ySDzVXfUbl_z_hYumLGx3RVsRDacniBianlrFdVZJx-7jH4uS3FSTTnFnnzThyKg4ClV73K8lsfx0N5LT4TqnZmKQbvFlzTGZL49LkI-U",
		"d": "DLsqL1iCWaa1IpdMC7FS3bfIhFgShPgKl1md9lss5McNj7AbcbaHobBIpGaSEpD9h3H73Q-_C1z7my_jeolepdQYrCleso6Go1cGnq1o85YJIc0Oq2cKXcuBtFiXIxCkRIPn1Sp9b2W7bKt3a-dcthqvsD8KQXE0olwkPQ-TMKm0VrZkrF9S6ckMUN95q7ctJgIO57g01HpXghbgVgf-sAtiOS72p8zsn5RQCn3bRkNSuAXJQGLyKTzPduBGveFpdLKFaM39NRlAJN8v_-VjOFGmAVqLA0vQ7RAJ_7UTPCVc9Y0iHdMWD10py87ccG3LOYTFkbHSJMqyT3zQwq8-sQ",
		"e": "AQAB",
		"use": "sig",
		"kid": "5BoZidMo6GVSAstsPgI10rZj-FmlpW8jVur2FjKJ0Zs",
		"qi": "kAvfh8Rx4vbEXnmzLmXGbxNqT6hV6HTXmrdnhY16nOBube9FVknGfY2aB8RAYH9ZXuFBpkmlNRxDclBHjs6eFJ7YZ3nxGR4wXzTjjCjG6xmyp3NG0Pbe0TqCBqcC79iQhxUxDidoMncbqlU7mYe_1HVM9ZxxNAY5qZXkOo7sOiM",
		"dp": "KdRhfSgl1blFZHLSgymcr92O5TfjHmbFVTS4DWAKMHDB8_GGgS8WzZ0Q7p79GuRyTC7uzk39_uZn__z8MjLgep0hc7k1ldlJwxwMUuHfoL9QDp7jdncCyyj1hnvXnHIIyaKa1o78P7IzNBvBri788V1fNfUygwiwCXMmbrLxEjE",
		"alg": "PS256",
		"dq": "EIuGN67C5wUdrMe9O7vPnOxjdIP87KTKBbgNf0rsO-6ylQnHY7J8ZzrhgwUDYBqvgzdG4GFe7XJxGeiaPqCeuwsZcamuKjAEqw7mUkLzLsoAPyDaW6WY3gNEq9N4dRDfpi-QComGn8EzIckeH5M2KL4BhhANhjl0LTvUHhwKW_0",
		"n": "q-Zc0-d1ZoxPjUEvHKVFvcNU9klv4fdadhNYwFwnm3g0kiHyaMWlKYRqNQtFuEyxZZHqdP05MSu8PAwc-moaCfszUbACGxhdPffZYG2vb253y-Zx0RHu-1a44cEhORGzFUBTLdZWrT4He_qthEfQtuFHlN1-QICXPkOfEc3UGrBNA_JJyVz-tvHs6tv5jhD80WRlbfv7Ll_WwxpaXhyRFyfjFWjALeLcEFDsvPaIl_5d_7s8a0nSh0xuxQaMKeaPbhrCS02xf6y32TuuIHiR06p7Q6BtZWCrcxAjJudAuCdmIU7onHstFAO504b7sQwG4fHG3_v5Y-KIQfEHKiWSdQ"
	}`), &jwk); err != nil {
		slog.Error("failed to unmarshal jwk", slog.String("err", err.Error()))
		os.Exit(1)
	}

	signSsa := func(ss map[string]any) (string, error) {
		now := time.Now().Unix()
		ss["iat"] = now
		ss["exp"] = now + 3600
		key := jose.SigningKey{
			Algorithm: jose.PS256,
			Key:       jwk,
		}
		opts := (&jose.SignerOptions{}).WithType("JWT")

		joseSigner, err := jose.NewSigner(key, opts)
		if err != nil {
			return "", fmt.Errorf("failed to create jose signer: %w", err)
		}

		jws, err := jwt.Signed(joseSigner).Claims(ss).Serialize()
		if err != nil {
			return "", fmt.Errorf("failed to serialize ssa: %w", err)
		}

		return jws, nil
	}

	ssBytes, err := os.ReadFile(SoftwareStatementFilePath)
	if err != nil {
		slog.Info("unable to read software_statement.json", slog.String("err", err.Error()))
		ssBytes = []byte(`{}`)
	}
	var ss map[string]any
	if err := json.Unmarshal(ssBytes, &ss); err != nil {
		slog.Info("unable to parse software_statement.json", slog.String("err", err.Error()))
		ss = map[string]any{}
	}

	mux := http.NewServeMux()

	mux.HandleFunc("/.well-known/openid-configuration", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_ = json.NewEncoder(w).Encode(map[string]any{
			"token_endpoint": "https://directory/token",
		})
	})

	mux.HandleFunc("/token", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_ = json.NewEncoder(w).Encode(map[string]any{
			"access_token": "token",
			"token_type":   "bearer",
		})
	})

	mux.HandleFunc("/participants", func(w http.ResponseWriter, r *http.Request) {
		http.ServeFile(w, r, ParticipantsFilePath)
	})

	mux.HandleFunc("/jwks", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		w.WriteHeader(http.StatusOK)
		_ = json.NewEncoder(w).Encode(jose.JSONWebKeySet{Keys: []jose.JSONWebKey{jwk}})
	})

	mux.HandleFunc("/organisations/{org_id}/softwarestatements/{ss_id}/assertion", func(w http.ResponseWriter, r *http.Request) {
		ssa, err := signSsa(ss)
		if err != nil {
			slog.Error("failed to sign ssa", slog.String("err", err.Error()))
			http.Error(w, "failed to sign ssa", http.StatusInternalServerError)
			return
		}
		w.Header().Set("Content-Type", "application/jwt")
		w.WriteHeader(http.StatusOK)
		if _, err := w.Write([]byte(ssa)); err != nil {
			http.Error(w, "failed to write response", http.StatusInternalServerError)
		}
	})

	mux.HandleFunc("/{org_id}/application.jwks", func(w http.ResponseWriter, r *http.Request) {
		http.ServeFile(w, r, ClientOnePublicJWKSPath)
	})
	mux.HandleFunc("/{org_id}/client_one/application.jwks", func(w http.ResponseWriter, r *http.Request) {
		http.ServeFile(w, r, ClientOnePublicJWKSPath)
	})
	mux.HandleFunc("/{org_id}/client_two/application.jwks", func(w http.ResponseWriter, r *http.Request) {
		http.ServeFile(w, r, ClientTwoPublicJWKSPath)
	})

	return mux
}

func tlsConfiguration() *tls.Config {

	serverCert, err := tls.LoadX509KeyPair(ServerCertFilePath, ServerKeyFilePath)
	if err != nil {
		slog.Error("failed to load server certificate", slog.String("err", err.Error()))
		os.Exit(1)
	}

	caCerts := caCertPool()
	return &tls.Config{
		Certificates:     []tls.Certificate{serverCert},
		ClientCAs:        caCerts,
		ClientAuth:       tls.VerifyClientCertIfGiven,
		MinVersion:       tls.VersionTLS12,
		MaxVersion:       tls.VersionTLS13,
		CurvePreferences: []tls.CurveID{tls.CurveP521, tls.CurveP384, tls.CurveP256},

		CipherSuites: []uint16{
			//TLS 1.2
			tls.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
			tls.TLS_RSA_WITH_AES_256_GCM_SHA384,
			tls.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
			tls.TLS_RSA_WITH_AES_128_GCM_SHA256,
			//TLS 1.3 these are actually ignored, but kept here to provide clarity on what's enabled by default.
			tls.TLS_CHACHA20_POLY1305_SHA256,
			tls.TLS_AES_128_GCM_SHA256,
			tls.TLS_AES_256_GCM_SHA384,
		},
	}
}

func caCertPool() *x509.CertPool {
	caCertPEM, err := os.ReadFile(CACertFilePath)
	if err != nil {
		slog.Error("unable to read ca.crt", slog.String("err", err.Error()))
		os.Exit(1)
	}
	caPool := x509.NewCertPool()
	if ok := caPool.AppendCertsFromPEM(caCertPEM); !ok {
		slog.Error("unable to append ca certs")
		os.Exit(1)
	}

	return caPool
}
