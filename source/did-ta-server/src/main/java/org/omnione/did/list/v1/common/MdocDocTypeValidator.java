package org.omnione.did.list.v1.common;

import org.omnione.did.base.exception.ErrorCode;
import org.omnione.did.base.exception.OpenDidException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Normalizes mDoc docType lists (e.g. "org.iso.18013.5.1.mDL"): trims, drops duplicates (keeping order),
 * and rejects empty lists or values that are blank, too long, or contain whitespace.
 */
public final class MdocDocTypeValidator {
    public static final int MAX_LENGTH = 200;
    private static final Pattern NO_WHITESPACE = Pattern.compile("^\\S+$");

    private MdocDocTypeValidator() {
    }

    public static List<String> docTypes(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new OpenDidException(ErrorCode.MDOC_ISSUER_DOCTYPE_INVALID);
        }
        Set<String> normalized = new LinkedHashSet<>();
        for (String value : values) {
            String docType = value == null ? "" : value.trim();
            if (docType.isEmpty() || docType.length() > MAX_LENGTH || !NO_WHITESPACE.matcher(docType).matches()) {
                throw new OpenDidException(ErrorCode.MDOC_ISSUER_DOCTYPE_INVALID);
            }
            normalized.add(docType);
        }
        return new ArrayList<>(normalized);
    }
}
