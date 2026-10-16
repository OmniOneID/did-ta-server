package org.omnione.did.list.mdoc;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.db.constant.MdocIssuerStatus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MdocIssuerStatusTest {
    @Test
    void permitsOnlyDefinedTransitions() {
        assertTrue(MdocIssuerStatus.ACTIVE.canTransitionTo(MdocIssuerStatus.SUSPENDED));
        assertTrue(MdocIssuerStatus.ACTIVE.canTransitionTo(MdocIssuerStatus.REVOKED));
        assertTrue(MdocIssuerStatus.SUSPENDED.canTransitionTo(MdocIssuerStatus.ACTIVE));
        assertTrue(MdocIssuerStatus.SUSPENDED.canTransitionTo(MdocIssuerStatus.REVOKED));

        assertFalse(MdocIssuerStatus.ACTIVE.canTransitionTo(MdocIssuerStatus.ACTIVE));
        assertFalse(MdocIssuerStatus.REVOKED.canTransitionTo(MdocIssuerStatus.ACTIVE));
        assertFalse(MdocIssuerStatus.REVOKED.canTransitionTo(MdocIssuerStatus.SUSPENDED));
    }

    @Test
    void requiresReasonForSuspensionAndRevocation() {
        assertTrue(MdocIssuerStatus.requiresReason(MdocIssuerStatus.SUSPENDED));
        assertTrue(MdocIssuerStatus.requiresReason(MdocIssuerStatus.REVOKED));
        assertFalse(MdocIssuerStatus.requiresReason(MdocIssuerStatus.ACTIVE));
    }
}
