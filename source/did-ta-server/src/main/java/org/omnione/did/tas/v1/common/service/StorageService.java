/*
 * Copyright 2024 OmniOne.
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

package org.omnione.did.tas.v1.common.service;

import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;

/**
 * Storage service interface for managing DID documents and verifiable credentials.
 */
public interface StorageService {
    void registerDidDoc(InvokedDidDoc didDoc, RoleType roleType);
    void updateDidDocStatus(String did, Object didDocStatus);
    DidDocument findDidDoc(String didKeyUrl);
    void registerVcMeta(VcMeta vcMeta);
    VcMeta findVcMeta(String vcId);
}
