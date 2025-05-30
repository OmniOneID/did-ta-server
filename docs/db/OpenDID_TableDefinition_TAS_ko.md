Open DID TAS Database Table Definition
==

- Date: 2025-05-29
- Version: v2.0.0

| Version | Date       | Changes         |
| ------- | ---------- | --------------- |
| v1.0.1 | 2024-03-31 | Initial version |
| v2.0.0  | 2025-05-29 | update Table    |

목차
--
- [Open DID TAS Database Table Definition](#open-did-tas-database-table-definition)
  - [목차](#목차)
  - [1. 개요](#1-개요)
    - [1.1 ERD](#11-erd)
  - [2. 테이블 정의](#2-테이블-정의)
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

## 1. 개요

이 문서는 TAS 서버에서 사용되는 데이터베이스 테이블의 구조를 정의합니다. 각 테이블의 필드 속성, 관계, 데이터 흐름을 설명하며, 시스템 개발 및 유지보수를 위한 필수 참조 자료로 활용됩니다.

### 1.1 ERD

[ERD](https://www.erdcloud.com/d/TZGGs3GPE6orphstF) 사이트에 접속하여 다이어그램을 확인하세요. 이 다이어그램은 TAS 서버 데이터베이스의 테이블 간 관계를 시각적으로 나타내며, 주요 속성, 기본 키, 외래 키 관계를 포함합니다.

## 2. 테이블 정의

### 2.1. TAS

이 테이블은 TAS와 관련된 정보를 저장합니다.

| Key | Column Name     | Data Type | Length | Nullable | Default | Description              |
| --- | --------------- | --------- | ------ | -------- | ------- | ------------------------ |
| PK  | id              | BIGINT    |        | NO       | N/A     | id                       |
|     | did             | VARCHAR   | 200    | NO       | N/A     | tas did                  |
|     | name            | VARCHAR   | 200    | NO       | N/A     | tas name                 |
|     | status          | VARCHAR   | 50     | NO       | N/A     | tas status               |
|     | server_url      | VARCHAR   | 2000   | NO       | N/A     | tas server URL           |
|     | certificate_url | VARCHAR   | 2000   | YES      | N/A     | tas's certificate VC URL |
|     | created_at      | TIMESTAMP |        | NO       | now()   | created date             |
|     | updated_at      | TIMESTAMP |        | YES      | N/A     | updated date             |

### 2.2. ENTITY

이 테이블은 엔티티와 관련된 정보를 저장합니다.

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

### 2.3. USER

이 테이블은 사용자와 관련된 정보를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description  |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id           |
|     | did         | VARCHAR   | 200    | NO       | N/A     | user did     |
|     | status      | VARCHAR   | 50     | NO       | N/A     | user status  |
|     | pii         | VARCHAR   | 100    | NO       | N/A     | user pii     |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date |

### 2.4. WALLET

이 테이블은 지갑과 관련된 정보를 저장합니다.

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

이 테이블은 앱과 관련된 정보를 저장합니다.

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

이 테이블은 인증서 VC와 관련된 정보를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                    |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------------------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                             |
|     | vc          | TEXT      |        | NO       | N/A     | certificate VC contents (json) |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                   |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                   |

### 2.7. TRANSACTION

이 테이블은 트랜잭션과 관련된 정보를 저장합니다.

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

이 테이블은 서브 트랜잭션과 관련된 정보를 저장합니다.

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

이 테이블은 토큰과 관련된 정보를 저장합니다.

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

이 테이블은 ECDH 트랜잭션과 관련된 정보를 저장합니다.

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

이 테이블은 DID 제안과 관련된 정보를 저장합니다.

| Key | Column Name    | Data Type | Length | Nullable | Default | Description                      |
| --- | -------------- | --------- | ------ | -------- | ------- | -------------------------------- |
| PK  | id             | BIGINT    |        | NO       | N/A     | id                               |
|     | offier_id      | VARCHAR   | 40     | NO       | N/A     | did offer id                     |
|     | type           | VARCHAR   | 50     | NO       | N/A     | did offer type                   |
|     | did            | VARCHAR   | 200    | NO       | N/A     | user did                         |
|     | valid_until    | TIMESTAMP |        | YES      | N/A     | did offer expiration date        |
|     | created_at     | TIMESTAMP |        | NO       | now()   | created date                     |
|     | updated_at     | TIMESTAMP |        | YES      | N/A     | updated date                     |
|     | transaction_id | BIGINT    |        | YES      | N/A     | transaction management table key |

### 2.12. VC_SCHEMA

이 테이블은 VC 스키마 정의와 관련된 정보를 저장합니다.

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

이 테이블은 KYC(Know Your Customer) 서비스와 관련된 정보를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                                    |
| --- | ----------- | --------- | ------ | -------- | ------- | ---------------------------------------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                             |
|     | name        | VARCHAR   | 200    | NO       | N/A     | KYC service name                               |
|     | server_url  | VARCHAR   | 2000   | NO       | N/A     | KYC server URL                                 |
|     | enabled     | BOOLEAN   |        | NO       | N/A     | flag indicating whether the service is enabled |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                                   |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                                   |

### 2.14. API

이 테이블은 API 구성과 관련된 정보를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                          |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------------------------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                   |
|     | type        | VARCHAR   | 50     | NO       | N/A     | type of API                          |
|     | config      | TEXT      |        | NO       | N/A     | configuration details in JSON format |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                         |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                         |

### 2.15. NOTIFICATION_SERVER

이 테이블은 알림 서버의 구성 정보를 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                          |
| --- | ----------- | --------- | ------ | -------- | ------- | ------------------------------------ |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                                   |
|     | server_type | VARCHAR   | 50     | NO       | N/A     | type of server                       |
|     | config      | TEXT      |        | NO       | N/A     | configuration details in JSON format |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                         |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                         |

### 2.16. NOTIFICATION_TEMPLATE

이 테이블은 서버 및 템플릿 유형에 따른 알림 메시지 템플릿을 저장합니다.

| Key | Column Name   | Data Type | Length | Nullable | Default | Description                                           |
| --- | ------------- | --------- | ------ | -------- | ------- | ----------------------------------------------------- |
| PK  | id            | BIGINT    |        | NO       | N/A     | id                                                    |
|     | server_type   | VARCHAR   | 50     | NO       | N/A     | type of server                                        |
|     | template_type | VARCHAR   | 50     | NO       | N/A     | type of template                                      |
|     | template      | TEXT      |        | NO       | N/A     | template content (can include variables/placeholders) |
|     | created_at    | TIMESTAMP |        | NO       | now()   | created date                                          |
|     | updated_at    | TIMESTAMP |        | YES      | N/A     | updated date                                          |

### 2.17. LIST_ALLOWED_CA

이 테이블은 특정 지갑에 허용된 CA 목록을 저장합니다.

| Key | Column Name | Data Type | Length | Nullable | Default | Description                      |
| --- | ----------- | --------- | ------ | -------- | ------- | -------------------------------- |
| PK  | id          | BIGINT    |        | NO       | N/A     | id                               |
|     | wallet_id   | VARCHAR   | 200    | NO       | N/A     | wallet identifier                |
|     | ca_list     | TEXT      |        | NO       | N/A     | list of allowed CA (JSON format) |
|     | created_at  | TIMESTAMP |        | NO       | now()   | created date                     |
|     | updated_at  | TIMESTAMP |        | YES      | N/A     | updated date                     |

### 2.18. LIST_VC_SCHEMA

이 테이블은 발급자가 발행한 VC 스키마 정보를 제목 및 설명과 같은 메타데이터와 함께 저장합니다.

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

이 테이블은 발급자가 발행한 VC 발급 계획을 계획 메타데이터 및 세부 정보와 함께 저장합니다.

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

### 2.20. ADMIN

이 테이블은 시스템을 관리하는 관리자에 대한 정보를 저장합니다.

| Key | Column Name            | Data Type | Length | Nullable | Default | Description                                      |
| --- | ---------------------- | --------- | ------ | -------- | ------- | ------------------------------------------------ |
| PK  | id                     | BIGINT    |        | NO       | N/A     | id                                               |
|     | login_id               | VARCHAR   | 50     | NO       | N/A     | administrator login ID                           |
|     | login_password         | VARCHAR   | 64     | NO       | N/A     | hashed login password                            |
|     | name                   | VARCHAR   | 200    | YES      | N/A     | admin's display name                             |
|     | email_verified         | BOOLEAN   |        | YES      | false   | whether the email has been verified              |
|     | require_password_reset | BOOLEAN   |        | NO       | true    | whether password reset is required at next login |
|     | role                   | VARCHAR   | 50     | NO       | N/A     | admin role                                       |
|     | created_by             | VARCHAR   | 50     | NO       | N/A     | who created this admin account                   |
|     | created_at             | TIMESTAMP |        | NO       | now()   | created date                                     |
|     | updated_at             | TIMESTAMP |        | YES      | N/A     | updated date                                     |

### 2.21. LIST_CREDENTIAL_SCHEMA

이 테이블은 발급자가 발행한 자격 증명 스키마를 저장합니다.

| Key | Column Name          | Data Type | Length | Nullable | Default | Description                             |
| --- | -------------------- | --------- | ------ | -------- | ------- | --------------------------------------- |
| PK  | id                   | BIGINT    |        | NO       | N/A     | id                                      |
|     | credential_schema_id | VARCHAR   | 200    | NO       | N/A     | credential schema identifier            |
|     | issuer_did           | VARCHAR   | 200    | NO       | N/A     | issuer DID                              |
|     | issuer_name          | VARCHAR   | 200    | NO       | N/A     | issuer name                             |
|     | name                 | VARCHAR   | 50     | NO       | N/A     | name of the credential schema           |
|     | credentialSchema     | TEXT      |        | NO       | N/A     | credential schema content (JSON format) |
|     | created_at           | TIMESTAMP |        | NO       | now()   | created date                            |
|     | updated_at           | TIMESTAMP |        | YES      | N/A     | updated date                            |

### 2.22. LIST_CREDENTIAL_DEFINITION

이 테이블은 발급자가 발행한 자격 증명 정의 메타데이터를 저장합니다.

| Key | Column Name               | Data Type | Length | Nullable | Default | Description                                 |
| --- | ------------------------- | --------- | ------ | -------- | ------- | ------------------------------------------- |
| PK  | id                        | BIGINT    |        | NO       | N/A     | id                                          |
|     | credential_definition_id  | VARCHAR   | 200    | NO       | N/A     | credential definition identifier            |
|     | credential_definition_tag | VARCHAR   | 100    | NO       | N/A     | credential definition tag                   |
|     | credential_schema_id      | VARCHAR   | 200    | NO       | N/A     | related credential schema identifier        |
|     | issuer_did                | VARCHAR   | 200    | NO       | N/A     | issuer DID                                  |
|     | issuer_name               | VARCHAR   | 200    | NO       | N/A     | issuer name                                 |
|     | credentialDefinition      | TEXT      |        | NO       | N/A     | credential definition content (JSON format) |
|     | created_at                | TIMESTAMP |        | NO       | now()   | created date                                |
|     | updated_at                | TIMESTAMP |        | YES      | N/A     | updated date                                |

### 2.23. DID_DOCUMENT

이 테이블은 엔티티와 연관된 DID 문서를 저장합니다.

| Key | Column Name  | Data Type | Length | Nullable | Default | Description                 |
| --- | ------------ | --------- | ------ | -------- | ------- | --------------------------- |
| PK  | id           | BIGINT    |        | NO       | N/A     | id                          |
|     | did_document | TEXT      |        | NO       | N/A     | DID Document content (JSON) |
|     | create_at    | TIMESTAMP |        | NO       | now()   | created date                |
|     | updated_at   | TIMESTAMP |        | YES      | N/A     | updated date                |
|     | entity_id    | BIGINT    |        | NO       | N/A     | related entity table key    |