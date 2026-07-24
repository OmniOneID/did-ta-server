package org.omnione.did.list.oid4vci;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.common.Oid4vciUriValidator;

import static org.junit.jupiter.api.Assertions.*;

class Oid4vciUriValidatorTest {
    @Test
    void acceptsWebUrisAndNormalizesTrailingSlash() {
        assertEquals("https://issuer.example.com",
                Oid4vciUriValidator.credentialIssuer("https://issuer.example.com/"));
        assertEquals("http://issuer.example.com",
                Oid4vciUriValidator.credentialIssuer("http://issuer.example.com/"));
        assertEquals("http://issuer.example.com/metadata",
                Oid4vciUriValidator.metadataUri("http://issuer.example.com/metadata"));
    }

    @Test
    void acceptsDidCredentialIssuer() {
        assertEquals("did:example:issuer-123",
                Oid4vciUriValidator.credentialIssuer("did:example:issuer-123"));
    }

    @Test
    void rejectsMissingIssuer() {
        assertError(ErrorCode.OID4VCI_ISSUER_INVALID_URI,
                () -> Oid4vciUriValidator.credentialIssuer(null));
    }

    @Test
    void rejectsUnsafeSchemes() {
        assertError(ErrorCode.OID4VCI_USER_INITIATION_URI_INVALID,
                () -> Oid4vciUriValidator.userInitiationUri("javascript:alert(1)"));
        assertError(ErrorCode.OID4VCI_METADATA_URI_INVALID,
                () -> Oid4vciUriValidator.metadataUri("file:///tmp/metadata"));
    }

    @Test
    void rejectsIssuerQueryAndFragment() {
        assertError(ErrorCode.OID4VCI_ISSUER_INVALID_URI,
                () -> Oid4vciUriValidator.credentialIssuer("https://issuer.example.com?tenant=1"));
        assertError(ErrorCode.OID4VCI_ISSUER_INVALID_URI,
                () -> Oid4vciUriValidator.credentialIssuer("https://issuer.example.com#fragment"));
    }

    @Test
    void generatesStandardMetadataUri() {
        assertEquals("https://issuer.example.com/.well-known/openid-credential-issuer",
                Oid4vciUriValidator.defaultMetadataUri("https://issuer.example.com"));
    }

    @Test
    void requiresExplicitMetadataUriForDidIssuer() {
        assertError(ErrorCode.OID4VCI_METADATA_URI_INVALID,
                () -> Oid4vciUriValidator.defaultMetadataUri("did:example:issuer-123"));
    }

    private void assertError(ErrorCode expected, Runnable action) {
        OpenDidException exception = assertThrows(OpenDidException.class, action::run);
        assertEquals(expected, exception.getErrorCode());
    }
}
