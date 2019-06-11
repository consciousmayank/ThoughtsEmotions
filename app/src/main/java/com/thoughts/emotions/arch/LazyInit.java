package com.thoughts.emotions.arch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation on any static or field that will be initialized lazily, where races yield no
 * semantic difference in the code (as, for example, is the case with {@link String#hashCode}). Note
 * that lazily initializing a non-volatile field is hard to do correctly, and one should rarely use
 * this. It should also only be done by developers who clearly understand the potential issues, and
 * then, always using the pattern as presented in the {@code getData} method of this sample code
 * below:
 *
 * <pre>{@code
 * private final String source;
 * {@literal @}LazyInit private String data;
 *
 * public String getData() {
 *   String local = data;
 *   if (local == null) {
 *     local = data = expensiveCalculation(source);
 *   }
 *   return local;
 *  }
 *
 * private static String expensiveCalculation(String string) {
 *   return string.replaceAll(" ", "_");
 * }
 * }</pre>
 *
 * <p>The need for using the {@code local} variable is detailed in
 * http://jeremymanson.blogspot.com/2008/12/benign-data-races-in-java.html (see, particularly, the
 * part after "Now, let's break the code").
 *
 * <p>Also note that {@code LazyInit} must not be used on 64-bit primitives ({@code long}s and
 * {@code double}s), because the Java Language Specification does not guarantee that writing to
 * these is atomic. Furthermore, when used for non-primitives, the non-primitive must be either
 * truly immutable or at least thread safe (in the Java memory model sense). And callers must
 * accommodate the fact that different calls to something like the above getData() method may return
 * different (though identically computed) objects, with different identityHashCode() values. Again,
 * unless you really understand this <b>and</b> you really need the performance benefits of
 * introducing the data race, do not use this construct.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LazyInit {
}
