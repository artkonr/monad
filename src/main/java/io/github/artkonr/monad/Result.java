package io.github.artkonr.monad;

import io.github.artkonr.ensure.Ensure;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A result monad. Distinguishes 2 states: success and failure.
 *  Provides {@link Optional}-like methods to handle its state
 *  in a functional style.
 * <p>At the core of it, this object is comprised of two fields
 *  only: {@code item} and {@code error}.
 * <p><b>Success</b> implies that this result bears a non-null object
 *  of some generified type. It is guaranteed that in such a
 *  case no error is present and client code can reason of the
 *  actual success of the operation this {@link Result} represents.
 * <p><b>Failure</b> implies that this result holds a reference
 *  to some error which is represented by a {@link OpFailedException}.
 *  This is done in order to allow client code to throw from the
 *  methods of this class without having to deal with the checked
 *  exceptions. It is guaranteed, that if this {@link Result} is a
 *  failure, no success object is present and client code may reason
 *  of the actual failure of the operation this {@link Result} represents.
 * <p>Implementation detail: to allow client code to reflect failures
 *  without having to deal with the overhead of filling in the stack
 *  trace upon exception object creation, {@link OpFailedException} is
 *  created in a "lightweight" mode. Stack trace is then filled in if
 *  the internal exception is thrown.
 * @param <T> type of the object this {@link Result} will
 *            bear if it represents a success
 * @author Artem Kononov [@artkonr]
 */
public class Result<T> {

  private final T item;
  private final RuntimeException error;

  /**
   * Factory method. Creates a new <b>success</b> {@link Result}.
   * @param item result item
   * @param <T> type of the result item
   * @return success {@link Result}
   * @throws IllegalArgumentException if no argument provided: this
   *  implementation implies that result item is always non-null or
   *  else considered not present.
   */
  public static <T> Result<T> ok(T item) {
    Ensure.notNull(item, "item");
    return new Result<>(item, null);
  }

  /**
   * Factory method. Creates a new <b>failure</b> {@link Result}.
   *  Wraps the provided {@link Throwable} cause into a stackless
   *  {@link OpFailedException} to avoid filling in the stack trace
   *  before it is actually needed.
   * @param error {@link Throwable} which represents the failure
   * @param <T> type of the result item
   * @return failure {@link Result}
   * @throws IllegalArgumentException if no argument provided
   */
  public static <T> Result<T> fail(Throwable error) {
    Ensure.notNull(error, "error");
    return new Result<>(null, OpFailedException.lightweight(error));
  }

  /**
   * Factory method. Creates a new <b>failure</b> {@link Result}.
   *  Uses the provided {@link String} to construct a stackless
   *  {@link OpFailedException} to avoid filling in the stack trace
   *  before it is actually needed.
   * @param reason failure reason
   * @param <T> type of the result item
   * @return failure {@link Result}
   * @throws IllegalArgumentException if provided failure message
   *  is {@code null} or empty
   */
  public static <T> Result<T> fail(String reason) {
    Ensure.notBlank(reason, "reason");
    return new Result<>(null,  OpFailedException.lightweight(reason));
  }

  /**
   * Checks if this {@link Result} is a <b>failure</b>.
   * @return {@code true} if this {@link Result} represents
   *  a failure and bears an error reference; {@code false}
   *  - if otherwise
   */
  public boolean isFailure() {
    return error != null;
  }

  /**
   * Checks if this {@link Result} is a <b>success</b>.
   * @return {@code true} if this {@link Result} represents
   *  a success and bears an item reference; {@code false}
   *  - if otherwise
   */
  public boolean isSuccess() {
    return item != null;
  }

  /**
   * Applies a provided {@link Function} to the
   *  {@link Result} item if it is a success {@link
   *  Result} or simply returns a new object with
   *  the error within.
   * @param func mapping function
   * @return processed {@link Result}
   * @throws IllegalArgumentException if no argument provided
   */
  public <M> Result<M> map(Function<T, M> func) {
    Ensure.notNull(func, "func");
    if (isFailure())
      return fail(this.error);
    return ok(func.apply(item));
  }

  /**
   * Applies a provided {@link Function} to the
   *  {@link Result} error if it is present or
   *  returns {@code this} if it is not.
   * <p>If the mapping succeeds, the {@link Result}
   *  returned is a <b>new</b> object compared to
   *  {@code this}.
   * @param func mapping function
   * @return processed {@link Result}
   * @throws IllegalArgumentException if no argument provided
   */
  public Result<T> mapError(Function<RuntimeException, RuntimeException> func) {
    Ensure.notNull(func, "func");
    if (isSuccess())
      return this;
    return fail(func.apply(error));
  }

  /**
   * Retrieves the error state of this {@link Result}.
   *  Throws if it is not a <b>failure</b>.
   * @return error
   * @throws NoSuchElementException if this {@link Result}
   *  is not a failure
   */
  public RuntimeException getError() {
    if (isSuccess())
      throw new NoSuchElementException(NO_ERR_MSG);
    return error;
  }

  /**
   * Retrieves the error state of this {@link Result}
   *  and applies a mapping {@link Function} which
   *  converts it to {@link Throwable}. Throws if it is
   *  not a <b>failure</b>.
   * @param remap remapping {@link Function}
   * @return error
   * @throws IllegalArgumentException if no argument provided
   * @throws NoSuchElementException if this {@link Result}
   *  is not a failure
   */
  public Throwable getError(Function<RuntimeException, Throwable> remap) {
    Ensure.notNull(remap, "remap");
    if (isSuccess())
      throw new NoSuchElementException(NO_ERR_MSG);
    return remap.apply(error);
  }

  /**
   * Throws the held error if it is present.
   */
  public void throwIfError() {
    if (isFailure())
      throw new OpFailedException(error.getMessage(), error.getCause(), false, true);
  }

  /**
   * Returns the {@link Result} item if this {@link
   *  Result} is a <b>success</b> or another object
   *  provided as argument.
   * @param another object to return if this {@link Result}
   *                is a failure; <b>nullable</b>
   * @return {@link Result} item or supplied object
   */
  public T orElse(T another) {
    if (isFailure())
      return another;
    return item;
  }

  /**
   * Returns the {@link Result} item if this {@link
   *  Result} is a <b>success</b> or another object
   *  created by a provided {@link Supplier}.
   * @param factory {@link Supplier} invoked to return if
   *                this {@link Result} is a failure;
   * @return {@link Result} item or object from {@link Supplier}
   * @throws IllegalArgumentException if no argument provided
   */
  public T orElseGet(Supplier<T> factory) {
    Ensure.notNull(factory, "factory");
    if (isSuccess())
      return item;
    return factory.get();
  }

  /**
   * Returns the {@link Result} item if this {@link
   *  Result} is a <b>success</b> or throws error held.
   * @return {@link Result} item or object from {@link Supplier}
   * @throws RuntimeException exception contained by this
   *  {@link Result} if it is a failure
   */
  public T orElseThrow() {
    if (isFailure())
      throw new OpFailedException(error.getMessage(), error.getCause(), false, true);
    return item;
  }

  /**
   * Returns the {@link Result} item if this {@link
   *  Result} is a <b>success</b> or throws the product
   *  of applying the provided remapping {@link Function}
   *  to the {@link Result} error. The said function returns
   *  {@link RuntimeException}.
   * @param remap remapping {@link Function}
   * @return {@link Result} item or object from {@link Supplier}
   * @throws RuntimeException result of mapping applied to the
   *  exception contained by this {@link Result} if it is a failure
   * @throws IllegalArgumentException if no argument provided
   */
  public T orElseThrow(Function<RuntimeException, RuntimeException> remap) {
    Ensure.notNull(remap, "remap");
    if (isFailure())
      throw remap.apply(new OpFailedException(error.getMessage(), error.getCause(), false, true));
    return item;
  }

  /**
   * Converts this {@link Result} to {@link Optional}
   *  by taking the {@link Result} item and discarding
   *  the error.
   * @return {@link Optional}
   */
  public Optional<T> toOptional() {
    return Optional.ofNullable(item);
  }

  /**
   * Converts this {@link Result} to {@link Stream}
   *  by taking the {@link Result} item and discarding
   *  the error.
   * @return {@link Stream}
   */
  public Stream<T> toStream() {
    return Stream.ofNullable(item);
  }

  /**
   * Performs an equality check.
   * @param o object to compare to
   * @return {@code true} if objects are equal;
   *  {@code false} - if otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Result<?> result = (Result<?>) o;

    if (!Objects.equals(item, result.item)) return false;
    return Objects.equals(error, result.error);
  }

  /**
   * Calculates hash code.
   * @return calculated hash code
   */
  @Override
  public int hashCode() {
    int result = item != null ? item.hashCode() : 0;
    result = 31 * result + (error != null ? error.hashCode() : 0);
    return result;
  }

  /**
   * Returns textual description.
   * @return textual representation
   */
  @Override
  public String toString() {
    return "Result[" + (isSuccess() ? "ok=" + item : "fail=" + error) + ']';
  }

  private Result(T item, RuntimeException error) {
    this.item = item;
    this.error = error;
  }

  private static final String NO_ERR_MSG = "Requested result object represents a success";

}
