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

import com.google.gson.JsonSyntaxException;
import lombok.RequiredArgsConstructor;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.base.util.BaseCoreDidUtil;
import org.omnione.did.base.util.BaseCoreVcUtil;
import org.omnione.did.base.util.BaseMultibaseUtil;
import org.omnione.did.common.util.JsonUtil;
import org.omnione.did.core.manager.DidManager;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.tas.v1.agent.api.RepositoryFeign;
import org.omnione.did.tas.v1.agent.api.dto.InputVcSchemaReqDto;
import org.omnione.did.tas.v1.agent.api.dto.RegisterDidApiReqDto;
import org.omnione.did.tas.v1.agent.api.dto.UpdateVcMetaStatusReqDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.vc.VcMeta;
import org.omnione.did.tas.v1.agent.api.dto.UpdateDidDocStatusReqDto;
import org.omnione.did.zkp.datamodel.util.GsonWrapper;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Storage service implementation for managing DID documents and verifiable credentials.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Primary
@Profile("lss")
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
                    .roleType(roleType.name())
                    .didDoc(didDoc)
                    .build();

            String request = JsonUtil.serializeToJson(apiRegisterDidReqDto);
            repositoryFeign.registerDid(request);
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
     * @param did          The DID of the document to update
     * @param didDocStatus The new status of the document
     */
    @Override
    public void updateDidDocStatus(String did, DidDocStatus didDocStatus) {
        try {
            UpdateDidDocStatusReqDto updateDidDocStatusReqDto = UpdateDidDocStatusReqDto.builder()
                    .did(did)
                    .didDocStatus(didDocStatus)
                    .build();;

            repositoryFeign.updateDid(updateDidDocStatusReqDto);
        } catch (OpenDidException e) {
            log.error("Failed to update DID document.", e);
            throw e;
        } catch (FeignException e) {
            log.error("Failed to update DID document.", e);
            throw new OpenDidException(ErrorCode.UPDATE_DID_DOC_FAILED);
        } catch (Exception e) {
            log.error("Failed to update DID document.", e);
            throw new OpenDidException(ErrorCode.UPDATE_DID_DOC_FAILED);
        }
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
            String didDocument = repositoryFeign.getDid(didKeyUrl);

            DidManager didManager = BaseCoreDidUtil.parseDidDoc(didDocument);

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
        try {
            repositoryFeign.registerVcMeta(vcMeta);
        } catch (OpenDidException e) {
            log.error("Failed to register VC Metadata.", e);
            throw e;
        } catch (FeignException e) {
            log.error("Failed to register VC Metadata.", e);
            throw new OpenDidException(ErrorCode.VC_META_REGISTRATION_FAILED);
        } catch (Exception e) {
            log.error("Failed to register VC Metadata.", e);
            throw new OpenDidException(ErrorCode.VC_META_REGISTRATION_FAILED);
        }
    }

    /**
     * Finds a verifiable credential meta data by VC ID.
     *
     * @param vcId The ID of the verifiable credential to find
     * @return The found verifiable credential meta data
     * @throws OpenDidException If the VC metadata cannot be found
     */
    @Override
    public VcMeta findVcMeta(String vcId) {
        try {
            String vcMetaData = repositoryFeign.getVcMetaData(vcId);

            return BaseCoreVcUtil.parseVcMeta(vcMetaData);
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

    @Override
    public void registerVcSchema(VcSchema vcSchema, String did) {
        String encodedVcSchema = encodeVcSchema(vcSchema);
        repositoryFeign.registerVcSchema(
                InputVcSchemaReqDto.builder()
                        .did(did)
                        .vcSchema(encodedVcSchema)
                        .build()
        );
    }

    private String encodeVcSchema(VcSchema vcSchema) {
        try {
            String vcSchemaJson = GsonWrapper.getGson().toJson(vcSchema);
            return BaseMultibaseUtil.encode(vcSchemaJson.getBytes(StandardCharsets.UTF_8));
        } catch (JsonSyntaxException e) {
            log.error("\t--> Failed to encode Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.ENCODING_FAILED);
        } catch (Exception e) {
            log.error("\t--> Unexpected error while encoding Credential Schema: {}", e.getMessage());
            throw new OpenDidException(ErrorCode.ENCODING_FAILED);
        }
    }

    @Override
    public VcSchema getVcSchema(String vcSchemaId) {
        String vcSchema = repositoryFeign.getVcSchema(vcSchemaId);
        return GsonWrapper.getGson().fromJson(vcSchema, VcSchema.class);
    }

    /**
     * Updates the status of a DID document.
     *
     * @param vcId        The VC ID of the VC Meta to update
     * @param vcStatus    The new status of the VC Meta
     */
    @Override
    public void updateVcMeta(String vcId, VcStatus vcStatus) {
        try {
            UpdateVcMetaStatusReqDto request = UpdateVcMetaStatusReqDto.builder()
                    .vcId(vcId)
                    .vcStatus(vcStatus)
                    .build();

            repositoryFeign.updateVcMetaStatus(request);
        } catch (OpenDidException e) {
            log.error("Failed to update VC Metadata.", e);
            throw e;
        } catch (FeignException e) {
            log.error("Failed to update VC Metadata.", e);
            throw new OpenDidException(ErrorCode.VC_STATUS_UPDATE_FAILED);
        } catch (Exception e) {
            log.error("Failed to update VC Metadata.", e);
            throw new OpenDidException(ErrorCode.VC_STATUS_UPDATE_FAILED);
        }
    }
}
