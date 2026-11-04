# Changelog


## [Unreleased] (v3.0.0)

### 🚀 New Features
- Certificate VC publishing to the List server, with the published URL returned on entity enrollment
- Certification VC Management in the admin console
- Registering a user with the same PII deactivates the previous user's DID, status, and app
- OID4VCI Credential Issuers and mDoc Trusted Issuers in the List Provider
- LSS API key configuration (Server Configuration), API logs, and audit logs
- Admin ID change, password policy, session management, and list search and refresh

### 🔧 Changes
- Server version 3.0.0 (`did-ta-server-3.0.0.jar`)
- Protocol logic runs through `did-ta-sdk-server` 1.0.0 with provider implementations. The API and error codes are unchanged
- Uses `did-common-sdk-server` 3.0.0 instead of local utilities

### 🐛 Bug Fixes
- Upgraded Node.js to 22.23.4 for the admin console build
- DID status changes go through the storage service, so they work with the `lss` profile
- Fixed the 'no registration' wording of SSRVTRA14010

## [Released] (v2.0.0)

### 🚀 New Features
- Admin Console
  - Credential Schema Management
  - Credential Definition Management
- Support for Besu blockchain


## [Unreleased] (v1.0.1)

### 🚀 New Features
- Admin Console
    - VC Schema Management
    - VC Plan Management
    - Notification Provider Settings
    - KYC Settings
    - List Provider Settings
    - Admin Account Management
- Removed hardcoded logic and sample data from TA Server through Admin Console
  - Removed hardcoded YAML configuration values
  - Removed sample data

## v1.0.0 (2024-10-18)

### 🚀 New Features
- TAS Server
    - TA Registration
    - Entity Registration
    - Wallet Registration
    - User Registration
    - VC Issuance