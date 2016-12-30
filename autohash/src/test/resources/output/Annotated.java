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
        if (hashCode == 0) {
            int hash = super.hashCode();
            hashCode = hash != 0 ? hash : 0xDEADBEEF;
        }
        return hashCode;
    }
}