package ovh.roro.gamepasschallenge.thewalls.util.tuple;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public interface Tuple<F, S> {

    static <F, S> MutableTuple<F, S> mutable(F first, S second) {
        return new MutableTuple<>(first, second);
    }

    static <F, S> ImmutableTuple<F, S> immutable(F first, S second) {
        return new ImmutableTuple<>(first, second);
    }

    F getFirst();

    void setFirst(F value);

    S getSecond();

    void setSecond(S value);
}
