package org.omnione.did.base.db.domain;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.*;
import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "\"admin_password_policy\"")
public class AdminPasswordPolicy extends BaseEntity implements Serializable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_length", nullable = false)
    private Short minLength;

    @Column(name = "require_uppercase", nullable = false)
    private Boolean requireUppercase;

    @Column(name = "require_number", nullable = false)
    private Boolean requireNumber;

    @Column(name = "require_special", nullable = false)
    private Boolean requireSpecial;

    @Column(name = "password_expiry_days", nullable = false)
    private Short passwordExpiryDays;
}
