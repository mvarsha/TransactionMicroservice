package com.accenture.techtask.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;
import java.util.Date;

public class OnAfterDateValidator implements
        ConstraintValidator<OnAfterDateConstraint, Date> {

    @Override
    public void initialize(OnAfterDateConstraint transactionTime) {
    }

    @Override
    public boolean isValid(Date transactionTime,
                           ConstraintValidatorContext cxt) {
        return transactionTime != null && transactionTime.compareTo(Date.from(Instant.now())) >= 0;
    }
}
