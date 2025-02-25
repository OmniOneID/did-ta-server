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
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.tas.v1.agent.api.RepositoryFeign;
import org.omnione.did.tas.v1.agent.api.dto.RegisterDidApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.DidDocApiResDto;
import org.omnione.did.tas.v1.agent.api.dto.VcMetaApiResDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.common.util.DidUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Storage service implementation for managing DID documents and verifiable credentials.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Primary
@Profile("repository")
public class RepositoryServiceImpl implements StorageService {
    private final RepositoryFeign repositoryFeign;

    /**
     * Registers a DID document.
     *
     * @param didDoc The DID document to register
     * @param roleType The role type of the DID document
     * @throws OpenDidException If the DID document cannot be registered
     */
    @Override
    public void registerDidDoc(InvokedDidDoc didDoc, RoleType roleType) {
        try {
            RegisterDidApiReqDto apiRegisterDidReqDto = RegisterDidApiReqDto.builder()
                    .roleTYpe(roleType.toString())
                    .didDoc(didDoc)
                    .build();

            repositoryFeign.registerDid(apiRegisterDidReqDto);
        } catch (OpenDidException e) {
            log.error("Failed to register DID document.", e);
            throw e;
        } catch (FeignException e) {
            log.error("Failed to register DID document.", e);
            throw new OpenDidException(ErrorCode.DID_DOC_REGISTRATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to register DID document.", e);
            throw new OpenDidException(ErrorCode.DID_DOC_REGISTRATION_FAILED);
        }
    }

    /**
     * Updates the status of a DID document.
     *
     * @param did The DID of the document to update
     * @param didDocStatus The new status of the document
     */
    @Override
    public DidDocument updateDidDocStatus(String did, DidDocStatus didDocStatus) {
        return null;
    }

    /**
     * Finds a DID document by DID key URL.
     *
     * @param didKeyUrl The DID key URL of the document to find
     * @return The found DID document
     * @throws OpenDidException If the DID document cannot be found
     */
    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        try {
            String did = DidUtil.extractDid(didKeyUrl);

            DidDocApiResDto didDocApiResDto = repositoryFeign.getDid(did);

            byte[] decodedDidDoc = BaseMultibaseUtil.decode(didDocApiResDto.getDidDoc());

            String didDocJson = new String(decodedDidDoc);
            DidManager didManager = BaseCoreDidUtil.parseDidDoc(didDocJson);

            return didManager.getDocument();
        } catch (OpenDidException e) {
            log.error("Failed to find DID document.", e);
            throw e;
        } catch (FeignException e) {
            log.error("Failed to find DID document.", e);
            throw new OpenDidException(ErrorCode.FIND_DID_DOC_FAILED);
        } catch (Exception e) {
            log.error("Failed to find DID document.", e);
            throw new OpenDidException(ErrorCode.FIND_DID_DOC_FAILED);
        }
    }

    /**
     * Registers a verifiable credential meta data.
     *
     * @param vcMeta The verifiable credential meta data to register
     */
    @Override
    public void registerVcMeta(VcMeta vcMeta) {

    }

    /**
     * Finds a verifiable credential meta data by VC ID.
     *
     * @param vcId The ID of the verifiable credential to find
     * @return The found verifiable credential meta data
     * @throws OpenDidException If the VC meta data cannot be found
     */
    @Override
    public VcMeta findVcMeta(String vcId) {
        try {
            VcMetaApiResDto vcMetaData = repositoryFeign.getVcMetaData(vcId);
            return BaseCoreVcUtil.parseVcMeta(vcMetaData.getVcMeta());
        } catch (OpenDidException e) {
            log.error("Failed to find VC meta data.", e);
            throw e;
        } catch (FeignException e) {
            log.error("Failed to find VC meta data.", e);
            throw new OpenDidException(ErrorCode.FIND_VC_META_FAILED);
        } catch (Exception e) {
            log.error("Failed to find VC meta data.", e);
            throw new OpenDidException(ErrorCode.FIND_VC_META_FAILED);
        }
    }
}
