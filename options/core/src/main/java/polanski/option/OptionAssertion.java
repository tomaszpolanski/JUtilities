package polanski.option;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import polanski.option.function.Func1;

public final class OptionAssertion<T> {

    @NonNull
    private final Option<T> actual;

    OptionAssertion(@NonNull final Option<T> actual) {
        checkNotNull(actual, "Option value cannot be null");

        this.actual = actual;
    }

    public void assertIsNone() {
        if (!actual.isNone()) {
            fail("Option was not None");
        }
    }

    @NonNull
    public OptionAssertion<T> assertIsSome() {
        if (!actual.isSome()) {
            fail("Option was not Some");
        }
        return this;
    }

    @NonNull
    public OptionAssertion<T> assertValue(@NonNull final Func1<T, Boolean> predicate) {
        checkNotNull(predicate, "Predicate function cannot be null");

        if (!actual.isSome()) {
            fail("Option was not Some");
        }

        if (!matches(actual, predicate)) {
            fail("Option value did not match predicate");
        }
        return this;
    }

    @NonNull
    public OptionAssertion<T> assertValue(@NonNull final T expected) {
        checkNotNull(expected, "Expected value cannot be null: use assertNone instead");

        if (!actual.isSome()) {
            fail("Option was not Some");
        }

        if (!matches(actual, equalsPredicate(expected))) {
            fail(String.format("Option value: <%s> did not equal expected value: <%s>",
                               OptionUnsafe.getUnsafe(actual),
                               expected));
        }
        return this;
    }

    @NonNull
    private static <T> Func1<T, Boolean> equalsPredicate(@NonNull final T expected) {
        return v -> v.equals(expected);
    }

    private static <T> boolean matches(@NonNull final Option<T> actual,
                                       @NonNull final Func1<T, Boolean> predicate) {
        return actual.filter(predicate).isSome();
    }

    private void fail(String message) {
        StringBuilder b = new StringBuilder();
        b.append(message);

        b.append(" (")
         .append("actual = ").append(actual.toString())
         .append(')');

        throw new AssertionError(b.toString());
    }

    private static <T> void checkNotNull(@Nullable T value, @NonNull String msg) {
        if (value == null) {
            throw new AssertionError(msg);
        }
    }
}
