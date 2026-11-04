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

package org.omnione.did.tas.v1.config.provider;

import org.omnione.did.tas.v1.common.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.base.util.BaseTasDidUtil;
import org.omnione.did.tas.exception.TaSdkErrorCode;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.provider.StorageProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK StorageProvider interface, delegating to the server's StorageService.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class StorageProviderImpl implements StorageProvider {

    private final StorageService storageService;

    @Override
    public void registerDidDocument(InvokedDidDoc invokedDidDoc, RoleType roleType) {
        storageService.registerDidDoc(invokedDidDoc, roleType);
    }

    @Override
    public DidDocument getDidDocument(String did) {
        DidDocument didDoc = storageService.findDidDoc(did);
        if (didDoc == null) {
            throw new TaSdkException(TaSdkErrorCode.TAS_DID_DOCUMENT_NOT_FOUND,
                    "DID Document not found: " + did);
        }
        return didDoc;
    }

    /**
     * Updates the status of the current version of the DID Document.
     */
    @Override
    public void updateDidDocumentStatus(String did, String status) {
        String didWithVersion = BaseTasDidUtil.getDidWithVersion(storageService.findDidDoc(did));
        storageService.updateDidDocStatus(didWithVersion, DidDocStatus.valueOf(status));
    }

    @Override
    public boolean existsDidDocument(String did) {
        return storageService.findDidDoc(did) != null;
    }

    @Override
    public String getDidDocumentVersionId(String did) {
        return getDidDocument(did).getVersionId();
    }

    @Override
    public DidDocument findDidDoc(String did) {
        return storageService.findDidDoc(did);
    }

    @Override
    public void registerVcMeta(VcMeta vcMeta) {
        storageService.registerVcMeta(vcMeta);
    }

    @Override
    public VcMeta findVcMeta(String vcId) {
        return storageService.findVcMeta(vcId);
    }
}
