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

package org.omnione.did.base.constants;

/**
 * Represents URL constants for the Open DID servers.
 * This class organizes all the API endpoints used in the application, ensuring that URLs are consistent and easy to manage.
 */
public class UrlConstant {

    /**
     * Contains URL constants for the TAS (Trusted Agent) API.
     */
    public static class Tas {
        public static final String AGENT_V1 = "/tas/api/v1";
        public static final String ADMIN_V1 = "/tas/admin/v1";
    }

    /**
     * Contains URL constants for the Noti (Notification) API.
     * @Note: Currently, the TAS service is handling Noti-related operations.
     */
    public static class Noti {
        public static final String AGENT_V1 = "/noti/api/v1";
        public static final String ADMIN_V1 = "/noti/admin/v1";
    }

    /**
     * Contains URL constants for the List API.
     * @Note: Currently, the TAS service is handling List-related operations.
     */
    public static class List {
        public static final String AGENT_V1 = "/list/api/v1";
        public static final String ADMIN_V1 = "/list/admin/v1";
    }

    /**
     * Contains URL constants for the Issuer API.
     * This class defines various endpoints related to issuing, revoking, and inspecting verifiable credentials (VCs).
     */
    public static class Issuer {
        public static final String V1 = "/api/v1";
        public static final String REQUEST_OFFER = "/request-offer";
        public static final String ISSUE_VC = "/issue-vc";
        public static final String INSPECT_PROPOSE_ISSUE = "/inspect-propose-issue";
        public static final String GENERATE_ISSUE_PROFILE = "/generate-issue-profile";
        public static final String COMPLETE_VC = "/complete-vc";
        public static final String INSPECT_PROPOSE_REVOKE = "/inspect-propose-revoke";
        public static final String REVOKE_VC = "/revoke-vc";
        public static final String COMPLETE_REVOKE = "/complete-revoke";
    }

    public static class LSS {
        public static final String V1 = "/lss/api/v1";
        public static final String DID = "/did-doc";
        public static final String VC_META = "/vc-meta";
        public static final String CREDENTIAL_SCHEMA = "/credential-schema";
        public static final String CREDENTIAL_DEFINITION = "/credential-definition";


    }
}
