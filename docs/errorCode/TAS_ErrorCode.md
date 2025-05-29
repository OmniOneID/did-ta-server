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
| SSRVTRA10000 | Failed to encode data.                     | -           | Check encoding process for issues.      |
| SSRVTRA10001 | Failed to decode data: incorrect encoding. | -           | Verify the data encoding format.        |
| SSRVTRA10002 | Failed to generate hash value.             | -           | Check hash generation process.          |
| SSRVTRA10003 | Failed to merge nonce.                     | -           | Investigate nonce merge procedure.      |
| SSRVTRA10004 | Failed to encrypt data.                    | -           | Check encryption configuration.         |
| SSRVTRA10005 | Failed to decrypt data.                    | -           | Verify decryption process.              |
| SSRVTRA10006 | Error occurred while processing JSON data. | -           | Ensure JSON data is properly formatted. |

### 1-2. DB (11000 ~ 11499)
| Error Code   | Error Message                | Description | Action Required                    |
| ------------ | ---------------------------- | ----------- | ---------------------------------- |
| SSRVTRA11000 | Failed to save DID offer.    | -           | Check the DID offer save process.  |
| SSRVTRA11001 | Failed to find DID offer.    | -           | Check DID offer retrieval process. |
| SSRVTRA11002 | Failed to update push token. | -           | Ensure push token update process.  |
| SSRVTRA11003 | Failed to save VC schema.    | -           | Check VC schema save logic.        |



### 1-3. API (12000 ~ 12999)
| Error Code   | Error Message                                          | Description | Action Required                                     |
| ------------ | ------------------------------------------------------ | ----------- | --------------------------------------------------- |
| SSRVTRA12000 | Failed to parse VC Schema.                             | -           | Check the VC schema structure and format.           |
| SSRVTRA12001 | Invalid role type provided.                            | -           | Check if the correct role type is provided.         |
| SSRVTRA12002 | Failed to retrieve verification method.                | -           | Check the verification method retrieval.            |
| SSRVTRA12003 | Failed to generate push data.                          | -           | Check the push data generation process.             |
| SSRVTRA12004 | Failed to process the request: invalid request body.   | -           | Validate the request body format.                   |
| SSRVTRA12005 | Unsupported token purpose provided.                    | -           | Confirm the token purpose is supported.             |
| SSRVTRA12006 | Failed to parse DID Document.                          | -           | Check the DID document structure.                   |
| SSRVTRA12007 | Provider DID mismatch.                                 | -           | Ensure the provider DID matches the expected value. |
| SSRVTRA12008 | Unsupported Cipher Type.                               | -           | Check if the cipher type is supported.              |
| SSRVTRA12009 | Invalid DID Document version.                          | -           | Confirm the DID document version is correct.        |
| SSRVTRA12010 | Failed to authenticate: password is incorrect.         | -           | Check the provided password for accuracy.           |
| SSRVTRA12011 | The requested DID does not match the DID in the Offer. | -           | Check the DID in the request matches the Offer.     |
| SSRVTRA12012 | Failed to find allowed CA list.                        | -           | Check the CA list is configured correctly.          |


### 1-4. TAS (13000 ~ 13499)
| Error Code   | Error Message                                                        | Description | Action Required                                     |
| ------------ | -------------------------------------------------------------------- | ----------- | --------------------------------------------------- |
| SSRVTRA13001 | TAS is not registered.                                               | -           | Check if the TAS is properly registered.            |
| SSRVTRA13002 | Tas Certificate VC data not found.                                   | -           | Check if the TAS certificate data exists.           |
| SSRVTRA13003 | Failed to register TAS: TAS is already registered.                   | -           | Check TAS is not already registered.                |
| SSRVTRA13004 | Failed to find TAS: TAS is not registered.                           | -           | Confirm the TAS registration status.                |
| SSRVTRA13005 | Failed to process TAS DID Document: invalid document.                | -           | Validate the TAS DID Document structure.            |
| SSRVTRA13006 | Failed to process the 'propose-enroll-tas' API request.              | -           | Check the 'propose-enroll-tas' API request process. |
| SSRVTRA13007 | Failed to process the 'get-certificate-vc' API request.              | -           | Check the 'get-certificate-vc' API request process. |
| SSRVTRA13008 | Failed to find TAS password.                                         | -           | Ensure TAS password exists.                         |
| SSRVTRA13009 | Failed to process request: TA secret does not match.                 | -           | Verify TA secret.                                   |
| SSRVTRA13010 | Failed to register TAS DID Document: document is already registered. | -           | Avoid duplicate registration.                       |

### 1-5. TAS Setting (13500 ~ 13999)
| Error Code   | Error Message                                                 | Description | Action Required                                   |
| ------------ | ------------------------------------------------------------- | ----------- | ------------------------------------------------- |
| SSRVTRA13500 | Failed to read email template.                                | -           | Verify the email template file and its format.    |
| SSRVTRA13501 | Failed to send FCM message.                                   | -           | Check the FCM service configuration and logs.     |
| SSRVTRA13502 | Failed to convert QR image.                                   | -           | Check the QR image conversion process.            |
| SSRVTRA13503 | Failed to configure mail settings.                            | -           | Ensure mail settings are correctly configured.    |
| SSRVTRA13504 | Failed to initialize server: invalid configuration.           | -           | Verify server configuration settings.             |
| SSRVTRA13505 | Failed to retrieve allowed CAs.                               | -           | Check the CA retrieval process and configuration. |
| SSRVTRA13506 | Failed to process push notification: invalid push type.       | -           | Check the push notification type is valid.        |
| SSRVTRA13507 | Failed to process QR code: invalid QR type.                   | -           | Check the QR code type and structure.             |
| SSRVTRA13508 | Failed to process ECC curve: invalid curve type.              | -           | Confirm the ECC curve type is supported.          |
| SSRVTRA13509 | Failed to process encryption: invalid symmetric cipher type.  | -           | Check if the symmetric cipher type is correct.    |
| SSRVTRA13510 | Failed to process encryption: invalid symmetric padding type. | -           | Validate the symmetric padding type used.         |
| SSRVTRA13511 | Failed to register TA certificate.                            | -           | Check TA certificate registration logic.          |
| SSRVTRA13512 | Failed to register TA DID Document.                           | -           | Check TA DID Document registration.               |
| SSRVTRA13513 | TA is already registered.                                     | -           | Avoid duplicate TA registration.                  |

### 1-6. Entity (14000 ~ 14499)
| Error Code   | Error Message                                                             | Description | Action Required                                           |
| ------------ | ------------------------------------------------------------------------- | ----------- | --------------------------------------------------------- |
| SSRVTRA14000 | Failed to find entity: entity is not registered.                          | -           | Verify if the entity is properly registered.              |
| SSRVTRA14001 | Failed to find issuer: issuer is not registered.                          | -           | Confirm the issuer's registration status.                 |
| SSRVTRA14002 | The issuer has not completed registration.                                | -           | Ensure the issuer completes the registration process.     |
| SSRVTRA14003 | The entity has not completed registration.                                | -           | Verify that the entity completes registration.            |
| SSRVTRA14004 | Failed to register entity: entity is already registered.                  | -           | Check if the entity is already registered.                |
| SSRVTRA14005 | Failed to process the 'propose-enroll-entity' API request.                | -           | Check the 'propose-enroll-entity' API process.            |
| SSRVTRA14006 | The provided DID does not match the entity that requested registration.   | -           | Ensure the DID matches the entity's registration request. |
| SSRVTRA14007 | Failed to process the 'request-enroll-entity' API request.                | -           | Check the 'request-enroll-entity' API process.            |
| SSRVTRA14008 | Failed to register quick entity.                                          | -           | Check quick entity registration process.                  |
| SSRVTRA14009 | Failed to register entity: entity name is already registered.             | -           | Avoid duplicate entity names.                             |
| SSRVTRA14010 | Failed to find entity DID Document: o registration request has been made. | -           | Check entity DID Document registration.                   |
| SSRVTRA14011 | Failed to register DID from entity.                                       | -           | Verify entity DID registration logic.                     |
| SSRVTRA14012 | Failed to approve entity DID Document.                                    | -           | Check entity DID Document approval flow.                  |
| SSRVTRA14013 | Failed to delete entity: entity is not deletable.                         | -           | Confirm entity can be deleted.                            |

### 1-7. BlockChain (15000 ~ 15499)
| Error Code   | Error Message                                      | Description | Action Required                                                 |
| ------------ | -------------------------------------------------- | ----------- | --------------------------------------------------------------- |
| SSRVTRA15000 | Failed to initialize blockchain.                   | -           | Check blockchain initialization process.                        |
| SSRVTRA15001 | Failed to register DID Document on the blockchain. | -           | Verify the registration process for DID Document.               |
| SSRVTRA15002 | Failed to retrieve DID document on the blockchain. | -           | Investigate blockchain to retrieve the DID Document.            |
| SSRVTRA15003 | Failed to update DID Document on the blockchain.   | -           | Check the update process of the DID Document on the blockchain. |
| SSRVTRA15004 | Failed to register VC meta on the blockchain.      | -           | Check the VC meta registration process on blockchain.           |
| SSRVTRA15005 | Failed to retrieve VC meta on the blockchain.      | -           | Ensure proper retrieval of VC meta from the blockchain.         |
| SSRVTRA18507 | Failed to update VC status on the blockchain.      | -           | Verify the VC status update process on the blockchain.          |
| SSRVTRA18508 | Failed to remove index on the blockchain.          | -           | Check the index removal process on the blockchain.              |

### 1-8. Other server connection (15500 ~ 15999)
| Error Code   | Error Message                                                           | Description | Action Required                                              |
| ------------ | ----------------------------------------------------------------------- | ----------- | ------------------------------------------------------------ |
| SSRVTRA15500 | Failed to register DID document.                                        | -           | Verify the DID document registration process.                |
| SSRVTRA15501 | Failed to process response: received unknown data from the issuer.      | -           | Check the issuer's response data.                            |
| SSRVTRA15502 | Failed to communicate with issuer: unknown error occurred.              | -           | Check communication channels and logs for errors.            |
| SSRVTRA15503 | Failed to process message: received an invalid message from the issuer. | -           | Verify the message format received from the issuer.          |
| SSRVTRA15504 | Failed to send email.                                                   | -           | Ensure email settings and server configurations are correct. |
| SSRVTRA15505 | Failed to communicate with KYC server: unknown error occurred.          | -           | Check KYC server connectivity.                               |
| SSRVTRA15506 | Failed to ping the URL.                                                 | -           | Verify the URL and network access.                           |


### 1-9. Transaction (16000 ~ 16499)

| Error Code   | Error Message                                                | Description | Action Required                       |
| ------------ | ------------------------------------------------------------ | ----------- | ------------------------------------- |
| SSRVTRA16000 | Failed to find transaction: the transaction does not exist.  | -           | Verify transaction existence.         |
| SSRVTRA16001 | Failed to process transaction: the transaction is not valid. | -           | Check transaction validity.           |
| SSRVTRA16002 | Failed to process transaction: the transaction has expired.  | -           | Verify transaction expiration status. |

### 1-10. ECDH or Proof (16500 ~ 16999)
| Error Code   | Error Message                                         | Description | Action Required                                          |
| ------------ | ----------------------------------------------------- | ----------- | -------------------------------------------------------- |
| SSRVTRA16500 | Failed to generate key.                               | -           | Check the key generation process.                        |
| SSRVTRA16501 | Failed to find ECDH information.                      | -           | Verify if the ECDH information is correct and available. |
| SSRVTRA16502 | Failed to uncompress public key.                      | -           | Check the public key uncompression process.              |
| SSRVTRA16503 | Failed to compress public key.                        | -           | Ensure proper compression of the public key.             |
| SSRVTRA16504 | Failed to generate nonce.                             | -           | Check the nonce generation process.                      |
| SSRVTRA16505 | Failed to generate key pair.                          | -           | Investigate key pair generation process.                 |
| SSRVTRA16506 | Failed to generate session key.                       | -           | Verify the session key generation process.               |
| SSRVTRA16507 | Failed to merge nonce and shared secret.              | -           | Ensure correct merging of nonce and shared secret.       |
| SSRVTRA16508 | Failed to generate initial vector.                    | -           | Check the initial vector generation process.             |
| SSRVTRA16509 | Failed to verify DID Auth.                            | -           | Validate DID Auth verification steps.                    |
| SSRVTRA16510 | Failed to verify signature: the signature is invalid. | -           | Verify the validity of the provided signature.           |
| SSRVTRA16511 | Failed to verify signature.                           | -           | Check the signature verification process.                |
| SSRVTRA16512 | Failed to generate signature.                         | -           | Check the signature generation process.                  |
| SSRVTRA16513 | Failed to process proof: invalid purpose.             | -           | Verify the proof purpose used in the process.            |
| SSRVTRA16514 | Failed to sign response data.                         | -           | Ensure correct response data signing process.            |
| SSRVTRA16515 | Failed to sign response data.                         | -           | Investigate why response data signing failed.            |
| SSRVTRA16516 | Failed to verify DID document key proof.              | -           | Check the verification of the DID document key proof.    |
| SSRVTRA16517 | Failed to add key proof to DID document.              | -           | Check the process for adding key proof to DID document.  |
| SSRVTRA16518 | Failed to extract signature message.                  | -           | Verify the extraction of the signature message.          |
| SSRVTRA16519 | Failed to process client nonce: invalid nonce.        | -           | Ensure the client nonce is valid.                        |
| SSRVTRA16520 | 'authNonce' does not match.                           | -           | Check for a mismatch in 'authNonce'.                     |
| SSRVTRA16521 | Failed to process the 'request-ecdh' API request.     | -           | Check the 'request-ecdh' API request process.            |

### 1-11. User (17000 ~ 17499)
| Error Code    | Error Message                                                  | Description | Action Required                                            |
| ------------- | -------------------------------------------------------------- | ----------- | ---------------------------------------------------------- |
| SSRVTRA17000  | Failed to register user DID: user DID already exists.          | -           | Verify if the user DID already exists before registration. |
| SSRVTRA17001  | Failed to find user DID: user DID not found.                   | -           | Check the existence of the user DID.                       |
| SSRVTRA17002  | Failed to find user: user is not registered.                   | -           | Ensure the user is registered.                             |
| SSRVTRA17003  | Failed to process request: user status is not 'Activated'.     | -           | Verify the user’s status and activate if needed.           |
| SSRVTRA17004  | Failed to find app: app is not registered.                     | -           | Confirm the app is registered.                             |
| SSRVTRA17005  | Failed to authenticate app: app ID does not match.             | -           | Verify the provided app ID.                                |
| SSRVTRA17006  | Failed to authenticate app: invalid app ID.                    | -           | Ensure the app ID is valid.                                |
| SSRVTRA17007  | Failed to find push token.                                     | -           | Verify the existence of the push token.                    |
| SSRVTRA17008  | Failed to process the 'propose-register-user' API request.     | -           | Check the 'propose-register-user' API process.             |
| SSRVTRA17009  | Failed to process the 'request-register-user' API request.     | -           | Check the 'request-register-user' API request.             |
| SSRVTRA17010  | Failed to process the 'request-confirm-user' API request.      | -           | Check the 'request-confirm-user' API request.              |
| SSRVTRA17011  | Failed to process the 'propose-update-diddoc' API request.     | -           | Check the 'propose-update-diddoc' API process.             |
| SSRVTRA17012  | Failed to process the 'request-update-diddoc' API request.     | -           | Check the 'request-update-diddoc' API process.             |
| SSRVTRA17013  | Failed to process the 'confirm-update-diddoc' API request.     | -           | Check the 'confirm-update-diddoc' API request.             |
| SSRVTRA17014  | Failed to process request: user status is not 'Deactivated'.   | -           | Check if the user status is 'Deactivated'.                 |
| SSRVTRA17015  | Failed to process the 'propose-restore-diddoc' API request.    | -           | Check the 'propose-restore-diddoc' API process.            |
| SSRVTRA17016  | Failed to process the 'request-restore-diddoc' API request.    | -           | Check the 'request-restore-diddoc' API process.            |
| SSRVTRA17017  | Failed to process the 'confirm-restore-diddoc' API request.    | -           | Check the 'confirm-restore-diddoc' API process.            |
| SSRVTRA17018  | Failed to process the 'retrieve-kyc' API request.              | -           | Check the 'retrieve-kyc' API process.                      |
| SSRVTRA17019  | Failed to process the 'offer-restore-did-push' API request.    | -           | Check the 'offer-restore-did-push' API process.            |
| SSRVTRA17020  | Failed to process the 'offer-restore-did-email' API request.   | -           | Check the 'offer-restore-did-email' API process.           |
| SSRVTRA17021  | Failed to process the 'update-push-token' API request.         | -           | Check the 'update-push-token' API process.                 |
| SSRVTRA17022  | Failed to process the 'update-diddoc-deactivated' API request. | -           | Check the 'update-diddoc-deactivated' API process.         |
| SSRVTRA117023 | Failed to process the 'update-diddoc-revoked' API request.     | -           | Check the 'update-diddoc-revoked' API process.             |

### 1-12. Wallet (17500 ~ 17999)
| Error Code   | Error Message                                                | Description | Action Required                                            |
| ------------ | ------------------------------------------------------------ | ----------- | ---------------------------------------------------------- |
| SSRVTRA17500 | Wallet Provider has not registered.                          | -           | Ensure the wallet provider is properly registered.         |
| SSRVTRA17501 | Wallet ID already exists.                                    | -           | Check if the wallet ID already exists before registration. |
| SSRVTRA17502 | Failed to find wallet: wallet is not registered.             | -           | Verify if the wallet is registered in the system.          |
| SSRVTRA17503 | Failed to create wallet.                                     | -           | Investigate issues with wallet creation process.           |
| SSRVTRA17504 | Failed to connect to wallet.                                 | -           | Check the connection process to the wallet.                |
| SSRVTRA17505 | Failed to change wallet password.                            | -           | Verify the wallet password change process.                 |
| SSRVTRA17506 | Failed to generate wallet signature.                         | -           | Check the wallet signature generation process.             |
| SSRVTRA17507 | Failed to establish wallet connection.                       | -           | Ensure proper wallet connection setup.                     |
| SSRVTRA17508 | Failed to authenticate wallet: wallet ID does not match.     | -           | Verify the wallet ID used for authentication.              |
| SSRVTRA17509 | Failed to get File wallet manager.                           | -           | Check the retrieval process for the File wallet manager.   |
| SSRVTRA17510 | Failed to process the 'request-register-wallet' API request. | -           | Check the 'request-register-wallet' API request.           |
| SSRVTRA17511 | Failed to create wallet: wallet already exists.              | -           | Avoid duplicate wallet creation.                           |
| SSRVTRA17512 | Failed to create wallet: invalid wallet file path.           | -           | Check wallet file path format.                             |
| SSRVTRA17513 | Failed to generate keys: key already exists.                 | -           | Ensure unique key generation.                              |
| SSRVTRA17514 | Failed to load key element.                                  | -           | Check key loading logic.                                   |


### 1-13. DID Document (18000 ~ 18499)
| Error Code   | Error Message                                                                              | Description | Action Required                                               |
| ------------ | ------------------------------------------------------------------------------------------ | ----------- | ------------------------------------------------------------- |
| SSRVTRA18000 | Failed to retrieve DID Document.                                                           | -           | Check the retrieval process for DID Document.                 |
| SSRVTRA18001 | Entity DID Document registration is required.                                              | -           | Ensure entity DID Document registration is completed.         |
| SSRVTRA18003 | Failed to generate DID document.                                                           | -           | Check the DID document generation process.                    |
| SSRVTRA18004 | Failed to save DID document.                                                               | -           | Verify the DID document saving process.                       |
| SSRVTRA18005 | Failed to retrieve DID document public key.                                                | -           | Check the process for retrieving the DID document public key. |
| SSRVTRA18006 | Failed to process DID document: invalid updated time.                                      | -           | Ensure the updated time in the DID document is correct.       |
| SSRVTRA18007 | Failed to process DID document: invalid context.                                           | -           | Verify the context in the DID document.                       |
| SSRVTRA18008 | Failed to process DID document: invalid document ID.                                       | -           | Check the document ID in the DID document.                    |
| SSRVTRA18009 | Failed to process DID document: invalid controller.                                        | -           | Verify the controller in the DID document.                    |
| SSRVTRA18010 | Failed to find DID Document.                                                               | -           | Ensure the DID Document exists and is accessible.             |
| SSRVTRA18012 | Failed to update DID Document.                                                             | -           | Check the process for updating the DID Document.              |
| SSRVTRA18013 | Failed to delete DID Document.                                                             | -           | Check the process for deleting the DID Document.              |
| SSRVTRA18014 | Failed to process DID document: invalid creation time.                                     | -           | Ensure the creation time in the DID document is valid.        |
| SSRVTRA18015 | Failed to process DID document: invalid deactivated.                                       | -           | Verify the deactivation status in the DID document.           |
| SSRVTRA18016 | Failed to register DID Document.                                                           | -           | Investigate issues with the DID Document registration.        |
| SSRVTRA18017 | Failed to generate Invoked Document.                                                       | -           | Check the Invoked Document generation process.                |
| SSRVTRA18018 | Failed to process request: ID of DID Document does not match the previously requested DID. | -           | Verify that the DID Document ID matches the requested DID.    |
| SSRVTRA18019 | Failed to process DID Document: invalid document.                                          | -           | Check DID Document structure.                                 |
| SSRVTRA18020 | Failed to register DID: DID is already registered.                                         | -           | Avoid duplicate DID registration.                             |

### 1-14. VC (18500 ~ 18999)
| Error Code   | Error Message                                              | Description | Action Required                                                   |
| ------------ | ---------------------------------------------------------- | ----------- | ----------------------------------------------------------------- |
| SSRVTRA18500 | VC ID does not match.                                      | -           | Verify that the VC ID matches the expected ID.                    |
| SSRVTRA18501 | Failed to retrieve VC plan.                                | -           | Check the retrieval process for the VC plan.                      |
| SSRVTRA18502 | Failed to retrieve VC categories.                          | -           | Investigate issues with retrieving VC categories.                 |
| SSRVTRA18503 | Failed to retrieve VC schema.                              | -           | Check the process for retrieving VC schema.                       |
| SSRVTRA18504 | Failed to generate VC meta.                                | -           | Examine the VC meta generation process.                           |
| SSRVTRA18505 | Failed to register VC meta.                                | -           | Verify the VC meta registration process.                          |
| SSRVTRA18506 | Failed to retrieve VC meta.                                | -           | Check the retrieval process for VC meta.                          |
| SSRVTRA18507 | Failed to update VC status.                                | -           | Check the VC status update process.                               |
| SSRVTRA18508 | Failed to extract VC origin data.                          | -           | Verify the extraction process for VC origin data.                 |
| SSRVTRA18509 | Failed to set VC proof.                                    | -           | Check the process for setting VC proof.                           |
| SSRVTRA18510 | Failed to find certificate VC.                             | -           | Ensure the certificate VC exists and is accessible.               |
| SSRVTRA18511 | Invalid certificate VC issuer.                             | -           | Verify the issuer of the certificate VC.                          |
| SSRVTRA18512 | Failed to set claim info.                                  | -           | Investigate issues with setting claim info.                       |
| SSRVTRA18513 | Failed to set VC type.                                     | -           | Check the process for setting VC type.                            |
| SSRVTRA18514 | Failed to encrypt VC data.                                 | -           | Check the encryption process for VC data.                         |
| SSRVTRA18515 | Failed to generate VC.                                     | -           | Verify the VC generation process.                                 |
| SSRVTRA18516 | Failed to find VC meta.                                    | -           | Check the process for finding VC meta.                            |
| SSRVTRA18517 | Failed to parse VC meta.                                   | -           | Check the parsing process for VC meta.                            |
| SSRVTRA18518 | Failed to revoke VC: VC is already revoked.                | -           | Verify if the VC is already revoked before attempting revocation. |
| SSRVTRA18519 | Failed to verify VC.                                       | -           | Examine the VC verification process.                              |
| SSRVTRA18520 | Failed to process the 'propose-issue-vc' API request.      | -           | Check the 'propose-issue-vc' API request process.                 |
| SSRVTRA18521 | Failed to process the 'request-issue-profile' API request. | -           | Check the 'request-issue-profile' API request process.            |
| SSRVTRA18522 | Failed to process the 'request-issue-vc' API request.      | -           | Check the 'request-issue-vc' API request process.                 |
| SSRVTRA18523 | Failed to process the 'confirm-issue-vc' API request.      | -           | Check the 'confirm-issue-vc' API request process.                 |
| SSRVTRA18524 | Failed to process the 'propose-revoke-vc' API request.     | -           | Check the 'propose-revoke-vc' API request process.                |
| SSRVTRA18525 | Failed to process the 'request-revoke-vc' API request.     | -           | Check the 'request-revoke-vc' API request process.                |
| SSRVTRA18526 | Failed to process the 'confirm-revoke-vc' API request.     | -           | Check the 'confirm-revoke-vc' API request process.                |
| SSRVTRA18527 | Failed to process the 'offer-issue-vc-qr' API request.     | -           | Check the 'offer-issue-vc-qr' API request process.                |
| SSRVTRA18528 | Failed to process the 'offer-issue-vc-push' API request.   | -           | Check the 'offer-issue-vc-push' API request process.              |
| SSRVTRA18529 | Failed to process the 'offer-issue-vc-email' API request.  | -           | Check the 'offer-issue-vc-email' API request process.             |
| SSRVTRA18530 | Failed to process the 'get-vc-schema' API request.         | -           | Check the 'get-vc-schema' API request process.                    |
| SSRVTRA18531 | Failed to register VC schema from issuer.                  | -           | Check VC schema registration process.                             |
| SSRVTRA18532 | Failed to register certificate VC schema.                  | -           | Validate certificate VC schema format.                            |
| SSRVTRA18533 | Failed to register VC plan from issuer.                    | -           | Check VC plan registration.                                       |
| SSRVTRA18534 | Failed to process VC schema: invalid schema.               | -           | Ensure VC schema is properly structured.                          |
| SSRVTRA18535 | Failed to process certificate VC: invalid JSON format.     | -           | Verify JSON structure of certificate VC.                          |
| SSRVTRA18536 | Failed to retrieve Credential Schema.                      | -           | Check Credential Schema retrieval logic.                          |
| SSRVTRA18536 | Failed to retrieve Credential Definition.                  | -           | Check Credential Definition retrieval logic.                      |

### 1-15. Token (19000 ~ 19499)
| Error Code   | Error Message                                             | Description | Action Required                                       |
| ------------ | --------------------------------------------------------- | ----------- | ----------------------------------------------------- |
| SSRVTRA19000 | Failed to generate server token.                          | -           | Verify the server token generation process.           |
| SSRVTRA19001 | Failed to encrypt server token data.                      | -           | Check the encryption process for server token data.   |
| SSRVTRA19002 | Failed to process token: the token has expired.           | -           | Ensure the token is valid and not expired.            |
| SSRVTRA19003 | Failed to authenticate: the token provided is invalid.    | -           | Validate the token provided for authentication.       |
| SSRVTRA19004 | Failed to find token: token is not registered.            | -           | Verify if the token is registered and accessible.     |
| SSRVTRA19005 | Failed to process the 'request-create-token' API request. | -           | Check the 'request-create-token' API request process. |


### 1-16. List Provider (19500 ~ 19999)
| Error Code   | Error Message                                            | Description | Action Required                                      |
| ------------ | -------------------------------------------------------- | ----------- | ---------------------------------------------------- |
| SSRVTRA19500 | Failed to process the 'get-vcplan-list' API request.     | -           | Check the 'get-vcplan-list' API request processing.  |
| SSRVTRA19501 | Failed to process the 'get-vcplan' API request.          | -           | Check the 'get-vcplan' API request processing.       |
| SSRVTRA19502 | Failed to process the 'get-allowed-ca-list' API request. | -           | Check the 'get-allowed-ca-list' API request process. |

### 1-17. Notification Provider (20000 ~ 20499)
| Error Code   | Error Message                                   | Description | Action Required                                |
| ------------ | ----------------------------------------------- | ----------- | ---------------------------------------------- |
| SSRVTRA20000 | Failed to process the 'send-email' API request. | -           | Check the 'send-email' API request processing. |
| SSRVTRA20001 | Failed to process the 'send-push' API request.  | -           | Check the 'send-push' API request processing.  |

### 1-18. KYC (20500 ~ 20999)
| Error Code   | Error Message                              | Description | Action Required                 |
| ------------ | ------------------------------------------ | ----------- | ------------------------------- |
| SSRVTRA20500 | Failed to find KYC: KYC is not registered. | -           | Verify KYC registration status. |


### 1-19. Admin (21000 ~ 21499)
| Error Code   | Error Message                                          | Description | Action Required                     |
| ------------ | ------------------------------------------------------ | ----------- | ----------------------------------- |
| SSRVTRA21000 | Failed to find admin: admin is not registered.         | -           | Confirm admin registration.         |
| SSRVTRA21001 | Failed to register admin: admin is already registered. | -           | Avoid duplicate admin registration. |


### 1-99. Other Errors (90000 ~ 99999)

| Error Code   | Error Message                                  | Description | Action Required                     |
| ------------ | ---------------------------------------------- | ----------- | ----------------------------------- |
| SSRVTRA90000 | Failed to find file: requested file not found. | -           | Check file existence.               |
| SSRVTRA90001 | Temporary error code: to be replaced.          | -           | Replace with a specific error code. |
| SSRVTRA90002 | Failed to process file: I/O error occurred.    | -           | Check file I/O process.             |
| SSRVTRA90003 | An unknown server error has occurred.          | -           | Investigate unknown server errors.  |
