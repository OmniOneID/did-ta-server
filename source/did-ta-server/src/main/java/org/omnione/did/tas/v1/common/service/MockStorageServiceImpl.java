package org.omnione.did.tas.v1.common.service;

import lombok.extern.slf4j.Slf4j;
import org.omnione.did.data.model.did.DidDocument;
import org.omnione.did.data.model.did.InvokedDidDoc;
import org.omnione.did.data.model.enums.did.DidDocStatus;
import org.omnione.did.data.model.enums.vc.RoleType;
import org.omnione.did.data.model.enums.vc.VcStatus;
import org.omnione.did.data.model.schema.VcSchema;
import org.omnione.did.data.model.vc.VcMeta;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("sample")
public class MockStorageServiceImpl implements StorageService {
    @Override
    public void registerDidDoc(InvokedDidDoc didDoc, RoleType roleType) {

    }

    @Override
    public void updateDidDocStatus(String did, DidDocStatus didDocStatus) {
    }

    @Override
    public DidDocument findDidDoc(String didKeyUrl) {
        return null;
    }

    @Override
    public void registerVcMeta(VcMeta vcMeta) {

    }
    @Override
    public void updateVcMeta(String vcId, VcStatus vcStatus) {
    }

    @Override
    public VcMeta findVcMeta(String vcId) {
        return null;
    }

    @Override
    public void registerVcSchema(VcSchema vcSchema, String did) {

    }

    @Override
    public VcSchema getVcSchema(String vcSchemaId) {
        return null;
    }
}
