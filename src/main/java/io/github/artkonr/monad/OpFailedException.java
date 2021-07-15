package io.github.artkonr.monad;

import io.github.artkonr.ensure.Ensure;

/**
 * An exception, which is suited to express general
 *  failures in a Java-friendly pattern by subclassing
 *  {@link RuntimeException} and delaying filling in of
 *  the stack trace until the created exception must be
 *  thrown. If throwing is not intended, behaves as a simple
 *  Java object and poses no additional overhead typically
 *  associated with Java exceptions.
 * <p>At the same time allows the client code to construct
 *  it like any other exception, i.e. with the stack trace
 *  filled.
 * @author Artem Kononov [@artkonr]
 */
public class OpFailedException extends RuntimeException {

  /**
   * Factory method. Constructs a new instance
   *  with the provided error message and fills
   *  in the stack trace.
   * @param message error message
   * @return new {@link RuntimeException}
   * @throws IllegalArgumentException if {@code null} or
   *  blank message provided
   */
  public static RuntimeException create(String message) {
    Ensure.notBlank(message, "message");
    return new OpFailedException(message, null, false, true);
  }

  /**
   * Factory method. Constructs a new instance
   *  with the provided root cause and fills
   *  in the stack trace.
   * @param cause root cause
   * @return new {@link RuntimeException}
   * @throws IllegalArgumentException if {@code null} argument provided
   */
  public static RuntimeException create(Throwable cause) {
    Ensure.notNull(cause, "cause");
    return new OpFailedException(cause.getMessage(), cause, false, true);
  }

  /**
   * Factory method. Constructs a new instance
   *  with the provided error message and root
   *  cause and fills in the stack trace.
   * @param message error message
   * @param cause root cause
   * @return new {@link RuntimeException}
   * @throws IllegalArgumentException if {@code null} or
   *  blank message provided or if no root cause provided
   */
  public static RuntimeException create(String message, Throwable cause) {
    Ensure.notBlank(message, "message");
    Ensure.notNull(cause, "cause");
    return new OpFailedException(message, cause, false, true);
  }

  /**
   * Factory method. Constructs a new instance
   *  with the provided error message and does
   *  not fill in the stack trace.
   * @param message error message
   * @return new {@link RuntimeException}
   * @throws IllegalArgumentException if {@code null} or
   *  blank message provided
   */
  public static RuntimeException lightweight(String message) {
    Ensure.notBlank(message, "message");
    return new OpFailedException(message, null, false, false);
  }

  /**
   * Factory method. Constructs a new instance
   *  with the provided root cause and does not
   *  fill in the stack trace.
   * @param cause root cause
   * @return new {@link RuntimeException}
   * @throws IllegalArgumentException if {@code null} argument provided
   */
  public static RuntimeException lightweight(Throwable cause) {
    Ensure.notNull(cause, "cause");
    return new OpFailedException(cause.getMessage(), cause, false, false);
  }

  /**
   * Factory method. Constructs a new instance
   *  with the provided error message and root
   *  cause and does not fill in the stack trace.
   * @param message error message
   * @param cause root cause
   * @return new {@link RuntimeException}
   * @throws IllegalArgumentException if {@code null} or
   *  blank message provided or if no root cause provided
   */
  public static RuntimeException lightweight(String message, Throwable cause) {
    Ensure.notBlank(message, "message");
    Ensure.notNull(cause, "cause");
    return new OpFailedException(message, cause, false, false);
  }

  /**
   * Default constructor.
   * @param message error message
   * @param cause root cause
   * @param enableSuppression whether to enable suppression
   * @param writableStackTrace whether to fill in stack trace on creation
   */
  OpFailedException(String message, Throwable cause,
                            boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

}
