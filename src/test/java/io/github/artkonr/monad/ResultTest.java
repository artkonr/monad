package io.github.artkonr.monad;

import io.github.artkonr.monad.Result;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

  @Test
  void ok_nullArgument_throws() {
    assertThrows(BAD_ARG, () -> Result.ok(null));
  }

  @Test
  void ok_isSuccess() {
    assertTrue(Result.ok(new Object()).isSuccess());
  }

  @Test
  void ok_notFailure() {
    assertFalse(Result.ok(new Object()).isFailure());
  }

  @Test
  void ok_getError_throws() {
    assertThrows(NoSuchElementException.class, () -> Result.ok(new Object()).getError());
  }

  @Test
  void ok_getErrorWithRemap_throws() {
    assertThrows(NoSuchElementException.class, () -> Result.ok(new Object()).getError(RuntimeException::new));
  }

  @Test
  void ok_getItem_same() {
    Object o = new Object();
    assertSame(o, Result.ok(o).orElseThrow());
  }

  @Test
  void ok_getItem_doesNotThrow() {
    assertDoesNotThrow(() -> Result.ok(new Object()).orElseThrow());
  }

  @Test
  void ok_getItem_notNull() {
    assertNotNull(Result.ok(new Object()).orElseThrow());
  }

  @Test
  void ok_getItemWrapError_doesNotThrow() {
    assertDoesNotThrow(() -> Result.ok(new Object()).orElseThrow(RuntimeException::new));
  }

  @Test
  void ok_getItemWrapError_notNull() {
    assertNotNull(Result.ok(new Object()).orElseThrow(RuntimeException::new));
  }

  @Test
  void ok_getItemOr_notNull() {
    assertNotNull(Result.ok(new Object()).orElse(null));
  }

  @Test
  void ok_getItemOr_same() {
    Object o1 = new Object();
    Object o2 = new Object();

    assertSame(o1, Result.ok(o1).orElse(o2));
  }

  @Test
  void ok_getItemOrProduce_nullArgument_throws() {
    assertThrows(BAD_ARG, () -> Result.ok(new Object()).orElseGet(null));
  }

  @Test
  void ok_getItemOrProduce_notNull() {
    assertNotNull(Result.ok(new Object()).orElseGet(() -> null));
  }

  @Test
  void ok_getItemOrProduce_same() {
    Object o1 = new Object();
    Object o2 = new Object();

    assertSame(o1, Result.ok(o1).orElseGet(() -> o2));
  }

  @Test
  void ok_throwIfError_doesNotThrow() {
    assertDoesNotThrow(() -> Result.ok(new Object()).throwIfError());
  }

  @Test
  void ok_toOptional_okOptional() {
    Object o = new Object();
    Optional<Object> r = Result.ok(o).toOptional();
    assertTrue(r.isPresent());
    assertSame(o, r.get());
  }

  @Test
  void ok_map_nullArgument_throws() {
    assertThrows(BAD_ARG, () -> Result.ok(new Object()).map(null));
  }

  @Test
  void ok_map_notNull() {
    assertNotNull(Result.ok(new Object()).map(o -> new Object()).orElseThrow());
  }

  @Test
  void ok_map_doesNotThrow() {
    assertDoesNotThrow(() -> Result.ok(new Object()).map(o -> new Object()).orElseThrow());
  }

  @Test
  void ok_map_resultNotSame() {
    Result<Object> r = Result.ok(new Object());
    assertNotSame(r, r.map(o -> new Object()));
  }

  @Test
  void ok_map_applied() {
    Object o1 = new Object();
    Object o2 = new Object();

    Result<Object> r = Result.ok(o1).map(o -> o2);
    assertNotSame(o1, r.orElseThrow());
  }

  @Test
  void ok_mapError_nullArgument_throws() {
    assertThrows(BAD_ARG, () -> Result.ok(new Object()).mapError(null));
  }

  @Test
  void ok_mapError_resultSame() {
    Result<Object> o = Result.ok(new Object());
    assertSame(o, o.mapError(RuntimeException::new));
  }

  @Test
  void ok_mapError_getError_throws() {
    assertThrows(NoSuchElementException.class, () -> Result.ok(new Object())
            .mapError(RuntimeException::new).getError());
  }

  // ----

  @Test
  void fail_nullArgument_throws() {
    assertThrows(BAD_ARG, () -> Result.fail((Throwable) null));
  }

  @Test
  void fail_notSuccess() {
    assertFalse(Result.fail(new RuntimeException()).isSuccess());
  }

  @Test
  void fail_isFailure() {
    assertTrue(Result.fail(new RuntimeException()).isFailure());
  }

  @Test
  void fail_getError_doesNotThrow() {
    assertDoesNotThrow(() -> Result.fail(new RuntimeException()).getError());
  }

  @Test
  void fail_getErrorWithRemap_doesNotThrow() {
    assertDoesNotThrow(() -> Result.fail(new RuntimeException()).getError(RuntimeException::new));
  }

  @Test
  void fail_getErrorWithRemap_notSame() {
    RuntimeException x = new RuntimeException();
    Result<Object> o = Result.fail(x);
    assertNotSame(x, o.getError(IOException::new));
  }

  @Test
  void fail_getItem_throws() {
    assertThrows(RuntimeException.class, () -> Result.fail(new RuntimeException()).orElseThrow());
  }

  @Test
  void fail_getItemWrapError_throws() {
    RuntimeException x1 = new RuntimeException();
    assertThrows(RuntimeException.class, () -> Result.fail(x1).orElseThrow(RuntimeException::new));
    try {
      Result.fail(x1).orElseThrow(RuntimeException::new);
    } catch (RuntimeException x2) {
      assertNotSame(x1, x2);
    }
  }

  @Test
  void fail_getItemOr_null() {
    assertNull(Result.fail(new RuntimeException()).orElse(null));
  }

  @Test
  void fail_getItemOr_doesNotThrow() {
    assertDoesNotThrow(() -> Result.fail(new RuntimeException()).orElse(new Object()));
  }

  @Test
  void fail_getItemOr_same() {
    Object o = new Object();

    assertSame(o, Result.fail(new RuntimeException()).orElse(o));
  }

  @Test
  void fail_getItemOrProduce_null() {
    assertNull(Result.fail(new RuntimeException()).orElseGet(() -> null));
  }

  @Test
  void fail_getItemOrProduce_same() {
    Object o = new Object();

    assertSame(o, Result.fail(new RuntimeException()).orElseGet(() -> o));
  }

  @Test
  void fail_throwIfError_throws() {
    assertThrows(RuntimeException.class, () -> Result.fail(new RuntimeException()).throwIfError());
  }

  @Test
  void fail_toOptional_emptyOptional() {
    Optional<Object> r = Result.fail(new RuntimeException()).toOptional();
    assertFalse(r.isPresent());
  }

  @Test
  void fail_map_throws() {
    assertThrows(RuntimeException.class, () -> Result.fail(new RuntimeException())
            .map(o -> new Object()).orElseThrow());
  }

  @Test
  void fail_mapError_resultNotSame() {
    Result<Object> o = Result.fail(new RuntimeException());
    assertNotSame(o, o.mapError(RuntimeException::new));
  }

  @Test
  void fail_mapError_applied() {
    RuntimeException x = new RuntimeException();
    Result<Object> o = Result.fail(x);
    assertNotSame(x, o.mapError(RuntimeException::new).getError());
  }

  private static final Class<? extends IllegalArgumentException> BAD_ARG = IllegalArgumentException.class;

}