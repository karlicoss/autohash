package test;

import com.google.auto.value.AutoValue;
import com.karlicoss.auto.value.hash.AutoHash;

@AutoValue
@AutoHash
public abstract class Annotated {
    public abstract int intp();

    public abstract String strp();
}