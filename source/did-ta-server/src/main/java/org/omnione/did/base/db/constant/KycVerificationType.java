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
 * Enum representing the KYC verification type.
 * <ul>
 *   <li>TRANSACTION: PII is retrieved from the CA server using a KYC transaction ID.</li>
 *   <li>TOKEN: PII is extracted from a JWT access token issued by the OP server.</li>
 * </ul>
 */
public enum KycVerificationType {
    TRANSACTION,
    TOKEN
}
