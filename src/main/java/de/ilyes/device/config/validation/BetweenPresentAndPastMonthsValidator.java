package de.ilyes.device.config.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZonedDateTime;

public class BetweenPresentAndPastMonthsValidator
    implements ConstraintValidator<BetweenPresentAndPastMonths, ZonedDateTime> {
  private int numberOfMonths = 1;

  @Override
  public void initialize(BetweenPresentAndPastMonths constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
    numberOfMonths = constraintAnnotation.numberOfMonths();
  }

  @Override
  public boolean isValid(
      ZonedDateTime zonedDateTime, ConstraintValidatorContext constraintValidatorContext) {

    if (zonedDateTime == null || numberOfMonths <= 0) {
      return false;
    }

    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime pastLimit = now.minusMonths(numberOfMonths);
    return zonedDateTime.isBefore(now) && zonedDateTime.isAfter(pastLimit);
  }
}
