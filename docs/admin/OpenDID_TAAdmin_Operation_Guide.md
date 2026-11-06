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

Open DID TA Admin Console Guide
==

- Date: 2026-11-06
- Version: v3.0.2

Revision History
==
| Version | Date       | Changes                                                |
| ------- | ---------- | ------------------------------------------------------ |
| v1.0.0  | 2025-03-31 | Initial version                                        |
| v1.0.1  | 2025-04-24 | Updated formal registration procedure in `3.1.1. TA Registration` |
|         |            | Removed `3.2. Entity Registration` section            |
|         |            | Modified `3.2. Entity Details` section                |
|         |            | Added `3.1.15. User Management` section               |
|         |            | Added `3.1.16. User List` section                     |
|         |            | Added `3.1.17. App List` section                      |
|         |            | Added `3.1.18. Wallet List` section                   |
| v2.0.0  | 2025-05-30 | Added Credential Schema and Credential Definition sections. |
| v3.0.0  | 2026-10-29 | Updated console menus, password policy, and current screen captures. |
| v3.0.1  | 2026-10-30 | Added API Log and Audit API Log guidance. |
| v3.0.2  | 2026-11-06 | Retook the screen examples, expanded the Certification VC, OID4VCI, mDoc, Server Configuration, Password Policy, and Log sections with item tables, and aligned earlier sections with the screens. |
| v3.0.2  | 2026-11-04 | Corrected the registration guidance and the `QUICK REGISTER` prerequisites and behavior in `3.2. Entity Management`. |

Table of Contents
==

- [Open DID TA Admin Console Guide](#open-did-ta-admin-console-guide)
- [Revision History](#revision-history)
- [Table of Contents](#table-of-contents)
- [1. Introduction](#1-introduction)
  - [1.1. Overview](#11-overview)
  - [1.2. Admin Console Definition](#12-admin-console-definition)
- [2. Basic Manual](#2-basic-manual)
  - [2.1. Login](#21-login)
  - [2.2. Main Screen Configuration](#22-main-screen-configuration)
  - [2.3. Menu Configuration](#23-menu-configuration)
    - [2.3.1. TA Unregistered State](#231-ta-unregistered-state)
    - [2.3.2. TA Registered State](#232-ta-registered-state)
  - [2.4. Password Change Management](#24-password-change-management)
- [3. Feature-Specific Detailed Manual](#3-feature-specific-detailed-manual)
  - [3.1. TA Management](#31-ta-management)
    - [3.1.1. TA Registration](#311-ta-registration)
    - [3.1.2. Registered TA Management](#312-registered-ta-management)
  - [3.2. Entity Management](#32-entity-management)
    - [3.2.1. Entity List Inquiry](#321-entity-list-inquiry)
    - [3.2.2. Entity Details](#322-entity-details)
  - [3.3. KYC Settings](#33-kyc-settings)
  - [3.4. API Settings](#34-api-settings)
  - [3.5. Expiration Settings](#35-expiration-settings)
  - [3.6. Key Exchange Policy](#36-key-exchange-policy)
  - [3.7. Notification Provider Settings](#37-notification-provider-settings)
  - [3.8. Email Server Settings](#38-email-server-settings)
  - [3.9. Email Template Settings](#39-email-template-settings)
  - [3.10. Push Server Settings](#310-push-server-settings)
  - [3.11. List Provider Settings](#311-list-provider-settings)
  - [3.12. Allowed CA Management](#312-allowed-ca-management)
    - [3.12.1. Allowed CA List Inquiry](#3121-allowed-ca-list-inquiry)
    - [3.12.2. Allowed CA Registration](#3122-allowed-ca-registration)
    - [3.12.3. Allowed CA Details](#3123-allowed-ca-details)
    - [3.12.4. Allowed CA Modification](#3124-allowed-ca-modification)
  - [3.13. VC Schema Management](#313-vc-schema-management)
    - [3.13.1 VC Schema List Inquiry](#3131-vc-schema-list-inquiry)
    - [3.13.2 VC Schema Details](#3132-vc-schema-details)
  - [3.14. VC Plan Management](#314-vc-plan-management)
    - [3.14.1 VC Plan List Inquiry](#3141-vc-plan-list-inquiry)
    - [3.14.2 VC Plan Details](#3142-vc-plan-details)
  - [3.15. Credential Schema Management](#315-credential-schema-management)
    - [3.15.1 Credential Schema List Inquiry](#3151-credential-schema-list-inquiry)
    - [3.15.2 Credential Schema Details](#3152-credential-schema-details)
  - [3.16. Credential Definition Management](#316-credential-definition-management)
    - [3.16.1 Credential Definition List Inquiry](#3161-credential-definition-list-inquiry)
    - [3.16.2 Credential Definition Details](#3162-credential-definition-details)
  - [3.17. User Management](#317-user-management)
  - [3.18. User List](#318-user-list)
    - [3.18.1. User List Inquiry](#3181-user-list-inquiry)
    - [3.18.2. User Details Information](#3182-user-details-information)
  - [3.19. App List](#319-app-list)
    - [3.19.1. App List Inquiry](#3191-app-list-inquiry)
    - [3.19.2. App Details Information](#3192-app-details-information)
  - [3.20. Wallet List](#320-wallet-list)
    - [3.20.1. Wallet List Inquiry](#3201-wallet-list-inquiry)
    - [3.20.2. Wallet Details Information](#3202-wallet-details-information)
  - [3.21. Admin Management](#321-admin-management)
    - [3.21.1. Admin List Inquiry](#3211-admin-list-inquiry)
    - [3.21.2. Admin Registration](#3212-admin-registration)
  - [3.22. Certification VC Management](#322-certification-vc-management)
    - [3.22.1. Certification VC List](#3221-certification-vc-list)
    - [3.22.2. Certification VC Details](#3222-certification-vc-details)
  - [3.23. OID4VCI Credential Issuers](#323-oid4vci-credential-issuers)
    - [3.23.1. Issuer List](#3231-issuer-list)
    - [3.23.2. Issuer Details and Status Changes](#3232-issuer-details-and-status-changes)
    - [3.23.3. Edit Issuer Information](#3233-edit-issuer-information)
  - [3.24. mDoc Trusted Issuers](#324-mdoc-trusted-issuers)
    - [3.24.1. mDoc Issuer List](#3241-mdoc-issuer-list)
    - [3.24.2. Register an mDoc Issuer](#3242-register-an-mdoc-issuer)
    - [3.24.3. mDoc Issuer Details and Status Changes](#3243-mdoc-issuer-details-and-status-changes)
  - [3.25. Server Configuration](#325-server-configuration)
  - [3.26. Password Policy Settings](#326-password-policy-settings)
  - [3.27. Log Management](#327-log-management)
    - [3.27.1. API Log](#3271-api-log)
    - [3.27.2. Audit API Log](#3272-audit-api-log)

# 1. Introduction

## 1.1. Overview

This document guides the installation and operation of the Open DID TA Admin Console.  
It is structured to provide step-by-step explanations from basic usage to detailed manuals for each function, enabling users to efficiently utilize the console.

Please refer to the [Open DID Installation Guide] for the complete installation guide of OpenDID.

<br/>

## 1.2. Admin Console Definition

The TA Admin Console is a web-based administrator tool for managing TA servers within the Open DID system.  

In the current version, the TA server performs not only its standalone functions but also the roles of Notification Provider and List Provider.  
Therefore, settings for these providers can also be managed together.

The main items that can be configured in the TA Admin Console are as follows:
- TA Provider Settings
  - TA server registration
  - Entity server registration
  - Transaction validity time and key exchange policy settings
- Notification Provider Settings
  - Email server settings
  - Push server settings
- List Provider Settings
  - Allowed CA list settings
  - VC Schema list management
  - VC plan list management
  - Credential Schema list management
  - Credential Definition list management
- Log Management
  - View Agent API calls and administrator API activity

<br/>

# 2. Basic Manual

This chapter guides the basic usage of the Open DID TA Admin Console.

## 2.1. Login

To access the Admin Console, follow these steps:

1. Open a web browser and access the TA Admin Console URL.

   ```
   http://<ta_domain>:<port>
   ```

2. Enter the administrator account email and password on the login screen.
   - Use an administrator account issued for the deployment. Check the deployment settings for initial credentials and change the password after first login.

3. Click the 'Login' button.

<img src="./images/live-sign-in.jpg" width="600"/>

> **Note**:
> Do not include initial account credentials or passwords in operational documentation.

<br/>

## 2.2. Main Screen Configuration

A typical console screen consists of the following elements:

<img src="./images/api-log-api-example.png" width="800"/>

> Screen rendered with example data.

| No. | Area              | Description                                                                                                            |
| --- | ----------------- | ---------------------------------------------------------------------------------------------------------------------- |
| 1   | Header Area       | You can navigate to the password change screen through the `SETTING` button in the upper right corner.                |
| 2   | Content Area      | The title and corresponding content of the currently selected menu are displayed. Screen content changes by each menu. |
| 3   | Side Menu         | Located on the left side of the screen, main menu items are arranged vertically. Selected menus are highlighted, and submenus expand when necessary. |
| 4   | User Info Area    | The email address of the currently logged-in administrator and the 'Sign Out' button are displayed.                   |

<br/>

## 2.3. Menu Configuration

The sidebar menu of the TA Admin Console **differs in screen configuration depending on the TA registration status**.

<br/>

### 2.3.1. TA Unregistered State

In the initial state where the TA server has not yet been registered,
only the `TA Registration` item is displayed in the menu.

<img src="./images/2-2.side-menu-before-registration.png" width="200"/>

### 2.3.2. TA Registered State

Once TA registration is complete, all management functions are activated, and the sidebar menu is configured as follows:

| No. | Menu Name                          | Depth | Description                                                                            |
| --- | ---------------------------------- | ----- | -------------------------------------------------------------------------------------- |
| 1   | **TA Management**                  | 1     | Menu for checking and managing basic TA server information (DID, URL, etc.).          |
| 2   | **Entity Management**              | 1     | Menu for registering and managing Entity servers such as Issuer, Verifier, etc.       |
| 3   | **KYC Settings**                   | 1     | Menu for managing KYC-related settings.                                               |
| 4   | **API Settings**                   | 1     | Parent menu for setting API operation policies.                                       |
| 5   | └ Expiration Settings             | 2 | Configure token and transaction timeouts. |
| 6   | └ Key Exchange Policy             | 2 | Configure the ECDH cipher and padding. |
| 7   | **Notification Provider Settings** | 1 | Email and push configuration. |
| 8   | └ Email Server Settings           | 2 | Configure SMTP settings. |
| 9   | └ Email Template Settings         | 2 | Edit VC issuance and DID recovery templates. |
| 10  | └ Push Server Settings            | 2 | Upload the push service configuration file. |
| 11  | **List Provider Settings**        | 1 | Manage trust and credential lists. |
| 12  | └ Allowed CA Management           | 2 | Manage allowed CAs by wallet. |
| 13  | └ VC Schema Management            | 2 | View VC schemas. |
| 14  | └ VC Plan Management              | 2 | View VC plans. |
| 15  | └ Credential Schema Management    | 2 | View credential schemas. |
| 16  | └ Credential Definition Management | 2 | View credential definitions. |
| 17  | └ Certification VC Management     | 2 | View entity certificate VCs. |
| 18  | └ OID4VCI Credential Issuers      | 2 | View and update OID4VCI issuers. |
| 19  | └ mDoc Trusted Issuers            | 2 | Register and manage trusted mDoc issuers. |
| 20  | **User Management**               | 1 | View user, app, and wallet records. |
| 21  | └ User List                       | 2 | View users. |
| 22  | └ App List                        | 2 | View apps. |
| 23  | └ Wallet List                     | 2 | View wallets. |
| 24  | **Log Management**                | 1 | View API calls and administrator activity. |
| 25  | └ API Log                         | 2 | View Agent API calls. |
| 26  | └ Audit API Log                   | 2 | View administrator API activity. |
| 27  | **Server Configuration**          | 1 | Configure external service API keys. |
| 28  | **Admin Management**              | 1 | Manage administrator accounts. |
| 29  | └ Admin Management                | 2 | View and register administrators. |
| 30  | └ Password Policy Settings        | 2 | Set administrator password policy. |

> Detailed instructions for the new menu items appear in sections 3.22–3.26.

<br/>

## 2.4. Password Change Management

User password change can be performed through the following steps:

1. Click the 'SETTING' button in the header area.
2. Select 'Change Password' from the settings menu.
3. On the password change screen:
   - Enter current password
   - Enter new password
   - Confirm new password
4. Click the 'Save' button to apply changes.

> **Note**: Check the active minimum length, expiration period, and composition rules in [Password Policy Settings](#326-password-policy-settings).

<br/>

# 3. Feature-Specific Detailed Manual

> Management screens after registration show the current sidebar with example data. Actual data varies by environment.

This chapter provides detailed usage instructions for the main functions of the TA Admin Console.

## 3.1. TA Management

TA Management is a function for registering and managing the status of TA servers.  

The TA server is a central component responsible for establishing and operating the trust chain in the Open DID system.  
TA must be registered in the system first before other Entity servers can be properly registered and operated. 

TA registration is performed only once initially,  
and thereafter, the registered status can be checked on the management screen.

<br/>

### 3.1.1. TA Registration

In the initial state where the TA server has not yet been registered in the Open DID system,  
only the `TA Registration` item is displayed in the left menu of the TA Admin Console.  

TA registration proceeds sequentially through a total of 4 steps.

<br/>

**Step 1 - Enter TA Password**

In this step, enter the **registration password** to be used in the final stage of TA registration.  
The password must be entered identically to the password preset in the **Trust Agent server configuration file**.

<img src="./images/3-1-1.ta-registration.png" width="800"/>

| Item                 | Description                                                  |
| -------------------- | ------------------------------------------------------------ |
| **Password**         | Enter the password to be used for TA registration           |
| **Confirm Password** | Re-enter the same value as the password entered above       |
| **NEXT Button**      | Move to the next step.                                       |

<br/>

**Step 2 - Enter TA Info**

This is the step to enter TA information.

<img src="./images/3-1-2.ta-registration.png" width="800"/>

| Item                     | Description                                                                    |
| ------------------------ | ------------------------------------------------------------------------------ |
| **Name**                 | Enter the name of the TA. Example: `TrustAgent` |
| **TA URL**               | TA server call URL. Format: `http://<IP>:8090/tas`. Example on the screen: `https://tas.example.org/tas` |
| **Test Connection Button** | Test server connection with the entered URL                                  |
| **BACK Button**          | Move to the previous step                                                      |
| **NEXT Button**          | Move to the next step                                                          |

<br/>

**Step 3 - Register DID Document**

In this step, generate the TA's DID Document and register it on the blockchain.  
Once registered, the DID Document **cannot be changed or re-registered**.

▶ **Step 3-1: Generate DID Document**

Generate the TA's DID Document.  

<img src="./images/3-1-3.ta-registration.png" width="800"/>

| Item               | Description                                    |
| ------------------ | ---------------------------------------------- |
| **GENERATE Button** | Generate the TA's DID Document.               |

When the DID Document is successfully generated, **Step 3-2 area is automatically displayed on the screen.**

<br/>

▶ **Step 3-2 - Register to Blockchain**

Register the generated DID Document on the blockchain.  

<img src="./images/3-1-4.ta-registration.png" width="800"/>
<img src="./images/3-1-5.ta-registration.png" width="800"/>

| Item               | Description                                              |
| ------------------ | -------------------------------------------------------- |
| **REGISTER Button** | Register the generated DID Document on the blockchain  |
| **BACK Button**    | Move to the previous step (Generate DID Document)       |
| **NEXT Button**    | Move to the next step (Issue Certificate VC)            |

<br/>

**Step 4 - Issue Certificate VC**

In this step, generate and register **TA's Certificate VC** on the blockchain.  
Certificate VC is **a certificate proving that the TA is officially registered in the OpenDID system**.  

In the OpenDID system, TA issues a certificate to itself (self-signed).  
The Certificate VC includes the TA's unique identifier **DN (Distinguished Name)**,  
which must have a unique value within the system.

▶ **Step 4-1 - Generate Certificate VC**

Enter DN and generate Certificate VC.  

<img src="./images/3-1-6.ta-registration.png" width="800"/>

| Item               | Description                                                                      |
| ------------------ | -------------------------------------------------------------------------------- |
| **DN**             | Value that serves as the TA's unique identifier. Example: `cn=TrustAgent,dc=example,dc=org` |
| **GENERATE Button** | Generate Certificate VC based on the entered DN information.                    |

When Certificate VC is generated, **Step 2 area is displayed on the screen.**

<br/>

▶ **Step 4-2 - Register to Blockchain**

Register the generated Certificate VC on the blockchain.  

<img src="./images/3-1-7.ta-registration.png" width="800"/>
<img src="./images/3-1-8.ta-registration.png" width="800"/>

| Item               | Description                                              |
| ------------------ | -------------------------------------------------------- |
| **REGISTER Button** | Register the generated Certificate VC on the blockchain. |
| **BACK Button**    | Move to the previous step.                               |
| **FINISH Button**  | Complete registration and move to the final completion screen. |

<br/>

### 3.1.2. Registered TA Management

Once TA registration is complete, the `TA Management` menu is activated,  
and you can check the registered TA's DID information, status, URL, etc.

<img src="./images/3-2.ta-management.png" width="800"/>

| No. | Item                  | Description                                                                   |
| --- | --------------------- | ----------------------------------------------------------------------------- |
| 1 | **DID**               | TA's unique identifier. Displayed in format like 'did:omn:tas'.              |
| 2 | **Name**              | TA's name.                                                                    |
| 3 | **Status**            | TA's current registration status. <br/>Status types are as follows:<br/>- `DID_DOCUMENT_REQUIRED`: DID Document not yet registered<br/>- `CERTIFICATE_VC_REQUIRED`: DID Document registered but Certificate VC not yet issued<br/>- `COMPLETED`: TA registration completed |
| 4 | **URL**               | TA server's base URL address.                                                 |
| 5 | **Certificate URL**   | URL address where TA's certificate can be verified.                          |
| 6 | **VIEW Button** | Button next to Certificate URL. Shows the certificate VC in a popup. |
| 7 | **Registered At**     | Date and time when TA was registered in Open DID.                           |
| 8 | **VIEW DID DOCUMENT** | Button to check DID Document. When clicked, DID document information registered on blockchain is displayed in popup format. |
| 9 | **DID Document Content** | Content of the DID Document displayed when VIEW DID DOCUMENT button is clicked. Includes TA's DID information, controller, creation time, verification methods, etc. in JSON format. |

<img src="./images/3-2.ta-management-did-document.png" width="800"/>

> The DID Document popup shown when you click `VIEW DID DOCUMENT`.

<br/>

## 3.2. Entity Management

In the `Entity Management` menu, you can register and manage Entity servers such as Issuer, Verifier, CA, Wallet  
that participate in the Open DID system.

> Register Entity servers one by one, following the procedure in `3.2.1. Entity List Inquiry` below.  
> In a test environment, you can register several servers at once with `QUICK REGISTER` after completing its prerequisites.  

When entering the menu, you can check the list of registered Entities in table format,  
and add server information through new registration or batch registration functions.

<img src="./images/3-3.entity-management-main.png" width="800"/>

<br/>

### 3.2.1. Entity List Inquiry

In the `Entity Management` menu, you can register and manage Entity servers such as Issuer, Verifier, CA, Wallet  
that participate in the Open DID system.

Entity server registration generally follows these procedures:

1. Entity Admin generates DID Document and sends registration request to TA Admin.
2. When TA administrator approves the registration request, DID Document is registered on blockchain.
3. Subsequently, Entity Admin requests Certificate VC issuance to TA Server.

---
> **Note:**  
> TA Admin can automate the above process through the `QUICK REGISTER` function.   
> This function automatically registers all Entity DID Documents from TA Admin and issues and delivers Certificate VCs.  
> However, the prerequisites in the `QUICK REGISTER` button description below must be in place.
---

The Entity list is provided in table format, allowing you to check the main information of registered servers at a glance.

| No. | Item                    | Description                                                                |
| --- | ----------------------- | -------------------------------------------------------------------------- |
| 1 | **DELETE Button**       | Delete Entity information.                                                 |
| 2 | **QUICK REGISTER Button** | Function to register all Entities at once for testing purposes.          |
| 3 | **Search** | Searches by Name, DID, Role, or Status. Role is selected from Issuer, Verifier, App Provider, Wallet Provider, and OP Provider. |
| 4 | **DID**                 | Entity's unique DID identifier.                                            |
| 5 | **Name**                | Entity's name. Click to go to detail page.                                |
| 6 | **Role**                | Entity's role (e.g., Issuer, Verifier, Wallet, etc.).                     |
| 7 | **Status**              | Entity's registration status. <br/>Status types are as follows:<br/>- `DID_DOCUMENT_REQUIRED`: DID Document not yet registered<br/>- `CERTIFICATE_VC_REQUIRED`: DID Document registered but Certificate VC not yet issued<br/>- `COMPLETED`: Entity registration completed |
| 8 | **URL**                 | Entity server's base call URL.                                             |
| 9 | **Registered At**       | Date and time when Entity was first registered.                            |
| 10 | **Updated At**          | Date and time when Entity information was last modified.                   |

---

🔸 `DELETE` Button

Delete Entity information.   
However, Entity can only be deleted when its DID Document has not yet been registered on blockchain (DID Document Required state).

<br/>

🔸 `QUICK REGISTER` Button

The `QUICK REGISTER` function is a temporary feature for testing convenience.  
When this button is clicked, the following tasks are automatically performed for the Issuer, Verifier, CA, and Wallet servers:

- Register each Entity server's DID Document  
- Issue membership certificates (VC)  
- Deliver issued membership certificates to the corresponding server

**Prerequisites**

If you installed with the Orchestrator, `Generate All` and the Orchestrator's configuration injection prepare these items. If you installed manually, prepare the following.

| Item | Description |
| ---- | ----------- |
| TA server settings | Set `setup.base-url` (for example, `http://192.168.0.10`) and `setup.path` (the base path of the DID Document files). |
| DID Document files | Place `Issuer/issuer.did`, `Verifier/verifier.did`, `CA/cas.did`, and `Wallet/wallet.did` under `setup.path`. |
| Entity server wallets | Each server's `wallet.file-path` must point to the wallet that created the DID Document above. |
| Entity server ports | Run Issuer on `8091`, Verifier on `8092`, CA on `8094`, and Wallet on `8095`. The registered URL has the form `{setup.base-url}:{port}/{issuer, verifier, cas, wallet}`. |

**Behavior by server state**

| Server state | Behavior |
| ------------ | -------- |
| Not registered yet | Registers the DID Document from the file, then issues and delivers the membership certificate. If the file is missing, an error occurs and registration stops. |
| Already registered (including individual registration) | Keeps the DID Document and issues and delivers a new membership certificate. |

A failed certificate delivery does not stop registration. If delivery failed because a server was not running, start the server and run `QUICK REGISTER` again.

The figure below shows the screen after executing the `QUICK REGISTER` function.  
You can see that registered Issuer, Verifier, CA, Wallet servers have been automatically added to the table.

<img src="./images/3-4.entity-management-after-quick-registeration.png" width="800"/>

<br/>

### 3.2.2. Entity Details

When you click on a specific Entity name in the `Entity Management` screen list,  
you move to a page where you can check detailed information about that server.

On the detail page, you can check main information such as registered Entity's DID, name, role, status, call URL, etc.

<img src="./images/3-6.entity-detail-information.png" width="800"/>

| No. | Item                      | Description                                                                          |
| --- | ------------------------- | ------------------------------------------------------------------------------------ |
| 1 | **DID**                   | Entity's unique DID identifier.                                                      |
| 2 | **VIEW DID DOCUMENT Button** | Shows the DID Document in a popup. |
| 3 | **Name**                  | Entity's name. |
| 4 | **Role**                  | Entity's role (e.g., Issuer, Verifier, Wallet, etc.).                               |
| 5 | **Status**                | Entity's registration status. <br/>Status types are as follows:<br/>- `DID_DOCUMENT_REQUIRED`: DID Document not yet registered<br/>- `CERTIFICATE_VC_REQUIRED`: DID Document registered but Certificate VC not yet issued<br/>- `COMPLETED`: Entity registration completed |
| 6 | **Server URL** | Entity server's base call URL. |
| 7 | **Registered At**         | Date and time when Entity was first registered.                                     |
| 8 | **Updated At**            | Date and time when Entity information was last modified.                            |
| 9 | **BACK Button**           | Close detail screen and return to previous page.                                    |
| 10 | **DID DOC APPROVAL Button** | Approve DID Document registration request. <br/> Only displayed when Status is in DID Document Required state (not yet registered on blockchain). |

---

🔸 `DID DOC APPROVAL` Button

Approve Entity's DID Document registration request.   
When button is clicked, DID Document is registered on blockchain, and Entity's status changes to **CERTIFICATE VC REQUIRED**.

Subsequently, Entity administrator must request Certificate VC issuance to TA server through Entity Admin.

<br/>

## 3.3. KYC Settings

The `KYC Settings` menu is a configuration function for retrieving user's identity information (PII: Personally Identifiable Information)  
from a pre-linked KYC server when issuing user DIDs.

The Open DID system does not provide KYC server functionality itself,  
and since the CA server also serves as a KYC server, CA server information must be entered in this setting.

| No. | Item                     | Description                                                                       |
| --- | ------------------------ | --------------------------------------------------------------------------------- |
| 1 | **Name**                 | Enter the KYC server name (3 to 20 characters). The CA server name is generally used. Example: `cas` |
| 2 | **Type** | KYC verification type. It is fixed to `Transaction-Based` and cannot be changed. |
| 3 | **Server URL**           | Call URL of the CA server used as the KYC server (up to 200 characters). Example: `https://cas.example.org/cas` |
| 4 | **Test Connection Button** | Tests the connection to the entered server URL. The test must pass before saving. |
| 5 | **REGISTER Button**      | Saves the entered KYC information. If it is already registered, the button is named `UPDATE`. |
| 6 | **RESET Button**         | Initialize all entered content.                                                   |

<img src="./images/live-kyc.jpg" width="800"/>

<br/>

## 3.4. API Settings

`API Settings` is a parent menu for configuring API operation policies used by Trust Agent.  
Actual configuration is done in sub-items [3.5. Expiration Settings](#35-expiration-settings) and [3.6. Key Exchange Policy](#36-key-exchange-policy) menus.

<br/>

## 3.5. Expiration Settings

In the `Expiration Settings` menu, you can configure expiration time-related settings used by the TA server.  
Set **token expiration time** and **transaction expiration time**, which play important roles in TA protocol operations.

<img src="./images/live-expiration.jpg" width="800"/>

| No. | Item                          | Description                                                                                                                                                                                                                                                                 |
| --- | ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **Token Timeout (seconds)**       | Set expiration time in seconds for **server tokens** issued by TA. Clients (Entity, App, etc.) must obtain server tokens when using TA's main protocols (e.g., Entity registration, user registration, VC issuance, etc.), and this setting means the validity period of such tokens. Default is **60 seconds**. The allowed range is 10 to 600 seconds. |
| 2   | **Transaction Timeout (seconds)** | Set **transaction-level expiration time**. One protocol consists of multiple API calls, and this item is the validity period for the entire protocol execution time. For example, if DID registration protocol consists of 5 APIs, this means the validity time for this entire flow. Default is **300 seconds**. The allowed range is 60 to 3600 seconds. |
| 3   | **UPDATE Button**                        | Save entered setting values.                                                                                                                                                                                                                                            |
| 4   | **RESET Button**                         | Initialize entered content.                                                                                                                                                                                                                                             |

---
> **Note:** Even without separate settings, Token Timeout defaults to 60 seconds and Transaction Timeout defaults to 300 seconds. 
---

<br/>

## 3.6. Key Exchange Policy

In the `Key Exchange Policy` menu, you can set encryption policies to be used when TA server performs **ECDH key exchange** with external Entities (Clients).  

This setting becomes the foundation for performing **session-level encrypted communication** with clients in major protocols provided by TA (e.g., Entity registration, user registration, VC issuance, etc.).

Most protocols include key exchange APIs, where both sides exchange information to generate encryption keys using ECDH method.  
Through this menu, you can specify **encryption algorithms and padding methods** to be used during key exchange.

<img src="./images/live-exchange.jpg" width="800"/>

| No. | Item             | Description                                                                                                             |
| --- | ---------------- | ----------------------------------------------------------------------------------------------------------------------- |
| 1   | **Cipher Type**  | Select **encryption algorithm** to be used for key exchange. <br>Supported options: `AES-256-CBC`, `AES-128-CBC`, `AES-256-ECB`, `AES-128-ECB` |
| 2   | **Padding Type** | Select **padding method** to be applied to encrypted data. <br>Supported options: `NOPAD`, `PKCS5`                     |
| 3   | **UPDATE Button**  | Save configured encryption policy.                                                                                      |
| 4   | **RESET Button**   | Initialize entered content.                                                                                             |

---
> **Note:** Even without separate settings, Cipher Type defaults to AES-256-CBC and Padding Type defaults to PKCS5. 
---

<br/>

## 3.7. Notification Provider Settings

In the Open DID system, the TA server also performs the role of Notification Provider.  
Therefore, TA Admin Console can also manage settings for Notification Provider together.

`Notification Provider Settings` is a menu for configuring email and push notification functions in the Open DID system.  
Email server, template, and push server configuration functions are included as sub-menus.

---
> **Note:** Configuration methods for each function are explained in detail in the sub-menu items below:  
> - [3.8 Email Server Settings](#38-email-server-settings)  
> - [3.9 Email Template Settings](#39-email-template-settings)  
> - [3.10 Push Server Settings](#310-push-server-settings)
---

<br/>

## 3.8. Email Server Settings

`Email Server Settings` is a menu for configuring outgoing email servers in the Open DID system.  
You can configure the email sending environment by entering SMTP server information, authentication information, SSL settings, timeout options, etc.

Once configuration is complete, you can send test emails to verify that the configured values work properly.  

<img src="./images/live-email-server.jpg" width="800"/>

| No. | Item                         | Description                                                                   |
| --- | ---------------------------- | ----------------------------------------------------------------------------- |
| 1   | **Host**                     | SMTP server host address (up to 200 characters). Example: `smtp.example.org` |
| 2   | **Port**                     | SMTP server port number (1 to 65535). Example: `587` |
| 3   | **User Name**                | Email account used for authentication (4 to 256 characters). |
| 4   | **Password**                 | Password for the email account (8 to 128 characters). |
| 5    | **Sender**                   | Sender email address (6 to 254 characters). |
| 6    | **Enable STARTTLS**          | Whether to use STARTTLS secure transmission.                                   |
| 7    | **Enable SSL**               | Whether to use SSL secure transmission.                                        |
| 8    | **Connection Timeout (seconds)** | Time limit for connection attempts (1 to 300 seconds). |
| 9    | **Read Timeout (seconds)** | Maximum time to wait for a response (1 to 600 seconds). |
| 10   | **Write Timeout (seconds)** | Time limit for sending a request (1 to 600 seconds). |
| 11   | **Turn Off SSL Check (For Testing)**       | Can disable SSL verification for testing purposes. (Not recommended for production environment) |
| 12   | **REGISTER Button**            | Saves the entered email settings. If they are already registered, the button is named `UPDATE`. |
| 13   | **RESET Button**               | Initialize all input fields.                                        |
| 14   | **TEST Button**                | Only works when all settings are properly entered. <br/>When clicked, `Send Test Email` popup appears, allowing you to directly enter email address to receive test mail. <br/>Test mail includes simple confirmation message. |

---
> **Note:**  
> **Turn on exactly one of** `Enable STARTTLS` and `Enable SSL`. You cannot save with both on or both off.  
> Both are methods for secure connection during email transmission, but **STARTTLS upgrades existing connection to encryption**,  
> while **SSL uses encrypted connection from the beginning**, so only one should be selected according to email server settings to avoid conflicts.
---

## 3.9. Email Template Settings

`Email Template Settings` is a menu where you can register and modify email templates sent from TA server in HTML format.  
Each template can be managed individually by separating tabs according to sending purpose.

TA sends emails to users in the following two cases:

- When issuing VC, sending VC issuance request email to users
- During DID recovery, sending DID recovery request email to users

Email templates are in a format that includes **keywords** that can be dynamically replaced in fixed email content.   
When external systems deliver values corresponding to each keyword along with template type,   
TA replaces those values with keywords to generate and send final emails.

This approach was introduced to reduce server load and   
enable more efficient email sending processing when email content is long or   
contains a lot of dynamically changing information.

<img src="./images/3-11.email-template-settings.png" width="800"/>

| No. | Item            | Description                                                                    |
| --- | --------------- | ------------------------------------------------------------------------------ |
| 1   | **Tab Selection** | Manage templates separately according to sending scenarios like VC issuance, DID recovery, etc. |
| 2   | **Keyword**     | Select keywords to add within template.                                        |
| 3   | **Add Button**  | Add keyword to where keyboard pointer is located in Content.                   |
| 4   | **Content**     | Area to enter HTML content of email body.                                     |
| 5   | **UPDATE Button** | Save template.                                                               |
| 6   | **RESET Button** | Return changed content to initial state.                                     |

<br/>

🔸 Available Keyword List

Below is a table organizing keywords available in each template and the meaning of each keyword.  
At sending time, keywords are automatically replaced with values delivered from external sources and inserted into email body.

| Keyword           | Description                                       | Usage Scenario    |
| ----------------- | ------------------------------------------------- | ----------------- |
| `{issuerName}`    | Name of Issuer issuing VC.                       | VC Issuance       |
| `{qrImg}`         | QR code image that user needs to scan.           | VC Issuance, DID Recovery |
| `{vcSchemaName}`  | Name or schema name of VC being issued.          | VC Issuance       |
| `{qrExpiredDate}` | Date and time when QR code expires.              | VC Issuance       |

---
> **Note:** All **keywords** available in each scenario must be included in template body for saving to be possible.  
> For example, in VC issuance scenario, all of `{issuerName}`, `{qrImg}`, `{vcSchemaName}`, `{qrExpiredDate}` must be included.
---

<br/>

## 3.10. Push Server Settings

`Push Server Settings` is a menu for registering FCM server configuration files for mobile app push notifications.  
In this menu, you can upload **Google Firebase Cloud Messaging (FCM)** configuration file (JSON) to  
configure integration with push server.

Notifications sent through Push server are used **when requesting VC issuance to users**.

<img src="./images/live-push.jpg" width="800"/>

| No. | Item                 | Description                                                                                                                                                       |
| --- | -------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **Warning Message**  | When configuration is not complete, warning message is displayed in **red box**. <br/>When configuration is complete, message indicating completion is displayed in **green box**. |
| 2   | **SELECT FILE Button** | Upload configuration JSON file issued from Firebase.                                                                                                             |
| 3   | **REGISTER Button**  | Save push server information based on uploaded configuration file.                                                                                               |
| 4   | **RESET Button**     | Initialize entered content.                                                                                                                                      |

<br/>

## 3.11. List Provider Settings

In the Open DID system, the TA server also performs the role of List Provider.  
List Provider provides list information necessary for each Entity participating in Open DID to perform their roles.  
For example, schema lists for VC issuance, trusted CA lists, VC issuance plans, etc. correspond to this.

`List Provider Settings` is a menu for managing these lists,  
where you can register and modify allowed CA lists, VC schema lists, VC issuance plans, etc.

Sub-menus are as follows:

- [3.12. Allowed CA Management](#312-allowed-ca-management)
- [3.13. VC Schema Management](#313-vc-schema-management)
- [3.14. VC Plan Management](#314-vc-plan-management)
- [3.15. Credential Schema Management](#315-credential-schema-management)
- [3.16. Credential Definition Management](#316-credential-definition-management)
- [3.22. Certification VC Management](#322-certification-vc-management)
- [3.23. OID4VCI Credential Issuers](#323-oid4vci-credential-issuers)
- [3.24. mDoc Trusted Issuers](#324-mdoc-trusted-issuers)

<br/>

## 3.12. Allowed CA Management

Allowed CA Management is a function for managing the list of CAs allowed for use by specific Wallets.  
Wallets are restricted to use VCs only from CAs included in this list.  
That is, the allowed CA list is used as a criterion for determining **trust in CAs that issued VCs**.

TA can register and modify allowed CA lists by Wallet Identifier, and these lists are used by Wallet SDK.

<br/>

### 3.12.1. Allowed CA List Inquiry

When entering the `Allowed CA Management` menu, the allowed CA list by Wallet Identifier is displayed in table format.  
You can manage the list of CAs that each Wallet can trust through register, modify, and delete functions.

<img src="./images/live-allowed-ca.jpg" width="800"/>

| No. | Item                       | Description                                                                 |
| --- | -------------------------- | --------------------------------------------------------------------------- |
| 1   | **REGISTER Button**        | Register new Wallet Identifier and allowed CA list.                        |
| 2   | **UPDATE Button**          | Modify allowed CA list of selected Wallet Identifier.                      |
| 3   | **DELETE Button**          | Delete selected Wallet Identifier item.                                    |
| 4   | **Wallet Identifier**      | Identifier of target Wallet where CA allow list will be applied.           |
| 5   | **Allowed CA List**        | List of CA DIDs that the Wallet can trust.                                 |
| 6   | **Registered At**          | Registration time.                                                          |
| 7   | **Updated At**             | Last modification time.                                                     |

---
> **Note:** Default Wallet SDK and CA identifiers provided by Open DID are  
> automatically registered as follows during initial installation:   
> - **Wallet Identifier**: `org.omnione.did.sdk.wallet`  
> - **CA**: `org.omnione.did.ca`
---

<br/>

### 3.12.2. Allowed CA Registration

When you click the **REGISTER** button on the `Allowed CA Management` screen, you move to the registration screen below

<img src="./images/3-14.allowed-ca-registration.png" width="800"/>

| No. | Item                   | Description                                                           |
| --- | ---------------------- | --------------------------------------------------------------------- |
| 1 | **Wallet Identifier**  | Target Wallet identifier (3 to 50 characters). Example: `org.example.wallet` |
| 2 | **Check Availability** | Check for duplicate Wallet Identifier.                               |
| 3 | **ADD CA Button**      | When clicked, new input row is added to CA list table below.         |
| 4 | **CA** | CA app identifier to allow. Example: `org.example.ca` |
| 5 | **Delete Icon**        | Displayed on right side of each CA input row, can delete that row.   |
| 6 | **REGISTER Button**    | Register entered content.                                             |
| 7 | **RESET Button**       | Initialize entered content.                                           |
| 8 | **CANCEL Button**      | Cancel registration and return to previous page.                     |

<br/>

### 3.12.3. Allowed CA Details

When you click Wallet Identifier in the `Allowed CA Management` list, you move to the detail screen below.

<img src="./images/3-15.allowed-ca-detail.png" width="800"/>

| No. | Item                  | Description                                                 |
| --- | --------------------- | ----------------------------------------------------------- |
| 1   | **Wallet Identifier** | Identifier of Wallet currently viewing detailed information. |
| 2   | **Allowed CA List**   | List of CAs allowed for that Wallet.                       |
| 3   | **BACK Button**       | Return to list screen.                                      |
| 4   | **GO TO EDIT Button** | Move to edit screen where you can modify that item.        |

<br/>

### 3.12.4. Allowed CA Modification

When you **click GO TO EDIT button on detail page of `Allowed CA Management`** or  
**click UPDATE button on list page**, you move to the modification screen below.  

<img src="./images/3-16.allowed-ca-update.png" width="800"/>

| No. | Item                   | Description                                             |
| --- | ---------------------- | ------------------------------------------------------- |
| 1 | **Wallet Identifier**  | Target Wallet identifier (3 to 50 characters). Example: `org.example.wallet` |
| 2 | **Check Availability** | Re-check for duplicate Wallet Identifier.              |
| 3 | **ADD CA Button**      | Add CA identifier to allow.                             |
| 4 | **CA** | CA app identifier to allow. Example: `org.example.ca` |
| 5 | **Delete Icon**        | Delete CA item in that row.                             |
| 6 | **UPDATE Button**      | Save modified content.                                  |
| 7 | **RESET Button**       | Initialize entered content.                             |
| 8 | **CANCEL Button**      | Cancel modification and return to previous page.       |

<br/>

## 3.13. VC Schema Management

When entering the `VC Schema Management` menu, the list of VC (Verifiable Credential) schemas available for issuance in the Open DID system is displayed in table format.  

VC schemas define the structure and content of each Credential,  
and are referenced when user's Wallet verifies the validity of issued VCs.

After VC Schema is created on Issuer Admin page,  
it is sent to TA Admin server for final registration.

VC Schema is registered on TA server, and administrators can view this list or check detailed content.

<br/>

### 3.13.1 VC Schema List Inquiry

When entering `List Provider Settings > VC Schema Management` menu, the list of registered VC schemas is displayed in table format.

<img src="./images/3-17.vc-schema-management-main.png" width="800"/>

| No. | Item              | Description                                                          |
| --- | ----------------- | -------------------------------------------------------------------- |
| 1   | **Title**         | VC schema title. Click to go to detail information page.            |
| 2   | **Description**   | Brief description of VC schema.                                      |
| 3   | **Issuer Name**   | Name of Issuer that issues this schema.                             |
| 4   | **Registered At** | Date and time when VC schema was first registered.                  |
| 5   | **Updated At**    | Date and time when VC schema was last modified.                     |

<br/>

### 3.13.2 VC Schema Details

When you click VC schema title in `VC Schema list`, you move to screen where you can check detailed information as below.

<img src="./images/3-18.vc-schema-detail.png" width="800"/>

| No. | Item                        | Description                                                                   |
| --- | --------------------------- | ----------------------------------------------------------------------------- |
| 1 | **Title**                   | VC schema title.                                                              |
| 2 | **Description**             | Description of this schema.                                                   |
| 3 | **Issuer Name**             | Name of Issuer that registered this VC schema.                               |
| 4 | **Registered At**           | Date and time when VC schema was first registered.                           |
| 5 | **Updated At** | Date and time of the last modification. |
| 6 | **VIEW VC SCHEMA Button**   | Button to check actual JSON structure of this VC schema. Displayed in popup. |
| 7 | **BACK Button**             | Return to list screen.                                                        |

<br/>

## 3.14. VC Plan Management

In the `VC Plan Management` menu, you can check the list of VC Plans available for issuance in the Open DID system.

VC Plan is a configuration that defines policies for how VCs will actually be issued for VC schemas created by Issuer.  
For example, policies such as whether issuance can be started directly from app (`allowUserInit`), whether Issuer can start issuance through QR code or push message (`allowIssuerInit`) are included.

VC Plan is created on Issuer Admin server, and created Plan is sent to TA Admin server for registration.  
Externally, knowing only `vcPlanId` allows checking what VC is issued in what way.

---
> **Note:** VC Plan is utilized in the following ways:
>
> - When user requests VC issuance from Issuer's existing issuance system (e.g., website),  
>   that site can deliver `vcPlanId` to TA server to query related issuance policies and start issuance procedure based on this.
>
> - User app queries VC Plan list from TA server,  
>   and when user selects desired VC, issuance procedure starts according to that Plan.
---

<br/>

### 3.14.1 VC Plan List Inquiry

When entering `VC Plan Management` menu, the list of registered VC Plans is displayed as below.  
Clicking each VC Plan ID moves to screen where you can check detailed information.

<img src="./images/3-19.vc-plan-managemnet.png" width="800"/>

| No. | Item              | Description                                                                 |
| --- | ----------------- | ----------------------------------------------------------------------- |
| 1   | **ID**            | VC Plan unique identifier. Click to go to detail information screen.   |
| 2   | **Name**          | VC Plan name.                                                           |
| 3   | **Description**   | Description of VC Plan.                                                 |
| 4   | **Issuer Name**   | Name of Issuer that created this VC Plan.                              |
| 5   | **Registered At** | Initial registration time.                                              |
| 6   | **Updated At**    | Last modification time.                                                 |

<br/>

### 3.14.2 VC Plan Details

When you click ID in VC Plan list, you move to detail information screen of that VC Plan.

<img src="./images/3-20.vc-plan-detail.png" width="800"/>

| No. | Item                  | Description                                                  |
| --- | --------------------- | ------------------------------------------------------------ |
| 1 | **ID**                | VC Plan unique identifier.                                   |
| 2 | **VIEW VC PLAN Button** | Check VC Plan JSON original text in popup format.         |
| 3 | **Name**              | VC Plan display name.                                        |
| 4 | **Description**       | Brief description of VC Plan.                                |
| 5 | **Issuer Name**       | Name of Issuer that created this Plan.                       |
| 6 | **Registered At**     | Registration date and time.                                  |
| 7 | **Updated At** | Date and time of the last modification. |
| 8 | **BACK Button**       | Return to previous screen.                                   |

<br/>

## 3.15. Credential Schema Management

In the `Credential Schema Management` menu, you can check the list of Credential Schemas available for issuance in the Open DID system.  
Credential Schema is a structure that defines claim names, attributes, etc. for issuing VCs using ZKP method,  
and is connected 1:1 with each Credential Definition.

After Credential Schema is created on Issuer Admin page,  
it is sent to TA Admin server for final registration.

Credential Schema is registered on TA server, and administrators can view this list or check detailed content.

<br/>

### 3.15.1 Credential Schema List Inquiry

When entering `List Provider Settings > Credential Schema Management` menu, the list of registered Credential Schemas is displayed in table format.

<img src="./images/3-29.credential-schema-management.png" width="800"/>

| No. | Item              | Description                                                        |
| --- | ----------------- | ------------------------------------------------------------------ |
| 1   | **Name**          | Credential Schema name. Click to go to detail information.        |
| 2   | **Issuer Name**   | Name of Issuer that registered this schema.                       |
| 3   | **Registered At** | Initial registration time.                                         |
| 4   | **Updated At**    | Last modification time. (Empty if none)                           |

<br/>

### 3.15.2 Credential Schema Details

When you click name (Name) in Credential Schema list, you move to detail information screen.

<img src="./images/3-30.credemtial-schema-detail.png" width="800"/>

| No. | Item                            | Description                                                                          |
| --- | ------------------------------- | ------------------------------------------------------------------------------------ |
| 1 | **Name**                        | Credential Schema name.                                                              |
| 2 | **Credential Schema ID**        | Unique identifier (DID format) of this schema.                                      |
| 3 | **Issuer Name**                 | Name of Issuer that registered this Credential Schema.                              |
| 4 | **Registered At**               | Date and time when Credential Schema was first registered.                          |
| 5 | **Updated At** | Date and time of the last modification. |
| 6 | **VIEW CREDENTIAL SCHEMA Button** | Check Credential Schema JSON original text in popup format.                       |
| 7 | **BACK Button**                 | Return to list screen.                                                               |

---

## 3.16. Credential Definition Management

In the `Credential Definition Management` menu, you can check the list of registered Credential Definitions.

Credential Definition is a definition for actually issuing VCs using ZKP method based on Credential Schema.  
Credential Definition has 1:1 relationship with each Credential Schema and includes metadata necessary for Issuer to create VCs.

<br/>

### 3.16.1 Credential Definition List Inquiry

When entering `List Provider Settings > Credential Definition Management` menu, Credential Definition list is displayed.

<img src="./images/3-31.credential-definition.management.png" width="800"/>

| No. | Item                          | Description                                                                 |
| --- | ----------------------------- | --------------------------------------------------------------------------- |
| 1   | **Credential Definition ID**  | DID identifier of this Credential Definition. Click to go to detail view.  |
| 2   | **Credential Schema ID**      | DID identifier of connected Credential Schema.                             |
| 3   | **Credential Definition Tag** | Tag value assigned to Definition.                                           |
| 4   | **Issuer Name**               | Name of Issuer that registered this Credential Definition.                 |
| 5   | **Registered At**             | Time when Credential Definition was first registered.                       |
| 6   | **Updated At**                | Last modification time. (Empty if none)                                    |

<br/>

### 3.16.2 Credential Definition Details

When you click ID in Credential Definition list, you move to detail information screen.

<img src="./images/3-32.credential-definition-detail.png" width="800"/>

| No. | Item                           | Description                                                                 |
| --- | ------------------------------ | --------------------------------------------------------------------------- |
| 1 | **Credential Definition ID**   | DID format identifier of Credential Definition.                            |
| 2 | **Credential Schema ID**       | DID identifier of connected Credential Schema.                             |
| 3 | **Credential Definition Tag**  | Tag value assigned when creating Definition.                                |
| 4 | **Issuer Name**                | Name of Issuer that registered this Definition.                            |
| 5 | **Registered At**              | Time when Credential Definition was first registered.                       |
| 6 | **Updated At** | Date and time of the last modification. |
| 7 | **VIEW DEFINITION Button**     | Check Credential Definition original text (JSON) in popup.                 |
| 8 | **BACK Button**                | Return to list screen.                                                      |

<br/>

## 3.17. User Management

In the `User Management` menu, you can view information of users registered in the Open DID system.   

Users are registered based on DID, and related information is managed separately in the following three categories:

- User: User identification information (DID, PII, etc.)
- App: User device (App) unique identification information (App ID, Push Token, etc.)
- Wallet: User wallet information (Wallet DID, Wallet ID, etc.)
  
Each item can be viewed individually in separate menus, and sub-menus are as follows:

- [3.18. User List](#318-user-list)
- [3.19. App List](#319-app-list)
- [3.20. Wallet List](#320-wallet-list)

<br/>

## 3.18. User List

`User List` is a menu where you can check DID and PII information of users registered in the Open DID system.

### 3.18.1. User List Inquiry

When entering `User List` menu, the list of registered users is displayed in table format.  
You can search user information by DID or PII criteria through search function.

<img src="./images/3-23.user-list.png" width="800"/>

| No. | Item              | Description                                                            |
| --- | ----------------- | ---------------------------------------------------------------------- |
| 1   | **DID**           | User's unique DID identifier. Click to go to detail screen.           |
| 2   | **PII**           | User's PII (Personal Identifiable Info) value.                        |
| 3   | **Registered At** | Date and time when user was first registered.                         |
| 4   | **Updated At**    | Date when user information was last modified.                         |

<br/>

### 3.18.2. User Details Information

When you click DID in User list, you move to detail information screen of that User.

<img src="./images/3-24.user-detail.png" width="800"/>

| No. | Item              | Description                                        |
| --- | ----------------- | -------------------------------------------------- |
| 1   | **DID**           | User's unique DID identifier.                      |
| 2   | **PII**           | Identification information for user identity verification. |
| 3   | **Status**        | User's current status. <br/>Status types are as follows:<br/>- `ACTIVATED`: Active state<br/>- `DEACTIVATED`: Inactive state<br/>- `REVOKED`: User's DID has been revoked |
| 4   | **Registered At** | Date and time when user was first registered.     |
| 5   | **Updated At**    | Date when user information was last modified.     |
| 6   | **BACK Button**   | Return to list screen.                             |

<br/>

## 3.19. App List

`App List` is a menu where you can check Push Token and status of Apps registered in the Open DID system.

### 3.19.1. App List Inquiry

When entering `App List` menu, the list of Apps registered in Open DID system is displayed in table format.  

<img src="./images/3-25.app-list.png" width="800"/>

| No. | Item              | Description                                                                                                                                    |
| --- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **App ID**        | App's unique ID. Click to go to detail screen.                                                                                                |
| 2   | **Push Token**    | Push token registered by this App.                                                                                                            |
| 3   | **Status**        | App's registration status.<br/>Status types are as follows:<br/>- `ASSIGNED`: Assigned to user<br/>- `CANCELLED`: Registration cancelled    |
| 4   | **Registered At** | Date and time when App was first registered.                                                                                                  |
| 5   | **Updated At**    | Time when App information was last modified.                                                                                                  |

<br/>

### 3.19.2. App Details Information

When you click App ID in App list, you move to detail information screen of that App.

<img src="./images/3-26.app-detail.png" width="800"/>

| No. | Item              | Description                                                                                                                                    |
| --- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | **DID**           | App's unique DID.                                                                                                                              |
| 2   | **Push Token**    | Push token registered by App.                                                                                                                  |
| 3   | **Status**        | App's registration status.<br/>Status types are as follows:<br/>- `ASSIGNED`: Assigned to user<br/>- `CANCELLED`: Registration cancelled    |
| 4   | **Registered At** | Date and time when App was first registered.                                                                                                  |
| 5   | **Updated At**    | Time when App information was last modified.                                                                                                  |
| 6   | **BACK Button**   | Return to list screen.                                                                                                                         |

<br/>

## 3.20. Wallet List

`Wallet List` is a menu for viewing Wallet information (DID, Wallet ID, etc.) registered in the Open DID system.

### 3.20.1. Wallet List Inquiry

When entering `Wallet List` menu, the list of registered Wallets is displayed in table format.

<img src="./images/3-27.wallet-list.png" width="800"/>

| No. | Item              | Description                                                                                                                                                        |
| --- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1   | **DID**           | Wallet's unique DID identifier.                                                                                                                                    |
| 2   | **Wallet ID**     | Wallet identifier.                                                                                                                                                 |
| 3   | **Status**        | Wallet's registration status.<br/>Status types are as follows:<br/>- `CREATED`: Created<br/>- `ASSIGNED`: Assigned to user<br/>- `CANCELLED`: Registration cancelled |
| 4   | **Registered At** | Time when Wallet was first registered.                                                                                                                            |
| 5   | **Cancelled At**  | Time when Wallet was cancelled if applicable.                                                                                                                     |

<br/>

### 3.20.2. Wallet Details Information

When you click DID in Wallet list, you move to detail information screen of that Wallet.

<img src="./images/3-28.wallet-detail.png" width="800"/>

| No. | Item              | Description                                                                                                                                                        |
| --- | ----------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1   | **DID**           | Wallet's unique DID identifier.                                                                                                                                    |
| 2   | **ID**            | Wallet identifier.                                                                                                                                                 |
| 3   | **Status**        | Wallet's registration status.<br/>Status types are as follows:<br/>- `CREATED`: Created<br/>- `ASSIGNED`: Assigned to user<br/>- `CANCELLED`: Registration cancelled |
| 4   | **Registered At** | Time when Wallet was first registered.                                                                                                                            |
| 5   | **BACK Button**   | Return to list screen.                                                                                                                                             |

<br/>

## 3.21. Admin Management

The `Admin Management` menu is a function for managing administrator accounts that can access the TA Admin Console.  

When TA server is installed, the `admin@opendid.omnione.net` account is automatically created with ROOT privileges by default.  
This account is the only ROOT account in the system and cannot be deleted.

Administrator accounts are divided into two privilege types: **ROOT** and **Normal Admin**.  
ROOT account can perform all functions in the `Admin Management` menu, while Normal Admin can only perform general inquiry functions.

---
> **Note:** Currently, the privilege difference between ROOT account and Normal Admin account is  
> only the difference in buttons displayed in the `Admin Management` menu (only Root can REGISTER / DELETE / CHANGE PASSWORD).  
> Access privileges or functional restrictions to other system menus are not yet applied.
---

<br/>

### 3.21.1. Admin List Inquiry

When entering the `Admin Management` menu, the list of registered administrator accounts is displayed in table format.

<img src="./images/live-admin-management.jpg" width="800"/>

| No. | Item                        | Description                                                             |
| --- | --------------------------- | ----------------------------------------------------------------------- |
| 1   | **REGISTER Button**         | Move to registration page where you can register new administrator account. |
| 2   | **DELETE Button**           | Delete selected administrator account. (Only ROOT administrator can)    |
| 3   | **CHANGE PASSWORD Button**  | Change password of selected administrator account.                      |
| 4   | **ID**                      | Email ID of registered administrator account.                           |
| 5   | **Role**                    | Role of administrator account. (e.g., ROOT, Normal Admin, etc.)         |
| 6   | **Registered At**           | Time when account was first registered.                                 |
| 7   | **Updated At**              | Last modification time.                                                 |

<br/>

### 3.21.2. Admin Registration

When you click **REGISTER** button on `Admin Management` screen, you move to registration screen as below.

<img src="./images/3-22.admin-registration.png" width="800"/>

| No. | Item                        | Description                                                                |
| --- | --------------------------- | -------------------------------------------------------------------------- |
| 1   | **ID**                      | ID of the administrator account to register. Use an email address of 3 to 30 characters. Example: `operator@example.org` |
| 2   | **Check Availability Button** | Check if entered ID is not duplicated.                                   |
| 3   | **Role**                    | Select privilege of administrator account to register. (e.g., Normal Admin) |
| 4   | **Password**                | Enter the password used to sign in (8 to 30 characters). If the password policy is on, its rules also apply. |
| 5   | **Re-enter Password**       | Enter password again to check consistency.                                 |
| 6   | **REGISTER Button**         | Register administrator account based on entered information.               |
| 7   | **RESET Button**            | Initialize all input values.                                               |
| 8   | **CANCEL Button**           | Cancel registration and return to previous screen.                        |


<br/>

## 3.22. Certification VC Management

`List Provider Settings > Certification VC Management` shows the certificate VCs issued to Entities. Certificate VCs are issued during Entity registration, so they are not registered from this menu.

<br/>

### 3.22.1. Certification VC List

<img src="./images/certificate-vc-management.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Search** | Searches by Name or DID. |
| 2 | **#** | List number. |
| 3 | **Name** | Entity name. Click to open the detail screen. |
| 4 | **DID** | Entity DID. |
| 5 | **Published At** | Date and time the certificate VC was published. |
| 6 | **Expired At** | Date and time the certificate VC expires. |
| 7 | **Registered At** | Date and time it was added to the list. |

<br/>

### 3.22.2. Certification VC Details

<img src="./images/certificate-vc-detail.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Name** | Entity name. |
| 2 | **DID** | Entity DID. |
| 3 | **Published URL** | URL of the published certificate VC. |
| 4 | **Published At** | Date and time the certificate VC was published. |
| 5 | **Expired At** | Date and time the certificate VC expires. |
| 6 | **Certificate VC / VIEW VC Button** | Shows the certificate VC in a popup. |
| 7 | **Registered At** | Date and time it was added to the list. |
| 8 | **Updated At** | Date and time of the last modification. |
| 9 | **BACK Button** | Returns to the list. |

<br/>

## 3.23. OID4VCI Credential Issuers

`List Provider Settings > OID4VCI Credential Issuers` manages the registration status of OID4VCI credential issuers. An issuer requests registration through `POST /list/api/v1/oid4vci/issuers`, and its status is `REQUESTED` right after the request. This menu has no register button; administrators approve requests or edit issuer information.

<br/>

### 3.23.1. Issuer List

<img src="./images/oid4vci-issuers.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Search** | Searches by Credential Issuer. The status selector at the top right filters the list. |
| 2 | **UPDATE Button** | Opens the edit screen of the selected issuer. |
| 3 | **Credential Issuer** | Issuer identifier. Click to open the detail screen. |
| 4 | **Issuer Metadata URI** | Issuer metadata URI. |
| 5 | **User Initiation URI** | URI where a user starts issuance. |
| 6 | **Status** | One of `REQUESTED`, `ACTIVE`, `REJECTED`, `SUSPENDED`, and `REVOKED`. |
| 7 | **Requested At** | Date and time registration was requested. |
| 8 | **Updated At** | Date and time of the last modification. |

<br/>

### 3.23.2. Issuer Details and Status Changes

<img src="./images/oid4vci-issuer-detail.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Credential Issuer** | Issuer identifier. |
| 2 | **Credential Issuer Metadata URI** | Issuer metadata URI. |
| 3 | **User Initiation URI** | URI where a user starts issuance. |
| 4 | **Current Status** | Current status. |
| 5 | **Registration Requested At** | Date and time registration was requested. |
| 6 | **Last Updated At** | Date and time of the last modification. |
| 7 | **BACK Button** | Returns to the list. |
| 8 | **EDIT Button** | Opens the edit screen. |
| 9 | **Status change buttons** | Only the statuses reachable from the current status are shown. |
| 10 | **Status History** | Shows the change date, previous status, new status, reason, and who made the change. |

| Current status | Allowed next status | Reason |
| --- | --- | --- |
| `REQUESTED` | `ACTIVE`, `REJECTED` | Required for `REJECTED` |
| `ACTIVE` | `SUSPENDED`, `REVOKED` | Required |
| `SUSPENDED` | `ACTIVE`, `REVOKED` | Required for `REVOKED` |
| `REJECTED`, `REVOKED` | None | - |

<br/>

### 3.23.3. Edit Issuer Information

<img src="./images/oid4vci-issuer-edit.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Credential Issuer*** | Issuer identifier. Example: `https://issuer.example.org` |
| 2 | **Credential Issuer Metadata URI*** | Issuer metadata URI. It is filled in with the default path when you enter Credential Issuer, and you can change it. Example: `https://issuer.example.org/.well-known/openid-credential-issuer` |
| 3 | **User Initiation URI*** | URI where a user starts issuance. Example: `https://issuer.example.org/issue/start` |
| 4 | **CANCEL Button** | Cancels and returns to the previous screen. |
| 5 | **SAVE Button** | Saves the changes. |

<br/>

## 3.24. mDoc Trusted Issuers

`List Provider Settings > mDoc Trusted Issuers` registers and manages trusted mDoc issuers.

<br/>

### 3.24.1. mDoc Issuer List

<img src="./images/live-mdoc-issuers.jpg" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Search** | Searches by DID or Issuer name. The status selector at the top right filters the list. |
| 2 | **REGISTER Button** | Opens the registration screen. |
| 3 | **UPDATE Button** | Opens the edit screen of the selected issuer. |
| 4 | **Issuer** | Issuer name. Click to open the detail screen. |
| 5 | **DID** | Issuer DID. |
| 6 | **docTypes** | Allowed document types. |
| 7 | **Status** | One of `ACTIVE`, `SUSPENDED`, and `REVOKED`. |
| 8 | **Registered At** | Date and time of registration. |
| 9 | **Updated At** | Date and time of the last modification. |

<br/>

### 3.24.2. Register an mDoc Issuer

<img src="./images/live-mdoc-register.jpg" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Issuer*** | Select the Issuer to register. Only Entities whose registration is complete (`COMPLETED`) are listed. Issuers already registered as `ACTIVE` or `SUSPENDED` cannot be selected. `REVOKED` issuers can be registered again. |
| 2 | **Issuer metadata URL*** | OID4VCI metadata URL of the Issuer. It is filled in when you select an Issuer, and you can change it. Example: `https://issuer.example.org/.well-known/openid-credential-issuer` |
| 3 | **LOAD DOCTYPES Button** | Loads the document types declared as `mso_mdoc` in the metadata. |
| 4 | **docTypes*** | Check the document types to allow. Only values declared in the metadata can be selected. Example: `org.iso.18013.5.1.mDL` |
| 5 | **CANCEL Button** | Cancels and returns to the previous screen. |
| 6 | **REGISTER Button** | Registers after confirmation. A registered issuer has the status `ACTIVE`. |

<br/>

### 3.24.3. mDoc Issuer Details and Status Changes

<img src="./images/mdoc-issuer-detail.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Issuer** | Issuer name. |
| 2 | **DID** | Issuer DID. |
| 3 | **Issuer metadata URL** | Metadata URL. |
| 4 | **docTypes** | Allowed document types. |
| 5 | **Current Status** | Current status. |
| 6 | **Entity ID** | ID of the linked Entity. |
| 7 | **Registered At** | Date and time of registration. |
| 8 | **Last Updated At** | Date and time of the last modification. |
| 9 | **BACK, EDIT Buttons** | Return to the list or open the edit screen. |
| 10 | **Status change buttons** | Only the statuses reachable from the current status are shown. A reason is required for `SUSPENDED` and `REVOKED`. |
| 11 | **RE-REGISTER Button** | Shown for `REVOKED` issuers. Opens the screen for registering again. |
| 12 | **Status History** | Shows the change date, previous status, new status, reason, and who made the change. |

| Current status | Allowed next status |
| --- | --- |
| `ACTIVE` | `SUSPENDED`, `REVOKED` |
| `SUSPENDED` | `ACTIVE`, `REVOKED` |
| `REVOKED` | None (register again with `RE-REGISTER`) |

<br/>

## 3.25. Server Configuration

`Server Configuration` sets API keys for integrating with external servers.

<img src="./images/server-configuration.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Ledger Service Server** | Enter the API key issued by LSS. It is stored as `API_KEY_LSS`, and the TA sends it in the `X-API-Key` header when calling LSS. |
| 2 | **UPDATE Button** | Saves the entered value. |
| 3 | **RESET Button** | Resets the input before saving. |

> **Caution**: Do not include API key values in screen captures, documents, or sample settings.

<br/>

## 3.26. Password Policy Settings

`Admin Management > Password Policy Settings` manages the password policy for administrator accounts.

<img src="./images/live-password-policy.jpg" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Minimum Length** | Minimum password length, from 6 to 20 characters. The default is 8. |
| 2 | **Password Expiration Period** | Password expiration period, from 30 to 365 days. The default is 365. |
| 3 | **Uppercase Letters (A-Z)** | Requires an uppercase letter. Off by default. |
| 4 | **Numbers (0-9)** | Requires a number. Off by default. |
| 5 | **Special Characters (!@#$%^&*)** | Requires a special character. Off by default. |
| 6 | **UPDATE Button** | Saves the policy. If no policy is registered yet, the button is named `REGISTER`. |
| 7 | **RESET Button** | Reverts to the input before the change. |

<br/>

## 3.27. Log Management

Once the TA is registered, API call records are available under `Log Management` in the sidebar. Both screens show the newest records first, page by page, and the refresh button reloads the list. Click `Called At` to open the detail dialog. Logs are read-only.

<br/>

### 3.27.1. API Log

`Log Management > API Log` (`/logs/api`) shows calls to TA and Notification Agent APIs that use `@ArticleLog`.

<img src="./images/api-log-api-example.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Search** | Searches by `URI` or `Result` (`SUCCESS`, `FAIL`). |
| 2 | **Called At** | Date and time of the call. Click to open the detail dialog. |
| 3 | **Method** | HTTP method. |
| 4 | **URI** | Called URI. |
| 5 | **Status** | HTTP status code. |
| 6 | **Result** | `SUCCESS` or `FAIL`. |
| 7 | **Requester ID** | Requester ID. |

<img src="./images/api-log-api-detail-example.png" width="800"/>

In addition to the list items, the detail dialog shows the following.

| Item | Description |
| --- | --- |
| **Duration Ms** | Processing time in milliseconds. |
| **Client IP** | Client IP address. |
| **User Agent** | User Agent of the request. |
| **Message** | Shown when an error message was recorded. |

<br/>

### 3.27.2. Audit API Log

`Log Management > Audit API Log` (`/logs/audit`) shows administrator operations performed through Admin APIs.

<img src="./images/api-log-audit-example.png" width="800"/>

| No. | Item | Description |
| --- | --- | --- |
| 1 | **Search** | Searches by `Action Type`, `Target Type`, `Result`, or `Requester ID`. `Action Type` is selected from `NONE`, `CREATE`, `UPDATE`, `DELETE`, `LOGIN`, and `LOGOUT`. |
| 2 | **Called At** | Date and time of the call. Click to open the detail dialog. |
| 3 | **Requester ID** | ID of the administrator who performed the operation. |
| 4 | **Action Type** | Operation type. |
| 5 | **Target Type** | Type of the target. |
| 6 | **Target ID** | ID of the target. |
| 7 | **Result** | `SUCCESS` or `FAIL`. |

<img src="./images/api-log-audit-detail-example.png" width="800"/>

In addition to the list items, the detail dialog shows the following.

| Item | Description |
| --- | --- |
| **Client IP** | Client IP address. |
| **Description** | Description of the operation. |
| **User Agent** | User Agent of the request. |
| **Message** | Shown when an error message was recorded. |

> Logs are created from API calls that use `@ArticleLog` on the server. If the result is `FAIL`, check the status code in API Log and the detail message of that log.

[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/main/release-V2.0.0.0/OpenDID_Installation_Guide-V2.0.0.0.md



