package com.github.karlicoss.auto.value.android_benchmark.hash;

import android.util.Pair;
import com.google.auto.value.AutoValue;
import com.karlicoss.auto.value.hash.AutoHash;

import java.util.List;

@AutoHash
@AutoValue
public abstract class Cached {

    public abstract String accountId();

    public abstract List<Integer> messageIds();

    public abstract List<Pair<String, String>> users();

    public static Cached create(String accountId, List<Integer> messageIds, List<Pair<String, String>> users) {
        return new AutoValue_Cached(accountId, messageIds, users);
    }
}
