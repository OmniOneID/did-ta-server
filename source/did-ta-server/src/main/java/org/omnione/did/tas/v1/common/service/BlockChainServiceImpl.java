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

import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseBlockChainUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.did.DidDocAndStatus;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service for managing DID Document operations, including registration and retrieval.
 * This service interacts with the blockchain to register and retrieve DID Documents.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!repository")
public class BlockChainServiceImpl implements StorageService {

    /**
     * Register the given DID Document with the blockchain.
     * Throws an OpenDidException if the DID Document cannot be registered.
     *
     * @param didDoc The DID Document to register
     * @param roleType The role type of the DID Document
     * @throws OpenDidException if the DID Document cannot be registered
     */
    @Override
    public void registerDidDoc(InvokedDidDoc didDoc, RoleType roleType) {
        try {
            BaseBlockChainUtil.registerDidDocument(didDoc, roleType);
        } catch (OpenDidException e) {
            log.error("Failed to register DID Document: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to register DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.DID_DOCUMENT_REGISTRATION_FAILED);
        }
    }

    /**
     * Update the status of the given DID Document in the blockchain.
     * Throws an OpenDidException if the status cannot be updated.
     *
     * @param did The DID to update the status for
     * @param didDocStatus The status to update the DID Document to
     */
    //@TODO: BlockChain SDK 연동 테스트 필요 {try, catch}
    @Override
    public void updateDidDocStatus(String did, Object didDocStatus) {
        log.debug("The DID document status has been successfully updated.");
    }

    /**
     * Retrieve the DID Document associated with the given DID key URL.
     * Throws an OpenDidException if the DID Document cannot be retrieved.
     *
     * @param didKeyUrl The DID key URL to retrieve the DID Document for
     * @return The DID Document associated with the given DID key URL
     * @throws OpenDidException if the DID Document cannot be retrieved
     */
    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        try {
            DidDocAndStatus didDocAndStatus = BaseBlockChainUtil.findDidDocument(didKeyUrl);
            return didDocAndStatus.getDocument();
        } catch (OpenDidException e) {
            log.error("Failed to find DID Document: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to find DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.UNKNOWN_SERVER_ERROR);
        }
    }

    /**
     * Register the given VC Meta with the blockchain.
     * Throws an OpenDidException if the VC Meta cannot be registered.
     *
     * @param vcMeta The VC Meta to register
     * @throws OpenDidException if the VC Meta cannot be registered
     */
    @Override
    public void registerVcMeta(VcMeta vcMeta) {
        BaseBlockChainUtil.registerVcMeta(vcMeta);
    }

    /**
     * Retrieve the VC Meta associated with the given VC ID.
     * Throws an OpenDidException if the VC Meta cannot be retrieved.
     *
     * @param vcId The VC ID to retrieve the VC Meta for
     * @return The VC Meta associated with the given VC ID
     * @throws OpenDidException if the VC Meta cannot be retrieved
     */
    @Override
    public VcMeta findVcMeta(String vcId) {
        try {
            VcMeta vcMeta = BaseBlockChainUtil.findVcMeta(vcId);
            return vcMeta;
        } catch (OpenDidException e) {
            log.error("Failed to find VC Meta: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to find VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_META_RETRIEVAL_FAILED);
        }
    }
}
