package test;

import com.google.auto.value.AutoValue;
import com.karlicoss.auto.value.hash.AutoHash;

@AutoValue
@AutoHash
public abstract class IntegrationTestHelper {

    /*
        This value will be xor'd with 1_000_003 by AutoValue, so the resulting hash will be 123_456
     */
    private static final int HASH_CODE = 123_456 ^ 1_000_003;

    public abstract HashCodeCaptor captor();

    public static class HashCodeCaptor {
        public int hashCodeCalls = 0;

        @Override
        public int hashCode() {
            hashCodeCalls++;
            return HASH_CODE;
        }
    }
}