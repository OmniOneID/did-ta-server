/*
 * Copyright 2025 OmniOne.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.omnione.did.base.db.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "list_credential_definition")
public class ListCredentialDefinition extends BaseEntity implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credential_definition_id", nullable = false, length = 200)
    private String credentialDefinitionId;

    @Column(name = "credential_definition_tag", nullable = false, length = 100)
    private String credentialDefinitionTag;

    @Column(name = "credential_schema_id", nullable = false, length = 200)
    private String credentialSchemaId;

    @Column(name = "issuer_did", nullable = false, length = 200)
    private String issuerDid;

    @Column(name = "issuer_name", nullable = false, length = 200)
    private String issuerName;

    @Column(name = "credentialDefinition", nullable = false)
    private String credentialDefinition;
}
