package main

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"crypto/x509/pkix"
	"encoding/asn1"
	"encoding/json"
	"encoding/pem"
	"flag"
	"fmt"
	"log"
	"math/big"
	"os"
	"path/filepath"
	"runtime"
	"time"

	"github.com/google/uuid"
	"github.com/luikyv/go-oidc/pkg/goidc"
)

// Custom certificate fields.
var (
	oidLDAPUID        = asn1.ObjectIdentifier{0, 9, 2342, 19200300, 100, 1, 1}
	oidX500UID        = asn1.ObjectIdentifier{2, 5, 4, 45}
	oidOrganizationID = asn1.ObjectIdentifier{2, 5, 4, 97}
)

func main() {
	_, filename, _, _ := runtime.Caller(0)
	sourceDir := filepath.Dir(filename)
	certsDir := filepath.Join(sourceDir, "./certs")
	// Create the "certs" directory if it doesn't exist.
	err := os.MkdirAll(certsDir, os.ModePerm)
	if err != nil {
		log.Fatalf("Failed to create certs directory: %v", err)
	}

	orgID := flag.String("org_id", uuid.NewString(), "Organization ID")
	flag.Parse()

	caCert, caKey := generateCACert("ca", certsDir)

	_, _ = generateCert("mtls", *orgID, caCert, caKey, certsDir)

	_, _ = generateCert("op", *orgID, caCert, caKey, certsDir)

	clientOneCert, clientOneKey := generateCert("client_one", *orgID, caCert, caKey, certsDir)
	generateJWKS("client_one", clientOneCert, clientOneKey, certsDir)

	clientTwoCert, clientTwoKey := generateCert("client_two", *orgID, caCert, caKey, certsDir)
	generateJWKS("client_two", clientTwoCert, clientTwoKey, certsDir)
}

// Generates a Certificate Authority (CA) key and self-signed certificate.
func generateCACert(name, dir string) (*x509.Certificate, *rsa.PrivateKey) {
	caTemplate := &x509.Certificate{
		SerialNumber: big.NewInt(time.Now().UnixNano()),
		Subject: pkix.Name{
			CommonName: name,
		},
		NotBefore:             time.Now(),
		NotAfter:              time.Now().Add(365 * 24 * time.Hour),
		KeyUsage:              x509.KeyUsageCertSign | x509.KeyUsageCRLSign,
		IsCA:                  true,
		BasicConstraintsValid: true,
	}

	return generateSelfSignedCert(name, caTemplate, dir)
}

func generateSelfSignedCert(
	name string,
	template *x509.Certificate,
	dir string,
) (
	*x509.Certificate,
	*rsa.PrivateKey,
) {
	key, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		log.Fatalf("Failed to generate CA private key: %v", err)
	}

	certBytes, err := x509.CreateCertificate(
		rand.Reader,
		template,
		template,
		&key.PublicKey,
		key,
	)
	if err != nil {
		log.Fatalf("Failed to create CA certificate: %v", err)
	}
	// This is important for when generation the claim "x5c" of the JWK
	// corresponding to this cert.
	template.Raw = certBytes

	keyBytes, err := x509.MarshalPKCS8PrivateKey(key)
	if err != nil {
		log.Fatalf("Failed to create CA key: %v", err)
	}
	savePEMFile(filepath.Join(dir, name+".key"), "PRIVATE KEY", keyBytes)
	savePEMFile(filepath.Join(dir, name+".crt"), "CERTIFICATE", certBytes)

	fmt.Printf("Generated self signed certificate and key for %s\n", name)
	return template, key
}

// Generates a certificate signed by the CA.
func generateCert(
	name, orgID string,
	caCert *x509.Certificate,
	caKey *rsa.PrivateKey,
	dir string,
) (
	*x509.Certificate,
	*rsa.PrivateKey,
) {
	key, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		log.Fatalf("Failed to generate private key: %v", err)
	}

	cert := &x509.Certificate{
		SerialNumber: big.NewInt(time.Now().UnixNano()),
		Subject: pkix.Name{
			CommonName: name,
			ExtraNames: []pkix.AttributeTypeAndValue{
				{
					Type:  oidX500UID,
					Value: name,
				},
				{
					Type:  oidLDAPUID,
					Value: uuid.NewString(),
				},
				{
					Type:  oidOrganizationID,
					Value: orgID,
				},
			},
		},
		NotBefore:   time.Now(),
		NotAfter:    time.Now().Add(365 * 24 * time.Hour),
		KeyUsage:    x509.KeyUsageDigitalSignature,
		ExtKeyUsage: []x509.ExtKeyUsage{x509.ExtKeyUsageClientAuth},
	}

	// Create client certificate signed by the CA.
	certBytes, err := x509.CreateCertificate(
		rand.Reader,
		cert,
		caCert,
		&key.PublicKey,
		caKey,
	)
	if err != nil {
		log.Fatalf("Failed to create certificate: %v", err)
	}
	// This is important for when generation the claim "x5c" of the JWK
	// corresponding to this cert.
	cert.Raw = certBytes

	// Save private key and certificate.
	keyBytes, err := x509.MarshalPKCS8PrivateKey(key)
	if err != nil {
		log.Fatalf("Failed to create key: %v", err)
	}
	savePEMFile(filepath.Join(dir, name+".key"), "PRIVATE KEY", keyBytes)
	savePEMFile(filepath.Join(dir, name+".crt"), "CERTIFICATE", certBytes)

	fmt.Printf("Generated key and certificate for %s\n", name)
	return cert, key
}

// Saves data to a PEM file.
func savePEMFile(filename, blockType string, data []byte) {
	file, err := os.Create(filename)
	if err != nil {
		log.Fatalf("Failed to create %s: %v", filename, err)
	}
	defer file.Close()

	err = pem.Encode(file, &pem.Block{Type: blockType, Bytes: data})
	if err != nil {
		log.Fatalf("Failed to write PEM data to %s: %v", filename, err)
	}
}

func generateJWKS(
	name string,
	cert *x509.Certificate,
	key *rsa.PrivateKey,
	dir string,
) {
	sigJWK := goidc.JSONWebKey{
		Key:          key,
		KeyID:        uuid.NewString(),
		Algorithm:    string(goidc.PS256),
		Use:          string(goidc.KeyUsageSignature),
		Certificates: []*x509.Certificate{cert},
	}
	hash := sha256.New()
	_, _ = hash.Write(cert.Raw)
	sigJWK.CertificateThumbprintSHA256 = hash.Sum(nil)

	encKey := generateEncryptionJWK()
	jwks := goidc.JSONWebKeySet{
		Keys: []goidc.JSONWebKey{sigJWK, encKey},
	}

	jwksBytes, err := json.MarshalIndent(jwks, "", " ")
	if err != nil {
		log.Fatal(err)
	}

	err = os.WriteFile(filepath.Join(dir, name+".jwks"), jwksBytes, 0644)
	if err != nil {
		log.Fatal(err)
	}

	var publicJWKS goidc.JSONWebKeySet
	for _, jwk := range jwks.Keys {
		publicJWKS.Keys = append(publicJWKS.Keys, jwk.Public())
	}

	publicJWKSBytes, err := json.MarshalIndent(publicJWKS, "", " ")
	if err != nil {
		log.Fatal(err)
	}

	err = os.WriteFile(filepath.Join(dir, name+"_pub.jwks"), publicJWKSBytes, 0644)
	if err != nil {
		log.Fatal(err)
	}
}

func generateEncryptionJWK() goidc.JSONWebKey {
	key, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		log.Fatalf("Failed to generate RSA private key: %v", err)
	}

	return goidc.JSONWebKey{
		Key:       key,
		KeyID:     uuid.NewString(),
		Algorithm: string(goidc.RSA_OAEP),
		Use:       string(goidc.KeyUsageEncryption),
	}
}
