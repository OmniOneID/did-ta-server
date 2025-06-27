---
puppeteer:
    pdf:
        format: A4
        displayHeaderFooter: true
        landscape: false
        scale: 0.8
        margin:
            top: 1.2cm
            right: 1cm
            bottom: 1cm
            left: 1cm
    image:
        quality: 100
        fullPage: false
---


TAS Server Error Code
==

- Date: 2025-05-29
- Version: v2.0.0

| Version | Date       | Changes         |
| ------- | ---------- | --------------- |
| v1.0.0  | 2024-08-20 | Initial version |
| v2.0.0  | 2025-05-29 | update error    |

<div style="page-break-after: always;"></div>

# Table of Contents
- [TAS Server Error Code](#tas-server-error-code)
- [Table of Contents](#table-of-contents)
- [Model](#model)
  - [Error Response](#error-response)
    - [Description](#description)
    - [Declaration](#declaration)
    - [Property](#property)
- [Error Code](#error-code)
  - [1. Tas Backend](#1-tas-backend)
    - [1-1. General (10000 ~ 10999)](#1-1-general-10000--10999)
    - [1-2. DB (11000 ~ 11499)](#1-2-db-11000--11499)
    - [1-3. API (12000 ~ 12999)](#1-3-api-12000--12999)
    - [1-4. TAS (13000 ~ 13499)](#1-4-tas-13000--13499)
    - [1-5. TAS Setting (13500 ~ 13999)](#1-5-tas-setting-13500--13999)
    - [1-6. Entity (14000 ~ 14499)](#1-6-entity-14000--14499)
    - [1-7. BlockChain (15000 ~ 15499)](#1-7-blockchain-15000--15499)
    - [1-8. Other server connection (15500 ~ 15999)](#1-8-other-server-connection-15500--15999)
    - [1-9. Transaction (16000 ~ 16499)](#1-9-transaction-16000--16499)
    - [1-10. ECDH or Proof (16500 ~ 16999)](#1-10-ecdh-or-proof-16500--16999)
    - [1-11. User (17000 ~ 17499)](#1-11-user-17000--17499)
    - [1-12. Wallet (17500 ~ 17999)](#1-12-wallet-17500--17999)
    - [1-13. DID Document (18000 ~ 18499)](#1-13-did-document-18000--18499)
    - [1-14. VC (18500 ~ 18999)](#1-14-vc-18500--18999)
    - [1-15. Token (19000 ~ 19499)](#1-15-token-19000--19499)
    - [1-16. List Provider (19500 ~ 19999)](#1-16-list-provider-19500--19999)
    - [1-17. Notification Provider (20000 ~ 20499)](#1-17-notification-provider-20000--20499)
    - [1-18. KYC (20500 ~ 20999)](#1-18-kyc-20500--20999)
    - [1-19. Admin (21000 ~ 21499)](#1-19-admin-21000--21499)
    - [1-99. Other Errors (90000 ~ 99999)](#1-99-other-errors-90000--99999)
# Model
## Error Response
### Description
```
Error struct for TAS Backend. It has code and message pair.
Code starts with SSRVTRA.
```

### Declaration
```java
public class ErrorResponse {
    private final String code;
    private final String description;
}
```

### Property

| Name    | Type   | Description                        | **M/O** | **Note** |
| ------- | ------ | ---------------------------------- | ------- | -------- |
| code    | String | Error code. It starts with SSRVTRA | M       |          |
| message | String | Error description                  | M       |          |

<br>

# Error Code
## 1. Tas Backend
### 1-1. General (10000 ~ 10999)
| Error Code   | Error Message                              | Description | Action Required                         |
| ------------ | ------------------------------------------ | ----------- | --------------------------------------- |
| SSRVTRA10000 | Failed to encode data.                     | -           | 인코딩 프로세스의 문제를 확인하세요.      |
| SSRVTRA10001 | Failed to decode data: incorrect encoding. | -           | 데이터 인코딩 형식을 확인하세요.        |
| SSRVTRA10002 | Failed to generate hash value.             | -           | 해시 생성 프로세스를 확인하세요.          |
| SSRVTRA10003 | Failed to merge nonce.                     | -           | nonce 병합 절차를 조사하세요.      |
| SSRVTRA10004 | Failed to encrypt data.                    | -           | 암호화 구성을 확인하세요.         |
| SSRVTRA10005 | Failed to decrypt data.                    | -           | 복호화 프로세스를 확인하세요.              |
| SSRVTRA10006 | Error occurred while processing JSON data. | -           | JSON 데이터가 올바른 형식인지 확인하세요. |

### 1-2. DB (11000 ~ 11499)
| Error Code   | Error Message                | Description | Action Required                    |
| ------------ | ---------------------------- | ----------- | ---------------------------------- |
| SSRVTRA11000 | Failed to save DID offer.    | -           | DID offer 저장 프로세스를 확인하세요.  |
| SSRVTRA11001 | Failed to find DID offer.    | -           | DID offer 검색 프로세스를 확인하세요. |
| SSRVTRA11002 | Failed to update push token. | -           | 푸시 토큰 업데이트 프로세스를 확인하세요.  |
| SSRVTRA11003 | Failed to save VC schema.    | -           | VC 스키마 저장 로직을 확인하세요.        |

### 1-3. API (12000 ~ 12999)
| Error Code   | Error Message                                          | Description | Action Required                                     |
| ------------ | ------------------------------------------------------ | ----------- | --------------------------------------------------- |
| SSRVTRA12000 | Failed to parse VC Schema.                             | -           | VC 스키마 구조와 형식을 확인하세요.           |
| SSRVTRA12001 | Invalid role type provided.                            | -           | 올바른 역할 유형이 제공되었는지 확인하세요.         |
| SSRVTRA12002 | Failed to retrieve verification method.                | -           | 검증 방법 검색을 확인하세요.            |
| SSRVTRA12003 | Failed to generate push data.                          | -           | 푸시 데이터 생성 프로세스를 확인하세요.         |
| SSRVTRA12004 | Failed to process the request: invalid request body.   | -           | 요청 본문 형식을 검증하세요.                   |
| SSRVTRA12005 | Unsupported token purpose provided.                    | -           | 토큰 목적이 지원되는지 확인하세요.             |
| SSRVTRA12006 | Failed to parse DID Document.                          | -           | DID 문서 구조를 확인하세요.                     |
| SSRVTRA12007 | Provider DID mismatch.                                 | -           | 제공업체 DID가 예상 값과 일치하는지 확인하세요. |
| SSRVTRA12008 | Unsupported Cipher Type.                               | -           | 암호화 타입이 지원되는지 확인하세요.            |
| SSRVTRA12009 | Invalid DID Document version.                          | -           | DID 문서 버전이 올바른지 확인하세요.            |
| SSRVTRA12010 | Failed to authenticate: password is incorrect.         | -           | 제공된 비밀번호의 정확성을 확인하세요.           |
| SSRVTRA12011 | The requested DID does not match the DID in the Offer. | -           | 요청의 DID가 Offer와 일치하는지 확인하세요.     |
| SSRVTRA12012 | Failed to find allowed CA list.                        | -           | CA 목록이 올바르게 구성되었는지 확인하세요.     |

### 1-4. TAS (13000 ~ 13499)
| Error Code   | Error Message                                                        | Description | Action Required                                     |
| ------------ | -------------------------------------------------------------------- | ----------- | --------------------------------------------------- |
| SSRVTRA13001 | TAS is not registered.                                               | -           | TAS가 올바르게 등록되었는지 확인하세요.            |
| SSRVTRA13002 | Tas Certificate VC data not found.                                   | -           | TAS 인증서 데이터가 존재하는지 확인하세요.           |
| SSRVTRA13003 | Failed to register TAS: TAS is already registered.                   | -           | TAS가 이미 등록되지 않았는지 확인하세요.                |
| SSRVTRA13004 | Failed to find TAS: TAS is not registered.                           | -           | TAS 등록 상태를 확인하세요.                |
| SSRVTRA13005 | Failed to process TAS DID Document: invalid document.                | -           | TAS DID 문서 구조를 검증하세요.            |
| SSRVTRA13006 | Failed to process the 'propose-enroll-tas' API request.              | -           | 'propose-enroll-tas' API 요청 프로세스를 확인하세요. |
| SSRVTRA13007 | Failed to process the 'get-certificate-vc' API request.              | -           | 'get-certificate-vc' API 요청 프로세스를 확인하세요. |
| SSRVTRA13008 | Failed to find TAS password.                                         | -           | TAS 비밀번호가 존재하는지 확인하세요.                         |
| SSRVTRA13009 | Failed to process request: TA secret does not match.                 | -           | TA 시크릿을 확인하세요.                                   |
| SSRVTRA13010 | Failed to register TAS DID Document: document is already registered. | -           | 중복 등록을 피하세요.                       |

### 1-5. TAS Setting (13500 ~ 13999)
| Error Code   | Error Message                                                 | Description | Action Required                                   |
| ------------ | ------------------------------------------------------------- | ----------- | ------------------------------------------------- |
| SSRVTRA13500 | Failed to read email template.                                | -           | 이메일 템플릿 파일과 형식을 확인하세요.    |
| SSRVTRA13501 | Failed to send FCM message.                                   | -           | FCM 서비스 구성과 로그를 확인하세요.     |
| SSRVTRA13502 | Failed to convert QR image.                                   | -           | QR 이미지 변환 프로세스를 확인하세요.            |
| SSRVTRA13503 | Failed to configure mail settings.                            | -           | 메일 설정이 올바르게 구성되었는지 확인하세요.    |
| SSRVTRA13504 | Failed to initialize server: invalid configuration.           | -           | 서버 구성 설정을 확인하세요.             |
| SSRVTRA13505 | Failed to retrieve allowed CAs.                               | -           | CA 검색 프로세스와 구성을 확인하세요. |
| SSRVTRA13506 | Failed to process push notification: invalid push type.       | -           | 푸시 알림 타입이 유효한지 확인하세요.        |
| SSRVTRA13507 | Failed to process QR code: invalid QR type.                   | -           | QR 코드 타입과 구조를 확인하세요.             |
| SSRVTRA13508 | Failed to process ECC curve: invalid curve type.              | -           | ECC 곡선 타입이 지원되는지 확인하세요.          |
| SSRVTRA13509 | Failed to process encryption: invalid symmetric cipher type.  | -           | 대칭 암호화 타입이 올바른지 확인하세요.    |
| SSRVTRA13510 | Failed to process encryption: invalid symmetric padding type. | -           | 사용된 대칭 패딩 타입을 검증하세요.         |
| SSRVTRA13511 | Failed to register TA certificate.                            | -           | TA 인증서 등록 로직을 확인하세요.          |
| SSRVTRA13512 | Failed to register TA DID Document.                           | -           | TA DID 문서 등록을 확인하세요.               |
| SSRVTRA13513 | TA is already registered.                                     | -           | 중복 TA 등록을 피하세요.                         |

### 1-6. Entity (14000 ~ 14499)
| Error Code   | Error Message                                                             | Description | Action Required                                           |
| ------------ | ------------------------------------------------------------------------- | ----------- | --------------------------------------------------------- |
| SSRVTRA14000 | Failed to find entity: entity is not registered.                          | -           | 엔티티가 올바르게 등록되었는지 확인하세요.              |
| SSRVTRA14001 | Failed to find issuer: issuer is not registered.                          | -           | 발급자의 등록 상태를 확인하세요.                 |
| SSRVTRA14002 | The issuer has not completed registration.                                | -           | 발급자가 등록 프로세스를 완료하도록 하세요.     |
| SSRVTRA14003 | The entity has not completed registration.                                | -           | 엔티티가 등록을 완료하는지 확인하세요.            |
| SSRVTRA14004 | Failed to register entity: entity is already registered.                  | -           | 엔티티가 이미 등록되었는지 확인하세요.                |
| SSRVTRA14005 | Failed to process the 'propose-enroll-entity' API request.                | -           | 'propose-enroll-entity' API 프로세스를 확인하세요.            |
| SSRVTRA14006 | The provided DID does not match the entity that requested registration.   | -           | DID가 엔티티의 등록 요청과 일치하는지 확인하세요. |
| SSRVTRA14007 | Failed to process the 'request-enroll-entity' API request.                | -           | 'request-enroll-entity' API 프로세스를 확인하세요.            |
| SSRVTRA14008 | Failed to register quick entity.                                          | -           | 빠른 엔티티 등록 프로세스를 확인하세요.                  |
| SSRVTRA14009 | Failed to register entity: entity name is already registered.             | -           | 중복 엔티티 이름을 피하세요.                             |
| SSRVTRA14010 | Failed to find entity DID Document: o registration request has been made. | -           | 엔티티 DID 문서 등록을 확인하세요.                   |
| SSRVTRA14011 | Failed to register DID from entity.                                       | -           | 엔티티 DID 등록 로직을 확인하세요.                     |
| SSRVTRA14012 | Failed to approve entity DID Document.                                    | -           | 엔티티 DID 문서 승인 플로우를 확인하세요.                  |
| SSRVTRA14013 | Failed to delete entity: entity is not deletable.                         | -           | 엔티티 삭제 가능 여부를 확인하세요.                            |

### 1-7. BlockChain (15000 ~ 15499)
| Error Code   | Error Message                                      | Description | Action Required                                                 |
| ------------ | -------------------------------------------------- | ----------- | --------------------------------------------------------------- |
| SSRVTRA15000 | Failed to initialize blockchain.                   | -           | 블록체인 초기화 프로세스를 확인하세요.                        |
| SSRVTRA15001 | Failed to register DID Document on the blockchain. | -           | DID 문서의 등록 프로세스를 확인하세요.               |
| SSRVTRA15002 | Failed to retrieve DID document on the blockchain. | -           | 블록체인에서 DID 문서 검색을 조사하세요.            |
| SSRVTRA15003 | Failed to update DID Document on the blockchain.   | -           | 블록체인에서 DID 문서의 업데이트 프로세스를 확인하세요. |
| SSRVTRA15004 | Failed to register VC meta on the blockchain.      | -           | 블록체인에서 VC 메타 등록 프로세스를 확인하세요.           |
| SSRVTRA15005 | Failed to retrieve VC meta on the blockchain.      | -           | 블록체인에서 VC 메타의 적절한 검색을 확인하세요.         |
| SSRVTRA18507 | Failed to update VC status on the blockchain.      | -           | 블록체인에서 VC 상태 업데이트 프로세스를 확인하세요.          |
| SSRVTRA18508 | Failed to remove index on the blockchain.          | -           | 블록체인에서 인덱스 제거 프로세스를 확인하세요.              |

### 1-8. Other server connection (15500 ~ 15999)
| Error Code   | Error Message                                                           | Description | Action Required                                              |
| ------------ | ----------------------------------------------------------------------- | ----------- | ------------------------------------------------------------ |
| SSRVTRA15500 | Failed to register DID document.                                        | -           | DID 문서 등록 프로세스를 확인하세요.                |
| SSRVTRA15501 | Failed to process response: received unknown data from the issuer.      | -           | 발급자의 응답 데이터를 확인하세요.            |
| SSRVTRA15502 | Failed to communicate with issuer: unknown error occurred.              | -           | 통신 채널과 오류 로그를 확인하세요.            |
| SSRVTRA15503 | Failed to process message: received an invalid message from the issuer. | -           | 발급자로부터 받은 메시지 형식을 확인하세요.          |
| SSRVTRA15504 | Failed to send email.                                                   | -           | 이메일 설정과 서버 구성이 올바른지 확인하세요. |
| SSRVTRA15505 | Failed to communicate with KYC server: unknown error occurred.          | -           | KYC 서버 연결성을 확인하세요.                               |
| SSRVTRA15506 | Failed to ping the URL.                                                 | -           | URL과 네트워크 접근성을 확인하세요.                           |

### 1-9. Transaction (16000 ~ 16499)

| Error Code   | Error Message                                                | Description | Action Required                       |
| ------------ | ------------------------------------------------------------ | ----------- | ------------------------------------- |
| SSRVTRA16000 | Failed to find transaction: the transaction does not exist.  | -           | 트랜잭션 존재 여부를 확인하세요.         |
| SSRVTRA16001 | Failed to process transaction: the transaction is not valid. | -           | 트랜잭션 유효성을 확인하세요.           |
| SSRVTRA16002 | Failed to process transaction: the transaction has expired.  | -           | 트랜잭션 만료 상태를 확인하세요. |

### 1-10. ECDH or Proof (16500 ~ 16999)
| Error Code   | Error Message                                         | Description | Action Required                                          |
| ------------ | ----------------------------------------------------- | ----------- | -------------------------------------------------------- |
| SSRVTRA16500 | Failed to generate key.                               | -           | 키 생성 프로세스를 확인하세요.                        |
| SSRVTRA16501 | Failed to find ECDH information.                      | -           | ECDH 정보가 올바르고 사용 가능한지 확인하세요. |
| SSRVTRA16502 | Failed to uncompress public key.                      | -           | 공개 키 압축 해제 프로세스를 확인하세요.              |
| SSRVTRA16503 | Failed to compress public key.                        | -           | 공개 키의 적절한 압축을 확인하세요.             |
| SSRVTRA16504 | Failed to generate nonce.                             | -           | nonce 생성 프로세스를 확인하세요.                      |
| SSRVTRA16505 | Failed to generate key pair.                          | -           | 키 쌍 생성 프로세스를 조사하세요.                 |
| SSRVTRA16506 | Failed to generate session key.                       | -           | 세션 키 생성 프로세스를 확인하세요.               |
| SSRVTRA16507 | Failed to merge nonce and shared secret.              | -           | nonce와 공유 비밀의 올바른 병합을 확인하세요.       |
| SSRVTRA16508 | Failed to generate initial vector.                    | -           | 초기 벡터 생성 프로세스를 확인하세요.             |
| SSRVTRA16509 | Failed to verify DID Auth.                            | -           | DID Auth 검증 단계를 검증하세요.                    |
| SSRVTRA16510 | Failed to verify signature: the signature is invalid. | -           | 제공된 서명의 유효성을 확인하세요.           |
| SSRVTRA16511 | Failed to verify signature.                           | -           | 서명 검증 프로세스를 확인하세요.                |
| SSRVTRA16512 | Failed to generate signature.                         | -           | 서명 생성 프로세스를 확인하세요.                  |
| SSRVTRA16513 | Failed to process proof: invalid purpose.             | -           | 프로세스에 사용된 증명 목적을 확인하세요.            |
| SSRVTRA16514 | Failed to sign response data.                         | -           | 올바른 응답 데이터 서명 프로세스를 확인하세요.            |
| SSRVTRA16515 | Failed to sign response data.                         | -           | 응답 데이터 서명 실패 이유를 조사하세요.            |
| SSRVTRA16516 | Failed to verify DID document key proof.              | -           | DID 문서 키 증명의 검증을 확인하세요.    |
| SSRVTRA16517 | Failed to add key proof to DID document.              | -           | DID 문서에 키 증명 추가 프로세스를 확인하세요.  |
| SSRVTRA16518 | Failed to extract signature message.                  | -           | 서명 메시지의 추출을 확인하세요.          |
| SSRVTRA16519 | Failed to process client nonce: invalid nonce.        | -           | 클라이언트 nonce가 유효한지 확인하세요.                        |
| SSRVTRA16520 | 'authNonce' does not match.                           | -           | 'authNonce'의 불일치를 확인하세요.                     |
| SSRVTRA16521 | Failed to process the 'request-ecdh' API request.     | -           | 'request-ecdh' API 요청 프로세스를 확인하세요.            |

### 1-11. User (17000 ~ 17499)
| Error Code    | Error Message                                                  | Description | Action Required                                            |
| ------------- | -------------------------------------------------------------- | ----------- | ---------------------------------------------------------- |
| SSRVTRA17000  | Failed to register user DID: user DID already exists.          | -           | 등록 전에 사용자 DID가 이미 존재하는지 확인하세요. |
| SSRVTRA17001  | Failed to find user DID: user DID not found.                   | -           | 사용자 DID의 존재를 확인하세요.                       |
| SSRVTRA17002  | Failed to find user: user is not registered.                   | -           | 사용자가 등록되었는지 확인하세요.                             |
| SSRVTRA17003  | Failed to process request: user status is not 'Activated'.     | -           | 사용자의 상태를 확인하고 필요시 활성화하세요.           |
| SSRVTRA17004  | Failed to find app: app is not registered.                     | -           | 앱이 등록되었는지 확인하세요.                             |
| SSRVTRA17005  | Failed to authenticate app: app ID does not match.             | -           | 제공된 앱 ID를 확인하세요.                                |
| SSRVTRA17006  | Failed to authenticate app: invalid app ID.                    | -           | 앱 ID가 유효한지 확인하세요.                                |
| SSRVTRA17007  | Failed to find push token.                                     | -           | 푸시 토큰의 존재를 확인하세요.                    |
| SSRVTRA17008  | Failed to process the 'propose-register-user' API request.     | -           | 'propose-register-user' API 프로세스를 확인하세요.             |
| SSRVTRA17009  | Failed to process the 'request-register-user' API request.     | -           | 'request-register-user' API 요청을 확인하세요.             |
| SSRVTRA17010  | Failed to process the 'request-confirm-user' API request.      | -           | 'request-confirm-user' API 요청을 확인하세요.              |
| SSRVTRA17011  | Failed to process the 'propose-update-diddoc' API request.     | -           | 'propose-update-diddoc' API 프로세스를 확인하세요.             |
| SSRVTRA17012  | Failed to process the 'request-update-diddoc' API request.     | -           | 'request-update-diddoc' API 프로세스를 확인하세요.             |
| SSRVTRA17013  | Failed to process the 'confirm-update-diddoc' API request.     | -           | 'confirm-update-diddoc' API 요청을 확인하세요.             |
| SSRVTRA17014  | Failed to process request: user status is not 'Deactivated'.   | -           | 사용자 상태가 '비활성화'인지 확인하세요.                 |
| SSRVTRA17015  | Failed to process the 'propose-restore-diddoc' API request.    | -           | 'propose-restore-diddoc' API 프로세스를 확인하세요.            |
| SSRVTRA17016  | Failed to process the 'request-restore-diddoc' API request.    | -           | 'request-restore-diddoc' API 프로세스를 확인하세요.            |
| SSRVTRA17017  | Failed to process the 'confirm-restore-diddoc' API request.    | -           | 'confirm-restore-diddoc' API 프로세스를 확인하세요.            |
| SSRVTRA17018  | Failed to process the 'retrieve-kyc' API request.              | -           | 'retrieve-kyc' API 프로세스를 확인하세요.                      |
| SSRVTRA17019  | Failed to process the 'offer-restore-did-push' API request.    | -           | 'offer-restore-did-push' API 프로세스를 확인하세요.            |
| SSRVTRA17020  | Failed to process the 'offer-restore-did-email' API request.   | -           | 'offer-restore-did-email' API 프로세스를 확인하세요.           |
| SSRVTRA17021  | Failed to process the 'update-push-token' API request.         | -           | 'update-push-token' API 프로세스를 확인하세요.                 |
| SSRVTRA17022  | Failed to process the 'update-diddoc-deactivated' API request. | -           | 'update-diddoc-deactivated' API 프로세스를 확인하세요.         |
| SSRVTRA117023 | Failed to process the 'update-diddoc-revoked' API request.     | -           | 'update-diddoc-revoked' API 프로세스를 확인하세요.             |

### 1-12. Wallet (17500 ~ 17999)
| Error Code   | Error Message                                                | Description | Action Required                                            |
| ------------ | ------------------------------------------------------------ | ----------- | ---------------------------------------------------------- |
| SSRVTRA17500 | Wallet Provider has not registered.                          | -           | 지갑 제공업체가 올바르게 등록되었는지 확인하세요.         |
| SSRVTRA17501 | Wallet ID already exists.                                    | -           | 등록 전에 지갑 ID가 이미 존재하는지 확인하세요. |
| SSRVTRA17502 | Failed to find wallet: wallet is not registered.             | -           | 지갑이 시스템에 등록되었는지 확인하세요.          |
| SSRVTRA17503 | Failed to create wallet.                                     | -           | 지갑 생성 프로세스의 문제를 조사하세요.           |
| SSRVTRA17504 | Failed to connect to wallet.                                 | -           | 지갑 연결 프로세스를 확인하세요.                |
| SSRVTRA17505 | Failed to change wallet password.                            | -           | 지갑 비밀번호 변경 프로세스를 확인하세요.                 |
| SSRVTRA17506 | Failed to generate wallet signature.                         | -           | 지갑 서명 생성 프로세스를 확인하세요.             |
| SSRVTRA17507 | Failed to establish wallet connection.                       | -           | 적절한 지갑 연결 설정을 확인하세요.                     |
| SSRVTRA17508 | Failed to authenticate wallet: wallet ID does not match.     | -           | 인증에 사용된 지갑 ID를 확인하세요.              |
| SSRVTRA17509 | Failed to get File wallet manager.                           | -           | 파일 지갑 관리자의 검색 프로세스를 확인하세요.   |
| SSRVTRA17510 | Failed to process the 'request-register-wallet' API request. | -           | 'request-register-wallet' API 요청을 확인하세요.           |
| SSRVTRA17511 | Failed to create wallet: wallet already exists.              | -           | 중복 지갑 생성을 피하세요.                           |
| SSRVTRA17512 | Failed to create wallet: invalid wallet file path.           | -           | 지갑 파일 경로 형식을 확인하세요.                             |
| SSRVTRA17513 | Failed to generate keys: key already exists.                 | -           | 고유한 키 생성을 확인하세요.                              |
| SSRVTRA17514 | Failed to load key element.                                  | -           | 키 로딩 로직을 확인하세요.                                   |

### 1-13. DID Document (18000 ~ 18499)
| Error Code   | Error Message                                                                              | Description | Action Required                                               |
| ------------ | ------------------------------------------------------------------------------------------ | ----------- | ------------------------------------------------------------- |
| SSRVTRA18000 | Failed to retrieve DID Document.                                                           | -           | DID 문서의 검색 프로세스를 확인하세요.                 |
| SSRVTRA18001 | Entity DID Document registration is required.                                              | -           | 엔티티 DID 문서 등록이 완료되었는지 확인하세요.         |
| SSRVTRA18003 | Failed to generate DID document.                                                           | -           | DID 문서 생성 프로세스를 확인하세요.                    |
| SSRVTRA18004 | Failed to save DID document.                                                               | -           | DID 문서 저장 프로세스를 확인하세요.                       |
| SSRVTRA18005 | Failed to retrieve DID document public key.                                                | -           | DID 문서 공개 키 검색 프로세스를 확인하세요. |
| SSRVTRA18006 | Failed to process DID document: invalid updated time.                                      | -           | DID 문서의 업데이트 시간이 올바른지 확인하세요.       |
| SSRVTRA18007 | Failed to process DID document: invalid context.                                           | -           | DID 문서의 컨텍스트를 확인하세요.                       |
| SSRVTRA18008 | Failed to process DID document: invalid document ID.                                       | -           | DID 문서의 문서 ID를 확인하세요.                    |
| SSRVTRA18009 | Failed to process DID document: invalid controller.                                        | -           | DID 문서의 컨트롤러를 확인하세요.                    |
| SSRVTRA18010 | Failed to find DID Document.                                                               | -           | DID 문서가 존재하고 접근 가능한지 확인하세요.             |
| SSRVTRA18012 | Failed to update DID Document.                                                             | -           | DID 문서 업데이트 프로세스를 확인하세요.              |
| SSRVTRA18013 | Failed to delete DID Document.                                                             | -           | DID 문서 삭제 프로세스를 확인하세요.              |
| SSRVTRA18014 | Failed to process DID document: invalid creation time.                                     | -           | DID 문서의 생성 시간이 유효한지 확인하세요.        |
| SSRVTRA18015 | Failed to process DID document: invalid deactivated.                                       | -           | DID 문서의 비활성화 상태를 확인하세요.           |
| SSRVTRA18016 | Failed to register DID Document.                                                           | -           | DID 문서 등록 문제를 조사하세요.        |
| SSRVTRA18017 | Failed to generate Invoked Document.                                                       | -           | 호출된 문서 생성 프로세스를 확인하세요.                |
| SSRVTRA18018 | Failed to process request: ID of DID Document does not match the previously requested DID. | -           | DID 문서 ID가 요청된 DID와 일치하는지 확인하세요.    |
| SSRVTRA18019 | Failed to process DID Document: invalid document.                                          | -           | DID 문서 구조를 확인하세요.                                 |
| SSRVTRA18020 | Failed to register DID: DID is already registered.                                         | -           | 중복 DID 등록을 피하세요.                             |

### 1-14. VC (18500 ~ 18999)
| Error Code   | Error Message                                              | Description | Action Required                                                   |
| ------------ | ---------------------------------------------------------- | ----------- | ----------------------------------------------------------------- |
| SSRVTRA18500 | VC ID does not match.                                      | -           | VC ID가 예상 ID와 일치하는지 확인하세요.                    |
| SSRVTRA18501 | Failed to retrieve VC plan.                                | -           | VC 계획의 검색 프로세스를 확인하세요.                      |
| SSRVTRA18502 | Failed to retrieve VC categories.                          | -           | VC 카테고리 검색 문제를 조사하세요.                 |
| SSRVTRA18503 | Failed to retrieve VC schema.                              | -           | VC 스키마 검색 프로세스를 확인하세요.                       |
| SSRVTRA18504 | Failed to generate VC meta.                                | -           | VC 메타 생성 프로세스를 확인하세요.                           |
| SSRVTRA18505 | Failed to register VC meta.                                | -           | VC 메타 등록 프로세스를 확인하세요.                          |
| SSRVTRA18506 | Failed to retrieve VC meta.                                | -           | VC 메타의 검색 프로세스를 확인하세요.                          |
| SSRVTRA18507 | Failed to update VC status.                                | -           | VC 상태 업데이트 프로세스를 확인하세요.                               |
| SSRVTRA18508 | Failed to extract VC origin data.                          | -           | VC 원본 데이터의 추출 프로세스를 확인하세요.                 |
| SSRVTRA18509 | Failed to set VC proof.                                    | -           | VC 증명 설정 프로세스를 확인하세요.                           |
| SSRVTRA18510 | Failed to find certificate VC.                             | -           | 인증서 VC가 존재하고 접근 가능한지 확인하세요.               |
| SSRVTRA18511 | Invalid certificate VC issuer.                             | -           | 인증서 VC의 발급자를 확인하세요.                          |
| SSRVTRA18512 | Failed to set claim info.                                  | -           | 클레임 정보 설정 문제를 조사하세요.                       |
| SSRVTRA18513 | Failed to set VC type.                                     | -           | VC 타입 설정 프로세스를 확인하세요.                            |
| SSRVTRA18514 | Failed to encrypt VC data.                                 | -           | VC 데이터의 암호화 프로세스를 확인하세요.                         |
| SSRVTRA18515 | Failed to generate VC.                                     | -           | VC 생성 프로세스를 확인하세요.                                 |
| SSRVTRA18516 | Failed to find VC meta.                                    | -           | VC 메타 찾기 프로세스를 확인하세요.                            |
| SSRVTRA18517 | Failed to parse VC meta.                                   | -           | VC 메타의 파싱 프로세스를 확인하세요.                            |
| SSRVTRA18518 | Failed to revoke VC: VC is already revoked.                | -           | 폐기 시도 전에 VC가 이미 폐기되었는지 확인하세요. |
| SSRVTRA18519 | Failed to verify VC.                                       | -           | VC 검증 프로세스를 확인하세요.                              |
| SSRVTRA18520 | Failed to process the 'propose-issue-vc' API request.      | -           | 'propose-issue-vc' API 요청 프로세스를 확인하세요.                 |
| SSRVTRA18521 | Failed to process the 'request-issue-profile' API request. | -           | 'request-issue-profile' API 요청 프로세스를 확인하세요.            |
| SSRVTRA18522 | Failed to process the 'request-issue-vc' API request.      | -           | 'request-issue-vc' API 요청 프로세스를 확인하세요.                 |
| SSRVTRA18523 | Failed to process the 'confirm-issue-vc' API request.      | -           | 'confirm-issue-vc' API 요청 프로세스를 확인하세요.                 |
| SSRVTRA18524 | Failed to process the 'propose-revoke-vc' API request.     | -           | 'propose-revoke-vc' API 요청 프로세스를 확인하세요.                |
| SSRVTRA18525 | Failed to process the 'request-revoke-vc' API request.     | -           | 'request-revoke-vc' API 요청 프로세스를 확인하세요.                |
| SSRVTRA18526 | Failed to process the 'confirm-revoke-vc' API request.     | -           | 'confirm-revoke-vc' API 요청 프로세스를 확인하세요.                |
| SSRVTRA18527 | Failed to process the 'offer-issue-vc-qr' API request.     | -           | 'offer-issue-vc-qr' API 요청 프로세스를 확인하세요.                |
| SSRVTRA18528 | Failed to process the 'offer-issue-vc-push' API request.   | -           | 'offer-issue-vc-push' API 요청 프로세스를 확인하세요.              |
| SSRVTRA18529 | Failed to process the 'offer-issue-vc-email' API request.  | -           | 'offer-issue-vc-email' API 요청 프로세스를 확인하세요.             |
| SSRVTRA18530 | Failed to process the 'get-vc-schema' API request.         | -           | 'get-vc-schema' API 요청 프로세스를 확인하세요.                    |
| SSRVTRA18531 | Failed to register VC schema from issuer.                  | -           | VC 스키마 등록 프로세스를 확인하세요.                             |
| SSRVTRA18532 | Failed to register certificate VC schema.                  | -           | 인증서 VC 스키마 형식을 검증하세요.                            |
| SSRVTRA18533 | Failed to register VC plan from issuer.                    | -           | VC 계획 등록을 확인하세요.                                       |
| SSRVTRA18534 | Failed to process VC schema: invalid schema.               | -           | VC 스키마가 올바르게 구조화되었는지 확인하세요.                          |
| SSRVTRA18535 | Failed to process certificate VC: invalid JSON format.     | -           | 인증서 VC의 JSON 구조를 확인하세요.                          |
| SSRVTRA18536 | Failed to retrieve Credential Schema.                      | -           | 자격 증명 스키마 검색 로직을 확인하세요.                          |
| SSRVTRA18536 | Failed to retrieve Credential Definition.                  | -           | 자격 증명 정의 검색 로직을 확인하세요.                      |

### 1-15. Token (19000 ~ 19499)
| Error Code   | Error Message                                             | Description | Action Required                                       |
| ------------ | --------------------------------------------------------- | ----------- | ----------------------------------------------------- |
| SSRVTRA19000 | Failed to generate server token.                          | -           | 서버 토큰 생성 프로세스를 확인하세요.           |
| SSRVTRA19001 | Failed to encrypt server token data.                      | -           | 서버 토큰 데이터의 암호화 프로세스를 확인하세요.   |
| SSRVTRA19002 | Failed to process token: the token has expired.           | -           | 토큰이 유효하고 만료되지 않았는지 확인하세요.            |
| SSRVTRA19003 | Failed to authenticate: the token provided is invalid.    | -           | 인증을 위해 제공된 토큰을 검증하세요.       |
| SSRVTRA19004 | Failed to find token: token is not registered.            | -           | 토큰이 등록되고 접근 가능한지 확인하세요.     |
| SSRVTRA19005 | Failed to process the 'request-create-token' API request. | -           | 'request-create-token' API 요청 프로세스를 확인하세요. |

### 1-16. List Provider (19500 ~ 19999)
| Error Code   | Error Message                                            | Description | Action Required                                      |
| ------------ | -------------------------------------------------------- | ----------- | ---------------------------------------------------- |
| SSRVTRA19500 | Failed to process the 'get-vcplan-list' API request.     | -           | 'get-vcplan-list' API 요청 처리를 확인하세요.  |
| SSRVTRA19501 | Failed to process the 'get-vcplan' API request.          | -           | 'get-vcplan' API 요청 처리를 확인하세요.       |
| SSRVTRA19502 | Failed to process the 'get-allowed-ca-list' API request. | -           | 'get-allowed-ca-list' API 요청 프로세스를 확인하세요. |

### 1-17. Notification Provider (20000 ~ 20499)
| Error Code   | Error Message                                   | Description | Action Required                                |
| ------------ | ----------------------------------------------- | ----------- | ---------------------------------------------- |
| SSRVTRA20000 | Failed to process the 'send-email' API request. | -           | 'send-email' API 요청 처리를 확인하세요. |
| SSRVTRA20001 | Failed to process the 'send-push' API request.  | -           | 'send-push' API 요청 처리를 확인하세요.  |

### 1-18. KYC (20500 ~ 20999)
| Error Code   | Error Message                              | Description | Action Required                 |
| ------------ | ------------------------------------------ | ----------- | ------------------------------- |
| SSRVTRA20500 | Failed to find KYC: KYC is not registered. | -           | KYC 등록 상태를 확인하세요. |

### 1-19. Admin (21000 ~ 21499)
| Error Code   | Error Message                                          | Description | Action Required                     |
| ------------ | ------------------------------------------------------ | ----------- | ----------------------------------- |
| SSRVTRA21000 | Failed to find admin: admin is not registered.         | -           | 관리자 등록을 확인하세요.         |
| SSRVTRA21001 | Failed to register admin: admin is already registered. | -           | 중복 관리자 등록을 피하세요. |

### 1-99. Other Errors (90000 ~ 99999)

| Error Code   | Error Message                                  | Description | Action Required                     |
| ------------ | ---------------------------------------------- | ----------- | ----------------------------------- |
| SSRVTRA90000 | Failed to find file: requested file not found. | -           | 파일 존재 여부를 확인하세요.               |
| SSRVTRA90001 | Temporary error code: to be replaced.          | -           | 특정 에러 코드로 교체하세요. |
| SSRVTRA90002 | Failed to process file: I/O error occurred.    | -           | 파일 I/O 프로세스를 확인하세요.             |
| SSRVTRA90003 | An unknown server error has occurred.          | -           | 알 수 없는 서버 에러를 조사하세요.  |