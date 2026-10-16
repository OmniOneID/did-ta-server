package org.omnione.did.list.v1.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.response.ErrorResponse;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * Reads the mso_mdoc docTypes an issuer declares in its OID4VCI metadata
 * ({@code credential_configurations_supported.*.doctype}).
 * Uses explicit timeouts so an unreachable issuer cannot hang the admin request.
 */
@Slf4j
@Component
public class MdocIssuerMetadataClient {
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(10);
    private static final String MDOC_FORMAT = "mso_mdoc";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public MdocIssuerMetadataClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(CONNECT_TIMEOUT)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public record IssuerMetadata(String metadataUri, String credentialIssuer, List<String> docTypes) {
    }

    public IssuerMetadata fetch(String metadataUri) {
        URI uri = validUri(metadataUri);
        JsonNode metadata;
        try {
            HttpResponse<String> response = httpClient.send(
                    HttpRequest.newBuilder(uri).timeout(REQUEST_TIMEOUT).header("Accept", "application/json").GET().build(),
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                log.warn("Issuer metadata request failed: uri={} status={}", uri, response.statusCode());
                throw new OpenDidException(ErrorCode.MDOC_ISSUER_METADATA_UNAVAILABLE);
            }
            metadata = objectMapper.readTree(response.body());
        } catch (OpenDidException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_METADATA_UNAVAILABLE);
        } catch (Exception e) {
            log.warn("Issuer metadata could not be read: uri={} error={}", uri, e.toString());
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_METADATA_UNAVAILABLE);
        }

        List<String> docTypes = mdocDocTypes(metadata);
        if (docTypes.isEmpty()) {
            // Name the formats that were found so the admin can tell a wrong URL from an issuer without mDoc.
            ErrorCode code = ErrorCode.MDOC_ISSUER_METADATA_NO_MDOC;
            Set<String> found = formats(metadata);
            throw new OpenDidException(new ErrorResponse(code.getCode(), code.getMessage()
                    + (found.isEmpty() ? " No credential_configurations_supported found." : " Found formats: " + String.join(", ", found) + ".")));
        }
        String credentialIssuer = metadata.path("credential_issuer").asText(null);
        return new IssuerMetadata(uri.toString(), credentialIssuer, docTypes);
    }

    /** mso_mdoc doctypes in declaration order, without duplicates. Accepts the map form and the older array form. */
    public static List<String> mdocDocTypes(JsonNode metadata) {
        Set<String> docTypes = new LinkedHashSet<>();
        for (JsonNode configuration : configurations(metadata)) {
            if (!isMdocFormat(configuration.path("format").asText(""))) {
                continue;
            }
            String docType = configuration.path("doctype").asText("").trim();
            if (!docType.isEmpty()) {
                docTypes.add(docType);
            }
        }
        return new ArrayList<>(docTypes);
    }

    /**
     * mso_mdoc and its variants: "mso-mdoc" (older issuer metadata) and DID-based profiles such as "mso_mdoc-did".
     * Hyphens and underscores are treated alike.
     */
    static boolean isMdocFormat(String format) {
        String normalized = format.trim().toLowerCase(Locale.ROOT).replace('-', '_');
        return normalized.equals(MDOC_FORMAT) || normalized.startsWith(MDOC_FORMAT + "_");
    }

    private static Set<String> formats(JsonNode metadata) {
        Set<String> formats = new LinkedHashSet<>();
        for (JsonNode configuration : configurations(metadata)) {
            String format = configuration.path("format").asText("").trim();
            if (!format.isEmpty()) {
                formats.add(format);
            }
        }
        return formats;
    }

    private static List<JsonNode> configurations(JsonNode metadata) {
        List<JsonNode> configurations = new ArrayList<>();
        if (metadata != null) {
            metadata.path("credential_configurations_supported").elements().forEachRemaining(configurations::add);
        }
        return configurations;
    }

    static URI validUri(String value) {
        if (value == null || value.isBlank()) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID);
        }
        try {
            URI uri = new URI(value.trim());
            boolean webScheme = "https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme());
            if (!webScheme || uri.getHost() == null || uri.getUserInfo() != null) {
                throw new OpenDidException(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID);
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID);
        }
    }
}
