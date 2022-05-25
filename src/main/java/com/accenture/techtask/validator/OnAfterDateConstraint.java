package com.accenture.techtask.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = OnAfterDateValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OnAfterDateConstraint {
    String message() default "Invalid transaction date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
