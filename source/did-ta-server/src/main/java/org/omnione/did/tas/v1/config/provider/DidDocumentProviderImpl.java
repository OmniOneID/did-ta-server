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
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.tas.exception.TaSdkErrorCode;
import org.omnione.did.tas.exception.TaSdkException;
import org.omnione.did.tas.provider.DidDocumentProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK DidDocumentProvider interface, delegating to the server's StorageService.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class DidDocumentProviderImpl implements DidDocumentProvider {

    private final StorageService storageService;
    private final TasQueryService tasQueryService;

    @Override
    public DidDocument getDidDocument(String did) {
        DidDocument didDoc = storageService.findDidDoc(did);
        if (didDoc == null) {
            throw new TaSdkException(TaSdkErrorCode.SDK_DID_DOCUMENT_NOT_FOUND,
                    "DID Document not found: " + did);
        }
        return didDoc;
    }

    @Override
    public DidDocument getTasDidDocument() {
        return getDidDocument(tasQueryService.findTas().getDid());
    }
}
