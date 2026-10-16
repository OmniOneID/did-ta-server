package org.omnione.did.list.mdoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.common.MdocIssuerMetadataClient;
import org.omnione.did.list.v1.common.MdocIssuerMetadataClient.IssuerMetadata;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MdocIssuerMetadataClientTest {
    private static final String MDL = "org.iso.18013.5.1.mDL";
    private static final String PID = "eu.europa.ec.eudi.pid.1";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MdocIssuerMetadataClient client = new MdocIssuerMetadataClient(objectMapper);
    private HttpServer server;
    private int status;
    private String body;

    @BeforeEach
    void startServer() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/.well-known/openid-credential-issuer", exchange -> {
            byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private String metadataUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/.well-known/openid-credential-issuer";
    }

    @Test
    void readsOnlyMdocDocTypesInBothFormatSpellings() throws Exception {
        String json = """
                {"credential_issuer":"https://issuer.example.com",
                 "credential_configurations_supported":{
                   "degree":{"format":"jwt_vc","doctype":"UniversityDegree"},
                   "mdl":{"format":"mso_mdoc","doctype":"%s"},
                   "pid":{"format":"mso-mdoc","doctype":"%s"},
                   "mdlAgain":{"format":"mso_mdoc","doctype":"%s"}}}""".formatted(MDL, PID, MDL);

        assertEquals(List.of(MDL, PID), MdocIssuerMetadataClient.mdocDocTypes(objectMapper.readTree(json)));
    }

    @Test
    void readsDidBasedMdocFormatFromDevIssuerMetadata() throws Exception {
        // Shape of the dev issuer metadata (http://192.168.3.110:8091/.well-known/openid-credential-issuer), trimmed.
        String json = """
                {"credential_issuer":"http://192.168.3.110:8091",
                 "credential_configurations_supported":{
                   "mDocPID":{"doctype":"%s","format":"mso_mdoc-did","scope":"eu.europa.ec.eudi.pid_mdoc"},
                   "VerifiableIdSD":{"format":"dc+sd-jwt-did","vct":"org.omnione.identity_card.1"}}}""".formatted(PID);

        assertEquals(List.of(PID), MdocIssuerMetadataClient.mdocDocTypes(objectMapper.readTree(json)));
    }

    @Test
    void acceptsOlderArrayForm() throws Exception {
        String json = """
                {"credential_configurations_supported":[{"format":"mso_mdoc","doctype":"%s"}]}""".formatted(MDL);

        assertEquals(List.of(MDL), MdocIssuerMetadataClient.mdocDocTypes(objectMapper.readTree(json)));
    }

    @Test
    void fetchesDeclaredDocTypesAndCredentialIssuer() {
        status = 200;
        body = """
                {"credential_issuer":"http://issuer.local:8082",
                 "credential_configurations_supported":{"mDoc":{"format":"mso-mdoc","doctype":"%s"}}}""".formatted(MDL);

        IssuerMetadata metadata = client.fetch(metadataUrl());

        assertEquals(metadataUrl(), metadata.metadataUri());
        assertEquals("http://issuer.local:8082", metadata.credentialIssuer());
        assertEquals(List.of(MDL), metadata.docTypes());
    }

    @Test
    void rejectsMetadataWithoutMdoc() {
        status = 200;
        body = """
                {"credential_configurations_supported":{
                   "degree":{"format":"jwt_vc","doctype":"UniversityDegree"},
                   "id":{"format":"dc+sd-jwt-did"}}}""";

        OpenDidException exception = assertThrows(OpenDidException.class, () -> client.fetch(metadataUrl()));
        assertEquals(ErrorCode.MDOC_ISSUER_METADATA_NO_MDOC.getCode(), exception.getErrorResponse().getCode());
        assertTrue(exception.getErrorResponse().getDescription().contains("Found formats: jwt_vc, dc+sd-jwt-did."));
    }

    @Test
    void reportsUnavailableOnErrorStatusOrInvalidJson() {
        status = 500;
        body = "{}";
        assertError(ErrorCode.MDOC_ISSUER_METADATA_UNAVAILABLE, metadataUrl());

        status = 200;
        body = "<html>not json</html>";
        assertError(ErrorCode.MDOC_ISSUER_METADATA_UNAVAILABLE, metadataUrl());
    }

    @Test
    void reportsUnavailableWhenServerIsDown() {
        String url = metadataUrl();
        server.stop(0);

        assertError(ErrorCode.MDOC_ISSUER_METADATA_UNAVAILABLE, url);
    }

    @Test
    void rejectsInvalidUrlsBeforeAnyRequest() {
        assertError(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID, null);
        assertError(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID, " ");
        assertError(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID, "ftp://issuer.example.com/metadata");
        assertError(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID, "http://user@issuer.example.com/metadata");
        assertError(ErrorCode.MDOC_ISSUER_METADATA_URI_INVALID, "not a url");
    }

    private void assertError(ErrorCode expected, String url) {
        OpenDidException exception = assertThrows(OpenDidException.class, () -> client.fetch(url));
        assertEquals(expected, exception.getErrorCode());
    }
}
