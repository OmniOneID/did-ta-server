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

package org.omnione.did.base.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validator for the {@link NonEmptyList} annotation.
 * This annotation is used to validate that a list is not empty.
 * If the list is null, the validation will pass.
 */
public class NonEmptyListValidator implements ConstraintValidator<NonEmptyList, List<?>> {

    /**
     * Validates whether the given list is not empty.
     * If the list is null, the validation passes by default, assuming that null checks are handled by the {@link jakarta.validation.constraints.NotNull} annotation.
     *
     * @param list The list to validate.
     * @param context The context in which the constraint is evaluated.
     * @return true if the list is null or not empty, false otherwise.
     */
    @Override
    public boolean isValid(List<?> list, ConstraintValidatorContext context) {
        if (list == null) {
            return true;
        }
        return !list.isEmpty();
    }
}