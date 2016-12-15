package com.github.karlicoss.auto.value.android_benchmark.hash;

import android.support.test.InstrumentationRegistry;
import android.util.Pair;
import dk.ilios.spanner.BeforeExperiment;
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

// TODO use black hole?
@RunWith(SpannerRunner.class)
public class CachedVsNotCachedBenchmark {

    private File filesDir = InstrumentationRegistry.getTargetContext().getFilesDir();
    private File resultsDir = new File(filesDir, "results");
//    private File baseLineFile = Utils.copyFromAssets("baseline.json");

    @BenchmarkConfiguration
    public SpannerConfig configuration = new SpannerConfig.Builder()
            .saveResults(resultsDir, CachedVsNotCachedBenchmark.class.getCanonicalName() + ".json") // Save results to disk
//            .useBaseline(baseLineFile) // Compare against a baseline
            .medianFailureLimit(Float.MAX_VALUE) // Fail if difference vs. baseline is to big. Should normally be 10-15%  (0.15)
            .addInstrument(
                    new RuntimeInstrumentConfig.Builder()
                    .measurements(100)
                    .build()
            )
            .build();

    // Public test parameters (value chosen and injected by Experiment)
    @Param(value = {"true", "false"})
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
        int count = 1;
        inputs = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            inputs.add(new Input(random));
        }
    }

    public List<Object> items;

    @BeforeExperiment
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
    public void combineHashCodes() {
        int combined = 0;
        for (Object item : items) {
            combined ^= item.hashCode();
        }
    }

    @Benchmark
    public void getFromSmallHashSet() {
        HashSet<Object> set = new HashSet<>(items);
        // add few items to prevent check for empty map
        set.add(new Object());
        set.add(4);

        boolean result = true;
        for (Object item: items) {
            result &= set.contains(item);
        }
    }

    @Benchmark
    public void putInHashSet() {
        HashSet<Object> set = new HashSet<>(items);
    }

    @Benchmark
    public void putInHashSetAndQuery() {
        HashSet<Object> set = new HashSet<>(items);

        boolean result = true;
        for (Object item: items) {
            result &= set.contains(item);
        }
    }

    @Benchmark
    public void putInHashSetAndQueryThreeTimes() {
        HashSet<Object> set = new HashSet<>(items);

        boolean result = true;
        for (int i = 0; i < 3; i++) {
            for (Object item: items) {
                result &= set.contains(item);
            }
        }
    }
}