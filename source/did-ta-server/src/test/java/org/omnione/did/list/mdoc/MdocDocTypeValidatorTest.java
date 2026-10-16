package org.omnione.did.list.mdoc;

import org.junit.jupiter.api.Test;
import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;
import org.omnione.did.list.v1.common.MdocDocTypeValidator;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MdocDocTypeValidatorTest {
    @Test
    void trimsAndRemovesDuplicatesKeepingOrder() {
        List<String> result = MdocDocTypeValidator.docTypes(List.of(
                " org.iso.18013.5.1.mDL ", "eu.europa.ec.eudi.pid.1", "org.iso.18013.5.1.mDL"));

        assertEquals(List.of("org.iso.18013.5.1.mDL", "eu.europa.ec.eudi.pid.1"), result);
    }

    @Test
    void rejectsMissingOrEmptyList() {
        assertInvalid(null);
        assertInvalid(List.of());
    }

    @Test
    void rejectsBlankWhitespaceOrTooLongValues() {
        assertInvalid(Arrays.asList("org.iso.18013.5.1.mDL", null));
        assertInvalid(List.of("  "));
        assertInvalid(List.of("org.iso mDL"));
        assertInvalid(List.of("a".repeat(MdocDocTypeValidator.MAX_LENGTH + 1)));
    }

    private static void assertInvalid(List<String> values) {
        OpenDidException exception = assertThrows(OpenDidException.class,
                () -> MdocDocTypeValidator.docTypes(values));
        assertEquals(ErrorCode.MDOC_ISSUER_DOCTYPE_INVALID, exception.getErrorCode());
    }
}
