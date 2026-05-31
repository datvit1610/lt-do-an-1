package com.codec.system.error.infrastructure.primary;

import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import com.codec.system.LogsSpy;
import com.codec.system.UnitTest;
import codec.error.domain.AssertionErrorType;
import codec.error.domain.AssertionException;
import com.codec.system.error_generator.domain.NullElementInCollectionExceptionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.MessageSource;

@UnitTest
@ExtendWith(LogsSpy.class)
class AssertionErrorsHandlerTest {

  private static final AssertionErrorsHandler handler = new AssertionErrorsHandler(mock(MessageSource.class));

  private final LogsSpy logs;

  public AssertionErrorsHandlerTest(LogsSpy logs) {
    this.logs = logs;
  }

  @Test
  void shouldLogPrimaryAssertionExceptionInInfo() {
    handler.handleAssertionError(new DefaultAssertionException());

    logs.shouldHave(Level.INFO, "Oops");
  }

  @Test
  void shouldLogDomainAssertionExceptionInError() {
    handler.handleAssertionError(NullElementInCollectionExceptionFactory.nullElementInCollection());

    logs.shouldHave(Level.ERROR, "a null element");
  }

  private static class DefaultAssertionException extends AssertionException {

    protected DefaultAssertionException() {
      super("field", "Oops");
    }

    @Override
    public AssertionErrorType type() {
      return AssertionErrorType.MISSING_MANDATORY_VALUE;
    }
  }
}
