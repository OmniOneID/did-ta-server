package org.omnione.did.tas.v1.agent.helper;

/**
 * Temporary class for providing hardcoded VC schema JSON.
 *
 * ⚠️ NOTE: This is a temporary solution for managing the certificate VC schema.
 *         It will be replaced with a database-managed implementation by June 2025.
 */
public class CertificateVcSchemaProvider {

    public static String getSchema(String serverUrl) {
        return """
            {
                "@id": "%s/api/v1/vc-schema?name=certificate",
                "@schema": "https://opendid.org/schema/vc.osd",
                "title": "OpenDID Certificate Verifiable Credential",
                "description": "VC-formatted OpenDID enrollment certificate.",
                "metadata": {
                    "language": "ko",
                    "formatVersion": "1.0"
                },
                "schema": {
                    "type": "object",
                    "properties": {
                        "credentialSubject": {
                            "type": "object",
                            "properties": {
                                "claims": {
                                    "type": "array",
                                    "items": {
                                        "type": "object"
                                    }
                                }
                            }
                        }
                    }
                },
                "credentialSubject": {
                    "claims": [{
                        "namespace": {
                            "id": "org.opendid.v1",
                            "name": "OpenDID - Certificate Verifiable Credential",
                            "ref": " "
                        },
                        "items": [
                            {"id": "subject", "caption": "subject", "type": "text", "format": "plain"},
                            {"id": "role", "caption": "role", "type": "text", "format": "plain"}
                        ]
                    }]
                }
            }
            """.formatted(serverUrl);
    }
}
