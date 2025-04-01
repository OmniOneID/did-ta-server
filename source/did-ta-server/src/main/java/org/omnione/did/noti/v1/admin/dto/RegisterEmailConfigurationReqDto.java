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
package org.omnione.did.noti.v1.admin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RegisterEmailConfigurationReqDto {
    @NotNull(message = "host cannot be null")
    private String host;
    @NotNull(message = "port cannot be null")
    private Integer port;
    @NotNull(message = "username cannot be null")
    private String username;
    @NotNull(message = "password cannot be null")
    private String password;
    @NotNull(message = "sender cannot be null")
    private String sender;
    @NotNull(message = "startTlsEnabled cannot be null")
    private Boolean startTlsEnabled;
    @NotNull(message = "sslEnabled cannot be null")
    private Boolean sslEnabled;
    @NotNull(message = "connectionTimeout cannot be null")
    private Integer connectionTimeout;
    @NotNull(message = "readTimeout cannot be null")
    private Integer readTimeout;
    @NotNull(message = "writeTimeout cannot be null")
    private Integer writeTimeout;
    @NotNull(message = "ignoreSslValidation cannot be null")
    private Boolean ignoreSslValidation;
}
