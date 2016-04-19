package benchmarks;

import com.google.caliper.Param;
import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

/**
 * Measures the various ways the JDK converts doubles to strings.
 * // TODO add license and references to the source
 */
public class DoubleToStringBenchmark extends SimpleBenchmark {
    @Param Method method;

    public enum Method {
        TO_STRING {
            @Override String convert(double d) {
                return ((Double) d).toString();
            }
            @Override String convert(Double d) {
                return d.toString();
            }
        },
        STRING_VALUE_OF {
            @Override String convert(double d) {
                return String.valueOf(d);
            }
            @Override String convert(Double d) {
                return String.valueOf(d);
            }
        },
        STRING_FORMAT {
            @Override String convert(double d) {
                return String.format("%f", d);
            }
            @Override String convert(Double d) {
                return String.format("%f", d);
            }
        },
        QUOTE_TRICK {
            @Override String convert(double d) {
                return "" + d;
            }
            @Override String convert(Double d) {
                return "" + d;
            }
        },
        ;

        abstract String convert(double d);
        abstract String convert(Double d);
    }

    enum Value {
        Pi(Math.PI),
        NegativeZero(-0.0),
        NegativeInfinity(Double.NEGATIVE_INFINITY),
        NaN(Double.NaN);

        final double value;

        Value(double value) {
            this.value = value;
        }
    }

    @Param Value value;

    public int timePrimitive(int reps) {
        double d = value.value;
        int dummy = 0;
        for (int i = 0; i < reps; i++) {
            dummy += method.convert(d).length();
        }
        return dummy;
    }

    public int timeWrapper(int reps) {
        Double d = value.value;
        int dummy = 0;
        for (int i = 0; i < reps; i++) {
            dummy += method.convert(d).length();
        }
        return dummy;
    }

    public static void main(String[] args) throws Exception {
        Runner.main(DoubleToStringBenchmark.class, args);
    }
}
