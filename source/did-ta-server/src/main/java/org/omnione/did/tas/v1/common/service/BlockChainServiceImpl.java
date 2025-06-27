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

package org.omnione.did.tas.v1.common.service;

import org.omnione.did.ContractApi;
import org.omnione.did.ContractFactory;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.property.BlockchainProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.did.DidDocAndStatus;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.fabric.FabricContractApi;
import org.omnione.exception.BlockChainException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Service for managing DID Document operations, including registration and retrieval.
 * This service interacts with the blockchain to register and retrieve DID Documents.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!lss & !sample")
public class BlockChainServiceImpl implements StorageService {

    private final ContractApi contractApi;

    private final BlockchainProperty blockchainProperty;

    /**
     * Initializes the blockchain connection.
     *
     * @return a ContractApi instance.
     */
    public ContractApi initBlockChain() {
        return ContractFactory.EVM.create(blockchainProperty.getFilePath());
    }


    /**
     * Registers a DID document on the blockchain.
     *
     * @param invokedDidDoc the DID document to register.
     * @param roleType the role type associated with the DID document.
     * @throws OpenDidException if the DID document cannot be registered.
     */
    @Override
    public void registerDidDoc(InvokedDidDoc invokedDidDoc, RoleType roleType) {
        try {
            contractApi.registDidDoc(invokedDidDoc, roleType);
        } catch (BlockChainException e) {
            log.error("Failed to register DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_DIDDOC_REGISTRATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to register DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.DID_DOCUMENT_REGISTRATION_FAILED);
        }
    }

    /**
     * Updates the status of a DID document on the blockchain.
     *
     * @param didKeyURl    the DID key URL.
     * @param didDocStatus the new status for the DID document.
     * @throws OpenDidException if the DID document status cannot be updated.
     */
    @Override
    public void updateDidDocStatus(String didKeyURl, DidDocStatus didDocStatus) {
        try {
            contractApi.updateDidDocStatus(didKeyURl, didDocStatus);
        } catch (BlockChainException e) {
            log.error("Failed to update DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_UPDATE_DID_DOC_FAILED);
        } catch (Exception e) {
            log.error("Failed to update DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.UPDATE_DID_DOC_FAILED);
        }
    }

    /**
     * Retrieves a DID document and its status from the blockchain.
     *
     * @param didKeyUrl the DID key URL to search for.
     * @return the DID document and its status.
     * @throws OpenDidException if the DID document cannot be found.
     */
    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        try {
            DidDocAndStatus didDocAndStatus = (DidDocAndStatus) contractApi.getDidDoc(didKeyUrl);

            return didDocAndStatus.getDocument();
        } catch (BlockChainException e) {
            log.error("Failed to get DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_GET_DID_DOC_FAILED);
        } catch (Exception e) {
            log.error("Failed to find DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.FIND_DID_DOC_FAILED);
        }
    }

    /**
     * Registers VC metadata on the blockchain.
     *
     * @param vcMeta the VC metadata to register.
     * @throws OpenDidException if the VC metadata cannot be registered.
     */
    @Override
    public void registerVcMeta(VcMeta vcMeta) {
        try {
            contractApi.registVcMetadata(vcMeta);
        } catch (BlockChainException e) {
            log.error("Failed to register VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_META_REGISTRATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to register VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_META_REGISTRATION_FAILED);
        }
    }

    /**
     * Updates the status of a VC Meta.
     *
     * @param vcId        The VC ID of the VC Meta to update
     * @param vcStatus    The new status of the VC Meta
     */
    @Override
    public void updateVcMeta(String vcId, VcStatus vcStatus) {
        try {
            contractApi.updateVcStatus(vcId, vcStatus);
        } catch (BlockChainException e) {
            log.error("Failed to update VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_STATUS_UPDATE_FAILED);
        } catch (Exception e) {
            log.error("Failed to update VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_STATUS_UPDATE_FAILED);
        }
    }

    /**
     * Retrieves VC metadata from the blockchain.
     *
     * @param vcId the VC ID to search for.
     * @return the VC metadata.
     * @throws OpenDidException if the VC metadata cannot be found.
     */
    @Override
    public VcMeta findVcMeta(String vcId) {
        try {
            return (VcMeta) contractApi.getVcMetadata(vcId);
        } catch (BlockChainException e) {
            log.error("Failed to find VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_META_RETRIEVAL_FAILED);
        } catch (Exception e) {
            log.error("Failed to find VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_META_RETRIEVAL_FAILED);
        }
    }

    @Override
    public void registerVcSchema(VcSchema vcSchema, String did) {
        try {
            contractApi.registVcSchema(vcSchema);
        } catch (BlockChainException e) {
            log.error("Failed to get VC Schema: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_SCHEMA_REGISTRATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to register VC Schema: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_SCHEMA_REGISTRATION_FAILED);
        }
    }

    @Override
    public VcSchema getVcSchema(String vcSchemaId) {
        try {
            return (VcSchema) contractApi.getVcSchema(vcSchemaId);
        } catch (BlockChainException e) {
            log.error("Failed to get VC Schema: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_SCHEMA_RETRIEVAL_FAILED);
        } catch (Exception e) {
            log.error("Failed to get DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_SCHEMA_NOT_FOUND);
        }
    }

    /**
     * Updates the status of a VC on the blockchain.
     *
     * @param vcId the VC ID.
     * @param vcStatus the new status for the VC.
     * @throws OpenDidException if the VC status cannot be updated.
     */
    public void updateVcStatus(String vcId, VcStatus vcStatus) {
        try {
            contractApi.updateVcStatus(vcId, vcStatus);
        } catch (BlockChainException e) {
            log.error("Failed to update VC Status: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_STATUS_UPDATE_FAILED);
        } catch (Exception e) {
            log.error("Failed to update VC Status: " + e.getMessage());
            throw new OpenDidException(ErrorCode.VC_STATUS_UPDATE_FAILED);
        }
    }

    /**
     * Removes an index from the blockchain.
     * (Caution: This method is for testing purposes only.)
     *
     * @param index the name of the index to remove.
     * @throws RuntimeException if the index cannot be removed.
     */
    public void removeIndex(String index) {
        try {
            FabricContractApi fabricContractApi = (FabricContractApi) contractApi;
            fabricContractApi.removeIndex(index);
        } catch (BlockChainException e) {
            log.error("Failed to remove index: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_REMOVE_INDEX_FAILED);
        }
    }

    /**
     * Removes all indexes from the blockchain.
     * (Caution: This method is for testing purposes only.)
     *
     * @throws RuntimeException if the indexes cannot be removed.
     */
    public void removeIndexAll() {
        try {
            FabricContractApi fabricContractApi = (FabricContractApi) contractApi;
            fabricContractApi.removeAll();
        } catch (BlockChainException e) {
            log.error("Failed to remove index: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_REMOVE_INDEX_FAILED);
        }
    }
}
