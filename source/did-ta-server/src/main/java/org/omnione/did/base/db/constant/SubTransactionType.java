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

package org.omnione.did.base.db.constant;

/**
 * Enum class for the type column in the SUB_TRANSACTION table.
 */
public enum SubTransactionType {
    REQUEST_ENROLL_TAS,
    PROPOSE_ENROLL_ENTITY,
    REQUEST_ECDH,
    REQUEST_ENROLL_ENTITY,
    CONFIRM_ENROLL_ENTITY,
    REQUEST_REGISTER_WALLET,
    PROPOSE_REGISTER_USER,
    REQUEST_CREATE_TOKEN,
    RETRIEVE_KYC,
    REQUEST_REGISTER_USER,
    CONFIRM_REGISTER_USER,
    PROPOSE_ISSUE_VC,
    REQUEST_ISSUE_PROFILE,
    REQUEST_ISSUE_VC,
    CONFIRM_ISSUE_VC,
    PROPOSE_UPDATE_DIDDOC,
    REQUEST_UPDATE_DIDDOC,
    CONFIRM_UPDATE_DIDDOC,
    UPDATE_USER_STATUS,
    PROPOSE_RESTORE_DIDDOC,
    REQUEST_RESTORE_DIDDOC,
    CONFIRM_RESTORE_DIDDOC,
    PROPOSE_REVOKE_VC,
    REQUEST_REVOKE_VC,
    CONFIRM_REVOKE_VC,
}
