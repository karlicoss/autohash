package test;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Unannotated {
    public abstract int intp();

    public abstract String strp();
}