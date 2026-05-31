package com.codec.system;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.entry;

import java.util.Set;
  import java.util.function.Predicate;
  import javax.validation.ConstraintViolation;
  import javax.validation.Validation;
  import javax.validation.Validator;
public final class BeanValidationAssertions {

  private BeanValidationAssertions() {}

  public static <T> BeanAsserter<T> assertThatBean(T bean) {
    return new BeanAsserter<>(bean);
  }

  public static class BeanAsserter<T> {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final Set<ConstraintViolation<T>> violations;

    private BeanAsserter(T bean) {
      assertThat(bean).as("Can't check validation agains a null bean").isNotNull();

      violations = validator.validate(bean);
    }

    public BeanAsserter<T> isValid() {
      assertThat(violations).isEmpty();

      return this;
    }

    public InvalidPropertyAsserter<T> hasInvalidProperty(String property) {
      assertThat(property).as("Can't check validation for a blank property").isNotBlank();

      return violations
        .stream()
        .filter(withProperty(property))
        .findFirst()
        .map(validation -> new InvalidPropertyAsserter<>(this, validation))
        .orElseThrow(() -> new AssertionError("Property " + property + " must be invalid and wasn't"));
    }

    private Predicate<ConstraintViolation<T>> withProperty(String property) {
      return validation -> property.equals(validation.getPropertyPath().toString());
    }
  }

  public static class InvalidPropertyAsserter<T> {

    private final BeanAsserter<T> beanAsserter;
    private final ConstraintViolation<T> violation;

    private InvalidPropertyAsserter(BeanAsserter<T> beanAsserter, ConstraintViolation<T> violation) {
      this.beanAsserter = beanAsserter;
      this.violation = violation;
    }

    public InvalidPropertyAsserter<T> withMessage(String message) {
      assertThat(message).as("Can't check message without message").isNotBlank();

      assertThat(violation.getMessage()).isEqualTo(message);

      return this;
    }

    public InvalidPropertyAsserter<T> withParameter(String parameter, Object value) {
      assertThat(parameter).as("Can't check parameter without parameter").isNotBlank();

      assertThat(violation.getConstraintDescriptor().getAttributes()).contains(entry(parameter, value));

      return this;
    }

    public BeanAsserter<T> and() {
      return beanAsserter;
    }
  }
}
