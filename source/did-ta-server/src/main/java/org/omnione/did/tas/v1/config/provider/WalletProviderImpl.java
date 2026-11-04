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

import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.common.exception.CommonSdkException;
import org.omnione.did.tas.v1.agent.service.FileWalletService;
import lombok.RequiredArgsConstructor;
import org.omnione.did.common.domain.did.DidDocCryptoHelper;
import org.omnione.did.common.util.multibase.BaseMultibaseUtil;
import org.omnione.did.common.util.vc.BaseCoreVcUtil;
import org.omnione.did.core.manager.VcManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.vc.VerifiableCredential;
import org.omnione.did.tas.provider.WalletProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Implementation of the SDK WalletProvider interface, delegating to the server's FileWalletService.
 */
@Component
@RequiredArgsConstructor
@Profile("!sample")
public class WalletProviderImpl implements WalletProvider {

    private final FileWalletService fileWalletService;

    @Override
    public void signVc(VerifiableCredential vc, DidDocument didDoc, String keyPurpose) {
        fileWalletService.ensureConnection();
        try {
            VerifiableCredential signed = BaseCoreVcUtil.signVerifiableCredential(
                    new VcManager(),
                    fileWalletService.getWalletManager(),
                    didDoc,
                    keyPurpose,
                    vc
            );
            vc.setProof(signed.getProof());
        } catch (CommonSdkException e) {
            throw new OpenDidException(ErrorCode.SIGNATURE_GENERATION_FAILED);
        }
    }

    @Override
    public InvokedDidDoc signInvokedDidDoc(InvokedDidDoc unsignedInvokedDidDoc, String keyId) {
        fileWalletService.ensureConnection();
        try {
            return DidDocCryptoHelper.signInvokedDidDoc(
                    unsignedInvokedDidDoc,
                    fileWalletService.getWalletManager(),
                    keyId
            );
        } catch (CommonSdkException e) {
            throw new OpenDidException(ErrorCode.SIGNATURE_GENERATION_FAILED);
        }
    }

    @Override
    public String sign(byte[] message, String keyId) {
        return BaseMultibaseUtil.encode(fileWalletService.signHash(keyId, message));
    }

    @Override
    public boolean isWalletConnected() {
        return fileWalletService.getWalletManager() != null
                && fileWalletService.getWalletManager().isConnect();
    }

    @Override
    public void ensureWalletConnection() {
        fileWalletService.ensureConnection();
    }
}
