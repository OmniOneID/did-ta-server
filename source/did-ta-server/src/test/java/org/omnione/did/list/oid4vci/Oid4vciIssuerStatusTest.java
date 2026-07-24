package org.omnione.did.list.oid4vci;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.db.constant.Oid4vciIssuerStatus;

import static org.junit.jupiter.api.Assertions.*;

class Oid4vciIssuerStatusTest {
    @Test
    void permitsOnlyDefinedTransitions() {
        assertTrue(Oid4vciIssuerStatus.REQUESTED.canTransitionTo(Oid4vciIssuerStatus.ACTIVE));
        assertTrue(Oid4vciIssuerStatus.REQUESTED.canTransitionTo(Oid4vciIssuerStatus.REJECTED));
        assertTrue(Oid4vciIssuerStatus.ACTIVE.canTransitionTo(Oid4vciIssuerStatus.SUSPENDED));
        assertTrue(Oid4vciIssuerStatus.SUSPENDED.canTransitionTo(Oid4vciIssuerStatus.ACTIVE));
        assertFalse(Oid4vciIssuerStatus.REJECTED.canTransitionTo(Oid4vciIssuerStatus.ACTIVE));
        assertFalse(Oid4vciIssuerStatus.REVOKED.canTransitionTo(Oid4vciIssuerStatus.ACTIVE));
        assertFalse(Oid4vciIssuerStatus.ACTIVE.canTransitionTo(Oid4vciIssuerStatus.REQUESTED));
    }
}
