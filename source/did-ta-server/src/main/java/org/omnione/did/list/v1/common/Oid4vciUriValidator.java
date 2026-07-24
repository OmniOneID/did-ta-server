package org.omnione.did.list.v1.common;

import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public final class Oid4vciUriValidator {
    private static final Pattern DID_PATTERN =
            Pattern.compile("^did:[a-z0-9]+:[A-Za-z0-9._:%-]+(?::[A-Za-z0-9._:%-]+)*$");

    private Oid4vciUriValidator() {
    }

    public static String credentialIssuer(String value) {
        if (value != null && DID_PATTERN.matcher(value.trim()).matches()) {
            return value.trim();
        }
        URI uri = parseWebUri(value, ErrorCode.OID4VCI_ISSUER_INVALID_URI);
        if (uri.getRawQuery() != null || uri.getRawFragment() != null) {
            throw new OpenDidException(ErrorCode.OID4VCI_ISSUER_INVALID_URI);
        }
        String normalized = uri.toString();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    public static String metadataUri(String value) {
        return parseWebUri(value, ErrorCode.OID4VCI_METADATA_URI_INVALID).toString();
    }

    public static String userInitiationUri(String value) {
        return parseWebUri(value, ErrorCode.OID4VCI_USER_INITIATION_URI_INVALID).toString();
    }

    public static String defaultMetadataUri(String credentialIssuer) {
        if (credentialIssuer.startsWith("did:")) {
            throw new OpenDidException(ErrorCode.OID4VCI_METADATA_URI_INVALID);
        }
        return credentialIssuer + "/.well-known/openid-credential-issuer";
    }

    private static URI parseWebUri(String value, ErrorCode errorCode) {
        if (value == null || value.isBlank()) {
            throw new OpenDidException(errorCode);
        }
        try {
            URI uri = new URI(value.trim());
            boolean webScheme = "https".equalsIgnoreCase(uri.getScheme())
                    || "http".equalsIgnoreCase(uri.getScheme());
            if (!webScheme || uri.getHost() == null || uri.getUserInfo() != null) {
                throw new OpenDidException(errorCode);
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new OpenDidException(errorCode);
        }
    }
}
