package de.ilyes.device.config.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = BetweenPresentAndPastMonthsValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BetweenPresentAndPastMonths {

  String message() default "Device creationDate must be between now and past month.";

  int numberOfMonths() default 1;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
