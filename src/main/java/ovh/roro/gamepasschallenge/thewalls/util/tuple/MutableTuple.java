package ovh.roro.gamepasschallenge.thewalls.util.tuple;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class MutableTuple<F, S> implements Tuple<F, S> {

    private F first;
    private S second;

    MutableTuple(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public F getFirst() {
        return this.first;
    }

    @Override
    public void setFirst(F value) {
        this.first = value;
    }

    @Override
    public S getSecond() {
        return this.second;
    }

    @Override
    public void setSecond(S value) {
        this.second = value;
    }
}
