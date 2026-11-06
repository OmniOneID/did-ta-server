
Open DID TAS Database Table Definition
==

- Date: 2026-11-06
- Version: v3.0.0

| Version | Date       | Changes         |
| ------- | ---------- | --------------- |
| v1.0.1 | 2024-03-31 | Initial version |
| v2.0.0  | 2025-05-29 | update Table    |
| v3.0.0  | 2026-11-06 | Added tables introduced in Liquibase sets 3–10 and aligned columns with the current schema |

Contents
--
- [Open DID TAS Database Table Definition](#open-did-tas-database-table-definition)
  - [Contents](#contents)
  - [1. Overview](#1-overview)
    - [1.1 ERD](#11-erd)
  - [2. Table Definition](#2-table-definition)
    - [2.1. TAS](#21-tas)
    - [2.2. ENTITY](#22-entity)
    - [2.3. USER](#23-user)
    - [2.4. WALLET](#24-wallet)
    - [2.5. APP](#25-app)
    - [2.6. CERTIFICATE\_VC](#26-certificate_vc)
    - [2.7. TRANSACTION](#27-transaction)
    - [2.8. SUB\_TRANSACTION](#28-sub_transaction)
    - [2.9. TOKEN](#29-token)
    - [2.10. ECDH](#210-ecdh)
    - [2.11. DID\_OFFER](#211-did_offer)
    - [2.12. VC\_SCHEMA](#212-vc_schema)
    - [2.13. KYC](#213-kyc)
    - [2.14. API](#214-api)
    - [2.15. NOTIFICATION\_SERVER](#215-notification_server)
    - [2.16. NOTIFICATION\_TEMPLATE](#216-notification_template)
    - [2.17. LIST\_ALLOWED\_CA](#217-list_allowed_ca)
    - [2.18. LIST\_VC\_SCHEMA](#218-list_vc_schema)
    - [2.19. LIST\_VC\_PLAN](#219-list_vc_plan)
    - [2.20. ADMIN](#220-admin)
    - [2.21. LIST\_CREDENTIAL\_SCHEMA](#221-list_credential_schema)
    - [2.22. LIST\_CREDENTIAL\_DEFINITION](#222-list_credential_definition)
    - [2.23. DID\_DOCUMENT](#223-did_document)
    - [2.24. SERVER\_CONFIG](#224-server_config)
    - [2.25. ADMIN\_PASSWORD\_POLICY](#225-admin_password_policy)
    - [2.26. API\_LOG](#226-api_log)
    - [2.27. LIST\_CERTIFICATE\_VC](#227-list_certificate_vc)
    - [2.28. LIST\_OID4VCI\_ISSUER](#228-list_oid4vci_issuer)
    - [2.29. LIST\_OID4VCI\_ISSUER\_STATUS\_HISTORY](#229-list_oid4vci_issuer_status_history)
    - [2.30. LIST\_MDOC\_ISSUER](#230-list_mdoc_issuer)
    - [2.31. LIST\_MDOC\_ISSUER\_DOC\_TYPE](#231-list_mdoc_issuer_doc_type)
    - [2.32. LIST\_MDOC\_ISSUER\_STATUS\_HISTORY](#232-list_mdoc_issuer_status_history)

## 1. Overview

This document defines the structure of the database tables used in the TAS server. It describes the field attributes, relationships, and data flow for each table, serving as essential reference material for system development and maintenance.

### 1.1 ERD

Access the [ERD](https://www.erdcloud.com/d/TZGGs3GPE6orphstF) site to view the diagram, which visually represents the relationships between the tables in the TAS server database, including key attributes, primary keys, and foreign key relationships.

## 2. Table Definition

### 2.1. TAS

This table stores information related to TAS.

| Key | Column Name     | Data Type | Length | Nullable | Default | Description              |
| --- | --------------- | --------- | ------ | -------- | ------- | ------------------------ |
| PK  | id              | BIGINT    |        | NO       | N/A     | id                       |
|     | did             | VARCHAR   | 200    | YES      | N/A     | tas did                  |
|     | name            | VARCHAR   | 200    | NO       | N/A     | tas name                 |
|     | status          | VARCHAR   | 50     | NO       | N/A     | tas status               |
|     | server_url      | VARCHAR   | 2000   | NO       | N/A     | tas server URL           |
|     | certificate_url | VARCHAR   | 2000   | YES      | N/A     | tas's certificate VC URL |
|     | created_at      | TIMESTAMP |        | NO       | now()   | created date             |
|     | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date             |
|     | dn                             | VARCHAR   | 200    | YES      | N/A     | distinguished name of the certificate VC subject |
|     | published_certificate_url      | VARCHAR   | 2000   | YES      | N/A     | URL of the published certificate VC |

### 2.2. ENTITY

This table stores information related to entities.

| Key | Column Name     | Data Type | Length | Nullable | Default | Description                 |
| --- | --------------- | --------- | ------ | -------- | ------- | --------------------------- |
| PK  | id              | BIGINT    |        | NO       | N/A     | entity id                   |
|     | did             | VARCHAR   | 200    | NO       | N/A     | entity did                  |
|     | name            | VARCHAR   | 200    | NO       | N/A     | entity name                 |
|     | role            | VARCHAR   | 30     | NO       | N/A     | entity role                 |
|     | status          | VARCHAR   | 50     | NO       | N/A     | entity status               |
|     | server_url      | VARCHAR   | 2000   | NO       | N/A     | entity server URL           |
|     | certificate_url | VARCHAR   | 2000   | YES      | N/A     | entity's certificate VC URL |
|     | created_at      | TIMESTAMP |        | NO       | now()   | created date                |
|     | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date                |
|     | dn              | VARCHAR   | 200    | YES      | N/A     | distinguished name of the certificate VC subject |
|     | published_certificate_url | VARCHAR   | 2000   | YES      | N/A     | URL of the published certificate VC |


### 2.3. USER

This table stores information related to users.

| Key | Column Name | Data Type | Length | Nullable | Default | Description  |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id           |
|     | did         | VARCHAR   | 200    | NO       | N/A     | user did     |
|     | status      | VARCHAR   | 50     | NO       | N/A     | user status  |
|     | pii         | VARCHAR   | 100    | NO       | N/A     | user pii     |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date |


### 2.4. WALLET

This table stores information related to wallets.

| Key | Column Name   | Data Type | Length | Nullable | Default | Description              |
| --- | ------------- | --------- | ------ | -------- | ------- | ------------------------ |
| PK  | id            | BIGINT    |        | NO       | N/A     | id                       |
|     | wallet_id     | VARCHAR   | 200    | NO       | N/A     | wallet ID                |
|     | did           | VARCHAR   | 200    | NO       | N/A     | wallet did               |
|     | status        | VARCHAR   | 50     | NO       | N/A     | wallet status            |
|     | registered_at | TIMESTAMP |        | NO       | N/A     | wallet registration date |
|     | cancelled_at  | TIMESTAMP |        | YES      | N/A     | wallet termination date  |
|     | created_at    | TIMESTAMP |        | NO       | now()   | created date             |
|     | updated_at    | TIMESTAMP |        | YES      | N/A     | updated date             |
|     | user_id       | BIGINT    |        | YES      | N/A     | user table key           |
|     | entity_id     | BIGINT    |        | NO       | N/A     | entity table key         |


### 2.5. APP

This table stores information related to apps.

| Key | Column Name | Data Type | Length | Nullable | Default | Description      |
| --- | ----------- | --------- | ------ | -------- | ------- | ---------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | entity table key |
|     | app_id      | VARCHAR   | 20     | NO       | N/A     | app ID           |
|     | push_token  | VARCHAR   | 255    | YES      | N/A     | push token       |
|     | status      | VARCHAR   | 50     | NO       | N/A     | app status       |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date     |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date     |
|     | user_id     | BIGINT    |        | NO       | N/A     | user table key   |


### 2.6. CERTIFICATE_VC

This table stores information related to certificate VCs.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                    |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------------------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                             |
|     | vc          | TEXT      |        | NO       | N/A     | certificate VC contents (json) |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                   |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                   |


### 2.7. TRANSACTION

This table stores information related to transactions.

| Key | Column Name    | Data Type | Length | Nullable | Default | Description                    |
| --- | -------------- | --------- | ------ | -------- | ------- | ------------------------------ |
| PK  | id             | BIGINT    |        | NO       | N/A     | id                             |
|     | tx_id          | VARCHAR   | 40     | NO       | N/A     | transaction id                 |
|     | type           | VARCHAR   | 50     | NO       | N/A     | transaction type               |
|     | status         | VARCHAR   | 50     | NO       | N/A     | transaction status             |
|     | did            | VARCHAR   | 200    | YES      | N/A     | transaction target did         |
|     | auth_nonce     | VARCHAR   | 100    | YES      | N/A     | DID Auth nonce                 |
|     | certificate_id | VARCHAR   | 50     | YES      | N/A     | certificate VC ID              |
|     | external_tx_id | VARCHAR   | 40     | YES      | N/A     | external server transaction id |
|     | external_did   | VARCHAR   | 200    | YES      | N/A     | external server did            |
|     | pii            | VARCHAR   | 100    | YES      | N/A     | user pii                       |
|     | expired_at     | TIMESTAMP |        | NO       | N/A     | expiration date                |
|     | created_at     | TIMESTAMP |        | NO       | now()   | created date                   |
|     | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date                   |


### 2.8. SUB_TRANSACTION

This table stores information related to sub-transactions.

| Key | Column Name    | Data Type | Length | Nullable | Default | Description            |
| --- | -------------- | --------- | ------ | -------- | ------- | ---------------------- |
| PK  | id             | BIGINT    |        | NO       | N/A     | id                     |
|     | step           | SMALLINT  |        | NO       | N/A     | sub transaction step   |
|     | type           | VARCHAR   | 50     | NO       | N/A     | sub transaction type   |
|     | status         | VARCHAR   | 50     | NO       | N/A     | sub transaction status |
|     | created_at     | TIMESTAMP |        | NO       | now()   | created date           |
|     | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date           |
|     | transaction_id | BIGINT    |        | NO       | N/A     | transaction table key  |


### 2.9. TOKEN

This table stores information related to tokens.

| Key | Column Name    | Data Type | Length | Nullable | Default | Description                      |
| --- | -------------- | --------- | ------ | -------- | ------- | -------------------------------- |
| PK  | id             | BIGINT    |        | NO       | N/A     | id                               |
|     | purpose        | VARCHAR   | 50     | NO       | N/A     | token purpose                    |
|     | token          | VARCHAR   | 200    | NO       | N/A     | token                            |
|     | app_id         | VARCHAR   | 20     | NO       | N/A     | app id                           |
|     | wallet_id      | VARCHAR   | 200    | NO       | N/A     | wallet id                        |
|     | expired_at     | TIMESTAMP |        | NO       | N/A     | expiration date                  |
|     | created_at     | TIMESTAMP |        | NO       | now()   | created date                     |
|     | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date                     |
|     | transaction_id | BIGINT    |        | NO       | N/A     | transaction management table key |


### 2.10. ECDH

This table stores information related to ECDH transactions.

| Key | Column Name    | Data Type | Length | Nullable | Default | Description                      |
| --- | -------------- | --------- | ------ | -------- | ------- | -------------------------------- |
| PK  | id             | BIGINT    |        | NO       | N/A     | id                               |
|     | client_did     | VARCHAR   | 200    | NO       | N/A     | client did                       |
|     | session_key    | VARCHAR   | 100    | NO       | N/A     | session key                      |
|     | nonce          | VARCHAR   | 100    | NO       | N/A     | nonce                            |
|     | cipher         | VARCHAR   | 20     | NO       | N/A     | cipher type                      |
|     | padding        | VARCHAR   | 20     | NO       | N/A     | padding type                     |
|     | created_at     | TIMESTAMP |        | NO       | now()   | created date                     |
|     | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date                     |
|     | transaction_id | BIGINT    |        | NO       | N/A     | transaction management table key |


### 2.11. DID_OFFER

This table stores information related to DID offers.

| Key | Column Name    | Data Type | Length | Nullable | Default | Description                      |
| --- | -------------- | --------- | ------ | -------- | ------- | -------------------------------- |
| PK  | id             | BIGINT    |        | NO       | N/A     | id                               |
|     | offer_id       | VARCHAR   | 40     | NO       | N/A     | did offer id                     |
|     | type           | VARCHAR   | 50     | NO       | N/A     | did offer type                   |
|     | did            | VARCHAR   | 200    | NO       | N/A     | user did                         |
|     | valid_until    | TIMESTAMP |        | YES      | N/A     | did offer expiration date        |
|     | created_at     | TIMESTAMP |        | NO       | now()   | created date                     |
|     | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date                     |
|     | transaction_id | BIGINT    |        | YES      | N/A     | transaction management table key |

### 2.12. VC_SCHEMA

This table stores information related to VC schema definitions.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                      |
| --- | ----------- | --------- | ------ | -------- | ------- | -------------------------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                               |
|     | type        | VARCHAR   | 50     | NO       | N/A     | schema type                      |
|     | schema_id   | VARCHAR   | 200    | NO       | N/A     | unique schema identifier         |
|     | version     | VARCHAR   | 10     | NO       | N/A     | schema version                   |
|     | schema      | TEXT      |        | NO       | N/A     | VC schema content in JSON format |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                     |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                     |


### 2.13. KYC

This table stores information related to KYC (Know Your Customer) services.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                                    |
| --- | ----------- | --------- | ------ | -------- | ------- | ---------------------------------------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                             |
|     | name        | VARCHAR   | 200    | NO       | N/A     | KYC service name                               |
|     | server_url  | VARCHAR   | 2000   | YES      | N/A     | KYC server URL                                 |
|     | enabled     | BOOLEAN   |        | NO       | N/A     | flag indicating whether the service is enabled |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                                   |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                                   |
|     | verification_type              | VARCHAR   | 50     | NO       | TRANSACTION | KYC verification type (TRANSACTION, TOKEN) |
|     | signer_did                     | VARCHAR   | 200    | YES      | N/A     | DID of the KYC token signer |

### 2.14. API

This table stores information related to API configurations.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                          |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------------------------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                   |
|     | type        | VARCHAR   | 50     | NO       | N/A     | type of API                          |
|     | config      | TEXT      |        | NO       | N/A     | configuration details in JSON format |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                         |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                         |

### 2.15. NOTIFICATION_SERVER

This table stores configuration information for notification servers.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                          |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------------------------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                   |
|     | server_type | VARCHAR   | 50     | NO       | N/A     | type of server                       |
|     | config      | TEXT      |        | NO       | N/A     | configuration details in JSON format |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                         |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                         |

### 2.16. NOTIFICATION_TEMPLATE

This table stores notification message templates based on server and template types.

| Key | Column Name   | Data Type | Length | Nullable | Default | Description                                           |
| --- | ------------- | --------- | ------ | -------- | ------- | ----------------------------------------------------- |
| PK  | id            | BIGINT    |        | NO       | N/A     | id                                                    |
|     | server_type   | VARCHAR   | 50     | NO       | N/A     | type of server                                        |
|     | template_type | VARCHAR   | 50     | NO       | N/A     | type of template                                      |
|     | template      | TEXT      |        | NO       | N/A     | template content (can include variables/placeholders) |
|     | created_at    | TIMESTAMP |        | NO       | now()   | created date                                          |
|     | updated_at    | TIMESTAMP |        | YES      | N/A     | updated date                                          |

### 2.17. LIST_ALLOWED_CA

This table stores a list of CA allowed for a specific wallet.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                      |
| --- | ----------- | --------- | ------ | -------- | ------- | -------------------------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                               |
|     | wallet_id   | VARCHAR   | 200    | NO       | N/A     | wallet identifier                |
|     | ca_list     | TEXT      |        | NO       | N/A     | list of allowed CA (JSON format) |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                     |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                     |

### 2.18. LIST_VC_SCHEMA

This table stores VC schema information published by issuers, including metadata like title and description.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                            |
| --- | ----------- | --------- | ------ | -------- | ------- | -------------------------------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                     |
|     | schema_id   | VARCHAR   | 200    | NO       | N/A     | schema identifier                      |
|     | issuer_did  | VARCHAR   | 200    | NO       | N/A     | DID of the issuer                      |
|     | issuer_name | VARCHAR   | 200    | NO       | N/A     | name of the issuer                     |
|     | title       | VARCHAR   | 50     | NO       | N/A     | title of the VC schema                 |
|     | description | VARCHAR   | 200    | NO       | N/A     | description of the VC schema           |
|     | schema      | TEXT      |        | NO       | N/A     | actual VC schema content (JSON format) |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                           |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                           |

### 2.19. LIST_VC_PLAN

This table stores VC issuance plans published by issuers, including plan metadata and details.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                       |
| --- | ----------- | --------- | ------ | -------- | ------- | --------------------------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                |
|     | vc_plan_id  | VARCHAR   | 50     | NO       | N/A     | unique ID of the VC issuance plan |
|     | name        | VARCHAR   | 200    | NO       | N/A     | name of the VC plan               |
|     | description | VARCHAR   | 200    | NO       | N/A     | description of the VC plan        |
|     | issuer_did  | VARCHAR   | 200    | NO       | N/A     | DID of the issuer                 |
|     | issuer_name | VARCHAR   | 200    | NO       | N/A     | name of the issuer                |
|     | vc_plan     | TEXT      |        | NO       | N/A     | VC plan content (JSON format)     |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                      |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                      |
|     | initiate                       | VARCHAR   | 20     | NO       | issuer_init | issuance initiation type (user_init, issuer_init) |

### 2.20. ADMIN

This table stores information about administrators who manage the system.

| Key | Column Name            | Data Type | Length | Nullable | Default | Description                                      |
| --- | ---------------------- | --------- | ------ | -------- | ------- | ------------------------------------------------ |
| PK  | id                     | BIGINT    |        | NO       | N/A     | id                                               |
|     | login_id               | VARCHAR   | 50     | NO       | N/A     | administrator login ID                           |
|     | login_password         | VARCHAR   | 64     | NO       | N/A     | hashed login password                            |
|     | name                   | VARCHAR   | 200    | YES      | N/A     | admin's display name                             |
|     | email_verified         | BOOLEAN   |        | NO       | false   | whether the email has been verified              |
|     | require_password_reset | BOOLEAN   |        | NO       | true    | whether password reset is required at next login |
|     | role                   | VARCHAR   | 50     | NO       | N/A     | admin role                                       |
|     | created_by             | VARCHAR   | 50     | NO       | N/A     | who created this admin account                   |
|     | created_at             | TIMESTAMP |        | NO       | now()   | created date                                     |
|     | updated_at             | TIMESTAMP |        | YES      | N/A     | updated date                                     |
|     | last_password_changed_at       | TIMESTAMP |        | YES      | N/A     | last password change date |
|     | password_reset_reason          | VARCHAR   | 20     | YES      | N/A     | reason a password change is required |

### 2.21. LIST_CREDENTIAL_SCHEMA

This table stores Credential Schemas published by issuers.

| Key | Column Name          | Data Type | Length | Nullable | Default | Description                             |
| --- | -------------------- | --------- | ------ | -------- | ------- | --------------------------------------- |
| PK  | id                   | BIGINT    |        | NO       | N/A     | id                                      |
|     | credential_schema_id | VARCHAR   | 200    | NO       | N/A     | credential schema identifier            |
|     | issuer_did           | VARCHAR   | 200    | NO       | N/A     | issuer DID                              |
|     | issuer_name          | VARCHAR   | 200    | NO       | N/A     | issuer name                             |
|     | name                 | VARCHAR   | 50     | NO       | N/A     | name of the credential schema           |
|     | credential_schema     | TEXT      |        | NO       | N/A     | credential schema content (JSON format) |
|     | created_at           | TIMESTAMP |        | NO       | now()   | created date                            |
|     | updated_at           | TIMESTAMP |        | YES      | N/A     | updated date                            |


### 2.22. LIST_CREDENTIAL_DEFINITION

This table stores Credential Definition metadata published by issuers.

| Key | Column Name               | Data Type | Length | Nullable | Default | Description                                 |
| --- | ------------------------- | --------- | ------ | -------- | ------- | ------------------------------------------- |
| PK  | id                        | BIGINT    |        | NO       | N/A     | id                                          |
|     | credential_definition_id  | VARCHAR   | 200    | NO       | N/A     | credential definition identifier            |
|     | credential_definition_tag | VARCHAR   | 100    | NO       | N/A     | credential definition tag                   |
|     | credential_schema_id      | VARCHAR   | 200    | NO       | N/A     | related credential schema identifier        |
|     | issuer_did                | VARCHAR   | 200    | NO       | N/A     | issuer DID                                  |
|     | issuer_name               | VARCHAR   | 200    | NO       | N/A     | issuer name                                 |
|     | credential_definition      | TEXT      |        | NO       | N/A     | credential definition content (JSON format) |
|     | created_at                | TIMESTAMP |        | NO       | now()   | created date                                |
|     | updated_at                | TIMESTAMP |        | YES      | N/A     | updated date                                |


### 2.23. DID_DOCUMENT

This table stores DID Documents associated with entities.

| Key | Column Name  | Data Type | Length | Nullable | Default | Description                 |
| --- | ------------ | --------- | ------ | -------- | ------- | --------------------------- |
| PK  | id           | BIGINT    |        | NO       | N/A     | id                          |
|     | did_document | TEXT      |        | NO       | N/A     | DID Document content (JSON) |
|     | created_at    | TIMESTAMP |        | NO       | now()   | created date                |
|     | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date                |
|     | entity_id    | BIGINT    |        | NO       | N/A     | related entity table key    |

### 2.24. SERVER_CONFIG

This table stores external server integration settings as key-value pairs.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
|     | config_key                     | VARCHAR   | 100    | NO       | N/A     | configuration key |
|     | config_value                   | TEXT      |        | NO       | N/A     | configuration value |
|     | description                    | VARCHAR   | 200    | YES      | N/A     | description of the configuration |
|     | created_at                     | TIMESTAMP |        | NO       | now()   | created date |
|     | updated_at                     | TIMESTAMP |        | YES      | N/A     | updated date |

### 2.25. ADMIN_PASSWORD_POLICY

This table stores the password policy for administrator accounts.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
|     | min_length                     | SMALLINT  |        | NO       | 8       | minimum password length |
|     | require_uppercase              | BOOLEAN   |        | NO       | false   | whether an uppercase letter is required |
|     | require_number                 | BOOLEAN   |        | NO       | false   | whether a number is required |
|     | require_special                | BOOLEAN   |        | NO       | false   | whether a special character is required |
|     | password_expiry_days           | SMALLINT  |        | NO       | 365     | password expiration period in days |
|     | created_at                     | TIMESTAMP |        | NO       | now()   | created date |
|     | updated_at                     | TIMESTAMP |        | YES      | N/A     | updated date |

### 2.26. API_LOG

This table stores API call logs, which are viewed in API Log and Audit API Log in the admin console.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
|     | method                         | VARCHAR   | 10     | NO       | N/A     | HTTP method |
|     | uri                            | VARCHAR   | 1048   | NO       | N/A     | request URI |
|     | status                         | INTEGER   |        | NO       | N/A     | HTTP status code |
|     | result                         | VARCHAR   | 20     | NO       | N/A     | processing result |
|     | api_name                       | VARCHAR   | 255    | YES      | N/A     | API name |
|     | api_description                | VARCHAR   | 1000   | YES      | N/A     | API description |
|     | requester_id                   | VARCHAR   | 128    | YES      | N/A     | requester identifier |
|     | duration_ms                    | BIGINT    |        | NO       | N/A     | processing time in milliseconds |
|     | client_ip                      | VARCHAR   | 64     | YES      | N/A     | client IP address |
|     | error_code                     | VARCHAR   | 64     | YES      | N/A     | error code |
|     | message                        | TEXT      |        | YES      | N/A     | error or result message |
|     | api_type                       | VARCHAR   | 20     | YES      | N/A     | API type |
|     | created_at                     | TIMESTAMP |        | NO       | N/A     | created date |
|     | updated_at                     | TIMESTAMP |        | YES      | N/A     | updated date |
|     | target_type                    | VARCHAR   | 50     | YES      | N/A     | type of the target |
|     | target_id                      | VARCHAR   | 128    | YES      | N/A     | identifier of the target |
|     | user_agent                     | TEXT      |        | YES      | N/A     | User-Agent header value |
|     | action_type                    | VARCHAR   | 20     | NO       | N/A     | action type |

### 2.27. LIST_CERTIFICATE_VC

This table stores the Entity certificate VCs served through the List Provider API.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
|     | did                            | VARCHAR   | 200    | NO       | N/A     | DID of the Entity |
|     | name                           | VARCHAR   | 200    | NO       | N/A     | name of the Entity |
|     | certificate_vc                 | TEXT      |        | NO       | N/A     | certificate VC (JSON format) |
|     | published_url                  | VARCHAR   | 2000   | NO       | N/A     | URL of the published certificate VC |
|     | published_at                   | TIMESTAMP |        | NO       | N/A     | published date |
|     | expired_at                     | TIMESTAMP |        | NO       | N/A     | expiration date |
|     | created_at                     | TIMESTAMP |        | NO       | now()   | created date |
|     | updated_at                     | TIMESTAMP |        | YES      | N/A     | updated date |

### 2.28. LIST_OID4VCI_ISSUER

This table stores the list of OID4VCI credential issuers.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
|     | credential_issuer              | VARCHAR   | 2000   | NO       | N/A     | credential issuer identifier |
|     | credential_issuer_metadata_uri | VARCHAR   | 2000   | NO       | N/A     | credential issuer metadata URI |
|     | user_initiation_uri            | VARCHAR   | 2000   | NO       | N/A     | URI where a user starts issuance |
|     | status                         | VARCHAR   | 20     | NO       | N/A     | issuer status |
|     | created_at                     | TIMESTAMP |        | NO       | now()   | created date |
|     | updated_at                     | TIMESTAMP |        | YES      | N/A     | updated date |

### 2.29. LIST_OID4VCI_ISSUER_STATUS_HISTORY

This table stores the status change history of OID4VCI credential issuers.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
| FK  | issuer_id                      | BIGINT    |        | NO       | N/A     | list_oid4vci_issuer id |
|     | previous_status                | VARCHAR   | 20     | YES      | N/A     | status before the change |
|     | new_status                     | VARCHAR   | 20     | NO       | N/A     | status after the change |
|     | reason                         | VARCHAR   | 1000   | YES      | N/A     | reason for the change |
|     | changed_by                     | VARCHAR   | 200    | YES      | N/A     | who made the change |
|     | changed_at                     | TIMESTAMP |        | NO       | now()   | changed date |

### 2.30. LIST_MDOC_ISSUER

This table stores mDoc trusted issuers. `entity_id` refers to an Entity logically, without a foreign key.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
|     | entity_id                      | BIGINT    |        | NO       | N/A     | entity id |
|     | status                         | VARCHAR   | 20     | NO       | N/A     | issuer status (ACTIVE, SUSPENDED, REVOKED) |
|     | created_at                     | TIMESTAMP |        | NO       | now()   | created date |
|     | updated_at                     | TIMESTAMP |        | YES      | N/A     | updated date |
|     | metadata_uri                   | VARCHAR   | 2000   | YES      | N/A     | issuer OID4VCI metadata URL |

### 2.31. LIST_MDOC_ISSUER_DOC_TYPE

This table stores the docTypes allowed for each mDoc trusted issuer.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
| FK  | mdoc_issuer_id                 | BIGINT    |        | NO       | N/A     | list_mdoc_issuer id |
|     | doc_type                       | VARCHAR   | 200    | NO       | N/A     | allowed docType |
|     | created_at                     | TIMESTAMP |        | NO       | now()   | created date |

### 2.32. LIST_MDOC_ISSUER_STATUS_HISTORY

This table stores the status change history of mDoc trusted issuers.

| Key | Column Name                    | Data Type | Length | Nullable | Default | Description |
| --- | ------------------------------ | --------- | ------ | -------- | ------- | ----------- |
| PK  | id                             | BIGINT    |        | NO       | N/A     | id |
| FK  | mdoc_issuer_id                 | BIGINT    |        | NO       | N/A     | list_mdoc_issuer id |
|     | previous_status                | VARCHAR   | 20     | YES      | N/A     | status before the change |
|     | new_status                     | VARCHAR   | 20     | NO       | N/A     | status after the change |
|     | reason                         | VARCHAR   | 1000   | YES      | N/A     | reason for the change |
|     | changed_by                     | VARCHAR   | 200    | YES      | N/A     | who made the change |
|     | changed_at                     | TIMESTAMP |        | NO       | now()   | changed date |
