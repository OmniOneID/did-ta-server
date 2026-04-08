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

package org.omnione.did.base.db.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;

/**
 * Entity class for the list_certificate_vc table.
 * Stores published certificate VCs accessible via the list provider API.
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "\"list_certificate_vc\"")
public class ListCertificateVc extends BaseEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "did", nullable = false, length = 200)
    private String did;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "certificate_vc", nullable = false, columnDefinition = "text")
    private String certificateVc;

    @Column(name = "published_url", nullable = false, length = 2000)
    private String publishedUrl;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;

    @Column(name = "expired_at", nullable = false)
    private Instant expiredAt;
}
