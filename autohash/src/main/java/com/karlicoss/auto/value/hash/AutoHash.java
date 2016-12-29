package com.karlicoss.auto.value.hash;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.auto.value.AutoValue;

/**
 * Mark {@link AutoValue} classes with this annotation to make them cache their
 * {@link Object#hashCode()} result and avoid recomputing it on subsequent calls.
 * <br>
 * This is <b>safe</b> as long as your object is <b>immutable</b>. Note that AutoValue instances being immutable (they've only got getters)
 * doesn't make underlying objects immutable. For instance, one of your properties might be an {@link java.util.ArrayList},
 * however as long as it is logically immutable, that is, you enforce a contract for immutability, you are fine.
 * <br>
 * This is also <b>thread safe</b> as long as <b>original object's hashCode is thread safe</b>. Note that immutability doesn't
 * imply thread safety as well, but as long as you don't change underlying data and publish it safely, you should be fine.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoHash {
}