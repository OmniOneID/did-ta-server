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

package org.omnione.did.base.util;

import org.omnione.did.ContractApi;
import org.omnione.did.ContractFactory;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.did.DidDocAndStatus;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.fabric.FabricContractApi;
import org.omnione.exception.BlockChainException;

/**
 * Utility class for blockchain operations.
 * This class provides methods to interact with the blockchain, including
 * initializing the blockchain, registering and retrieving DID documents,
 * and managing VC metadata and status.
 */
@Slf4j
public class BaseBlockChainUtil {

    private static ContractApi contractApiInstance = getContractApiInstance();

    /**
     * Initializes the blockchain connection.
     *
     * @return a ContractApi instance.
     */
    public static ContractApi initBlockChain() {
        return ContractFactory.EVM.create("/Users/gwnam/Documents/team/opensource/project/github/did-orchestrator-server/source/did-orchestrator-server/shells/Besu/blockchain.propertiess");

    }

    /**
     * Resets the ContractApi instance.
     * Use this method to reinitialize the blockchain connection.
     */
    public static ContractApi getContractApiInstance() {
        if (contractApiInstance == null) {
            synchronized (BaseBlockChainUtil.class) {
                if (contractApiInstance == null) {
                    contractApiInstance = initBlockChain();
                }
            }
        }
        return contractApiInstance;
    }

    public static synchronized void resetContractApiInstance() {
        contractApiInstance = null;
    }

    /**
     * Registers a DID document on the blockchain.
     *
     * @param invokedDidDoc the DID document to register.
     * @param roleType the role type associated with the DID document.
     * @throws OpenDidException if the DID document cannot be registered.
     */
    public static void registerDidDocument(InvokedDidDoc invokedDidDoc, RoleType roleType) {
        try {
            ContractApi contractApi = getContractApiInstance();
            contractApi.registDidDoc(invokedDidDoc, roleType);
        } catch (BlockChainException e) {
            log.error("Failed to register DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_DIDDOC_REGISTRATION_FAILED);
        }
    }

    /**
     * Retrieves a DID document and its status from the blockchain.
     *
     * @param didKeyUrl the DID key URL to search for.
     * @return the DID document and its status.
     * @throws OpenDidException if the DID document cannot be found.
     */
    public static DidDocAndStatus findDidDocument(String didKeyUrl) {
        try {
            ContractApi contractApi = getContractApiInstance();
            return (DidDocAndStatus) contractApi.getDidDoc(didKeyUrl);
        } catch (BlockChainException e) {
            log.error("Failed to get DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_GET_DID_DOC_FAILED);
        }
    }

    /**
     * Updates the status of a DID document on the blockchain.
     *
     * @param didKeyURl the DID key URL.
     * @param didDocStatus the new status for the DID document.
     * @return the updated DID document.
     * @throws OpenDidException if the DID document status cannot be updated.
     */
    public static DidDocument updateDidDocStatus(String didKeyURl, DidDocStatus didDocStatus) {
        try {
            ContractApi contractApi = getContractApiInstance();
            return (DidDocument) contractApi.updateDidDocStatus(didKeyURl, didDocStatus);
        } catch (BlockChainException e) {
            log.error("Failed to update DID Document: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_UPDATE_DID_DOC_FAILED);
        }
    }

    /**
     * Registers VC metadata on the blockchain.
     *
     * @param vcMeta the VC metadata to register.
     * @throws OpenDidException if the VC metadata cannot be registered.
     */
    public static void registerVcMeta(VcMeta vcMeta) {
        try {
            ContractApi contractApi = getContractApiInstance();
            contractApi.registVcMetadata(vcMeta);
        } catch (BlockChainException e) {
            log.error("Failed to register VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_META_REGISTRATION_FAILED);
        }
    }

    /**
     * Retrieves VC metadata from the blockchain.
     *
     * @param vcId the VC ID to search for.
     * @return the VC metadata.
     * @throws OpenDidException if the VC metadata cannot be found.
     */
    public static VcMeta findVcMeta(String vcId) {
        try {
            ContractApi contractApi = getContractApiInstance();
            return (VcMeta) contractApi.getVcMetadata(vcId);
        } catch (BlockChainException e) {
            log.error("Failed to find VC Meta: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_META_RETRIEVAL_FAILED);
        }
    }

    /**
     * Updates the status of a VC on the blockchain.
     *
     * @param vcId the VC ID.
     * @param vcStatus the new status for the VC.
     * @throws OpenDidException if the VC status cannot be updated.
     */
    public static void updateVcStatus(String vcId, VcStatus vcStatus) {
        try {
            ContractApi contractApi = getContractApiInstance();
            contractApi.updateVcStatus(vcId, vcStatus);
        } catch (BlockChainException e) {
            log.error("Failed to update VC Status: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_VC_STATUS_UPDATE_FAILED);
        }
    }

    /**
     * Removes an index from the blockchain.
     * (Caution: This method is for testing purposes only.)
     *
     * @param index the name of the index to remove.
     * @throws RuntimeException if the index cannot be removed.
     */
    public static void removeIndex(String index) {
        try {
            FabricContractApi fabricContractApi = (FabricContractApi)getContractApiInstance();
            fabricContractApi.removeIndex(index);
        } catch (BlockChainException e) {
            log.error("Failed to remove index: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_REMOVE_INDEX_FAILED);
        }
    }

    public static void removeIndexAll() {
        try {
            FabricContractApi fabricContractApi = (FabricContractApi)getContractApiInstance();
            fabricContractApi.removeAll();
        } catch (BlockChainException e) {
            log.error("Failed to remove index: " + e.getMessage());
            throw new OpenDidException(ErrorCode.BLOCKCHAIN_REMOVE_INDEX_FAILED);
        }
    }
}
