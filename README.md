[![Build Status](https://travis-ci.org/karlicoss/autohash.svg?branch=master)](https://travis-ci.org/karlicoss/autohash)  [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.karlicoss.auto.value/autohash/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.karlicoss.auto.value/autohash)

TLDR: cache your hash!

Imagine you've got an immutable [AutoValue](https://github.com/google/auto/tree/master/value) entity. AutoValue generates `hashCode` for us, but if you pass your immutable
object around in your code, each time `hashCode` is called, it will be recomputed. Sounds like a waste of precious CPU given that the object is immutable, right?

AutoHash is an extension which makes your immutable value classes cache their `hashCode` result in a thread safe and efficient manner.

The implementation is similar to `java.lang.String::hashCode`.

# Usage
First, beware that you should only use this if you know that your objects are logically immutable
 (e.g. `ArrayList` is totally mutable, but for your specific object you might have the contract of immutability).
 If immutability is the case though, just add the `@AutoHash` annotation to your value class definition. It's that simple!

```java
@AutoHash
@AutoValue
abstract class Person {
    abstract String name();
    abstract String passportNumber();
    abstract List<Integer> genes();
}
```

, and that's it! Now if you call `hashCode` on a `Person` multiple times, it will only be computed once
(in worst case, once for each thread which uses your object).

# Download

The library is published on Maven Central/JCenter.

For regular Java Maven/Gradle project, you just need the dependency `com.github.karlicoss.auto.value:autohash:<version>`
 in `provided` configuration.

For Android, you're gonna need the [android-apt](https://bitbucket.org/hvisser/android-apt) plugin. 
You need a dependency in `provided` and `apt` configurations:

```groovy
dependencies {
    def autoHashVersion = 'com.github.karlicoss.auto.value:autohash:<version>'
    provided autoHashVersion
    apt autoHashVersion
}
```

# Benchmarks

See detailed benchmark reports in `benchmarks` directory.

## JMH (desktop JVM benchmark)

### Running

    ./gradlew autohash:jmh

### Benchmark results

    Benchmark                                   (cachingOn)  Mode  Cnt  Score    Error  Units
    TestBenchmark.combineHashCodes                    false  avgt   30  0.087 ±  0.001  us/op
    TestBenchmark.combineHashCodes                     true  avgt   30  0.088 ±  0.001  us/op
    TestBenchmark.getFromSmallHashSet                 false  avgt   30  0.195 ±  0.001  us/op
    TestBenchmark.getFromSmallHashSet                  true  avgt   30  0.192 ±  0.001  us/op
    TestBenchmark.putInHashSet                        false  avgt   30  0.147 ±  0.023  us/op
    TestBenchmark.putInHashSet                         true  avgt   30  0.140 ±  0.021  us/op
    TestBenchmark.putInHashSetAndGetOnce              false  avgt   30  0.222 ±  0.035  us/op
    TestBenchmark.putInHashSetAndGetOnce               true  avgt   30  0.147 ±  0.001  us/op
    TestBenchmark.putInHashSetAndGetTenTimes          false  avgt   30  0.952 ±  0.140  us/op
    TestBenchmark.putInHashSetAndGetTenTimes           true  avgt   30  0.209 ±  0.001  us/op

Some explanations:

* `combineHashCodes` is just a sanity check; it invokes `hashCode` only once for each object, so performance should be roughly the same for caching and non caching versions.
* `getFromSmallHashSet`: queries items from a `HashSet` which contains none of them. As expected, performance is roughly the same.
* `putInHashSet`: just builds a `HashSet` from all items. As expected, performance is roughly the same.
* `putInHashSetAndGetOnce`: builds a `HashSet` from all items and then queries them. As expected, performance is better with caching.
* `putInHashSetAndGetTenTimes`: same as above, but queries each item ten times. As expected, version with caching is way faster.

## Spanner (Android benchmark)

Android microbenchmarking tools do not seem to be as advanced as desktop JVM ones. Anyway, I used 
[Spanner](https://github.com/cmelchior/spanner) project which is a fork of [Caliper](https://github.com/google/caliper) 
framework with some improvements. Nicest thing about Spanner is it provides Gradle plugin, so we can run our benchmarks 
as JUnit tests. Both Spanner and Caliper lack proper documentation though, so I hope I got everything right.

If you want to learn more about Spanner/Caliper benchmarks, look at examples and their source code:

* https://github.com/google/caliper/tree/master/examples/src/main/java/examples
* https://github.com/google/caliper/tree/master/tutorial
* https://github.com/cmelchior/spanner/tree/master/sample

### Running

Make sure your device is connected (emulator would not be a good benchmarking target). Run with: 

    ./gradlew android-benchmark:connectedAndroidTest

### Benchmark results

Benchmarks were ran on Nexus 6P (see `deviceinfo.txt` in the benchmark directory).

Easiest way to collect results is just use `adb logcat -s Spanner` command.
Also, results are saved on the device in the file specified by the `SpannerConfig.Builder#saveResults` method.

      Benchmark Methods:   [combineHashCodes, getFromSmallHashSet, putInHashSet, putInHashSetAndGetOnce, putInHashSetAndGetTenTimes]
      Instruments:   [RuntimeInstrument]
      User parameters:   {}
      Selection type:    null
    This selection yields 10 experiments.
    Trial Report (1 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=combineHashCodes, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=20573.00, 1st qu.=71888.25, median=118047.00 (-), mean=102183.15, 3rd qu.=123580.75, max=264792.00
    Trial Report (2 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=combineHashCodes, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=18802.00, 1st qu.=119739.25, median=123125.00 (-), mean=123047.22, 3rd qu.=126653.25, max=278386.00
    Trial Report (3 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=getFromSmallHashSet, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=85469.00, 1st qu.=164284.00, median=192292.00 (-), mean=203028.47, 3rd qu.=207226.00, max=1193437.00
    Trial Report (4 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=getFromSmallHashSet, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=64479.00, 1st qu.=193815.00, median=202161.50 (-), mean=205173.97, 3rd qu.=216576.00, max=1213385.00
    Trial Report (5 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSet, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=64739.00, 1st qu.=166797.00, median=174505.00 (-), mean=174710.40, 3rd qu.=186210.75, max=352292.00
    Trial Report (6 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSet, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=61198.00, 1st qu.=149636.00, median=163046.50 (-), mean=163324.18, 3rd qu.=173242.00, max=335313.00
    Trial Report (7 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetOnce, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=37553.00, 1st qu.=163229.00, median=167969.00 (-), mean=172181.58, 3rd qu.=179675.00, max=474115.00
    Trial Report (8 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetOnce, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=55104.00, 1st qu.=176914.25, median=190547.00 (-), mean=187500.89, 3rd qu.=205768.75, max=888334.00
    Trial Report (9 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetTenTimes, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=116042.00, 1st qu.=310273.00, median=319193.00 (-), mean=311776.21, 3rd qu.=324726.25, max=489479.00
    Trial Report (10 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetTenTimes, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=89271.00, 1st qu.=236302.00, median=243698.00 (-), mean=248912.01, 3rd qu.=271068.00, max=363594.00
    Collected 3000 measurements from:
      1 instrument(s)
      10 benchmark(s)
    Execution complete: 7.856 min.


From the results, you can notice that for `combineHashCodes`, `getFromSmallHashSet` and `putInHashSet`, there is almost no difference as expected. For `putInHashSetAndGetOnce` cached version is slightly worse, however for `putInHashSetAndGetTenTimes` we can finally spot the performance improvement.

To take a closer look, I ran same benchmark, but with 10 objects being cached instead of 1:

    Experiment selection:
      Benchmark Methods:   [combineHashCodes, getFromSmallHashSet, putInHashSet, putInHashSetAndGetOnce, putInHashSetAndGetTenTimes]
      Instruments:   [RuntimeInstrument]
      User parameters:   {}
      Selection type:    null
    This selection yields 10 experiments.
    Trial Report (1 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=combineHashCodes, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=66042.00, 1st qu.=182526.00, median=188541.00 (-), mean=194221.17, 3rd qu.=196549.00, max=1485520.00
    Trial Report (2 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=combineHashCodes, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=58072.00, 1st qu.=197813.00, median=205026.50 (-), mean=202310.79, 3rd qu.=213060.00, max=584583.00
    Trial Report (3 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=getFromSmallHashSet, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=67865.00, 1st qu.=309492.25, median=317552.00 (-), mean=319214.55, 3rd qu.=331718.75, max=805729.00
    Trial Report (4 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=getFromSmallHashSet, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=122709.00, 1st qu.=289544.00, median=297943.00 (-), mean=296414.07, 3rd qu.=308138.00, max=483282.00
    Trial Report (5 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSet, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=128229.00, 1st qu.=307904.00, median=319245.00 (-), mean=314665.10, 3rd qu.=326966.00, max=536771.00
    Trial Report (6 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSet, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=126302.00, 1st qu.=316822.50, median=337500.00 (-), mean=332563.06, 3rd qu.=350507.25, max=795208.00
    Trial Report (7 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetOnce, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=111458.00, 1st qu.=337656.00, median=364714.00 (-), mean=359480.19, 3rd qu.=383332.75, max=706823.00
    Trial Report (8 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetOnce, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=103282.00, 1st qu.=307292.00, median=336458.00 (-), mean=332103.98, 3rd qu.=348073.00, max=496406.00
    Trial Report (9 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetTenTimes, parameters={cachingOn=false}}
      Results:
        runtime(ns): min=282812.00, 1st qu.=820547.25, median=835130.00 (-), mean=830399.96, 3rd qu.=841393.00, max=1272136.00
    Trial Report (10 of 10):
      Experiment {instrument=RuntimeInstrument, benchmarkMethod=putInHashSetAndGetTenTimes, parameters={cachingOn=true}}
      Results:
        runtime(ns): min=158490.00, 1st qu.=471067.50, median=494687.00 (-), mean=484364.10, 3rd qu.=502955.25, max=904583.00
    Collected 3000 measurements from:
      1 instrument(s)
      10 benchmark(s)
    Execution complete: 7.917 min.

Here, the difference between cached and non cached versions is more noticable: you can see improvement for `putInHashSetAndGetOnce` and `putInHashSetAndGetTenTimes`.

Anyway, the Android benchmark was way harder to conduct, and I wouldn't call it very reliable since the benchmarking tool is not as advanced as JMH. Although you can notice the general trend.


# License

    Copyright 2016 Dmitrii Gerasimov.
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
