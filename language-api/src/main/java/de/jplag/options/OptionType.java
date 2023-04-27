package de.jplag.options;

/**
 * The available types for language specific options.
 * @param <T> The java type of the option.
 */
public sealed abstract class OptionType<T> permits OptionType.StringType, OptionType.IntegerType {
    final static class StringType extends OptionType<String> {
        public static StringType INSTANCE = new StringType();

        private StringType() {
            super(String.class);
        }
    }

    final static class IntegerType extends OptionType<Integer> {
        public static IntegerType INSTANCE = new IntegerType();

        private IntegerType() {
            super(Integer.class);
        }
    }

    public static StringType string() {
        return StringType.INSTANCE;
    }

    public static IntegerType integer() {
        return IntegerType.INSTANCE;
    }

    private final Class<T> javaType;

    private OptionType(Class<T> javaType) {
        this.javaType = javaType;
    }

    public Class<T> getJavaType() {
        return javaType;
    }
}
