[![Build Status](https://travis-ci.org/karlicoss/autohash.svg?branch=master)](https://travis-ci.org/karlicoss/autohash)

Cache your hash! Autohash is an [AutoValue](https://github.com/google/auto/tree/master/value) extension which makes your immutable value classes cache
their `hashCode` result in a thread safe and efficient manner.

The implementation is similar to `java.lang.String::hashCode`.


## TODO

- [ ] Make equals method use hashCode to check whether it should compare the entities field-by-field at all, make it configurable.