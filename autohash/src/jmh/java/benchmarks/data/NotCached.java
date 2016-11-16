package benchmarks.data;

import com.google.auto.value.AutoValue;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@AutoValue
public abstract class NotCached {

    public abstract String accountId();

    public abstract List<Integer> messageIds();

    public abstract List<Pair<String, String>> users();

    public static NotCached create(String accountId, List<Integer> messageIds, List<Pair<String, String>> users) {
        return new AutoValue_NotCached(accountId, messageIds, users);
    }
}