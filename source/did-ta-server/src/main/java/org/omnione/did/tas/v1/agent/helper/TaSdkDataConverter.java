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

package org.omnione.did.tas.v1.agent.helper;

import org.omnione.did.base.datamodel.data.AccE2e;
import org.omnione.did.base.datamodel.data.AttestedDidDoc;
import org.omnione.did.base.datamodel.data.DidAuth;
import org.omnione.did.base.datamodel.data.E2e;
import org.omnione.did.base.datamodel.data.Proof;
import org.omnione.did.base.datamodel.data.ReqRevokeVc;
import org.omnione.did.base.datamodel.data.SignedDidDoc;
import org.omnione.did.base.datamodel.data.Wallet;
import org.omnione.did.base.datamodel.enums.ProofPurpose;
import org.omnione.did.base.datamodel.enums.ProofType;
import org.omnione.did.common.datamodel.data.provider.Provider;

import java.util.List;

/**
 * Converts the server's API data types to and from the data types of the SDKs.
 * <p>
 * Signatures are computed over the serialized objects, so every field must be copied as is. The enums of the server
 * and the SDKs share their wire strings and are converted through them.
 */
public final class TaSdkDataConverter {

    private TaSdkDataConverter() {
    }

    public static org.omnione.did.common.datamodel.data.did.AttestedDidDoc toCommonAttestedDidDoc(AttestedDidDoc source) {
        if (source == null) {
            return null;
        }
        return new org.omnione.did.common.datamodel.data.did.AttestedDidDoc(
                source.getWalletId(),
                source.getOwnerDidDoc(),
                source.getProvider() == null ? null : new Provider(source.getProvider().getDid(), source.getProvider().getCertVcRef()),
                source.getNonce(),
                toCommonProof(source.getProof()));
    }

    /**
     * Converts to the DID Auth type of P120 Entity enrollment.
     */
    public static org.omnione.did.common.datamodel.data.did.DidAuth toCommonDidAuth(DidAuth source) {
        if (source == null) {
            return null;
        }
        return new org.omnione.did.common.datamodel.data.did.DidAuth(
                source.getDid(), source.getAuthNonce(), toDataModelProof(source.getProof()));
    }

    /**
     * Converts to the DID Auth type of the P141, P142 and P210 protocols.
     */
    public static org.omnione.did.tas.model.data.DidAuth toSdkDidAuth(DidAuth source) {
        if (source == null) {
            return null;
        }
        return org.omnione.did.tas.model.data.DidAuth.builder()
                .did(source.getDid())
                .authNonce(source.getAuthNonce())
                .proof(toCommonProof(source.getProof()))
                .build();
    }

    public static org.omnione.did.tas.model.data.SignedDidDoc toSdkSignedDidDoc(SignedDidDoc source) {
        if (source == null) {
            return null;
        }
        return org.omnione.did.tas.model.data.SignedDidDoc.builder()
                .ownerDidDoc(source.getOwnerDidDoc())
                .wallet(toSdkWallet(source.getWallet()))
                .nonce(source.getNonce())
                .proof(toCommonProof(source.getProof()))
                .build();
    }

    public static org.omnione.did.tas.model.data.AccE2e toSdkAccE2e(AccE2e source) {
        if (source == null) {
            return null;
        }
        return org.omnione.did.tas.model.data.AccE2e.builder()
                .publicKey(source.getPublicKey())
                .iv(source.getIv())
                .proof(toDataModelProof(source.getProof()))
                .build();
    }

    public static AccE2e toServerAccE2e(org.omnione.did.tas.model.data.AccE2e source) {
        if (source == null) {
            return null;
        }
        return AccE2e.builder()
                .publicKey(source.getPublicKey())
                .iv(source.getIv())
                .proof(toServerProof(source.getProof()))
                .build();
    }

    public static org.omnione.did.tas.model.data.E2e toSdkE2e(E2e source) {
        if (source == null) {
            return null;
        }
        return org.omnione.did.tas.model.data.E2e.builder()
                .iv(source.getIv())
                .encVc(source.getEncVc())
                .build();
    }

    public static E2e toServerE2e(org.omnione.did.tas.model.data.E2e source) {
        if (source == null) {
            return null;
        }
        return E2e.builder()
                .iv(source.getIv())
                .encVc(source.getEncVc())
                .build();
    }

    public static org.omnione.did.tas.model.data.ReqRevokeVc toSdkReqRevokeVc(ReqRevokeVc source) {
        if (source == null) {
            return null;
        }
        List<org.omnione.did.common.datamodel.data.signature.Proof> proofs = source.getProofs() == null ? null
                : source.getProofs().stream().map(TaSdkDataConverter::toCommonProof).toList();
        return org.omnione.did.tas.model.data.ReqRevokeVc.builder()
                .vcId(source.getVcId())
                .issuerNonce(source.getIssuerNonce())
                .proof(toCommonProof(source.getProof()))
                .proofs(proofs)
                .build();
    }

    public static ReqRevokeVc toServerReqRevokeVc(org.omnione.did.tas.model.data.ReqRevokeVc source) {
        if (source == null) {
            return null;
        }
        List<Proof> proofs = source.getProofs() == null ? null
                : source.getProofs().stream().map(TaSdkDataConverter::toServerProof).toList();
        return ReqRevokeVc.builder()
                .vcId(source.getVcId())
                .issuerNonce(source.getIssuerNonce())
                .proof(toServerProof(source.getProof()))
                .proofs(proofs)
                .build();
    }

    private static org.omnione.did.tas.model.data.Wallet toSdkWallet(Wallet source) {
        if (source == null) {
            return null;
        }
        return org.omnione.did.tas.model.data.Wallet.builder()
                .id(source.getId())
                .did(source.getDid())
                .build();
    }

    private static org.omnione.did.common.datamodel.data.signature.Proof toCommonProof(Proof source) {
        if (source == null) {
            return null;
        }
        return new org.omnione.did.common.datamodel.data.signature.Proof(
                source.getType() == null ? null
                        : org.omnione.did.common.datamodel.enums.did.ProofType.fromDisplayName(source.getType().toString()),
                source.getCreated(),
                source.getVerificationMethod(),
                source.getProofPurpose() == null ? null : toCommonProofPurpose(source.getProofPurpose()),
                source.getProofValue());
    }

    private static org.omnione.did.data.model.did.Proof toDataModelProof(Proof source) {
        if (source == null) {
            return null;
        }
        org.omnione.did.data.model.did.Proof proof = new org.omnione.did.data.model.did.Proof();
        proof.setType(source.getType() == null ? null : source.getType().toString());
        proof.setCreated(source.getCreated());
        proof.setVerificationMethod(source.getVerificationMethod());
        proof.setProofPurpose(source.getProofPurpose() == null ? null : source.getProofPurpose().toString());
        proof.setProofValue(source.getProofValue());
        return proof;
    }

    private static Proof toServerProof(org.omnione.did.common.datamodel.data.signature.Proof source) {
        if (source == null) {
            return null;
        }
        return Proof.builder()
                .type(source.getType() == null ? null : ProofType.fromDisplayName(source.getType().toString()))
                .created(source.getCreated())
                .verificationMethod(source.getVerificationMethod())
                .proofPurpose(source.getProofPurpose() == null ? null : ProofPurpose.valueOf(source.getProofPurpose().name()))
                .proofValue(source.getProofValue())
                .build();
    }

    private static Proof toServerProof(org.omnione.did.data.model.did.Proof source) {
        if (source == null) {
            return null;
        }
        return Proof.builder()
                .type(source.getType() == null ? null : ProofType.fromDisplayName(source.getType()))
                .created(source.getCreated())
                .verificationMethod(source.getVerificationMethod())
                .proofPurpose(source.getProofPurpose() == null ? null : toServerProofPurpose(source.getProofPurpose()))
                .proofValue(source.getProofValue())
                .build();
    }

    private static org.omnione.did.common.datamodel.enums.did.ProofPurpose toCommonProofPurpose(ProofPurpose source) {
        return org.omnione.did.common.datamodel.enums.did.ProofPurpose.valueOf(source.name());
    }

    private static ProofPurpose toServerProofPurpose(String wireValue) {
        for (ProofPurpose purpose : ProofPurpose.values()) {
            if (purpose.toString().equals(wireValue)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Unknown proof purpose: " + wireValue);
    }
}
