package test;

import java.lang.Override;
import java.lang.String;

final class AutoValue_Annotated extends $AutoValue_Annotated {
    private int hashCode;

    AutoValue_Annotated(int intp, String strp) {
        super(intp, strp);
    }

    @Override
    public int hashCode() {
        int hash = hashCode;
        if (hash == 0) {
            int zeroable = super.hashCode();
            hash = zeroable != 0 ? zeroable : 0xDEADBEEF;
            hashCode = hash;
        }
        return hash;
    }
}