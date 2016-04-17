package benchmarks;

import javafx.util.Pair;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.String.valueOf;


@Warmup(iterations=10)
@Measurement(iterations=10)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(10)
public class TestBenchmark {

    @State(Scope.Thread)
    public static class MyState {
        public static class Single {
            public final String accountId;
            public final List<Integer> messageIds;
            public final List<Pair<String, String>> users;
            public Single(Random random) {
                int length1 = random.nextInt(10);
                int length2 = random.nextInt(10);
                accountId = String.valueOf(random.nextLong());
                messageIds = new ArrayList<>(length1);
                for (int i = 0; i < length1; i++) {
                    messageIds.add(random.nextInt());
                }
                users = new ArrayList<>(length2);
                for (int i = 0; i < length2; i++) {
                    Pair<String, String> p = new Pair<>(valueOf(random.nextLong()), valueOf(random.nextLong()));
                    users.add(p);
                }
            }
        }


        public final List<Single> singles;
        {
            Random random = new Random(123413);
            singles = new ArrayList<>(100);
            for (int i = 0; i < 100; i++) {
                singles.add(new Single(random));
            }
        }
        public final List<Cached> cacheds = new ArrayList<>(singles.size());
        {
            for (MyState.Single single : singles) {
                cacheds.add(Cached.create(single.accountId, single.messageIds, single.users));
            }
        }
        public final List<NotCached> notCacheds = new ArrayList<>(singles.size());
        {
            for (MyState.Single single : singles) {
                notCacheds.add(NotCached.create(single.accountId, single.messageIds, single.users));
            }
        }


    }

    @Benchmark
    public void cached(MyState state, Blackhole hole) {
        List<Cached> items = state.cacheds;
        HashSet<Cached> set = new HashSet<>();
        for (Cached cached : items) {
            set.add(cached);
        }
        HashSet<Cached> set2 = new HashSet<>();
        for (Cached cached : items) {
            set2.add(cached);
        }

        HashSet<Cached> set3 = new HashSet<>(set);
        set3.addAll(set2);

        boolean res = true;
        for (Cached item : set3) {
            res &= set.contains(item);
            res &= set2.contains(item);
        }
        hole.consume(res);
    }


    @Benchmark
    public void notCached(MyState state, Blackhole hole) {
        List<NotCached> items = state.notCacheds;
        HashSet<NotCached> set = new HashSet<>();
        for (NotCached notCached : items) {
            set.add(notCached);
        }
        HashSet<NotCached> set2 = new HashSet<>();
        for (NotCached cached : items) {
            set2.add(cached);
        }

        HashSet<NotCached> set3 = new HashSet<>(set);
        set3.addAll(set2);

        boolean res = true;
        for (NotCached item : set3) {
            res &= set.contains(item);
            res &= set2.contains(item);
        }
        hole.consume(res);
    }

}
