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

package org.omnione.did.tas.v1.config;

import org.omnione.did.tas.v1.config.provider.CertificateProviderImpl;
import org.omnione.did.tas.v1.config.provider.DidDocumentProviderImpl;
import org.omnione.did.tas.v1.config.provider.EcdhSessionProviderImpl;
import org.omnione.did.tas.v1.config.provider.EntityProviderImpl;
import org.omnione.did.tas.v1.config.provider.KycProviderImpl;
import org.omnione.did.tas.v1.config.provider.StorageProviderImpl;
import org.omnione.did.tas.v1.config.provider.TasProviderImpl;
import org.omnione.did.tas.v1.config.provider.TokenProviderImpl;
import org.omnione.did.tas.v1.config.provider.TransactionProviderImpl;
import org.omnione.did.tas.v1.config.provider.UserProviderImpl;
import org.omnione.did.tas.v1.config.provider.VcSchemaProviderImpl;
import org.omnione.did.tas.v1.config.provider.WalletProviderImpl;
import org.omnione.did.tas.v1.config.provider.WalletRegistrationProviderImpl;
import lombok.RequiredArgsConstructor;
import org.omnione.did.tas.core.vc.VcBuilder;
import org.omnione.did.tas.v1.common.service.query.TasQueryService;
import org.omnione.did.tas.protocol.entity.EntityEnrollmentProtocol;
import org.omnione.did.tas.protocol.entity.EntityEnrollmentProtocolImpl;
import org.omnione.did.tas.protocol.tas.TasEnrollmentProtocol;
import org.omnione.did.tas.protocol.tas.TasEnrollmentProtocolImpl;
import org.omnione.did.tas.protocol.diddoc.DidDocRestoreProtocol;
import org.omnione.did.tas.protocol.diddoc.DidDocRestoreProtocolImpl;
import org.omnione.did.tas.protocol.diddoc.DidDocUpdateProtocol;
import org.omnione.did.tas.protocol.diddoc.DidDocUpdateProtocolImpl;
import org.omnione.did.tas.protocol.user.UserRegistrationProtocol;
import org.omnione.did.tas.protocol.user.UserRegistrationProtocolImpl;
import org.omnione.did.tas.protocol.vc.VcIssuanceProtocol;
import org.omnione.did.tas.protocol.vc.VcIssuanceProtocolImpl;
import org.omnione.did.tas.protocol.vc.VcRevocationProtocol;
import org.omnione.did.tas.protocol.vc.VcRevocationProtocolImpl;
import org.omnione.did.tas.protocol.wallet.WalletRegistrationProtocol;
import org.omnione.did.tas.protocol.wallet.WalletRegistrationProtocolImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Spring configuration that registers SDK protocol implementations as beans.
 *
 * <p>Each protocol accesses the server's database and wallet through Provider interfaces.</p>
 */
@Configuration
@RequiredArgsConstructor
@Profile("!sample")
public class TaSdkConfiguration {

    private final TasProviderImpl tasProvider;
    private final StorageProviderImpl storageProvider;
    private final WalletProviderImpl walletProvider;
    private final CertificateProviderImpl certificateProvider;
    private final VcSchemaProviderImpl vcSchemaProvider;
    private final TransactionProviderImpl transactionProvider;
    private final EntityProviderImpl entityProvider;
    private final EcdhSessionProviderImpl ecdhSessionProvider;
    private final DidDocumentProviderImpl didDocumentProvider;
    private final WalletRegistrationProviderImpl walletRegistrationProvider;
    private final TokenProviderImpl tokenProvider;
    private final KycProviderImpl kycProvider;
    private final UserProviderImpl userProvider;
    private final TasQueryService tasQueryService;

    @Bean
    public VcBuilder vcBuilder() {
        return new CertificateVcBuilder(vcSchemaProvider, tasQueryService);
    }

    @Bean
    public TasEnrollmentProtocol tasEnrollmentProtocol(VcBuilder vcBuilder) {
        return new TasEnrollmentProtocolImpl(
                tasProvider,
                storageProvider,
                walletProvider,
                certificateProvider,
                vcBuilder
        );
    }

    @Bean
    public WalletRegistrationProtocol walletRegistrationProtocol() {
        return new WalletRegistrationProtocolImpl(
                entityProvider,
                walletRegistrationProvider,
                storageProvider,
                didDocumentProvider,
                certificateProvider,
                walletProvider
        );
    }

    @Bean
    public EntityEnrollmentProtocol entityEnrollmentProtocol(VcBuilder vcBuilder) {
        return new EntityEnrollmentProtocolImpl(
                transactionProvider,
                entityProvider,
                ecdhSessionProvider,
                storageProvider,
                walletProvider,
                didDocumentProvider,
                certificateProvider,
                vcBuilder
        );
    }

    @Bean
    public UserRegistrationProtocol userRegistrationProtocol() {
        return new UserRegistrationProtocolImpl(
                tokenProvider,
                kycProvider,
                userProvider,
                didDocumentProvider,
                walletProvider,
                storageProvider,
                certificateProvider
        );
    }

    @Bean
    public DidDocRestoreProtocol didDocRestoreProtocol() {
        return new DidDocRestoreProtocolImpl(
                tokenProvider,
                transactionProvider,
                didDocumentProvider,
                storageProvider
        );
    }

    @Bean
    public DidDocUpdateProtocol didDocUpdateProtocol() {
        return new DidDocUpdateProtocolImpl(
                tokenProvider,
                userProvider,
                didDocumentProvider,
                walletProvider,
                storageProvider,
                certificateProvider,
                transactionProvider
        );
    }

    @Bean
    public VcIssuanceProtocol vcIssuanceProtocol() {
        return new VcIssuanceProtocolImpl(
                tokenProvider,
                didDocumentProvider
        );
    }

    @Bean
    public VcRevocationProtocol vcRevocationProtocol() {
        return new VcRevocationProtocolImpl();
    }
}
