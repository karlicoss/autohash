package test;

import javax.annotation.Generated;

@Generated("com.google.auto.value.processor.AutoValueProcessor")
final class AutoValue_Unannotated extends Unannotated {

    private final int intp;
    private final String strp;

    AutoValue_Unannotated(int intp, String strp) {
        this.intp = intp;
        if (strp == null) {
            throw new NullPointerException("Null strp");
        }
        this.strp = strp;
    }

    @Override
    public int intp() {
        return intp;
    }

    @Override
    public String strp() {
        return strp;
    }

    @Override
    public String toString() {
        return "Unannotated{"
                + "intp=" + intp + ", "
                + "strp=" + strp
                + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof Unannotated) {
            Unannotated that = (Unannotated) o;
            return (this.intp == that.intp())
                    && (this.strp.equals(that.strp()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.intp;
        h *= 1000003;
        h ^= this.strp.hashCode();
        return h;
    }
}