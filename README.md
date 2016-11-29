[![Build Status](https://travis-ci.org/karlicoss/autohash.svg?branch=master)](https://travis-ci.org/karlicoss/autohash)

Cache your hash! Autohash is an [AutoValue](https://github.com/google/auto/tree/master/value) extension which makes your immutable value classes cache
their `hashCode` result in a thread safe and efficient manner.

The implementation is similar to `java.lang.String::hashCode`.

# Benchmarks

See detailed benchmark reports in `benchmarks` directory.

## JMH (desktop JVM benchmark)

Running:

    ./gradlew autohash:jmh

Benchmark results:

    Benchmark                                   (cachingOn)  Mode  Cnt  Score    Error  Units
    TestBenchmark.combineHashCodes                    false  avgt   30  0.087 ±  0.001  us/op
    TestBenchmark.combineHashCodes                     true  avgt   30  0.088 ±  0.001  us/op
    TestBenchmark.getFromSmallHashSet                 false  avgt   30  0.195 ±  0.001  us/op
    TestBenchmark.getFromSmallHashSet                  true  avgt   30  0.192 ±  0.001  us/op
    TestBenchmark.putInHashSet                        false  avgt   30  0.147 ±  0.023  us/op
    TestBenchmark.putInHashSet                         true  avgt   30  0.140 ±  0.021  us/op
    TestBenchmark.putInHashSetAndQueryOnce            false  avgt   30  0.222 ±  0.035  us/op
    TestBenchmark.putInHashSetAndQueryOnce             true  avgt   30  0.147 ±  0.001  us/op
    TestBenchmark.putInHashSetAndQueryTenTimes        false  avgt   30  0.952 ±  0.140  us/op
    TestBenchmark.putInHashSetAndQueryTenTimes         true  avgt   30  0.209 ±  0.001  us/op

Some explanations:

* `combineHashCodes` is just a sanity check; it invokes `hashCode` only once for each object, so performance should be roughly the same for caching and non caching versions.
* `getFromSmallHashSet`: queries items from a `HashSet` which contains none of them. As expected, performance is roughly the same.
* `putInHashSet`: just builds a `HashSet` from all items. As expected, performance is roughly the same.
* `putInHashSetAndQueryOnce`: builds a `HashSet` from all items and then queries them. As expected, performance is roughly the same.
* `putInHashSetAndQueryTenTimes`: same as above, but queries each item ten times. As expected, version with caching is way faster.

## Spanner (Android benchmark)

    TODO