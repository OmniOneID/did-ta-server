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

- Date: 2025-04-24
- Version: v1.0.1

개정 이력
==
| 버전   | 일자       | 변경 내용                                    |
| ------ | ---------- | -------------------------------------------- |
| v1.0.0 | 2025-03-31 | 최초 작성                                    |
| v1.0.1 | 2025-04-24 | `3.1.1. TA 등록`에서 정식 등록 절차 업데이트 |
|        |            | `3.2. Entity 등록` 장 삭제          |
|        |            | `3.2. Entity 상세` 장 수정          |
|        |            | `3.1.15. User Management` 장 추가          |
|        |            | `3.1.16. User List` 장 추가          |
|        |            | `3.1.17. App List` 장 추가          |
|        |            | `3.1.18. Wallet List` 장 추가          |
| v2.0.0 | 2025-05-30 | `3.16. Credential Schema List`에서 Credential Schema 장 추가 |
|        |            | `3.16. Credential Definition List`에서 Credential Definition 장 추가  |





목차
==

- [Open DID TA Admin Console Guide](#open-did-ta-admin-console-guide)
- [개정 이력](#개정-이력)
- [목차](#목차)
- [1. 소개](#1-소개)
  - [1.1. 개요](#11-개요)
  - [1.2. Admin Console 정의](#12-admin-console-정의)
- [2. 기본 메뉴얼](#2-기본-메뉴얼)
  - [2.1. 로그인](#21-로그인)
  - [2.2. 메인 화면 구성](#22-메인-화면-구성)
  - [2.3. 메뉴 구성](#23-메뉴-구성)
    - [2.3.1. TA 미등록 상태](#231-ta-미등록-상태)
    - [2.3.2. TA 등록 상태](#232-ta-등록-상태)
  - [2.4. 비밀번호 변경 관리](#24-비밀번호-변경-관리)
- [3. 기능별 상세 메뉴얼](#3-기능별-상세-메뉴얼)
  - [3.1. TA Management](#31-ta-management)
    - [3.1.1. TA 등록](#311-ta-등록)
    - [3.1.2. 등록된 TA 관리](#312-등록된-ta-관리)
  - [3.2. Entity Management](#32-entity-management)
    - [3.2.1. Entity 목록 조회](#321-entity-목록-조회)
    - [3.2.2. Entity 상세](#322-entity-상세)
  - [3.3. KYC Settings](#33-kyc-settings)
  - [3.4. API Settings](#34-api-settings)
  - [3.5. Expiration Settings](#35-expiration-settings)
  - [3.6. Key Exchange Policy](#36-key-exchange-policy)
  - [3.7. Notification Provider Settings](#37-notification-provider-settings)
  - [3.8. Email Server Settins](#38-email-server-settins)
  - [3.9. Email Template Settings](#39-email-template-settings)
  - [3.10. Push Server Settings](#310-push-server-settings)
  - [3.11. List Provider Settings](#311-list-provider-settings)
  - [3.12. Allowed CA Management](#312-allowed-ca-management)
    - [3.12.1. Allowed CA 목록 조회](#3121-allowed-ca-목록-조회)
    - [3.12.2. Allowed CA 등록](#3122-allowed-ca-등록)
    - [3.12.3. Allowed CA 상세](#3123-allowed-ca-상세)
    - [3.12.4. Allowed CA 수정](#3124-allowed-ca-수정)
  - [3.13. VC Schema Management](#313-vc-schema-management)
    - [3.13.1 VC Schema 목록 조회](#3131-vc-schema-목록-조회)
    - [3.13.2 VC Schema 상세](#3132-vc-schema-상세)
  - [3.14. VC Plan Management](#314-vc-plan-management)
    - [3.14.1 VC Plan 목록 조회](#3141-vc-plan-목록-조회)
    - [3.14.2 VC Plan 상세](#3142-vc-plan-상세)
  - [3.15. Credential Schema Management](#315-credential-schema-management)
    - [3.15.1 Credential Schema 목록 조회](#3151-credential-schema-목록-조회)
    - [3.15.2 Credential Schema 상세](#3152-credential-schema-상세)
  - [3.16. Credential Definition Management](#316-credential-definition-management)
    - [3.16.1 Credential Definition 목록 조회](#3161-credential-definition-목록-조회)
    - [3.16.2 Credential Definition 상세](#3162-credential-definition-상세)
  - [3.17. User Management](#317-user-management)
  - [3.18. User List](#318-user-list)
    - [3.18.1. User 목록 조회](#3181-user-목록-조회)
    - [3.18.2. User 상세 정보](#3182-user-상세-정보)
  - [3.19. App List](#319-app-list)
    - [3.19.1. App 목록 조회](#3191-app-목록-조회)
    - [3.19.2. App 상세 정보](#3192-app-상세-정보)
  - [3.20. Wallet List](#320-wallet-list)
    - [3.20.1. Wallet 목록 조회](#3201-wallet-목록-조회)
    - [3.20.2. Wallet 상세 정보](#3202-wallet-상세-정보)
  - [3.21. Admin Management](#321-admin-management)
    - [3.21.1. Admin 목록 조회](#3211-admin-목록-조회)
    - [3.21.2. Admin 등록](#3212-admin-등록)

# 1. 소개

## 1.1. 개요

본 문서는 Open DID TA Admin Console의 설치 및 구동 방법을 안내합니다.  
기본 사용법부터 각 기능별 상세 메뉴얼까지 단계적으로 설명하여, 사용자가 콘솔을 효율적으로 활용할 수 있도록 구성되어 있습니다.

OpenDID의 전체 설치에 대한 가이드는 [Open DID Installation Guide]를 참고해 주세요.

<br/>

## 1.2. Admin Console 정의

TA Admin Console은 Open DID 시스템 내에서 TA 서버를 관리하기 위한 웹 기반의 관리자 도구입니다.  

현재 버전에서는 TA 서버가 단독 기능 외에도 Notification 사업자와 List 사업자의 역할도 함께 수행하고 있기 때문에,  
해당 사업자들에 대한 설정도 함께 관리할 수 있습니다.

TA Admin Console에서 설정할 수 있는 주요 항목은 다음과 같습니다:
- TA 사업자 설정
  - TA 서버 등록
  - Entity 서버 등록
  - 트랜잭션 유효 시간 및 키 교환 정책 설정
- Notification 사업자 설정
  - 이메일 서버 설정
  - Push 서버 설정
- List 사업자 설정
  - 허용된 CA 목록 설정
  - VC Schema 목록 관리
  - VC plan 목록 관리
  - Credential Schema 목록 관리
  - Credential Definition 목록 관리

<br/>

# 2. 기본 메뉴얼

이 장에서는 Open DID TA Admin Console의 기본적인 사용 방법에 대해 안내합니다.

## 2.1. 로그인

Admin Console에 접속하려면 다음 단계를 따르세요:

1. 웹 브라우저를 열고 TA Admin Console URL에 접속합니다.

   ```
   http://<ta_domain>:<port>
   ```

2. 로그인 화면에서 관리자 계정의 이메일과 비밀번호를 입력합니다.
   - 기본 관리자 계정: <admin@opendid.omnione.net>
   - 초기 비밀번호: password (최초 로그인 시 변경 필요)

3. '로그인' 버튼을 클릭합니다.

> **참고**:  
> 보안상의 이유로 최초 로그인 시에는 비밀번호 변경이 필요합니다.

<br/>

## 2.2. 메인 화면 구성

로그인 후 표시되는 메인 화면은 다음과 같은 요소들로 구성되어 있습니다:

<img src="./images/2-1.main-screen.png" width="600"/>

| 번호 | 영역             | 설명                                                                                                                                |
| ---- | ---------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| 1    | 헤더 영역        | 우측 상단의 `SETTING` 버튼을 통해 비밀번호 변경 화면으로 이동할 수 있습니다.                                                        |
| 2    | 콘텐츠 영역      | 현재 선택된 메뉴의 제목과 해당 콘텐츠가 표시됩니다. 각 메뉴에 따라 화면 내용이 바뀝니다.                                            |
| 3    | 사이드 메뉴      | 화면 왼쪽에 위치하며, 주요 메뉴 항목들이 세로로 정렬되어 있습니다. 선택한 메뉴는 강조 표시되며, 필요한 경우 하위 메뉴가 펼쳐집니다. |
| 4    | 사용자 정보 영역 | 현재 로그인한 관리자의 이메일 주소와 '로그아웃(Sign Out)' 버튼이 표시됩니다.                                                        |


<br/>

## 2.3. 메뉴 구성

TA Admin Console의 사이드바 메뉴는 **TA 등록 상태에 따라 화면 구성에 차이**가 있습니다.

<br/>

### 2.3.1. TA 미등록 상태

TA 서버가 아직 등록되지 않은 초기 상태에서는  
메뉴에 `TA Registration` 항목만 단독으로 표시됩니다.

<img src="./images/2-2.side-menu-before-registration.png" width="200"/>

### 2.3.2. TA 등록 상태

TA 등록이 완료되면 전체 관리 기능이 활성화되며, 사이드바 메뉴는 다음과 같이 구성됩니다:

<img src="./images/2-3.side-menu-after-registration.png" height="500"/>

| 번호 | 메뉴 명칭                          | Depth | 설명                                                             |
| ---- | ---------------------------------- | ----- | ---------------------------------------------------------------- |
| 1    | **TA Management**                  | 1     | TA 서버의 기본 정보(DID, URL 등)를 확인하고 관리하는 메뉴입니다. |
| 2    | **Entity Management**              | 1     | Issuer, Verifier 등 Entity 서버들을 등록 및 관리하는 메뉴입니다. |
| 3    | **KYC Settings**                   | 1     | KYC 관련 설정을 관리하는 메뉴입니다.                             |
| 4    | **API Settings**                   | 1     | API 동작 정책을 설정하는 상위 메뉴입니다.                        |
| 5    | └ Expiration Settings              | 2     | API 요청의 유효시간(만료 시간)을 설정할 수 있습니다.             |
| 6    | └ Key Exchange Policy              | 2     | 키 교환에 대한 정책을 설정할 수 있습니다.                        |
| 7    | **Notification Provider Settings** | 1     | 알림 제공자(Notification Provider)를 설정하는 상위 메뉴입니다.   |
| 8    | └ Email Server Settings            | 2     | 발신용 이메일 서버 정보를 설정하는 메뉴입니다.                   |
| 9    | └ Email Template Settings          | 2     | 발송되는 이메일의 템플릿을 설정하는 메뉴입니다.                  |
| 10   | └ Push Server Settings             | 2     | 푸시 알림 서버 정보를 설정하는 메뉴입니다.                       |
| 11   | **List Provider Settings**         | 1     | 목록 제공자(List Provider)를 설정하는 상위 메뉴입니다.           |
| 12   | └ Allowed CA Management            | 2     | 허용된 CA(Certificate Authority) 목록을 설정하는 메뉴입니다.     |
| 13   | └ VC Schema Management             | 2     | VC 발급에 사용할 VC 스키마를 관리하는 메뉴입니다.                |
| 14   | └ VC Plan Management               | 2     | VC 발급 플랜을 설정하는 메뉴입니다.                              |
| 15   | **User Management**                | 1     | 사용자의 정보를 관리하는 메뉴입니다.                             |
| 16   | └ User List                        | 2     | Open DID에 등록된 사용자의 목록을 조회하는 메뉴입니다.           |
| 17   | └ App List                         | 2     | Open DID에 등록된 사용자의 App 정보를 조회하는 메뉴입니다.       |
| 18   | └ Wallet List                      | 2     | Open DID에 등록된 사용자의 월렛 정보를 조회하는 메뉴입니다.      |
| 19   | **Admin Management**               | 1     | 관리자의 계정 및 권한을 관리하는 메뉴입니다.                     |

> **참고**:  
> 위 메뉴 구성에 대한 각 기능의 상세 사용법은  
> [3장. 기능별 상세 메뉴얼](#3-기능별-상세-메뉴얼)에서 번호 순서에 따라 설명합니다.

<br/>

## 2.4. 비밀번호 변경 관리

사용자 비밀번호 변경은 다음 단계를 통해 수행할 수 있습니다:

1. 헤더 영역의 'SETTING' 버튼을 클릭합니다.
2. 설정 메뉴에서 '비밀번호 변경'을 선택합니다.
3. 비밀번호 변경 화면에서:
   - 현재 비밀번호 입력
   - 새 비밀번호 입력
   - 새 비밀번호 확인 입력
4. '저장' 버튼을 클릭하여 변경 사항을 적용합니다.

> **참고**: 비밀번호는 8자 이상, 64자 이하의 알파벳 대/소문자, 숫자, 특수문자를 포함해야 합니다.

<br/>

# 3. 기능별 상세 메뉴얼

이 장에서는 TA Admin Console의 주요 기능에 대한 상세 사용 방법을 안내합니다.


## 3.1. TA Management

TA Management는 TA 서버의 등록 및 상태 관리를 위한 기능입니다.  

TA 서버는 Open DID 시스템의 신뢰 체인 구축 및 운영을 담당하는 중심 구성 요소로,  
TA가 시스템에 먼저 등록되어야 다른 Entity 서버들도 정상적으로 등록 및 작동할 수 있습니다. 

TA 등록은 최초 1회만 수행되며,
이후에는 관리 화면에서 등록된 상태를 확인할 수 있습니다.

<br/>

### 3.1.1. TA 등록

TA 서버가 아직 Open DID 시스템에 등록되지 않은 초기 상태에서는,  
TA Admin Console 좌측 메뉴에 `TA Registration` 항목만 표시됩니다.  

TA 등록은 총 4단계의 스텝을 통해 순차적으로 진행됩니다.

<br/>

**Step 1 - Enter TA Password**

이 단계에서는 TA 등록 마지막 단계에서 사용할 **등록용 비밀번호**를 입력합니다.  
비밀번호는 **Trust Agent 서버 설정 파일**에서 사전에 설정된 비밀번호와 동일하게 입력되어야 합니다.

<img src="./images/3-1-1.ta-registration.png" width="600"/>

| 항목                 | 설명                                               |
| -------------------- | -------------------------------------------------- |
| **Password**         | TA 등록에 사용할 비밀번호를 입력합니다             |
| **Confirm Password** | 위에 입력한 비밀번호와 동일한 값을 다시 입력합니다 |
| **NEXT 버튼**        | 다음 단계로 이동합니다.                                |

<br/>

**Step 2 - Enter TA Info**

TA의 정보를 입력하는 단계입니다.

<img src="./images/3-1-2.ta-registration.png" width="600"/>

| 항목                     | 설명                                                                    |
| ------------------------ | ----------------------------------------------------------------------- |
| **Name**                 | TA의 이름을 입력합니다. 예: `tas`                                       |
| **TA URL**               | TA 서버의 호출 URL. 예: `http://<IP>:8090/tas` 형식으로 입력해야 합니다 |
| **Test Connection 버튼** | 입력한 URL로 서버 연결을 테스트합니다                                   |
| **BACK 버튼**            | 이전 단계로 이동합니다                                  |
| **NEXT 버튼**            | 다음 단계로 이동합니다                           |

<br/>

**Step 3 - Register DID Document**

이 단계에서는 TA의 DID Document를 생성하고 블록체인에 등록합니다.  
한 번 등록된 DID Document는 **변경하거나 재등록할 수 없습니다.**

▶ **Step 3-1: Generate DID Document**

TA의 DID Document를 생성합니다.  

<img src="./images/3-1-3.ta-registration.png" width="600"/>

| 항목         | 설명                                                  |
| ------------ | ----------------------------------------------------- |
| **GENERATE 버튼** | TA의 DID Document를 생성합니다.                         |

DID Document가 성공적으로 생성되면, **Step 3-2 영역이 화면에 자동으로 표시됩니다.**

<br/>

▶ **Step 3-2 - Register to Blockchain**

생성된 DID Document를 블록체인에 등록합니다.  

<img src="./images/3-1-4.ta-registration.png" width="600"/>
<img src="./images/3-1-5.ta-registration.png" width="600"/>

| 항목         | 설명                                                           |
| ------------ | ---------------------------------------------------------------- |
| **REGISTER 버튼** | 생성된 DID Document를 블록체인에 등록합니다                   |
| **BACK 버튼**| 이전 단계(Generate DID Document)로 이동합니다                 |
| **NEXT 버튼**| 다음 단계(Issue Certificate VC)로 이동합니다                  |

<br/>

**Step 4 - Issue Certificate VC**

이 단계에서는 **TA의 Certificate VC**를 생성하고 블록체인에 등록합니다.  
Certificate VC는 **TA가 OpenDID 시스템에 정식 등록되었음을 증명하는 인증서**입니다.  

OpenDID 시스템에서 TA는 스스로 자신에게 인증서를 발급(self-signed)합니다.  
Certificate VC에는 TA의 고유 식별자인 **DN(Distinguished Name)**이 포함되어 있으며,  
이는 시스템 내에서 유일한 값을 가져야 합니다.

▶ **Step 4-1 - Generate Certificate VC**

DN을 입력하고 Certificate VC를 생성합니다.  

<img src="./images/3-1-6.ta-registration.png" width="600"/>

| 항목             | 설명                                                                 |
| ---------------- | -------------------------------------------------------------------- |
| **DN**           | TA의 고유 식별자 역할을 하는 값입니다. 예: `cn=TrustAgent,dc=opendid,dc=com`  |
| **GENERATE 버튼**     | 입력한 DN 정보를 기반으로 Certificate VC를 생성합니다.              |

Certificate VC가 생성되면 **Step 2 영역이 화면에 표시됩니다.**

<br/>

▶ **Step 4-2 - Register to Blockchain**

생성된 Certificate VC를 블록체인에 등록합니다.  

<img src="./images/3-1-7.ta-registration.png" width="600"/>
<img src="./images/3-1-8.ta-registration.png" width="600"/>

| 항목              | 설명                                             |
| ----------------- | ------------------------------------------------ |
| **REGISTER 버튼** | 생성된 Certificate VC를 블록체인에 등록합니다.   |
| **BACK 버튼**     | 이전 단계 이동합니다. |
| **FINISH 버튼**   | 등록을 완료하고 최종 완료 화면으로 이동합니다.   |

<br/>


### 3.1.2. 등록된 TA 관리

TA 등록이 완료되면 `TA Management` 메뉴가 활성화되며,  
등록된 TA의 DID 정보, 상태, URL 등을 확인할 수 있습니다.

<img src="./images/3-2.ta-management.png" width="600"/>

| 번호 | 항목                  | 설명                                                                                                                                                   |
| ---- | --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1    | **DID**               | TA의 고유 식별자입니다. 형식은 'did:omn:tas'와 같은 형태로 표시됩니다.                                                                                 |
| 2    | **Name**              | TA의 이름입니다.                                                                                                                                       |
| 3    | **Status**            | TA의 현재 등록 상태를 표시합니다. <br/>Status의 종류는 다음과 같습니다:<br/>- `DID_DOCUMENT_REQUIRED`: DID Document가 아직 등록되지 않은 상태<br/>- `CERTIFICATE_VC_REQUIRED`: DID Document는 등록되었으나 가입증명서(Certificated VC)는 아직 발급되지 않은 상태<br/>- `COMPLETED`: TA 등록이 완료된 상태 |
| 4    | **URL**               | TA 서버의 기본 URL 주소입니다.                                                                                                                         |
| 5    | **Certificate URL**   | TA의 가입증명서를 확인할 수 있는 URL 주소입니다.                                                                                                       |
| 6    | **Registered At**     | TA가 Open DID에 등록된 날짜와 시간을 표시합니다.                                                                                                       |
| 7    | **VIEW DID DOCUMENT** | DID Document를 확인할 수 있는 버튼입니다. 클릭 시 팝업 형태로 블록체인에 등록된 DID 문서 정보가 표시됩니다.                                            |
| 8    | **DID Document 내용** | VIEW DID DOCUMENT 버튼을 클릭했을 때 표시되는 DID Document의 내용입니다. JSON 형식으로 TA의 DID 정보, controller, 생성일시, 검증 방법 등이 포함됩니다. |

<br/>

## 3.2. Entity Management

`Entity Management` 메뉴에서는 Issuer, Verifier, CA, Wallet 등  
Open DID 시스템에 참여하는 Entity 서버들을 등록하고 관리할 수 있습니다.

> 현재 Open DID 시스템에서는 정식 Entity 등록 기능이 완전히 구현되지 않았습니다.  
> 따라서 대부분의 경우, `QUICK REGISTER` 기능을 사용해 서버를 등록하는 것을 권장합니다.  

메뉴에 진입하면 등록된 Entity 목록을 테이블 형태로 확인할 수 있으며,  
신규 등록 또는 일괄 등록 기능을 통해 서버 정보를 추가할 수 있습니다.

<img src="./images/3-3.entity-management-main.png" width="800"/>

<br/>

### 3.2.1. Entity 목록 조회

`Entity Management` 메뉴에서는 Issuer, Verifier, CA, Wallet 등  
Open DID 시스템에 참여하는 Entity 서버들을 등록하고 관리할 수 있습니다.

Entity 서버의 등록은 일반적으로 다음과 같은 절차를 따릅니다:

1. Entity Admin에서 DID Document를 생성하고 TA Admin으로 등록 요청을 보냅니다.
2. TA 관리자가 등록 요청을 승인하면, DID Document가 블록체인에 등록됩니다.
3. 이후 Entity Admin은 TA Server에 Ceritificate VC 발급을 요청합니다.

---
> **참고:**  
> TA Admin에서는 `QUICK REGISTER` 기능을 통해 위 과정을 자동화할 수 있습니다.   
> 해당 기능은 TA Admin에서 모든 Entity의 DID Document를 자동 등록하고, Certificate VC를 발급 및 전달합니다.  
> 단, 모든 서버가 Orchestrator를 통해 실행 중일 때에만 정상 동작합니다.
---

Entity 목록은 테이블 형태로 제공되며, 등록된 서버들의 주요 정보를 한눈에 확인할 수 있습니다.

| 번호 | 항목                    | 설명                                                                |
| ---- | ----------------------- | ------------------------------------------------------------------- |
| 1    | **DELETE 버튼**         | Entity 정보를 삭제합니다.                                           |
| 2    | **QUICK REGISTER 버튼** | 테스트 목적 등으로 모든 Entity를 한 번에 등록할 수 있는 기능입니다. |
| 3    | **DID**                 | Entity의 고유 DID 식별자입니다.                                     |
| 4    | **Name**                | Entity의 이름입니다. 클릭하면 상세 페이지로 이동합니다.             |
| 5    | **Role**                | Entity의 역할(예: Issuer, Verifier, Wallet 등)을 표시합니다.        |
| 6    | **Status**              | Entity의 등록 상태입니다. <br/>Status의 종류는 같습니다:<br/>- `DID_DOCUMENT_REQUIRED`: DID Document가 아직 등록되지 않은 상태<br/>- `CERTIFICATE_VC_REQUIRED`: DID Document는 등록되었으나 가입증명서(Certificate VC)는 아직 발급되지 않은 상태<br/>- `COMPLETED`: Entity 등록이 완료된 상태 |
| 7    | **URL**                 | Entity 서버의 기본 호출 URL입니다.                                  |
| 8    | **Registered At**       | Entity가 최초로 등록된 날짜 및 시간입니다.                          |
| 9    | **Updated At**          | Entity 정보가 마지막으로 수정된 날짜 및 시간입니다.                 |

---

🔸 `DELETE` 버튼

Entity의 정보를 삭제합니다.   
단, Entity의 DID Document가 아직 블록체인에 등록되지 않는 상태(DID Document Required)일 때만 삭제할 수 있습니다.


<br/>

🔸 `QUICK REGISTER` 버튼

`QUICK REGISTER` 기능은 테스트 편의성을 위한 임시 기능입니다.  
해당 버튼을 클릭하면 Orchestrator에서 설치된 모든 Entity 서버에 대해 다음 작업이 자동으로 수행됩니다:

- 각 Entity 서버의 DID Document 등록  
- 가입 증명서(VC) 발급  
- 발급된 가입증명서를 해당 서버에 전달

TA가 가입증명서를 해당 Entity 서버에 전달하기 위해서는, 각 서버가 **사전에 실행된 상태**여야 합니다.   

만약 서버를 구동하지 않은 상태에서 등록이 진행되었다면,   
이후 서버를 실행한 뒤 `QUICK REGISTER`를 다시 실행하면 TA가 **가입 증명서 전달만** 수행합니다.

아래 그림은 `QUICK REGISTER` 기능을 실행 이후의 화면입니다.  
등록된 Issuer, Verifier, CA, Wallet 서버가 테이블에 자동으로 추가된 것을 확인할 수 있습니다.

<img src="./images/3-4.entity-management-after-quick-registeration.png" width="800"/>

<br/>

### 3.2.2. Entity 상세

`Entity Management` 화면의 목록에서 특정 Entity 이름을 클릭하면,  
해당 서버의 상세 정보를 확인할 수 있는 페이지로 이동합니다.

상세 페이지에서는 등록된 Entity의 DID, 이름, 역할, 상태, 호출 URL 등 주요 정보를 확인할 수 있습니다.

<img src="./images/3-6.entity-detail-information.png" width="600"/>

| 번호 | 항목                      | 설명                                                                                                                                          |
| ---- | ------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **DID**                   | Entity의 고유 DID 식별자입니다.                                                                                                               |
| 2    | **Name**                  | Entity의 이름입니다. 클릭하면 상세 페이지로 이동합니다.                                                                                       |
| 3    | **Role**                  | Entity의 역할(예: Issuer, Verifier, Wallet 등)을 표시합니다.                                                                                  |
| 4    | **Status**              | Entity의 등록 상태입니다. <br/>Status의 종류는 같습니다:<br/>- `DID_DOCUMENT_REQUIRED`: DID Document가 아직 등록되지 않은 상태<br/>- `CERTIFICATE_VC_REQUIRED`: DID Document는 등록되었으나 가입증명서(Certificate VC)는 아직 발급되지 않은 상태<br/>- `COMPLETED`: Entity 등록이 완료된 상태 |
| 5    | **URL**                   | Entity 서버의 기본 호출 URL입니다.                                                                                                            |
| 6    | **Registered At**         | Entity가 최초로 등록된 날짜 및 시간입니다.                                                                                                    |
| 7    | **Updated At**            | Entity 정보가 마지막으로 수정된 날짜 및 시간입니다.                                                                                           |
| 8    | **BACK 버튼**             | 상세 화면을 닫고 이전 페이지로 돌아갑니다.                                                                                                    |
| 8    | **DID DOC APPROVAL 버튼** | DID Document 등록 요청을 승인합니다. <br/> Status가 아직 블록체인에 등록되지 않는 상태(DID Document Required)인 경우에만 노출됩니다. |

---

🔸 `DID DOC APPROVAL` 버튼

Entity의 DID Document 등록 요청을 승인합니다.   
버튼을 클릭하면 DID Document이 블록체인에 등록되며, Entity의 상태는 **CERTIFICATE VC REQUIRED**로 변경됩니다.

이후 Entity 관리자는 Entity Admin을 통해 TA 서버에 가입증명서(Certificate VC) 발급을 요청해야 합니다.

<br/>


## 3.3. KYC Settings

`KYC Settings` 메뉴는 사용자의 DID 발급 시 필요한 신원 정보(PII: Personally Identifiable Information)를  
사전에 연동된 KYC 서버로부터 조회하기 위한 설정 기능입니다.

Open DID 시스템은 자체적으로 KYC 서버 기능을 제공하지 않으며,  
CA 서버가 KYC 서버의 역할을 겸임하고 있으므로, 이 설정에서는 CA 서버의 정보를 입력해야 합니다.

| 번호 | 항목                     | 설명                                                                       |
| ---- | ------------------------ | -------------------------------------------------------------------------- |
| 1    | **Name**                 | KYC 서버의 이름을 입력합니다. 일반적으로 CA 서버명을 사용합니다. 예: `cas` |
| 2    | **Server URL**           | KYC 서버로 사용할 CA 서버의 호출 URL입니다. 예: `http://<IP>:8094/cas`     |
| 3    | **Test Connection 버튼** | 입력한 서버 URL과의 연결을 테스트합니다.                                   |
| 4    | **REGISTER 버튼**        | 입력한 KYC 정보를 저장합니다.                                              |
| 5    | **RESET 버튼**           | 입력한 모든 내용을 초기화합니다.                                           |

<img src="./images/3-7.kyc-settings.png" width="600"/>

<br/>

## 3.4. API Settings

`API Settings`는 Trust Agent에서 사용하는 API 동작 정책을 설정하는 상위 메뉴입니다.  
실제 설정은 하위 항목인 [3.5. Expiration Settings](#35-expiration-settings)와 [3.6. Key Exchange Policy](#36-key-exchange-policy) 메뉴에서 진행됩니다.

<br/>

## 3.5. Expiration Settings

`Expiration Settings` 메뉴에서는 TA 서버에서 사용하는 만료 시간 관련 설정을 구성할 수 있습니다.  
**토큰 만료 시간**과 **트랜잭션 만료 시간**을 설정하며, 이는 TA의 프로토콜 동작에 중요한 역할을 합니다.

<img src="./images/3-8.expiration-settings.png" width="600"/>

| 번호 | 항목                          | 설명                                                                                                                                                                                                                                                                                    |
| ---- | ----------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **Token Timeout (seconds)**       | TA가 발급하는 **서버 토큰**의 만료 시간을 초 단위로 설정합니다. 클라이언트(Entity, App 등)는 TA의 주요 프로토콜(예: Entity 등록, 사용자 등록, VC 발급 등)을 사용할 때 반드시 서버 토큰을 발급받아야 하며, 이 설정은 해당 토큰의 유효 기간을 의미합니다. 기본값은 **60초**입니다.        |
| 2    | **Transaction Timeout (seconds)** | **트랜잭션 단위의 만료 시간**을 설정합니다. 하나의 프로토콜은 여러 개의 API 호출로 구성되며, 이 항목은 전체 프로토콜 수행 시간에 대한 유효 기간입니다. 예를 들어, DID 등록 프로토콜이 5개의 API로 구성되어 있다면, 이 전체 흐름에 대한 유효시간을 의미합니다. 기본값은 **300초**입니다. |
| 3    | **UPDATE 버튼**                        | 입력한 설정 값을 저장합니다.                                                                                                                                                                                                                                                            |
| 4    | **RESET 버튼**                         | 입력한 내용을 초기화합니다.                                                                                                                                                                                                                                                             |

---
> **참고:** 별도의 설정이 없더라도, Token Timeout은 60초, Transaction Timeout은 300초로 기본 적용됩니다. 
---

<br/>

## 3.6. Key Exchange Policy

`Key Exchange Policy` 메뉴에서는 TA 서버가 외부 Entity(Client)와의 **ECDH 키 교환** 시 사용할 암호화 정책을 설정할 수 있습니다.  

이 설정은 TA가 제공하는 주요 프로토콜(예: Entity 등록, 사용자 등록, VC 발급 등)에서  
클라이언트와 **세션 단위의 암호화 통신**을 수행하기 위한 기반이 됩니다.

대부분의 프로토콜에는 키 교환 API가 포함되어 있으며, 해당 API에서는 양측이 ECDH 방식으로 암호 키를 생성하기 위한 정보를 주고받습니다.  
이 메뉴를 통해 키교환시 사용할 **암호화 알고리즘과 패딩 방식**을 지정할 수 있습니다.

<img src="./images/3-9.key-exchange-policy.png" width="600"/>

| 번호 | 항목             | 설명                                                                                                                             |
| ---- | ---------------- | -------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **Cipher Type**  | 키 교환에 사용할 **암호화 알고리즘**을 선택합니다. <br>지원되는 옵션: `AES-256-CBC`, `AES-128-CBC`, `AES-256-ECB`, `AES-128-ECB` |
| 2    | **Padding Type** | 암호화된 데이터에 적용할 **패딩(Padding) 방식**을 선택합니다. <br>지원되는 옵션: `NOPAD`, `PKCS5`                                |
| 3    | **UPDATE 버튼**  | 설정한 암호화 정책을 저장합니다.                                                                                                 |
| 4    | **RESET 버튼**   | 입력한 내용을 초기화합니다.                                                                                                      |

---
> **참고:** 별도의 설정이 없더라도, Cipher Type은 AES-256-CBC초, Padding Type은 PKCS5로 기본 적용됩니다. 
---

<br/>

## 3.7. Notification Provider Settings

Open DID 시스템에서 TA 서버는 Notification 사업자의 역할도 함께 수행합니다.  
따라서 TA Admin Console에서는 Notification 사업자에 대한 설정 또한 함께 관리할 수 있습니다.

`Notification Provider Settings`는 Open DID 시스템에서 이메일 및 푸시 알림 기능을 설정하는 메뉴입니다.  
이메일 서버, 템플릿, 푸시 서버 설정 기능이 하위 메뉴로 포함되어 있습니다.

---
> **참고:** 각 기능의 설정 방법은 하위 메뉴별로 아래 항목에서 자세히 설명합니다:  
> - [3.8 Email Server Settings](#38-email-server-settings)  
> - [3.9 Email Template Settings](#39-email-template-settings)  
> - [3.10 Push Server Settings](#310-push-server-settings)
---

<br/>

## 3.8. Email Server Settins

`Email Server Settings`는 Open DID 시스템에서 발신용 이메일 서버를 설정하는 메뉴입니다.  
SMTP 서버 정보, 인증 정보, SSL 설정, 타임아웃 옵션 등을 입력하여 메일 발송 환경을 구성할 수 있습니다.

설정이 완료되면 테스트 이메일을 발송하여 설정 값이 정상적으로 작동하는지 확인할 수 있습니다.  

<img src="./images/3-10.email-server-settings.png" width="600"/>

| 번호 | 항목                         | 설명                                                                  |
| ---- | ---------------------------- | --------------------------------------------------------------------- |
| 1    | **Host**                     | SMTP 서버의 호스트 주소입니다.                                        |
| 2    | **Port**                     | SMTP 서버의 포트 번호입니다.                                          |
| 3    | **User Name**                | 인증에 사용할 이메일 계정입니다.                                      |
| 4    | **Password**                 | 이메일 계정의 비밀번호입니다.                                         |
| 5    | **Sender**                   | 발신자 이메일 주소입니다.                                             |
| 6    | **Enable STARTTLS**          | STARTTLS 보안 전송 사용 여부입니다.                                   |
| 7    | **Enable SSL**               | SSL 보안 전송 사용 여부입니다.                                        |
| 8    | **Connection Timeout (sec)** | 연결 시도에 대한 제한 시간입니다.                                     |
| 9    | **Read Timeout (sec)**       | 응답을 기다리는 최대 시간입니다.                                      |
| 10   | **Write Timeout (sec)**      | 요청 전송에 대한 제한 시간입니다.                                     |
| 11   | **Turn Off SSL Check**       | 테스트 목적으로 SSL 검증을 비활성화할 수 있습니다. (운영 환경 비권장) |
| 12   | **REGISTER 버튼**            | 입력한 이메일 설정 정보를 저장합니다.                                 |
| 13   | **RESET 버튼**               | 모든 입력 필드를 초기화합니다.                                        |
| 14   | **TEST 버튼**                | 모든 설정이 정상적으로 입력된 경우에만 작동합니다. <br/>클릭 시 `Send Test Email` 팝업이 나타나며, 테스트 메일을 수신할 이메일 주소를 직접 입력할 수 있습니다. <br/>테스트 메일에는 간단한 확인용 메시지가 포함되어 전송됩니다. |

---
> **참고:**  
> `Enable STARTTLS`와 `Enable SSL`은 **동시에 활성화할 수 없습니다.**      
> 둘 다 이메일 전송 시 보안 연결을 위한 방식이지만, **STARTTLS는 기존 연결을 암호화로 업그레이드**하는 방식이고,  
> **SSL은 초기부터 암호화된 연결을 사용하는 방식**이므로, 이메일 서버 설정에 따라 하나만 선택해야 충돌을 방지할 수 있습니다.
---

<br/>

## 3.9. Email Template Settings

`Email Template Settings`는 TA 서버에서 발송하는 이메일의 템플릿을 HTML 형식으로 등록하고 수정할 수 있는 메뉴입니다.  
발송 용도에 따라 탭을 구분하여 각 템플릿을 개별 관리할 수 있습니다.

TA는 다음 두 가지 경우에 사용자에게 이메일을 발송합니다:

- VC 발급 시, 사용자에게 VC 발급 요청 메일을 전송하는 경우
- DID 복구시, 사용자에게 DID 복구 요청 메일을 전송하는 경우

---
> **참고:**  
> VC 발급 이메일 전송 기능은 데모 서버의 VC 발급 페이지에서 확인 가능합니다.   
> DID 복구 관련 기능은 아직 데모 서버에서 제공되지 않으며, 추후 제공될 예정입니다.
---

이메일 템플릿은 고정된 이메일 본문에 동적으로 치환할 수 있는 **키워드**를 포함한 형식입니다.   
외부 시스템이 템플릿 종류와 함께 각 키워드에 대응하는 값을 전달하면,   
TA는 해당 값을 키워드에 치환하여 최종 이메일을 생성하고 발송합니다.

이러한 방식은 이메일 본문의 내용이 길거나   
동적으로 변경되는 정보가 많은 경우 서버의 부하를 줄이고,   
보다 효율적인 메일 발송 처리를 가능하게 하기 위해 도입되었습니다.

<img src="./images/3-11.email-template-settings.png" width="600"/>

| 번호 | 항목            | 설명                                                                    |
| ---- | --------------- | ----------------------------------------------------------------------- |
| 1    | **탭 선택**     | VC 발급, DID 복구 등 발송 시나리오에 따라 템플릿을 분리하여 관리합니다. |
| 2    | **Keyword**     | 템플릿 내에 추가할 키워드를 선택합니다.                                 |
| 3    | **Add 버튼**    | Content 안의 키보드 포인터가 있는 곳으로 키워드를 추가합니다.           |
| 4    | **Content**     | 이메일 본문의 HTML 내용을 입력하는 영역입니다.                          |
| 5    | **UPDATE 버튼** | 템플릿을 저장합니다.                                                    |
| 6    | **RESET 버튼**  | 변경한 내용을 초기 상태로 되돌립니다.                                   |

<br/>

🔸 사용 가능한 키워드 목록

아래는 각 템플릿에서 사용 가능한 키워드와 해당 키워드가 의미하는 내용을 정리한 표입니다.  
발송 시점에 키워드는 외부에서 전달된 값으로 자동 치환되어 메일 본문에 삽입됩니다.

| 키워드            | 설명                                       | 사용 시나리오     |
| ----------------- | ------------------------------------------ | ----------------- |
| `{issuerName}`    | VC를 발급하는 Issuer의 이름입니다.         | VC 발급           |
| `{qrImg}`         | 사용자가 스캔해야 할 QR 코드 이미지입니다. | VC 발급, DID 복구 |
| `{vcSchemaName}`  | 발급되는 VC의 이름 또는 스키마 명칭입니다. | VC 발급           |
| `{qrExpiredDate}` | QR 코드가 만료되는 날짜 및 시간입니다.     | VC 발급           |

---
> **참고:** 각 시나리오에서 사용할 수 있는 **모든 키워드**를 템플릿 본문에 포함해야만 저장이 가능합니다.  
> 예를 들어, VC 발급 시나리오에서는 `{issuerName}`, `{qrImg}`, `{vcSchemaName}`, `{qrExpiredDate}`를 모두 포함해야 합니다.
---

<br/>

## 3.10. Push Server Settings

`Push Server Settings`는 모바일 앱 푸시 알림을 위한 FCM 서버 설정 파일을 등록하는 메뉴입니다.  
이 메뉴에서는 **Google Firebase Cloud Messaging(FCM)** 설정 파일(JSON)을 업로드하여  
푸시 서버와의 연동을 설정할 수 있습니다.

Push 서버를 통해 발송되는 알림은 **사용자에게 VC 발급 요청 시** 사용됩니다.

<img src="./images/3-12.push-server-settings.png" width="600"/>

| 번호 | 항목                 | 설명                                                                                                                                                       |
| ---- | -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **경고 메시지**      | 설정이 완료되지 않은 경우 **붉은색 박스**에 경고 메시지가 표시됩니다. <br/>설정이 완료된 경우에는 **녹색 박스**에 설정이 완료되었다는 메시지가 표시됩니다. |
| 2    | **SELECT FILE 버튼** | Firebase에서 발급받은 설정 JSON 파일을 업로드합니다.                                                                                                       |
| 3    | **REGISTER 버튼**    | 업로드한 설정 파일을 기반으로 푸시 서버 정보를 저장합니다.                                                                                                 |
| 4    | **RESET 버튼**       | 입력한 내용을 초기화합니다.                                                                                                                                |

<br/>

## 3.11. List Provider Settings

Open DID 시스템에서 TA 서버는 List 사업자의 역할도 함께 수행합니다.  
List 사업자는 Open DID에 참여하는 각 Entity가 역할을 수행하는 데 필요한 목록 정보를 제공합니다.  
예를 들어 VC를 발급할 수 있는 스키마 목록, 신뢰할 수 있는 CA 목록, VC 발급 플랜 등이 이에 해당합니다.

`List Provider Settings`는 이러한 목록들을 관리하기 위한 메뉴로,  
허용된 CA 목록, VC 스키마 목록, VC 발급 플랜 등을 등록 및 수정할 수 있습니다.

하위 메뉴는 다음과 같습니다:

- [3.12. Allowed CA Management](#312-allowed-ca-management)
- [3.13. VC Schema Management](#313-vc-schema-management)
- [3.14. VC Plan Management](#314-vc-plan-management)

<br/>

## 3.12. Allowed CA Management

Allowed CA Management는 특정 Wallet에서 사용할 수 있도록 허용된 CA 목록을 관리하는 기능입니다.  
Wallet은 이 목록에 포함된 CA만 VC를 사용할 수 있도록 제한합니다.  
즉, 허용된 CA 목록은 VC를 **발급한 CA에 대한 신뢰 여부**를 판단하기 위한 기준으로 사용됩니다.

TA는 Wallet Identifier별로 허용된 CA 목록을 등록 및 수정할 수 있으며, 해당 목록은 Wallet SDK에서 사용됩니다.

<br/>

### 3.12.1. Allowed CA 목록 조회

`Allowed CA Management` 메뉴에 진입하면, Wallet Identifier별로 허용된 CA 목록이 테이블 형태로 표시됩니다.  
등록, 수정, 삭제 기능을 통해 각 Wallet이 신뢰할 수 있는 CA 목록을 관리할 수 있습니다.

<img src="./images/3-13.allowed-ca-management-main.png" width="800"/>

| 번호 | 항목                       | 설명                                                                 |
| ---- | -------------------------- | -------------------------------------------------------------------- |
| 1    | **REGISTER 버튼**         | 새로운 Wallet Identifier와 허용된 CA 목록을 등록할 수 있습니다.       |
| 2    | **UPDATE 버튼**           | 선택한 Wallet Identifier의 허용 CA 목록을 수정할 수 있습니다.         |
| 3    | **DELETE 버튼**           | 선택한 Wallet Identifier 항목을 삭제할 수 있습니다.                   |
| 4    | **Wallet Identifier**     | CA 허용 목록이 적용될 대상 Wallet의 식별자입니다.                     |
| 5    | **Allowed CA List**       | 해당 Wallet이 신뢰할 수 있는 CA들의 DID 목록입니다.                   |
| 6    | **Registered At**         | 등록된 일시입니다.                                                    |
| 7    | **Updated At**            | 마지막으로 수정된 일시입니다.   


---
> **참고:** Open DID에서 제공하는 기본 Wallet SDK와 CA에 해당하는 식별자는  
> 초기 설치 시 자동으로 아래와 같이 등록되어 있습니다:   
> - **Wallet Identifier**: `org.omnione.did.sdk.wallet`  
> - **CA**: `org.omnione.did.ca`
---

<br/>

### 3.12.2. Allowed CA 등록

`Allowed CA Management` 화면에서 **REGISTER** 버튼을 클릭하면 아래와 같은 등록 화면으로 이동합니다

<img src="./images/3-14.allowed-ca-registration.png" width="600"/>

| 번호 | 항목                   | 설명                                                           |
| ---- | ---------------------- | -------------------------------------------------------------- |
| 1    | **Wallet Identifier**  | 대상 Wallet의 식별자입니다.                                    |
| 2    | **Check Availability** | Wallet Identifier의 중복 여부를 확인합니다.                    |
| 3    | **ADD CA 버튼**        | 클릭 시 아래 CA 목록 테이블에 새로운 입력 행이 추가됩니다.     |
| 4    | **삭제 아이콘**        | 각 CA 입력 행 오른쪽에 표시되며, 해당 행을 삭제할 수 있습니다. |
| 5    | **REGISTER 버튼**      | 입력한 내용을 등록합니다.                                      |
| 6    | **RESET 버튼**         | 입력한 내용을 초기화합니다.                                    |
| 7    | **CANCEL 버튼**        | 등록을 취소하고 이전 페이지로 돌아갑니다.                      |


<br/>

### 3.12.3. Allowed CA 상세

`Allowed CA Management` 목록에서 Wallet Identifier를 클릭하면 아래와 같은 상세 화면으로 이동합니다.

<img src="./images/3-15.allowed-ca-detail.png" width="600"/>

| 번호 | 항목                  | 설명                                                 |
| ---- | --------------------- | ---------------------------------------------------- |
| 1    | **Wallet Identifier** | 현재 상세 정보를 조회 중인 Wallet의 식별자입니다.    |
| 2    | **Allowed CA List**   | 해당 Wallet에 허용된 CA의 목록입니다.                |
| 3    | **BACK 버튼**         | 목록 화면으로 돌아갑니다.                            |
| 4    | **GO TO EDIT 버튼**   | 해당 항목을 수정할 수 있는 편집 화면으로 이동합니다. |

<br/>

### 3.12.4. Allowed CA 수정

`Allowed CA Management`의 **상세 페이지에서 GO TO EDIT 버튼을 클릭하거나**,  
**목록 페이지에서 UPDATE 버튼을 클릭하면** 아래와 같은 수정 화면으로 이동합니다.  

<img src="./images/3-16.allowed-ca-update.png" width="600"/>

| 번호 | 항목                   | 설명                                             |
| ---- | ---------------------- | ------------------------------------------------ |
| 1    | **Wallet Identifier**  | 수정할 대상 Wallet의 식별자입니다.               |
| 2    | **Check Availability** | Wallet Identifier의 중복 여부를 다시 확인합니다. |
| 3    | **ADD CA 버튼**        | 허용할 CA 식별자를 추가할 수 있습니다.           |
| 4    | **삭제 아이콘**        | 해당 행의 CA 항목을 삭제합니다.                  |
| 5    | **UPDATE 버튼**        | 수정된 내용을 저장합니다.                        |
| 6    | **RESET 버튼**         | 입력한 내용을 초기화합니다.                      |
| 7    | **CANCEL 버튼**        | 수정 작업을 취소하고 이전 페이지로 돌아갑니다.   |

<br/>

## 3.13. VC Schema Management

`VC Schema Management` 메뉴에 진입하면, Open DID 시스템에서 발급 가능한 VC(Verifiable Credential) 스키마 목록이 테이블 형태로 표시됩니다.  

VC 스키마는 각 Credential의 구조와 내용을 정의하며,  
사용자의 Wallet이 발급받은 VC의 유효성을 검증할 때 참조됩니다.

Issuer Admin 페이지에서 VC Schema가 생성된 후,  
TA Admin 서버로 전송되어 최종 등록됩니다.

VC Schema는 TA 서버에 등록되어 있으며, 관리자는 이 목록을 조회하거나 상세 내용을 확인할 수 있습니다.

<br/>

### 3.13.1 VC Schema 목록 조회

`List Provider Settings > VC Schema Management` 메뉴에 진입하면 등록된 VC 스키마 목록이 테이블 형식으로 표시됩니다.

<img src="./images/3-17.vc-schema-management-main.png" width="800"/>

| 번호 | 항목              | 설명                                                         |
| ---- | ----------------- | ------------------------------------------------------------ |
| 1    | **Title**         | VC 스키마의 제목입니다. 클릭 시 상세 정보 페이지로 이동합니다. |
| 2    | **Description**   | VC 스키마에 대한 간략한 설명입니다.                          |
| 3    | **Issuer Name**   | 해당 스키마를 발급하는 Issuer의 이름입니다.                  |
| 4    | **Registered At** | VC 스키마가 최초 등록된 날짜 및 시간입니다.                  |
| 5    | **Updated At**    | VC 스키마가 마지막으로 수정된 날짜 및 시간입니다.            |

<br/>

### 3.13.2 VC Schema 상세

`VC Schema 목록`에서 VC 스키마 제목을 클릭하면, 아래와 같이 상세 정보를 확인할 수 있는 화면으로 이동합니다.

<img src="./images/3-18.vc-schema-detail.png" width="600"/>

| 번호 | 항목              | 설명                                                                   |
| ---- | ----------------- | ---------------------------------------------------------------------- |
| 1    | **Title**         | VC 스키마의 제목입니다.                                                |
| 2    | **Description**   | 해당 스키마에 대한 설명입니다.                                         |
| 3    | **Issuer Name**   | 해당 VC 스키마를 등록한 Issuer의 이름입니다.                          |
| 4    | **Registered At** | VC 스키마가 최초로 등록된 날짜와 시간입니다.                          |
| 5    | **VIEW VC SCHEMA 버튼** | 해당 VC 스키마의 실제 JSON 구조를 확인할 수 있는 버튼입니다. 팝업으로 표시됩니다. |
| 6    | **BACK 버튼**     | 목록 화면으로 되돌아갑니다.                                           |

<br/>

## 3.14. VC Plan Management

`VC Plan Management` 메뉴에서는 Open DID 시스템에서 발급 가능한 VC Plan의 목록을 확인할 수 있습니다.

VC Plan은 Issuer가 생성한 VC 스키마에 대해 실제 어떤 방식으로 VC를 발급할지에 대한 정책을 정의한 구성입니다.  
예를 들어 앱에서 직접 발급을 시작할 수 있는지(`allowUserInit`), Issuer가 QR 코드나 푸시 메시지를 통해 발급을 시작할 수 있는지(`allowIssuerInit`) 등의 정책이 포함됩니다.

VC Plan은 Issuer Admin 서버에서 생성되며, 생성된 Plan은 TA Admin 서버로 전송되어 등록됩니다.  
외부에서는 `vcPlanId`만 알고 있으면 어떤 VC가 어떤 방식으로 발급되는지 확인할 수 있습니다.

---
> **참고:** VC Plan은 다음과 같은 방식으로 활용됩니다:
>
> - Issuer의 기존 발급 시스템(예: 웹사이트)에서 사용자가 VC 발급을 요청하면,  
>   해당 사이트는 TA 서버에 `vcPlanId`를 전달하여 관련 발급 정책을 조회하고, 이를 기반으로 발급 절차를 시작할 수 있습니다.
>
> - 사용자 앱에서는 TA 서버로부터 VC Plan 목록을 조회한 뒤,  
>   사용자가 원하는 VC를 선택하면 해당 Plan에 따라 발급 절차가 시작됩니다.
---

<br/>

### 3.14.1 VC Plan 목록 조회

`VC Plan Management` 메뉴에 진입하면 등록된 VC Plan 목록이 아래와 같이 표시됩니다.  
각 VC Plan의 ID를 클릭하면 상세 정보를 확인할 수 있는 화면으로 이동합니다.

<img src="./images/3-19.vc-plan-managemnet.png" width="800"/>

| 번호 | 항목              | 설명                                                                 |
| ---- | ----------------- | -------------------------------------------------------------------- |
| 1    | **ID**            | VC Plan의 고유 식별자입니다. 클릭 시 상세 정보 화면으로 이동합니다.  |
| 2    | **Name**          | VC Plan의 이름입니다.                                                |
| 3    | **Description**   | VC Plan에 대한 설명입니다.                                           |
| 4    | **Issuer Name**   | 해당 VC Plan을 생성한 Issuer의 이름입니다.                           |
| 5    | **Registered At** | 최초 등록 일시입니다.                                                |
| 6    | **Updated At**    | 마지막 수정 일시입니다.                                              |

<br/>

### 3.14.2 VC Plan 상세

VC Plan 목록에서 ID를 클릭하면 해당 VC Plan의 상세 정보 화면으로 이동합니다.

<img src="./images/3-20.vc-plan-detail.png" width="600"/>


| 번호 | 항목                  | 설명                                                  |
| ---- | --------------------- | ----------------------------------------------------- |
| 1    | **ID**                | VC Plan의 고유 식별자입니다.                          |
| 2    | **VIEW VC PLAN 버튼** | VC Plan의 JSON 원문을 팝업 형태로 확인할 수 있습니다. |
| 3    | **Name**              | VC Plan의 표시 이름입니다.                            |
| 4    | **Description**       | VC Plan에 대한 간단한 설명입니다.                     |
| 5    | **Issuer Name**       | 이 Plan을 생성한 Issuer의 이름입니다.                 |
| 6    | **Registered At**     | 등록된 날짜 및 시간입니다.                            |
| 7    | **BACK 버튼**         | 이전 화면으로 돌아갑니다.                             |

<br/>

## 3.15. Credential Schema Management

`Credential Schema Management` 메뉴에서는 Open DID 시스템에서 발급 가능한 Credential Schema의 목록을 확인할 수 있습니다.  
Credential Schema는 ZKP 방식으로 VC를 발급하기 위한 클레임 이름, 속성 등을 정의한 구조이며,  
각 Credential Definition과 1:1로 연결됩니다.

Issuer Admin 페이지에서 Credential Schema가 생성된 후,  
TA Admin 서버로 전송되어 최종 등록됩니다.

Credential Schema는 TA 서버에 등록되어 있으며, 관리자는 이 목록을 조회하거나 상세 내용을 확인할 수 있습니다.

<br/>

### 3.15.1 Credential Schema 목록 조회

`List Provider Settings > Credential Schema Management` 메뉴에 진입하면 등록된 Credential Schema 목록이 테이블 형식으로 표시됩니다.

<img src="./images/3-29.credential-schema-management.png" width="800"/>

| 번호 | 항목           | 설명                                                        |
| ---- | -------------- | ----------------------------------------------------------- |
| 1    | **Name**       | Credential Schema의 이름입니다. 클릭 시 상세 정보로 이동합니다. |
| 2    | **Issuer Name** | 해당 스키마를 등록한 Issuer의 이름입니다.                     |
| 3    | **Registered At** | 최초 등록 일시입니다.                                     |
| 4    | **Updated At** | 마지막 수정 일시입니다. (없을 경우 비어있음)                  |

<br/>

### 3.15.2 Credential Schema 상세

Credential Schema 목록에서 이름(Name)을 클릭하면 상세 정보 화면으로 이동합니다.

<img src="./images/3-30.credemtial-schema-detail.png" width="600"/>

| 번호 | 항목                       | 설명                                                                          |
| ---- | -------------------------- | ----------------------------------------------------------------------------- |
| 1    | **Name**                   | Credential Schema의 이름입니다.                                               |
| 2    | **Credential Schema ID**   | 해당 스키마의 고유 식별자(DID 형식)입니다.                                    |
| 3    | **Issuer Name**            | 해당 Credential Schema를 등록한 Issuer의 이름입니다.                          |
| 4    | **Registered At**          | Credential Schema가 최초로 등록된 날짜 및 시간입니다.                        |
| 5    | **VIEW CREDENTIAL SCHEMA 버튼** | Credential Schema의 JSON 원문을 팝업 형태로 확인할 수 있습니다.                |
| 6    | **BACK 버튼**              | 목록 화면으로 되돌아갑니다.                                                  |

---

## 3.16. Credential Definition Management

`Credential Definition Management` 메뉴에서는 등록된 Credential Definition의 목록을 확인할 수 있습니다.

Credential Definition은 Credential Schema를 기반으로 실제 ZKP 방식으로 VC를 발급하기 위한 정의입니다.  
Credential Definition은 각 Credential Schema와 1:1 관계를 가지며, Issuer가 VC를 생성할 수 있도록 필요한 메타데이터를 포함합니다.

<br/>

### 3.16.1 Credential Definition 목록 조회

`List Provider Settings > Credential Definition Management` 메뉴에 진입하면 Credential Definition 목록이 표시됩니다.

<img src="./images/3-31.credential-definition.management.png" width="800"/>

| 번호 | 항목                     | 설명                                                                 |
| ---- | ------------------------ | -------------------------------------------------------------------- |
| 1    | **Credential Definition ID** | 해당 Credential Definition의 DID 식별자입니다. 클릭 시 상세 보기로 이동합니다. |
| 2    | **Credential Schema ID** | 연결된 Credential Schema의 DID 식별자입니다.                        |
| 3    | **Credential Definition Tag** | Definition에 부여된 태그 값입니다.                                 |
| 4    | **Issuer Name**          | 해당 Credential Definition을 등록한 Issuer의 이름입니다.             |
| 5    | **Registered At**        | Credential Definition이 최초로 등록된 시간입니다.                    |
| 6    | **Updated At**           | 마지막으로 수정된 시간입니다. (없을 경우 비어있음)                   |

<br/>

### 3.16.2 Credential Definition 상세

Credential Definition 목록에서 ID를 클릭하면 상세 정보 화면으로 이동합니다.

<img src="./images/3-32.credential-definition-detail.png" width="600"/>

| 번호 | 항목                           | 설명                                                                 |
| ---- | ------------------------------ | -------------------------------------------------------------------- |
| 1    | **Credential Definition ID**   | Credential Definition의 DID 형식 식별자입니다.                       |
| 2    | **Credential Schema ID**       | 연결된 Credential Schema의 DID 식별자입니다.                         |
| 3    | **Credential Definition Tag**  | Definition 생성 시 부여된 태그 값입니다.                             |
| 4    | **Issuer Name**                | 해당 Definition을 등록한 Issuer의 이름입니다.                        |
| 5    | **Registered At**              | Credential Definition이 최초로 등록된 시간입니다.                    |
| 6    | **VIEW DEFINITION 버튼**       | Credential Definition의 원문(JSON)을 팝업으로 확인할 수 있습니다.     |
| 7    | **BACK 버튼**                  | 목록 화면으로 돌아갑니다.                                            |

<br/>

## 3.17. User Management

`User Management` 메뉴에서는 Open DID 시스템에서 등록된 사용자의 정보를 조회합니다.   

사용자는 DID를 기반으로 등록되며, 관련 정보는 아래와 같은 세 가지 항목으로 구분되어 관리됩니다

- User: 사용자 식별 정보 (DID, PII 등)
- App: 사용자 단말(App)의 고유 식별 정보 (App ID, Push Token 등)
- Wallet: 사용자 월렛 정보 (Wallet DID, Wallet ID 등)
  
각 항목은 별도 메뉴에서 개별적으로 조회할 수 있으며, 하위 메뉴는 다음과 같습니다:

- [3.18. User List](#318-user-list)
- [3.19. App List](#319-app-list)
- [3.20. Wallet List](#320-wallet-list)

<br/>

## 3.18. User List

`User List`는 Open DID 시스템에 등록된 사용자의 DID 및 PII 정보를 확인할 수 있는 메뉴입니다.

### 3.18.1. User 목록 조회

`User List` 메뉴에 진입하면, 등록된 사용자의 목록이 테이블 형태로 표시됩니다.  
검색 기능을 통해 DID 또는 PII 기준으로 사용자 정보를 조회할 수 있습니다.

<img src="./images/3-23.user-list.png" width="800"/>

| 번호 | 항목              | 설명                                                            |
| ---- | ----------------- | --------------------------------------------------------------- |
| 1    | **DID**           | 사용자의 고유 DID 식별자입니다. 클릭시 상세화면으로 이동합니다. |
| 2    | **PII**           | 사용자의 PII(Personal Identifiable Info) 값입니다.              |
| 3    | **Registered At** | 사용자가 최초로 등록된 날짜 및 시간입니다.                      |
| 4    | **Updated At**    | 사용자 정보가 마지막으로 수정된 날짜입니다.                     |


<br/>

### 3.18.2. User 상세 정보

User 목록에서 DID를 클릭하면 해당 User의 상세 정보 화면으로 이동합니다.

<img src="./images/3-24.user-detail.png" width="600"/>

| 번호 | 항목              | 설명                                        |
| ---- | ----------------- | ------------------------------------------- |
| 1    | **DID**           | 사용자의 고유 DID 식별자입니다.             |
| 2    | **PII**           | 사용자 신원 확인을 위한 식별 정보입니다.    |
| 3    | **Status**        | 사용자의 현재 상태입니다. <br/>Status의 종류는 다음과 같습니다:<br/>- `ACTIVATED`: 활성화 상태<br/>- `DEACTIVATED`: 비활성화 상태<br/>- `REVOKED`: 사용자의 DID가 폐기된 상태 |
| 4    | **Registered At** | 사용자가 최초로 등록된 날짜 및 시간입니다.  |
| 5    | **Updated At**    | 사용자 정보가 마지막으로 수정된 날짜입니다. |
| 6    | **BACK 버튼**     | 목록 화면으로 돌아갑니다.                   |

<br/>

## 3.19. App List

`App List`는 Open DID 시스템에 등록된 App의 Push Token과 상태를 확인할 수 있는 메뉴입니다.

### 3.19.1. App 목록 조회

`App List` 메뉴에 진입하면, Open DID 시스템에 등록한 App의 목록이 테이블 형태로 표시됩니다.  

<img src="./images/3-25.app-list.png" width="800"/>

| 번호 | 항목              | 설명                                                                                                                                    |
| ---- | ----------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **App ID**        | App의 고유 ID입니다. 클릭 시 상세 화면으로 이동합니다.                                                                                  |
| 2    | **Push Token**    | 해당 App이 등록한 푸시 토큰입니다.                                                                                                      |
| 3    | **Status**        | App의 등록 상태입니다.<br/>Status의 종류는 다음과 같습니다:<br/>- `ASSIGNED`: 사용자에게 할당됨<br/>- `CANCELLED`: 등록이 취소된 상태 |
| 4    | **Registered At** | App이 최초로 등록된 날짜 및 시간입니다.                                                                                                 |
| 5    | **Updated At**    | App 정보가 마지막으로 수정된 일시입니다.                                                                                                |


<br/>

### 3.19.2. App 상세 정보

App 목록에서 App ID를 클릭하면 해당 App의 상세 정보 화면으로 이동합니다.

<img src="./images/3-26.app-detail.png" width="600"/>

| 번호 | 항목              | 설명                                                                                                                                    |
| ---- | ----------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **DID**           | App의 고유 DID입니다.                                                                                                                   |
| 2    | **Push Token**    | App이 등록한 푸시 토큰입니다.                                                                                                           |
| 3    | **Status**        | App의 등록 상태입니다.<br/>Status의 종류는 다음과 같습니다:<br/>- `ASSIGNED`: 사용자에게 할당됨<br/>- `CANCELLED`: 등록이 취소된 상태 |
| 4    | **Registered At** | App이 최초로 등록된 날짜 및 시간입니다.                                                                                                 |
| 5    | **Updated At**    | App 정보가 마지막으로 수정된 일시입니다.                                                                                                |
| 6    | **BACK 버튼**     | 목록 화면으로 돌아갑니다.                                                                                                               |

<br/>

## 3.20. Wallet List

`Wallet List`는 Open DID 시스템에 등록된 Wallet 정보(DID, Wallet ID 등)를 조회하는 메뉴입니다.

### 3.20.1. Wallet 목록 조회

`Wallet List` 메뉴에 진입하면 등록된 Wallet 목록이 테이블 형태로 표시됩니다.

<img src="./images/3-27.wallet-list.png" width="800"/>

| 번호 | 항목              | 설명                                                                                                                                                       |
| ---- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **DID**           | Wallet의 고유 DID 식별자입니다.                                                                                                                            |
| 2    | **Wallet ID**     | Wallet의 색별자입니다.                                                                                                                                     |
| 3    | **Status**        | Wallet의 등록 상태입니다.<br/>Status의 종류는 다음과 같습니다:<br/>- `CREATED`: 생성됨<br/>- `ASSIGNED`: 사용자에게 할당됨<br/>- `CANCELLED`: 등록 취소됨 |
| 4    | **Registered At** | Wallet이 최초로 등록된 일시입니다.                                                                                                                         |
| 5    | **Cancelled At**  | Wallet이 해지된 경우 해당 일시가 표시됩니다.                                                                                                               |

<br/>

### 3.20.2. Wallet 상세 정보

Wallet 목록에서 DID를 클릭하면 해당 Wallet의 상세 정보 화면으로 이동합니다.

<img src="./images/3-28.wallet-detail.png" width="600"/>

| 번호 | 항목              | 설명                                                                                                                                                       |
| ---- | ----------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **DID**           | Wallet의 고유 DID 식별자입니다.                                                                                                                            |
| 2    | **ID**            | Wallet 식별자입니다.                                                                                                                                       |
| 3    | **Status**        | Wallet의 등록 상태입니다.<br/>Status의 종류는 다음과 같습니다:<br/>- `CREATED`: 생성됨<br/>- `ASSIGNED`: 사용자에게 할당됨<br/>- `CANCELLED`: 등록 취소됨 |
| 4    | **Registered At** | Wallet이 최초로 등록된 일시입니다.                                                                                                                         |
| 5    | **BACK 버튼**     | 목록 화면으로 돌아갑니다.                                                                                                                                  |

<br/>

## 3.21. Admin Management

`Admin Management` 메뉴는 TA Admin Console에 접근할 수 있는 관리자 계정을 관리하는 기능입니다.  

TA 서버를 설치하면 기본적으로 `admin@opendid.omnione.net` 계정이 ROOT 권한으로 자동 생성됩니다.  
이 계정은 시스템 내 유일한 ROOT 계정이며, 삭제할 수 없습니다.

관리자 계정은 **ROOT**와 **Normal Admin** 두 가지 권한 유형으로 구분됩니다.  
ROOT 계정은 `Admin Management` 메뉴에서 모든 기능을 수행할 수 있으며, Normal Admin은 일반적인 조회 기능만 가능합니다.

---
> **참고:** 현재는 ROOT 계정과 Normal Admin 계정 간의 권한 차이는  
> `Admin Management` 메뉴에서 표시되는 버튼의 차이(Root만 REGISTER / DELETE / CHANGE PASSWORD 가능) 외에는 없습니다.  
> 그 외 시스템의 다른 메뉴에 대한 접근 권한이나 기능 제한은 아직 적용되어 있지 않습니다.
---


<br/>

### 3.21.1. Admin 목록 조회


`Admin Management` 메뉴에 진입하면 등록된 관리자 계정들의 목록이 테이블 형태로 표시됩니다.

<img src="./images/3-21.admin-management.png" width="800"/>

| 번호 | 항목                    | 설명                                                             |
| ---- | ----------------------- | ---------------------------------------------------------------- |
| 1    | **REGISTER 버튼**       | 새로운 관리자 계정을 등록할 수 있는 등록 페이지로 이동합니다.       |
| 2    | **DELETE 버튼**         | 선택한 관리자 계정을 삭제합니다. (ROOT 관리자만 가능)              |
| 3    | **CHANGE PASSWORD 버튼** | 선택한 관리자 계정의 비밀번호를 변경할 수 있습니다.                |
| 4    | **ID**                  | 등록된 관리자 계정의 이메일 ID입니다.                              |
| 5    | **Role**                | 해당 관리자 계정의 역할(Role)입니다. (예: ROOT, Normal Admin 등)   |
| 6    | **Registered At**       | 해당 계정이 최초 등록된 일시입니다.                               |
| 7    | **Updated At**          | 마지막으로 수정된 일시입니다.     

<br/>

### 3.21.2. Admin 등록

`Admin Management` 화면에서 **REGISTER** 버튼을 클릭하면, 아래와 같은 등록 화면으로 이동합니다.

<img src="./images/3-22.admin-registration.png" width="600"/>

| 번호 | 항목                        | 설명                                                                |
| ---- | --------------------------- | ------------------------------------------------------------------- |
| 1    | **ID**                      | 등록할 관리자 계정의 ID입니다. 이메일 형식을 사용해야 합니다.         |
| 2    | **Check Availability 버튼** | 입력한 ID가 중복되지 않는지 확인합니다.                             |
| 3    | **Role**                    | 등록할 관리자 계정의 권한을 선택합니다. (예: Normal Admin)           |
| 4    | **Password**                | 로그인 시 사용할 비밀번호를 입력합니다.                              |
| 5    | **Re-enter Password**       | 비밀번호를 한 번 더 입력하여 일치 여부를 확인합니다.                |
| 6    | **REGISTER 버튼**           | 입력한 정보를 바탕으로 관리자 계정을 등록합니다.                    |
| 7    | **RESET 버튼**              | 모든 입력값을 초기화합니다.                                         |
| 8    | **CANCEL 버튼**             | 등록을 취소하고 이전 화면으로 돌아갑니다.                           |


[Open DID Installation Guide]: https://github.com/OmniOneID/did-release/blob/develop/release-V2.0.0.0/OpenDID_Installation_Guide-V2.0.0.0_ko.md

