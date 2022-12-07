package compiler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class CustomIterator<E> implements Iterator<E> {
    private final Iterator<E> iterator;
    private final List<E> buffer;
    private int index;

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public E next() {
        return null;
    }

    public boolean moveNext(){
        if (index < buffer.size() - 1) {
            index++;
            return true;
        }

        if (!iterator.hasNext()) return false;
        buffer.add(iterator.next());
        index++;
        return true;
    }

    public boolean movePrev(){
        if (index <= 0) {
            return false;
        }

        index--;
        return true;
    }
    public E getCurrent() {
        if (index < 0 || index >= buffer.size())
            throw new IllegalStateException();

        return buffer.get(index);
    }
    @Override
    public void remove() {
        Iterator.super.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super E> action) {
        Iterator.super.forEachRemaining(action);
    }

    public CustomIterator(Iterator<E> iterator){
        this.iterator = iterator;
        this.buffer = new ArrayList<E>();
        this.index = -1;
    }
}
