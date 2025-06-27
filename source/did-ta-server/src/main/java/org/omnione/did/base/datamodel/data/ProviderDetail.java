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

package org.omnione.did.base.datamodel.data;

import lombok.*;

/**
 * This class represents the ProviderDetail structure.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProviderDetail {
    //@Spread Provider
    private String did;
    private String certVcRef; //provider 가입증명서 VC URL
    private String description; //provider 설명
    private String name; //provider 이름
    private String ref; //provider 참조정보 URL(홈페이지 등)
    private LogoImage logo; //provider 로고 이미지

}
