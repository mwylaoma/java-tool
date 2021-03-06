package org.osgl.util;

import org.osgl.$;
import org.osgl.exception.NotAppliedException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * The namespace under which number relevant structures, functions and logics are
 * defined
 */
public class N {

    N() {
    }

    public static enum Type {
        BYTE(1) {
            @Override
            Number add(Number a, Number b) {
                return a.byteValue() + b.byteValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.byteValue() - b.byteValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.byteValue() * b.byteValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.byteValue() / b.byteValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.byteValue();
            }
        }, SHORT(5) {
            @Override
            Number add(Number a, Number b) {
                return a.shortValue() + b.shortValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.shortValue() - b.shortValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.shortValue() * b.shortValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.shortValue() / b.shortValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.shortValue();
            }
        }, INT(10) {
            @Override
            Number add(Number a, Number b) {
                return a.intValue() + b.intValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.intValue() - b.intValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.intValue() * b.intValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.intValue() / b.intValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.intValue();
            }
        }, LONG(15) {
            @Override
            Number add(Number a, Number b) {
                return a.longValue() + b.longValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.longValue() - b.longValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.longValue() * b.longValue();
            }

            @Override
            Number div(Number a, Number b) {
                return a.longValue() / b.longValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.longValue();
            }
        }, FLOAT(20) {
            @Override
            Number add(Number a, Number b) {
                return a.floatValue() + b.floatValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.floatValue() - b.floatValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.floatValue() * b.floatValue();
            }

            @Override
            Number div(Number a, Number b) {
                if (b.floatValue() == 0.0d) {
                    throw new ArithmeticException("/ by zero");
                }
                return a.floatValue() / b.floatValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.floatValue();
            }
        }, DOUBLE(25) {
            @Override
            Number add(Number a, Number b) {
                return a.doubleValue() + b.doubleValue();
            }

            @Override
            Number sub(Number a, Number b) {
                return a.doubleValue() - b.doubleValue();
            }

            @Override
            Number mul(Number a, Number b) {
                return a.doubleValue() * b.doubleValue();
            }

            @Override
            Number div(Number a, Number b) {
                if (0.0d == b.doubleValue()) {
                    throw new ArithmeticException("/ by zero");
                }
                return a.doubleValue() / b.doubleValue();
            }

            @Override
            Number valueOf(Number n) {
                return n.doubleValue();
            }
        };

        private int _w;

        private Type(int weight) {
            _w = weight;
        }

        abstract Number add(Number a, Number b);

        abstract Number sub(Number a, Number b);

        abstract Number mul(Number a, Number b);

        abstract Number div(Number a, Number b);

        abstract Number valueOf(Number n);

        public boolean lt(Type type) {
            return _w < type._w;
        }

        public boolean gt(Type type) {
            return _w > type._w;
        }

        public boolean eq(Type type) {
            return type == this;
        }
    }

    public static enum Op implements $.Func2<Number, Number, Number> {
        ADD {
            @Override
            public Number apply(Number a, Number b) {
                return t(a, b).add(a, b);
            }
        },
        SUB {
            @Override
            public Number apply(Number a, Number b) {
                return t(a, b).sub(a, b);
            }
        },
        MUL {
            @Override
            public Number apply(Number a, Number b) {
                return t(a, b).mul(a, b);
            }
        },
        DIV {
            @Override
            public Number apply(Number a, Number b) {
                return t(t(a, b), Type.DOUBLE).div(a, b);
            }
        };

        private static Type t(Number a, Number b) {
            Type ta = _type(a);
            Type tb = _type(b);
            return ta.gt(tb) ? ta : tb;
        }

        private static Type t(Type a, Type b) {
            return a.gt(b) ? a : b;
        }

        @Override
        public Number apply(Number o, Number o2) {
            return apply(o, o2);
        }

        public $.F0<Number> curry(final Number a, final Number b) {
            return curry(a, b);
        }

    }

    public static class Num<T extends Number> extends Number {

        private Number _n;
        private Type _t;

        public Num(Number n) {
            if (n instanceof Num) {
                _n = ((Num) n)._n;
                _t = ((Num) n)._t;
            } else {
                this._n = n;
                this._t = _m.get(n.getClass());
                if (null == _t) {
                    // TODO handle BigInteger, AtomicLong and BigDecimal
                    _t = Type.DOUBLE;
                }
            }
        }

        public T get() {
            return (T) _n;
        }

        public <T extends Number> T as(Class<T> cls) {
            return (T) _type(cls).valueOf(_n);
        }

        @Override
        public String toString() {
            return String.valueOf(_n);
        }

        @Override
        public int intValue() {
            return _n.intValue();
        }

        @Override
        public long longValue() {
            return _n.longValue();
        }

        @Override
        public float floatValue() {
            return _n.floatValue();
        }

        @Override
        public double doubleValue() {
            return _n.doubleValue();
        }

        public Num<?> add(Number n) {
            return valueOf(Op.ADD.apply(_n, n));
        }

        public Num<?> sub(Number n) {
            return valueOf(Op.SUB.apply(_n, n));
        }

        public Num<?> mul(Number n) {
            return valueOf(Op.MUL.apply(_n, n));
        }

        public Num<?> div(Number n) {
            return valueOf(Op.DIV.apply(_n, n));
        }

        public double exp() {
            return N.exp(doubleValue());
        }

        public double log() {
            return N.log(doubleValue());
        }

        public double log10() {
            return N.log10(doubleValue());
        }

        public double sqrt() {
            return N.sqrt(doubleValue());
        }

        public double cbrt() {
            return N.cbrt(doubleValue());
        }

        public double ceil() {
            return N.ceil(doubleValue());
        }

        public double floor() {
            return N.floor(doubleValue());
        }

        public double pow(double b) {
            return N.pow(doubleValue(), b);
        }

        public int sign() {
            return N.sign(_n);
        }

        public boolean eq(Number number) {
            return N.eq(this, number);
        }

        public boolean lt(Number number) {
            return N.lt(this, number);
        }

        public boolean gt(Number number) {
            return N.gt(this, number);
        }

        public static Num valueOf(Number n) {
            if (n instanceof Num) {
                return (Num) n;
            } else {
                return new Num(n);
            }
        }

    }

    public static class RangeStep<T extends Number> extends $.F2<T, Integer, T> implements Serializable {
        protected int times = 1;

        public RangeStep() {
        }

        public RangeStep(int times) {
            org.osgl.util.E.invalidArgIf(times < 1, "times must be positive integer");
            this.times = times;
        }

        protected final int step(int i) {
            return i * times;
        }

        @Override
        public T apply(T t, Integer steps) throws NotAppliedException, $.Break {
            return (T) num(t).add(steps * times).get();
        }

        public RangeStep<T> times(int n) {
            if (n < 1) {
                throw new IllegalArgumentException("n must be positive integer");
            }
            return new RangeStep<T>(this.times * n);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof RangeStep) {
                RangeStep that = (RangeStep) obj;
                return that.times == times && that.getClass().equals(getClass());
            }
            return false;
        }
    }

    public static class IntRangeStep extends RangeStep<Integer> {
        public IntRangeStep() {
            super();
        }

        public IntRangeStep(int times) {
            super(times);
        }

        @Override
        public Integer apply(Integer from, Integer steps) throws NotAppliedException, $.Break {
            return from + step(steps);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof IntRangeStep) {
                IntRangeStep that = (IntRangeStep) obj;
                return that.times == times;
            }
            return false;
        }


        @Override
        public IntRangeStep times(int n) {
            if (n < 1) {
                throw new IllegalArgumentException("n must be positive integer");
            }
            return new IntRangeStep(times * n);
        }
    }

    public static class LongRangeStep extends RangeStep<Long> {
        public LongRangeStep() {
        }

        public LongRangeStep(int times) {
            super(times);
        }

        @Override
        public Long apply(Long from, Integer steps) throws NotAppliedException, $.Break {
            return from + step(steps);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof LongRangeStep) {
                LongRangeStep that = (LongRangeStep) obj;
                return that.times == times;
            }
            return false;
        }

        @Override
        public LongRangeStep times(int n) {
            if (n < 1) {
                throw new IllegalArgumentException("n must be positive integer");
            }
            return new LongRangeStep(times * n);
        }
    }

    public static class ShortRangeStep extends RangeStep<Short> {
        public ShortRangeStep() {
        }

        public ShortRangeStep(int times) {
            super(times);
        }

        @Override
        public Short apply(Short from, Integer steps) throws NotAppliedException, $.Break {
            steps = step(steps);
            short limit = (steps < 0) ? Short.MIN_VALUE : Short.MAX_VALUE;
            if ((limit - from) < steps) {
                throw new NoSuchElementException();
            }
            return (short) (from + step(steps));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ShortRangeStep) {
                ShortRangeStep that = (ShortRangeStep) obj;
                return that.times == times;
            }
            return false;
        }


        @Override
        public ShortRangeStep times(int n) {
            if (n < 1) {
                throw new IllegalArgumentException("n must be positive integer");
            }
            return new ShortRangeStep(times * n);
        }
    }

    public static class ByteRangeStep extends RangeStep<Byte> {
        public ByteRangeStep() {
        }

        public ByteRangeStep(int times) {
            super(times);
        }

        @Override
        public Byte apply(Byte from, Integer steps) throws NotAppliedException, $.Break {
            steps = step(steps);
            byte limit = (steps < 0) ? Byte.MIN_VALUE : Byte.MAX_VALUE;
            if ((limit - from) < steps) {
                throw new NoSuchElementException();
            }
            return (byte) (from + step(steps));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof ByteRangeStep) {
                ByteRangeStep that = (ByteRangeStep) obj;
                return that.times == times;
            }
            return false;
        }


        @Override
        public ByteRangeStep times(int n) {
            if (n < 1) {
                throw new IllegalArgumentException("n must be positive integer");
            }
            return new ByteRangeStep(times * n);
        }
    }

    private static Map<Class<? extends Number>, Type> _m;

    static {
        _m = new HashMap<Class<? extends Number>, Type>();
        _m.put(Byte.class, Type.BYTE);
        _m.put(Short.class, Type.SHORT);
        _m.put(Integer.class, Type.INT);
        _m.put(Long.class, Type.LONG);
        _m.put(Float.class, Type.FLOAT);
        _m.put(Double.class, Type.DOUBLE);
    }

    private static Type _type(Class<? extends Number> c) {
        Type t = _m.get(c);
        if (null == t) {
            t = Type.DOUBLE;
        }
        return t;
    }

    private static Type _type(Number n) {
        if (n instanceof Num) {
            return ((Num) n)._t;
        } else {
            return _type(n.getClass());
        }
    }


    /**
     * The <code>double</code> value that is closer than any other to
     * <i>e</i>, the base of the natural logarithms.
     */
    public static final double E = 2.7182818284590452354;

    /**
     * The <code>double</code> value that is closer than any other to
     * <i>pi</i>, the ratio of the circumference of a circle to its
     * diameter.
     */
    public static final double PI = 3.14159265358979323846;

    public static double exp(double a) {
        return StrictMath.exp(a); // default impl. delegates to StrictMath
    }

    public static double log(double a) {
        return StrictMath.log(a); // default impl. delegates to StrictMath
    }

    public static double log10(double a) {
        return StrictMath.log10(a); // default impl. delegates to StrictMath
    }

    public static double sqrt(double a) {
        return StrictMath.sqrt(a);
    }


    public static double cbrt(double a) {
        return StrictMath.cbrt(a);
    }

    public static double ceil(double a) {
        return StrictMath.ceil(a);
    }

    public static double floor(double a) {
        return StrictMath.floor(a);
    }

    public static double pow(double a, double b) {
        return StrictMath.pow(a, b);
    }

    public static int round(float a) {
        return Math.round(a);
    }

    public static float round(float value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public static long round(double a) {
        return Math.round(a);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    /**
     * Check if a long is a perfect square number.
     *
     * A perfect square number is an integer whose square root is also
     * an integer
     *
     * @param n a number
     * @return `true` if the number is perfect square number
     * @see <a href="http://stackoverflow.com/questions/295579/fastest-way-to-determine-if-an-integers-square-root-is-an-integer">SO</a>
     */
    public final static boolean isPerfectSquare(long n) {
        if (n < 0)
            return false;

        switch ((int) (n & 0x3F)) {
            case 0x00:
            case 0x01:
            case 0x04:
            case 0x09:
            case 0x10:
            case 0x11:
            case 0x19:
            case 0x21:
            case 0x24:
            case 0x29:
            case 0x31:
            case 0x39:
                long sqrt;
                if (n < 410881L) {
                    //John Carmack hack, converted to Java.
                    // See: http://www.codemaestro.com/reviews/9
                    int i;
                    float x2, y;

                    x2 = n * 0.5F;
                    y = n;
                    i = Float.floatToRawIntBits(y);
                    i = 0x5f3759df - (i >> 1);
                    y = Float.intBitsToFloat(i);
                    y = y * (1.5F - (x2 * y * y));

                    sqrt = (long) (1.0F / y);
                } else {
                    //Carmack hack gives incorrect answer for n >= 410881.
                    sqrt = (long) Math.sqrt(n);
                }
                return sqrt * sqrt == n;

            default:
                return false;
        }
    }

    /**
     * Check if a string `s` represent an int or long
     * @param s the string
     * @return `true` if s represents an int or `false` otherwise
     */
    public static boolean isInt(String s) {
        return isInt(s, 10);
    }

    /**
     * Check if a string `s` represent an int or long as per radix specified
     * @param s the string
     * @param radix the radix
     * @return `true` if s represents an int or `false` otherwise
     */
    public static boolean isInt(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }

    /**
     * Check if a String `s` is numeric string
     * @param s the string
     * @return `true` if `s` is numeric or `false` otherwise
     */
    public static boolean isNumeric(String s) {
        if (isInt(s)) {
            return true;
        }
        try {
            Double.parseDouble(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a {@link BigDecimal} is negative
     * @param number the number to be tested
     * @return `true` if the number specified is negative or `false` otherwise
     */
    public static boolean isNegative(BigDecimal number) {
        return -1 == number.signum();
    }

    /**
     * Check if a {@link BigInteger} is negative
     * @param number the number to be tested
     * @return `true` if the number specified is negative or `false` otherwise
     */
    public static boolean isNegative(BigInteger number) {
        return -1 == number.signum();
    }

    /**
     * Check if a {@link BigDecimal} is positive
     * @param number the number to be tested
     * @return `true` if the number specified is positive or `false` otherwise
     */
    public static boolean isPositive(BigDecimal number) {
        return 1 == number.signum();
    }

    /**
     * Check if a {@link BigInteger} is positive
     * @param number the number to be tested
     * @return `true` if the number specified is positive or `false` otherwise
     */
    public static boolean isPositive(BigInteger number) {
        return 1 == number.signum();
    }

    /**
     * @see java.util.Random#nextInt()
     * @return a random int value
     */
    public static int randInt() {
        return new Random().nextInt();
    }

    public static int randIntWithSymbol() {
        return randSymbol() * randInt();
    }

    /**
     * @see Random#nextInt(int)
     * @param max the max limit (exclusive) of the number generated
     * @return a random int value
     */
    public static int randInt(int max) {
        return new Random().nextInt(max);
    }

    public static int randIntWithSymbol(int max) {
        return randSymbol() * randInt(max);
    }

    public static float randFloat() {
        return new Random().nextFloat();
    }

    public static float randFloatWithSymbol() {
        return randSymbol() * randFloat();
    }

    public static long randLong() {
        return new Random().nextLong();
    }

    public static long randLongWithSymbol() {
        return randSymbol() * randLong();
    }

    /**
     * @see java.util.Random#nextDouble()
     * @return a random double value
     */
    public static double randDouble() {
        return new Random().nextDouble();
    }

    public static double randDoubleWithSymbol() {
        return randSymbol() * randDouble();
    }

    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    public static long abs(long a) {
        return (a < 0) ? -a : a;
    }

    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }

    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }

    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    public static long max(long a, long b) {
        return (a >= b) ? a : b;
    }

    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    public static long min(long a, long b) {
        return Math.min(a, b);
    }

    public static float min(float a, float b) {
        return Math.min(a, b);
    }

    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    public static int sign(Number number) {
        org.osgl.util.E.NPE(number);
        int n = number.intValue();
        if (n == 0) {
            return 0;
        }
        if (n > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    public static final boolean eq(Number a, Number b) {
        if (null == a) {
            return null == b;
        }
        if (null == b) {
            return false;
        }
        return (a.doubleValue() - b.doubleValue()) <= Double.MIN_NORMAL;
    }

    public static final boolean lt(Number a, Number b) {
        return a.doubleValue() < b.doubleValue();
    }

    public static final boolean gt(Number a, Number b) {
        return a.doubleValue() > b.doubleValue();
    }

    public static final Num num(Number number) {
        return new Num(number);
    }

    public static final Num num(String s) {
        if (S.empty(s)) {
            return new Num(0);
        }
        if (s.contains(".")) {
            return new Num(Double.parseDouble(s));
        } else if (s.length() > 0) {
            return new Num(Long.parseLong(s));
        } else {
            return new Num(Integer.parseInt(s));
        }
    }

    public final static class F {

        public static final $.F1<Number, Number> NEGATIVE = new $.F1<Number, Number>() {
            @Override
            public Number apply(Number number) throws NotAppliedException, $.Break {
                return num(number).mul(-1);
            }
        };

        public static final $.F1 DBL = multiplyBy(2);

        public static $.Predicate<Integer> gt(final int n) {
            return new $.Predicate<Integer>() {
                @Override
                public boolean test(Integer integer) {
                    return integer > n;
                }
            };
        }

        public static $.Predicate<Integer> greaterThan(int n) {
            return gt(n);
        }

        public static $.Predicate<Integer> gte(int n) {
            return gt(n - 1);
        }

        public static $.Predicate<Integer> greaterThanOrEqualsTo(int n) {
            return gte(n);
        }

        public static $.Predicate<Integer> lt(int n) {
            return $.F.negate(gte(n));
        }

        public static $.Predicate<Integer> lessThan(int n) {
            return lt(n);
        }

        public static $.Predicate<Integer> lte(int n) {
            return $.F.negate(gt(n));
        }

        public static $.Predicate<Integer> lessThanOrEqualsTo(int n) {
            return lte(n);
        }

        public static <T extends Number> $.F1<T, Number> dbl() {
            return DBL;
        }

        public static <T extends Number> $.F1<T, Number> dbl(Class<T> clz) {
            return mul(2, clz);
        }

        public static final $.F1 HALF = div(2);

        public static <T extends Number> $.F1<T, Number> half() {
            return HALF;
        }

        public static <T extends Number> $.F1<T, Number> half(Class<T> clz) {
            return div(2, clz);
        }

        public static <T extends Number> $.F1<T, Number> add(final Number n) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) {
                    return num(t).add(n).get();
                }
            };
        }

        public static <T extends Number> $.F1<T, Number> add(final Number n, final Class<T> clz) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) {
                    return num(t).add(n).as(clz);
                }
            };
        }

        public static <T extends Number> $.F2<T, T, Number> addTwo() {
            return new $.F2<T, T, Number>() {
                @Override
                public Number apply(T t1, T t2) throws NotAppliedException, $.Break {
                    return num(t1).add(t2);
                }
            };
        }

        public static <T extends Number> $.F2<T, T, T> addTwo(final Class<T> c) {
            return new $.F2<T, T, T>() {
                @Override
                public T apply(T t1, T t2) throws NotAppliedException, $.Break {
                    return (T) num(t1).add(t2).as(c);
                }
            };
        }

        public static <T extends Number, X> $.F2<T, X, Number> adder(final $.Function<X, T> func) {
            return new $.F2<T, X, Number>() {
                @Override
                public Number apply(T t, X x) throws NotAppliedException, $.Break {
                    return num(t).add(func.apply(x));
                }
            };
        }

        public static <T extends Number, X> $.F2<T, X, T> adder(final $.Function<X, T> func, final Class<T> clz) {
            return new $.F2<T, X, T>() {
                @Override
                public T apply(T t, X x) throws NotAppliedException, $.Break {
                    return (T) num(t).add(func.apply(x)).as(clz);
                }
            };
        }

        public static $.F2<Number, Number, Number> MULTIPLY = new $.F2<Number, Number, Number>() {
            @Override
            public Number apply(Number n1, Number n2) throws NotAppliedException, $.Break {
                return num(n1).mul(n2);
            }
        };

        public static <P1 extends Number, P2 extends Number, R extends Number>
        $.F2<P1, P2, R> multiply(final Class<R> type) {
            return new $.F2<P1, P2, R>() {
                @Override
                public R apply(P1 n1, P2 n2) throws NotAppliedException, $.Break {
                    return (R) num(n1).mul(n2).as(type);
                }
            };
        }

        public static <T extends Number> $.F1<T, Number> multiplyBy(final Number n) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) {
                    return num(t).mul(n).get();
                }
            };
        }

        public static <T extends Number> $.F1<T, Number> multiplyBy(final Number n, final Class<T> clz) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) {
                    return num(t).mul(n).as(clz);
                }
            };
        }

        /**
         * Use {@link #multiplyBy(Number)}
         * @param n the number `n`
         * @param <T> the type of number `n`
         * @return the function that when applied will return number multiply parameter by `n`
         */
        @Deprecated
        public static <T extends Number> $.F1<T, Number> mul(final Number n) {
            return multiplyBy(n);
        }

        /**
         * Use {@link #multiplyBy(Number, Class)}
         * @param n the number
         * @param clz the number class
         * @param <T> the number type
         * @return the function that when applied will multiple the param by `n`
         */
        @Deprecated
        public static <T extends Number> $.F1<T, Number> mul(final Number n, final Class<T> clz) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) {
                    return num(t).mul(n).as(clz);
                }
            };
        }

        public static <P1 extends Number, P2 extends Number, R extends Number> $.F2<P1, P2, R> divide(
                final Class<R> type
        ) {
            return new $.F2<P1, P2, R>() {
                @Override
                public R apply(P1 n1, P2 n2) throws NotAppliedException, $.Break {
                    return (R) num(n1).div(n2).as(type);
                }
            };
        }

        public static $.F2<Number, Number, Number> DIVIDE = new $.F2<Number, Number, Number>() {
            @Override
            public Number apply(Number n1, Number n2) throws NotAppliedException, $.Break {
                return num(n1).div(n2);
            }
        };

        public static <T extends Number> $.F1<T, Number> divideBy(final Number n) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) {
                    return num(t).div(n).get();
                }
            };
        }

        public static <T extends Number> $.F1<T, Number> divideBy(final Number n, final Class<T> type) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) {
                    return num(t).div(n).as(type);
                }
            };
        }

        /**
         * Use {@link #divideBy(Number)} instead
         * @param n the dividend
         * @param <T> the number type
         * @return a function that when applied will divide the input by `n`
         */
        @Deprecated
        public static <T extends Number> $.F1<T, Number> div(final Number n) {
            return divideBy(n);
        }

        /**
         * Use {@link #divideBy(Number, Class)} instead
         * @param n the dividend
         * @param clz the number class
         * @param <T> the number type
         * @return a function that when applied will divide the input by `n`
         */
        @Deprecated
        public static <T extends Number> $.F1<T, Number> div(final Number n, final Class<T> clz) {
            return divideBy(n, clz);
        }

        public static <T extends Number> $.F1<T, Number> sqr() {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) throws NotAppliedException, $.Break {
                    Num<T> nm = num(t);
                    return nm.mul(nm).get();
                }
            };
        }

        public static <T extends Number> $.F1<T, Number> sqr(final Class<T> clz) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) throws NotAppliedException, $.Break {
                    Num<T> nm = num(t);
                    return nm.mul(nm).as(clz);
                }
            };
        }

        public static <T extends Number> $.F1<T, Double> sqrt() {
            return new $.F1<T, Double>() {
                @Override
                public Double apply(T t) throws NotAppliedException, $.Break {
                    Num<T> nm = num(t);
                    return nm.sqrt();
                }
            };
        }


        public static <T extends Number> $.F1<T, Number> cubic() {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) throws NotAppliedException, $.Break {
                    Num<T> nm = num(t);
                    return nm.mul(nm).mul(nm).get();
                }
            };
        }

        public static <T extends Number> $.F1<T, Number> cubic(final Class<T> clz) {
            return new $.F1<T, Number>() {
                @Override
                public Number apply(T t) throws NotAppliedException, $.Break {
                    N.Num<T> nm = N.num(t);
                    return nm.mul(nm).mul(nm).as(clz);
                }
            };
        }

        public static <T extends Number> $.F1<T, Double> cbrt() {
            return new $.F1<T, Double>() {
                @Override
                public Double apply(T t) throws NotAppliedException, $.Break {
                    return num(t).cbrt();
                }
            };
        }

        public static <T extends Number> $.F1<T, Integer> sign() {
            return new $.F1<T, Integer>() {
                @Override
                public Integer apply(T t) throws NotAppliedException, $.Break {
                    return N.sign(t);
                }
            };
        }

        public static <T extends Number> $.F2<T, T, T> aggregate(final Class<T> clz) {
            return new $.F2<T, T, T>() {
                @Override
                public T apply(T e, T v) {
                    return (T) N.num(e).add(v).as(clz);
                }
            };
        }

        public static <T extends Number> $.F2<T, T, T> subtract(final Class<T> clz) {
            return new $.F2<T, T, T>() {
                @Override
                public T apply(T minuend, T subtraend) throws NotAppliedException, $.Break {
                    return (T) N.num(minuend).sub(subtraend).as(clz);
                }
            };
        }

        public static final IntRangeStep INT_RANGE_STEP = new IntRangeStep();

        public static final IntRangeStep intRangeStep(int times) {
            return new IntRangeStep(times);
        }

        public static final ByteRangeStep BYTE_RANGE_STEP = new ByteRangeStep();

        public static ByteRangeStep byteRangeStep(int times) {
            return new ByteRangeStep(times);
        }

        public static final ShortRangeStep SHORT_RANGE_STEP = new ShortRangeStep();

        public static ByteRangeStep shortRangeStep(int times) {
            return new ByteRangeStep(times);
        }

        public static final LongRangeStep LONG_RANGE_STEP = new LongRangeStep();

        public static LongRangeStep longRangeStep(int times) {
            return new LongRangeStep(times);
        }

        public static final $.F2 COUNTER = new $.F2<Integer, Object, Integer>() {
            @Override
            public Integer apply(Integer integer, Object o) throws NotAppliedException, $.Break {
                return integer + 1;
            }
        };

        public static <T> $.F2<Integer, T, Integer> counter() {
            return $.cast(COUNTER);
        }

        public static $.Predicate<Integer> IS_EVEN = new $.Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer % 2 == 0;
            }
        };

        public static $.Predicate<Integer> IS_ODD = $.F.negate(IS_EVEN);
    }

    private static int randSymbol() {
        return new Random().nextInt(2) == 0 ? -1 : 1;
    }
}
