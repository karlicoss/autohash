package com.karlicoss.auto.value.hash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark {@link com.google.auto.value.AutoValue} classes with this annotation to make them cache their
 * {@link Object#hashCode()} result. TODO see String.hashCode?
 *
 * This is safe as long as: hashCode is idempotent, that is,
 * returns same values on each invokation. Note that AutoValue instances being immutable doesn't mean something could
 * make underlying object change (e.g. you are using mutable Lists), but if it is the case, you are probably doing
 * something wrong anyway.
 *
 * // TODO should hashCode be thread safe?
 * This is also thread safe as long as original entities' hashCode is thread safe. Not that immutability doesn't always
 * implies thread safety as well, but as long as you don't change underlying data, you should be fine.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoHash {
}