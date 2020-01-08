package ovh.roro.gamepasschallenge.thewalls.util.tuple;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class ImmutableTuple<F, S> implements Tuple<F, S> {

    private final F first;
    private final S second;

    ImmutableTuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public F getFirst() {
        return this.first;
    }

    @Override
    public void setFirst(F value) {
        throw new UnsupportedOperationException("Cannot set First value of an ImmutableTuple");
    }

    @Override
    public S getSecond() {
        return this.second;
    }

    @Override
    public void setSecond(S value) {
        throw new UnsupportedOperationException("Cannot set Second value of an ImmutableTuple");
    }
}
