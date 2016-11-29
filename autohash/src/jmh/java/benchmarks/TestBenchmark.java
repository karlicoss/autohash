package benchmarks;

import benchmarks.data.Cached;
import benchmarks.data.NotCached;
import org.apache.commons.lang3.tuple.Pair;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;


@Warmup(iterations=5)
@Measurement(iterations=30)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(1)
public class TestBenchmark {

    @State(Scope.Thread)
    public static class MyState {
        static class Input {
            public final String accountId;
            public final List<Integer> messageIds;
            public final List<Pair<String, String>> users;
            public Input(Random random) {
                int length1 = 5 + random.nextInt(5);
                int length2 = 5 + random.nextInt(5);
                accountId = String.valueOf(random.nextLong());
                messageIds = new ArrayList<>(length1);
                for (int i = 0; i < length1; i++) {
                    messageIds.add(random.nextInt());
                }
                users = new ArrayList<>(length2);
                for (int i = 0; i < length2; i++) {
                    Pair<String, String> p = Pair.of(valueOf(random.nextLong()), valueOf(random.nextLong()));
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

        @Param({"false", "true"})
        public boolean cachingOn;

        public List<Object> items;

        @Setup(Level.Invocation)
        public void setup() {
            items = new ArrayList<>(inputs.size());
            for (Input input : inputs) {
                final Object item;
                if (cachingOn) {
                    item = Cached.create(input.accountId, input.messageIds, input.users);
                } else {
                    item = NotCached.create(input.accountId, input.messageIds, input.users);
                }
                items.add(item);
            }
        }
    }

    /**
     * Just a sanity check to avoid any additional overhead.
     * Should be roughly the same, with enabled caching might be slightly slower.
     */
    @Benchmark
    public void combineHashCodes(MyState state, Blackhole hole) {
        final List<Object> items = state.items;

        int combined = 0;
        for (Object item : items) {
            combined ^= item.hashCode();
        }
        hole.consume(combined);
    }

    /**
     * Should be slower with enabled caching.
     */
    @Benchmark
    public void getFromSmallHashSet(MyState state, Blackhole hole) {
        final List<Object> items = state.items;

        HashSet<Object> set = new HashSet<>();
        // add few items to prevent check for empty map
        set.add(new Object());
        set.add(4);
        set.add("tralalal");

        boolean result = true;
        for (Object item: items) {
            result &= set.contains(item);
        }
        hole.consume(result);
    }

    /**
     * Should be slower with enabled caching.
     */
    @Benchmark
    public void putInHashSet(MyState state, Blackhole hole) {
        final List<Object> items = state.items;

        HashSet<Object> set = new HashSet<>(items);
        hole.consume(set);
    }

    /**
     * Should be faster with enabled caching
     */
    @Benchmark
    public void putInHashSetAndQueryOnce(MyState state, Blackhole hole) {
        final List<Object> items = state.items;

        HashSet<Object> set = new HashSet<>(items);

        boolean result = true;
        for (Object item: items) {
            result &= set.contains(item);
        }
        hole.consume(result);
    }

    /**
     * Should be faster with enabled caching.
     */
    @Benchmark
    public void putInHashSetAndQueryTenTimes(MyState state, Blackhole hole) {
        final List<Object> items = state.items;

        HashSet<Object> set = new HashSet<>(items);

        boolean result = true;
        for (int i = 0; i < 10; i++) {
            for (Object item: items) {
                result &= set.contains(item);
            }
        }
        hole.consume(result);
    }
}
