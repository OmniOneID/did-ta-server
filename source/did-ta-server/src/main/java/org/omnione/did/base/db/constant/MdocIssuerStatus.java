package org.omnione.did.base.db.constant;

import java.util.EnumSet;
import java.util.Set;

public enum MdocIssuerStatus {
    ACTIVE,
    SUSPENDED,
    REVOKED;

    public boolean canTransitionTo(MdocIssuerStatus target) {
        Set<MdocIssuerStatus> allowed = switch (this) {
            case ACTIVE -> EnumSet.of(SUSPENDED, REVOKED);
            case SUSPENDED -> EnumSet.of(ACTIVE, REVOKED);
            case REVOKED -> EnumSet.noneOf(MdocIssuerStatus.class);
        };
        return allowed.contains(target);
    }

    public static boolean requiresReason(MdocIssuerStatus target) {
        return target == SUSPENDED || target == REVOKED;
    }
}
