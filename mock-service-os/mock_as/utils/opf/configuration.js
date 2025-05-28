import { errors } from 'oidc-provider';
import Debug from 'debug';
import pkg from 'jose';
const { JWT } = pkg;
import { bankAdapter } from './adapter.js';
import {
  isRecurringPayment,
  isPayment,
  isEnrollment,
  getConsentId,
  getRecurringConsentId,
  getEnrollmentId,
} from './helpers.js';

const log = Debug('raidiam:server:info');
const err = Debug('raidiam:server:error');

// Validate that the consent ID sent in the token request is authorized before
// issuing an access token.
// If there's no consent ID in the request, the validation is skipped.
async function validateConsent(token) {
  if (!token.scope.includes('openid')) {
    log('the token does not include the scope openid');
    return;
  }

  let data;
  if (isEnrollment(token.scope)) {
    log('handling consent for enrollment');
    let id = getEnrollmentId(token.scope);
    log(`enrollment id: ${id}`);
    let enrollment = await bankAdapter.getEnrollment(id);
    data = enrollment.data;
  } else if (isPayment(token.scope)) {
    log('handling consent for payment');
    let id = getConsentId(token.scope);
    log(`payment consent id: ${id}`);
    let paymentConsent = await bankAdapter.getPaymentConsent(id);
    data = paymentConsent.data;
  } else if (isRecurringPayment(token.scope)) {
    log('handling consent for recurring payment');
    let id = getRecurringConsentId(token.scope);
    log(`recurring payment consent id: ${id}`);
    let recurringPaymentConsent = await bankAdapter.getRecurringPaymentConsent(id);
    data = recurringPaymentConsent.data;
  } else {
    log('handling simple consent');
    let id = getConsentId(token.scope);
    log(`consent id: ${id}`);
    let consent = await bankAdapter.getConsent(id);
    data = consent.data;
  }

  const status = data?.status;
  if (['REJECTED', 'REVOKED', 'CONSUMED', 'AWAITING_AUTHORISATION'].includes(status)) {
    throw new errors.InvalidGrant(`grant status is ${status}`);
  }

  console.log('consent is authorised');
}

export default function (mtlsIssuer, ssaJwks) {
  return {
    scopes: [
      'openid',
      'profile',
      'email',
      'address',
      'phone',
      'consent',
      'payments',
      'user:account',
      'user:consent',
      'user:janitor',
      'org:admin',
      'accounts',
      'credit-cards-accounts',
      'consents',
      'customers',
      'invoice-financings',
      'financings',
      'resources',
      'op:consent',
      'op:payments',
      'unarranged-accounts-overdraft',
      'loans',
      'bank-fixed-incomes',
      'credit-fixed-incomes',
      'variable-incomes',
      'treasure-titles',
      'funds',
      'recurring-payments',
      'exchanges',
      'nrp-consents',
      'credit-portability',
    ],
    interactions: {
      url(ctx, interaction) {
        // eslint-disable-line no-unused-vars
        return `/interaction/${interaction.uid}`;
      },
    },
    cookies: {
      keys: ['some secret key', 'and also the old rotated away some time ago', 'and one more'],
    },
    claims: {
      address: ['address'],
      email: ['email', 'email_verified'],
      phone: ['phone_number', 'phone_number_verified'],
      profile: [
        'birthdate',
        'family_name',
        'gender',
        'given_name',
        'locale',
        'middle_name',
        'name',
        'nickname',
        'picture',
        'preferred_username',
        'profile',
        'updated_at',
        'website',
        'zoneinfo',
      ],
      openid: ['sub', 'acr'],
    },
    acrValues: ['urn:brasil:openbanking:loa2', 'urn:brasil:openbanking:loa3'],
    enabledJWA: {
      authorizationSigningAlgValues: ['PS256'],
      introspectionSigningAlgValues: ['PS256'],
      requestObjectSigningAlgValues: ['PS256'],
      clientAuthSigningAlgValues: ['PS256'],
      userinfoSigningAlgValues: ['PS256'],
      idTokenSigningAlgValues: ['PS256'],
      requestObjectEncryptionAlgValues: ['RSA-OAEP'],
      requestObjectEncryptionEncValues: ['A256GCM'],
      idTokenEncryptionAlgValues: ['RSA-OAEP'],
      idTokenEncryptionEncValues: ['A256GCM'],
    },
    clientDefaults: {
      grant_types: ['authorization_code', 'client_credentials', 'refresh_token', 'implicit'],
      id_token_signed_response_alg: 'PS256',
      request_object_signed_response_alg: 'PS256',
      request_object_signing_alg: 'PS256',
      authorization_signed_response_alg: 'PS256',
      response_types: ['code', 'code id_token'],
      tls_client_certificate_bound_access_tokens: true,
    },
    ttl: {
      AccessToken: function AccessTokenTTL(ctx, token, client) {
        if (token.resourceServer) {
          return token.resourceServer.accessTokenTTL || 60 * 15; // 15 minutes in seconds
        }
        return 60 * 15; // 15 minutes in seconds
      },
      AuthorizationCode: 900 /* 15 minutes in seconds */,
      ClientCredentials: function ClientCredentialsTTL(ctx, token, client) {
        if (token.resourceServer) {
          return token.resourceServer.accessTokenTTL || 15 * 60; // 15 minutes in seconds
        }
        return 15 * 60; // 15 minutes in seconds
      },
      DeviceCode: 900 /* 15 minutes in seconds */,
      Grant: function GrantTTL(ctx, token, client) {
        if (token.grantTtl) {
          return token.grantTtl;
        }
        return 157680000; /* 5 years in seconds */
      },
      IdToken: 3600 /* 1 hour in seconds */,
      Interaction: 600 /* 1 hour in seconds */,
      RefreshToken: function RefreshTokenTTL(ctx, token, client) {
        if (
          ctx &&
          ctx.oidc.entities.RotatedRefreshToken &&
          client.applicationType === 'web' &&
          client.tokenEndpointAuthMethod === 'none' &&
          !token.isSenderConstrained()
        ) {
          // Non-Sender Constrained SPA RefreshTokens do not have infinite expiration through rotation
          return ctx.oidc.entities.RotatedRefreshToken.remainingTTL;
        }

        return 365 * 24 * 60 * 60; // 1 Year In seconds
      },
      Session: 2 * 60 * 60 /* 2 Hours in seconds */,
    },
    jwks: {
      keys: [
        {
          crv: 'P-256',
          d: 'K9xfPv773dZR22TVUB80xouzdF7qCg5cWjPjkHyv7Ws',
          kty: 'EC',
          use: 'sig',
          x: 'FWZ9rSkLt6Dx9E3pxLybhdM6xgR5obGsj5_pqmnz5J4',
          y: '_n8G69C-A2Xl4xUW2lF0i8ZGZnk_KPYrhv4GbTGu5G4',
        },
      ],
    },
    features: {
      devInteractions: { enabled: false }, // defaults to true
      fapi: {
        enabled: true,
        profile: '1.0 Final',
      },
      encryption: {
        enabled: true,
      },
      pushedAuthorizationRequests: {
        allowUnregisteredRedirectUris: false,
        enabled: true,
        requirePushedAuthorizationRequests: false,
      },
      introspection: { enabled: true }, // defaults to false
      jwtResponseModes: { enabled: true },
      clientCredentials: { enabled: true },
      requestObjects: {
        mode: 'strict',
        request: true,
        requestUri: false,
        requireSignedRequestObject: false,
        requireUriRegistration: true,
      },
      registration: {
        enabled: true,
      },
      registrationManagement: {
        enabled: true,
        rotateRegistrationAccessToken: false,
      },
      deviceFlow: { enabled: true }, // defaults to false
      revocation: { enabled: true }, // defaults to false
      mTLS: {
        enabled: true,
        certificateBoundAccessTokens: true,
        selfSignedTlsClientAuth: false,
        tlsClientAuth: true,
        getCertificate(ctx) {
          console.log('Geting client cert');
          console.log(ctx.get('BANK-TLS-Certificate'));
          return ctx.get('BANK-TLS-Certificate');
        },
        certificateAuthorized(ctx) {
          return ctx.get('BANK-TLS-Certificate');
        },
        certificateSubjectMatches(ctx, property, expected) {
          if (property !== 'tls_client_auth_subject_dn') {
            log.error(`${property} is not supported by this deployment`);
            throw new Error(`${property} is not supported by this deployment`);
          }
          const subject = ctx.get('X-BANK-Certificate-DN');
          if (subject === expected) {
            return true;
          }
          var decoded = decodeURI(subject);
          if (decoded === expected) {
            return true;
          }
          // Else return false
          return false;
        },
      },
    },
    issueRefreshToken: async (ctx, client, code) => {
      if (!client.grantTypeAllowed('refresh_token')) {
        return false;
      }
      return true;
    },
    jwks: {
      keys: [
        {
          p: '5BKxIVlA8DKoAbXnyNr-M_nAAi63lUCrCki7ADrsifHgTspQydfdQVA8DcqS0JxaHGlWr-mCjrMSSd8x1WOWW8TNqf0NF9O3XZGuCG35xbLG8V72pIMPM5HWr91RTQ0w6FqYkRJsot2ZYK53rtsDSwqQPK7LbRZTaSs-MCB-6SE',
          kty: 'RSA',
          q: 'u1_MSt9DNMqgL1N24S5VHXYmNH8p1ZP70KqH4WmJuYQfbgqQ7sU0L7nkR_H_IHHZqL3bruYVNPcKaE7tmHH5sRkix_R_MudjynV2la03UCKoSvnUgb0dguW9xDHKaXyVhzi24OPjolhhu0RYOqzF2GSJ2yZ0Z1zjPNLksEhxC2c',
          d: 'lWi6shKVV8-nggjqc8PmpWOmMIDvfOiYUWVinjwyDEueljRBFUqrc8Z_lNQraEfM9dQ-GfNycEM9wN581H6M80hoVepTROMSYPZfDE_mX6aE48OReo6hJQvB3tUAuSkdjQj9_Tc_TLQEott-L89IJsAqP7AQSS0WvzfJL4O-YtIiyYNqbgbRfVTfaGAMIUKlO_dEf8jsbigBAbGVT7LIcAf9UokUBKc7Kudl_xCMzbDdM03xJeC5Ml0peOmnnTGc2NdBHSyXITnkrOJlnQWz5ZzyiJ3Os9Zm34gWcdXDz8emS9AHftqv6c9FbmBq9jMNU4_tiIMdAo6-p-xizlJawQ',
          e: 'AQAB',
          use: 'sig',
          qi: 'TsQgLFpb6TozodDm_zoUcoPY6sWlijhvHgFFKAjnB2ssCJ6lO7X3bnep_cUt-dtkV7eZnh5A0cUNVtNnP3ni9EkpxWwsSTrG_TZHmbHxaHXnF-G4s6lrYGWBXbgucALegEFpmuJUThxpLEbpPOUzsWTxI66nOd6T5Quwx7qGv30',
          dp: '0FrfJMckEwtD_qQOxqiBeDweFCBXqGs2liORaolqFC86quAa4_pnb8Z7xmGctCVSEQiOoBAkLHcdKw1Suk3LS7TD6hp6Pp00s69lnN_TQa-sHU-S5QGx_nup9GmsX0bAulQhcs6xHixxdSiNv9jm7kQNNtK8lsDBnJ9bpZ3aMuE',
          alg: 'PS256',
          dq: 'Z7QtrYLD_4PmBEt9kEPEd_ncS1HWJY8x39uCOQ_gWfz2KEFQ1dXvfDq2TdtyCNL6VJo_7B0Lv7S63eBRP_5U49-1kFWR0OqgIH3ClDS6WG_WFSkQpH22x6u_y8aC8L8zQxPwo6d9ZWzlKnA5JMBa_9klM1WlN1ABtLhEOgzeBCE',
          n: 'pu8AVLEIfYppnbU0r2M1PNhCvYpGnVXbSXj-OxRX72e_us-pYg2KnkTOPTIm3vQ53GsVYb8ajktGxvjWNzBeI2-OPXhwhvBKG14m_EON8t3_6fiB6PKsoFU474LLHilOr4TwOUh_oYjv_-5Ej5x1Je6XMHnsKkDCCmO1tzKoGZnoFgXxov12dZ84U374q5zwLzngPk7BC2Q0G7wIFbwf1Xm5ECSHXFHT_17iaRhu2s5eQ6B1dgx9RJBXjN-cgqZQIeNptbqXH67I3LaM_JcbKfrpx7KbDWivvKrfeWTyBJuJ9t8WD7k_4lfbbb4HUMKM761MgiIMv7GAZ8sItqU3Rw',
        },
        {
          p: '_aSA0u5saMEl1hc9-Sglp9LDOeZcgs_Gw7Olxefs77bIjMQpFwrFsIWR4HH6K9nscTIAKNM9AVq30Y1TTB0idebzPbjECB90KgYa3hm2g4A6pHkaOuHs0RGTWbWavDUkQka-CSB8hE7sTNSrmDpG8FbLihuSzDFWCdLGsDqXeuk',
          kty: 'RSA',
          q: 'gvBSWfBZtjHBqhwxXdO5k9J0nNqPta-sBuKc7PNhODbr0UWNkHcailKWs3f0ViXaSRAEW-EB9Ty4plgMBjy-ycc4va1Rfg-6Pn_tnVYbB5-4nmHO8vAFZR4EP4MHipyizJfNPuSlawLNc71Eo5lAUWzPRpTBZ1XvQ9AZgx-wA2M',
          d: 'et4yFr71HRMW2epVzYPNcNfqGJqTU7NsbVMCSH-ZDJ_ysPn5CgTmAK-NZh2hJvra4RCBgpOQiEYqEqX5jc3xPZyTUtCTJwRpgVNLnhylk031hy22qA2QqWRsWGLBxRvgP8gb9intIs6MkrIiPkO2t5o3J9OYpF7aO40mXH5CM2EJm-FxqGuKMVb_zWVqImmh3mqC2GlPBsiZLcHeFIbtGopsel07nngBBSmCOf7XAmtqYvZAGiJQkd1poI7p_c5n7x3aj1jPGShVLzfLBWqNipoZk0GfbY7qTlkY6dT2x098V_MSpSip9tkQ__whdHOlR5GE_HT0vlmhfwixZKaTEQ',
          e: 'AQAB',
          use: 'enc',
          qi: 'eiD4hKfSwXUVN8q14yL2JK4rUt0heIZ93CHVtkonsA8VasPOI1E6D51WaFRHaJxgvn7CiY16h2qg9xjP1uMBNcuscSKRnyqAeGJuyPh576-FWxJlZSqh9PoSxj4eHQMCWmBBi7TL820hrgA2mhc0KLekCRT36-89-Va7G74N5A8',
          dp: 'n1NJNLZd1MOXD8-Tt0HXvX6v8VvZurXnhiD_vbw84isv-PRzVy0GFycgBhuyaP8__a7J2NswE_y3QOOEcmhOsD79hkTcprmTT558HA2MzzeqHoyPxHMMPhvLMmvYIedDunoTf0ovzTCCUJS6oSniS7BJtJwzbx6CjDMhaau0YZk',
          alg: 'RSA-OAEP',
          dq: 'G1DXXTvu-ztWE47eHZzV0ijNewt9f4GueaE865G6bmfGulmwNrsiJkkkdzxHFNHAwA0_W4uNRQPt4YXsvEBf7OhKxgcqQQo26GL3xyL3cJe5hBETg0rfVUD10eob4Kbcr6Hbh4tblv92rPaHIzoNWO9CLo9J6azbxWHccKZjqdE',
          n: 'gbulO7BqCAKwVy3ZqrR033OM1Mp-SqOViwD1manyHjhDSB5dPLL8AG9zdl8hoQwQO8TVR4Ske2oYLkr9zxtWROTYKvF6Ssp0W5Df-sE6lEnMRqPr0GNrIubA0i2I0-uuK26N-x2_KJZbrMviH8qAdQGKopJ1-9DTvgXbOZmzQDuP3s0V8BB7pSroOaBpE7wtKAr5akPElbw_XR7m5ocmbd2TIHu8kdLU4W60Aha7x427KaYhetbtVkkS3h6j7FP9Wm2iMSkneo2ZA0WP4N4jqv3wqA2c7d_IeQNWmUxFrIoApmhy4MoMMDXjmWM_7JwH1UK6RsaknAfT7C0YJjVDGw',
        },
      ],
    },
    extraClientMetadata: {
      properties: [
        'software_statement',
        'software_id',
        'client_description',
        'org_id',
        'org_name',
        'org_number',
        'webhook_uris',
      ],
      validator(ctx, key, value, metadata) {
        if (key === 'software_statement') {
          if (value === undefined && ctx === undefined) return;
          // software_statement is not stored, but used to convey client metadata

          if (metadata.jwks) {
            throw new errors.InvalidClientMetadata('jwks by value not permitted');
          }
          let payload;
          try {
            // extraClientMetadata.validator must be sync :sadface:
            payload = JWT.verify(value, ssaJwks, {
              algorithms: ['PS256'],
              issuer: process.env.TRUSTFRAMEWORK_SSA_ISS,
              maxTokenAge: '5 days',
              typ: 'JWT',
            });

            // This has the double benefit of also ensuring that the DCR is presented over a mtls link
            // const subject = ctx.get('X-BANK-Certificate-DN');
            // if (
            //   !subject ||
            //   !payload.software_id ||
            //   (!subject.includes(`CN=${payload.software_id}`) &&
            //     !subject.includes(`UID=${payload.software_id}`))
            // ) {
            //   throw new errors.UnapprovedSoftwareStatement(
            //     `software_statement not approved for use with presented client tls certificate or no client tls certificate presented. expected x509 CN or UID: ${
            //       payload.software_id
            //     }, actual x509: ${ctx.get('X-BANK-Certificate-DN')}`,
            //   );
            // }

            if (payload.org_status != 'Active') {
              throw new errors.UnapprovedSoftwareStatement(
                'software_statement not approved for use, organisation inactive',
              );
            }

            if (!payload.software_roles || payload.software_roles.length == 0) {
              throw new errors.UnapprovedSoftwareStatement(
                'software_statement not approved for use, no regulatory roles defined',
              );
            }

            if (!payload.software_redirect_uris || payload.software_redirect_uris.length == 0) {
              throw new errors.UnapprovedSoftwareStatement(
                'software_statement not approved for use, no redirect uris defined',
              );
            }

            if (metadata.software_id && metadata.software_id != payload.software_id) {
              throw new errors.InvalidSoftwareStatement('software statement does not blelong to this client');
            }

            if (metadata.jwks_uri && metadata.jwks_uri != payload.software_jwks_uri) {
              throw new errors.InvalidClientMetadata('jwks uri is invalid');
            }

            if (metadata.org_id && metadata.org_id != payload.org_id) {
              throw new errors.InvalidSoftwareStatement('software statement does not blelong to this client');
            }

            if (!metadata.redirect_uris || metadata.redirect_uris.length == 0) {
              throw new errors.InvalidClientMetadata('no redirect uris defined');
            }

            metadata.redirect_uris = metadata.redirect_uris.filter((item) =>
              payload.software_redirect_uris.includes(item),
            );

            if (!metadata.redirect_uris || metadata.redirect_uris.length == 0) {
              throw new errors.InvalidClientMetadata('no valid redirect uris defined');
            }

            // Validate the webhook URIs if requested by the client.
            if (metadata.webhook_uris && metadata.webhook_uris.length != 0) {
              // Deny the request if the client sent webhook_uris, but the field software_api_webhook_uris in the ssa is empty.
              if (!payload.software_api_webhook_uris || payload.software_api_webhook_uris == 0) {
                throw new errors.InvalidClientMetadata('no webhooks uris defined in the software statement');
              }

              // Deny the request if an invalid webhook_uri was requested.
              let valid_webhook_uris = metadata.webhook_uris.filter((item) =>
                payload.software_api_webhook_uris.includes(item),
              );
              if (metadata.webhook_uris.length != valid_webhook_uris.length) {
                throw new errors.InvalidClientMetadata('invalid webhook uri');
              }
            }

            const scopes = ['openid'];
            if (payload.software_roles && payload.software_roles.includes('DADOS')) {
              scopes.push(
                'accounts',
                'credit-cards-accounts',
                'consents',
                'customers',
                'invoice-financings',
                'financings',
                'resources',
                'loans',
                'unarranged-accounts-overdraft',
                'bank-fixed-incomes',
                'credit-fixed-incomes',
                'variable-incomes',
                'treasure-titles',
                'funds',
                'recurring-payments',
                'exchanges',
                'nrp-consents',
                'credit-portability',
              );
            }
            if (payload.software_roles && payload.software_roles.includes('PAGTO')) {
              scopes.push('payments');
            }

            let requestedArray;
            if (metadata.scope) {
              requestedArray = metadata.scope.split(' ');
            }

            const {
              software_jwks_uri,
              org_id,
              software_client_name,
              software_client_uri,
              software_tos_uri,
              software_logo_uri,
              software_policy_uri,
              software_id,
              software_client_description,
              org_name,
              org_number,
            } = payload;
            Object.assign(metadata, {
              software_id,
              org_id,
              org_name,
              org_number,
              client_description: software_client_description,
              jwks_uri: software_jwks_uri,
              application_type: 'web',
              client_name: software_client_name,
              id_token_signed_response_alg: 'PS256',
              request_object_signing_alg: 'PS256',
              authorization_signed_response_alg: 'PS256',
              tos_uri: software_tos_uri,
              logo_uri: software_logo_uri,
              request_object_encryption_alg: 'RSA-OAEP',
              request_object_encryption_enc: 'A256GCM',
              policy_uri: software_policy_uri,
              default_max_age: 0,
              require_signed_request_object: true,
              subject_type: 'public',
              ...(requestedArray && { scope: requestedArray.join(' ') }),
              ...(!requestedArray && { scope: scopes.join(' ') }),
              response_types: ['code id_token', 'code'],
              grant_types: ['client_credentials', 'authorization_code', 'refresh_token', 'implicit'],
              client_uri: software_client_uri,
              tls_client_certificate_bound_access_tokens: true,
            });

            // software_statement is not stored, but used to convey client metadata
            delete metadata.software_statement;
          } catch (error) {
            err(`${error.message}: ${JSON.stringify(metadata)}`);
            if (error.code === 'ERR_JWT_EXPIRED' || error.code === 'ERR_JWS_VERIFICATION_FAILED') {
              console.log(error);
              throw new errors.InvalidSoftwareStatement(`could not verify software_statement: ${error.message}`, error);
            } else if (error instanceof errors.InvalidClientMetadata) {
              throw error;
            } else if (error instanceof errors.InvalidSoftwareStatement) {
              throw error;
            } else console.log(error);
            throw new errors.InvalidClientMetadata(
              `unknown processing error, have you entered invalid client metadata: ${error.message}`,
            );
          }
        }
      },
    },
    extraTokenClaims: async (ctx, token) => {
      let claims = {
        org_id: token.client.org_id,
        org_name: token.client.org_name,
        org_number: token.client.org_number,
        software_id: token.client.software_id,
      };

      let oidcProvider = ctx?.oidc?.provider;
      if (!oidcProvider) {
        // When the bank adapter creates a token, this function is called with
        // ctx as undefined.
        return;
      }

      await validateConsent(token);

      return claims;
    },
    discovery: {
      mtls_endpoint_aliases: {
        token_endpoint: `${mtlsIssuer}/token`,
        revocation_endpoint: `${mtlsIssuer}/token/revocation`,
        introspection_endpoint: `${mtlsIssuer}/token/introspection`,
        device_authorization_endpoint: `${mtlsIssuer}/device/auth`,
        registration_endpoint: `${mtlsIssuer}/reg`,
        userinfo_endpoint: `${mtlsIssuer}/me`,
        pushed_authorization_request_endpoint: `${mtlsIssuer}/request`,
      },
    },
  };
}
