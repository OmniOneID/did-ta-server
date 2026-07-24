package org.omnione.did.base.db.constant;

import java.util.EnumSet;
import java.util.Set;

public enum Oid4vciIssuerStatus {
    REQUESTED,
    ACTIVE,
    REJECTED,
    SUSPENDED,
    REVOKED;

    public boolean canTransitionTo(Oid4vciIssuerStatus target) {
        Set<Oid4vciIssuerStatus> allowed = switch (this) {
            case REQUESTED -> EnumSet.of(ACTIVE, REJECTED);
            case ACTIVE -> EnumSet.of(SUSPENDED, REVOKED);
            case SUSPENDED -> EnumSet.of(ACTIVE, REVOKED);
            case REJECTED, REVOKED -> EnumSet.noneOf(Oid4vciIssuerStatus.class);
        };
        return allowed.contains(target);
    }

    public static boolean requiresReason(Oid4vciIssuerStatus target) {
        return target == REJECTED || target == SUSPENDED || target == REVOKED;
    }
}
