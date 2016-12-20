package com.github.karlicoss.auto.value.android_benchmark.hash;

import android.support.test.InstrumentationRegistry;
import android.util.Pair;
import dk.ilios.spanner.BeforeRep;
import dk.ilios.spanner.Benchmark;
import dk.ilios.spanner.BenchmarkConfiguration;
import dk.ilios.spanner.Param;
import dk.ilios.spanner.SpannerConfig;
import dk.ilios.spanner.config.RuntimeInstrumentConfig;
import dk.ilios.spanner.junit.SpannerRunner;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import static java.lang.String.valueOf;

@RunWith(SpannerRunner.class)
public class CachedVsNotCachedBenchmark {

    private static final File RESULTS_DIR = new File(InstrumentationRegistry.getTargetContext().getFilesDir(), "results");

    private static final String BENCHMARK_FILENAME = CachedVsNotCachedBenchmark.class.getCanonicalName() + "_" + System.currentTimeMillis() + ".json";

    @BenchmarkConfiguration
    public SpannerConfig configuration = new SpannerConfig.Builder()
            .saveResults(RESULTS_DIR, BENCHMARK_FILENAME) // Save results to disk
            .addInstrument(
                    new RuntimeInstrumentConfig.Builder()
                            .measurements(300)
                            .build()
            )
            .build();
    
    @Param(value = {"false", "true",})
    public String cachingOn;

    static class Input {
        public final String accountId;
        public final List<Integer> messageIds;
        public final List<Pair<String, String>> users;
        public Input(Random random) {
            int length1 = 5 + random.nextInt(5);
            int length2 = 5 + random.nextInt(5);
            accountId = valueOf(random.nextLong());
            messageIds = new ArrayList<>(length1);
            for (int i = 0; i < length1; i++) {
                messageIds.add(random.nextInt());
            }
            users = new ArrayList<>(length2);
            for (int i = 0; i < length2; i++) {
                Pair<String, String> p = Pair.create(valueOf(random.nextLong()), valueOf(random.nextLong()));
                users.add(p);
            }
        }
    }

    private final List<Input> inputs;
    {
        Random random = new Random(123413);
        int count = 10;
        inputs = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            inputs.add(new Input(random));
        }
    }

    public List<Object> items;

    @BeforeRep
    public void before() {
        boolean on = Boolean.parseBoolean(cachingOn);
        items = new ArrayList<>(inputs.size());
        for (Input input : inputs) {
            final Object item;
            if (on) {
                item = Cached.create(input.accountId, input.messageIds, input.users);
            } else {
                item = NotCached.create(input.accountId, input.messageIds, input.users);
            }
            items.add(item);
        }
    }

    /*
        Just a sanity check to avoid any additional overhead.
        Results should be roughly the same, cached version will be slightly slower.
     */
    @Benchmark
    public int combineHashCodes() {
        int dummy = 0;

        int combined = 0;
        for (Object item : items) {
            combined ^= item.hashCode();
        }

        dummy += combined;

        return dummy;
    }

    @Benchmark
    public boolean getFromSmallHashSet() {
        HashSet<Object> set = new HashSet<>();
        // add few items to prevent check for empty map
        set.add(new Object());
        set.add(4);
        set.add("tralalal");

        boolean dummy = true;
        for (Object item: items) {
            dummy &= set.contains(item);
        }

        return dummy;
    }

    @Benchmark
    public int putInHashSet() {
        HashSet<Object> set = new HashSet<>(items);

        int dummy = set.size();

        return dummy;
    }

    @Benchmark
    public boolean putInHashSetAndGetOnce() {
        HashSet<Object> set = new HashSet<>(items);

        boolean dummy = true;
        for (Object item: items) {
            dummy &= set.contains(item);
        }

        return dummy;
    }

    @Benchmark
    public boolean putInHashSetAndGetTenTimes() {
        HashSet<Object> set = new HashSet<>(items);

        boolean dummy = true;
        for (int q = 0; q < 10; q++) {
            for (Object item : items) {
                dummy &= set.contains(item);
            }
        }

        return dummy;
    }
}