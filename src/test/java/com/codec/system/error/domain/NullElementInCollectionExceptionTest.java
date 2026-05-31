package com.codec.system.error.domain;

import static org.assertj.core.api.Assertions.*;

import codec.error.domain.AssertionErrorType;
import codec.error.domain.NullElementInCollectionException;
import com.codec.system.UnitTest;
import org.junit.jupiter.api.Test;

@UnitTest
class NullElementInCollectionExceptionTest {

  @Test
  void shouldGetExceptionInformation() {
    NullElementInCollectionException exception = new NullElementInCollectionException("myField");

    assertThat(exception.type()).isEqualTo(AssertionErrorType.NULL_ELEMENT_IN_COLLECTION);
    assertThat(exception.field()).isEqualTo("myField");
    assertThat(exception.parameters()).isEmpty();
    assertThat(exception.getMessage()).isEqualTo("The field \"myField\" contains a null element");
  }
}
