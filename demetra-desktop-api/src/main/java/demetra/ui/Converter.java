package demetra.ui;

import java.util.function.Function;

public interface Converter<A, B> {

    B doForward(A a);

    A doBackward(B b);

    static <A, B> Converter<A, B> of(Function<A, B> forward, Function<B, A> backward) {
        return new Converter<A, B>() {
            @Override
            public B doForward(A a) {
                return forward.apply(a);
            }

            @Override
            public A doBackward(B b) {
                return backward.apply(b);
            }
        };
    }

    default Converter<B, A> reverse() {
        Converter<A, B> original = this;
        return new Converter<B, A>() {
            @Override
            public A doForward(B b) {
                return original.doBackward(b);
            }

            @Override
            public B doBackward(A a) {
                return original.doForward(a);
            }
        };
    }

    default <C> Converter<A, C> andThen(Converter<B, C> converter) {
        Converter<A, B> original = this;
        return new Converter<A, C>() {
            @Override
            public C doForward(A a) {
                return converter.doForward(original.doForward(a));
            }

            @Override
            public A doBackward(C c) {
                return original.doBackward(converter.doBackward(c));
            }
        };
    }
}
