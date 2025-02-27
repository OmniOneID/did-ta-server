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

import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.base.exception.ErrorCode;

/**
 * Enum class for the role column in the ENTITY table.
 */
public enum Role {
    TAS,
    WALLET,
    ISSUER,
    VERIFIER,
    WALLET_PROVIDER,
    APP_PROVIDER,
    LIST_PROVIDER,
    OP_PROVIDER,
    KYC_PROVIDER,
    NOTIFICATION_PROVIDER,
    LOG_PROVIDER,
    PORTAL_PROVIDER,
    DELEGATION_PROVIDER,
    STORAGE_PROVIDER,
    BACKUP_PROVIDER,
    ETC,
    ;

    public RoleType toRoleType() {
        switch (this) {
            case TAS:
                return RoleType.TAS;
            case WALLET:
                return RoleType.WALLET;
            case ISSUER:
                return RoleType.ISSUER;
            case VERIFIER:
                return RoleType.VERIFIER;
            case WALLET_PROVIDER:
                return RoleType.WALLET_PROVIDER;
            case APP_PROVIDER:
                return RoleType.APP_PROVIDER;
            case LIST_PROVIDER:
                return RoleType.LIST_PROVIDER;
            case OP_PROVIDER:
                return RoleType.OP_PROVIDER;
            case KYC_PROVIDER:
                return RoleType.KYC_PROVIDER;
            case NOTIFICATION_PROVIDER:
                return RoleType.NOTIFICATION_PROVIDER;
            case LOG_PROVIDER:
                return RoleType.LOG_PROVIDER;
            case PORTAL_PROVIDER:
                return RoleType.PORTAL_PROVIDER;
            case DELEGATION_PROVIDER:
                return RoleType.DELEGATION_PROVIDER;
            case STORAGE_PROVIDER:
                return RoleType.STORAGE_PROVIDER;
            case BACKUP_PROVIDER:
                return RoleType.BACKUP_PROVIDER;
            case ETC:
                return RoleType.ETC;
            default:
                throw new OpenDidException(ErrorCode.INVALID_ROLE_TYPE);
        }
    }

    public static Role fromRoleType(RoleType roleType) {
        switch (roleType) {
            case TAS:
                return Role.TAS;
            case WALLET:
                return Role.WALLET;
            case ISSUER:
                return Role.ISSUER;
            case VERIFIER:
                return Role.VERIFIER;
            case WALLET_PROVIDER:
                return Role.WALLET_PROVIDER;
            case APP_PROVIDER:
                return Role.APP_PROVIDER;
            case LIST_PROVIDER:
                return Role.LIST_PROVIDER;
            case OP_PROVIDER:
                return Role.OP_PROVIDER;
            case KYC_PROVIDER:
                return Role.KYC_PROVIDER;
            case NOTIFICATION_PROVIDER:
                return Role.NOTIFICATION_PROVIDER;
            case LOG_PROVIDER:
                return Role.LOG_PROVIDER;
            case PORTAL_PROVIDER:
                return Role.PORTAL_PROVIDER;
            case DELEGATION_PROVIDER:
                return Role.DELEGATION_PROVIDER;
            case STORAGE_PROVIDER:
                return Role.STORAGE_PROVIDER;
            case BACKUP_PROVIDER:
                return Role.BACKUP_PROVIDER;
            case ETC:
                return Role.ETC;
            default:
                throw new OpenDidException(ErrorCode.INVALID_ROLE_TYPE);
        }
    }
}
