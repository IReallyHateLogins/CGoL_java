package com.github.ireallyhatelogins.cgol.longs.storage;

import com.github.ireallyhatelogins.cgol.longs.LongCellSet;
import org.eclipse.collections.impl.set.mutable.primitive.LongHashSet;

import java.util.function.LongConsumer;

public class CellLongSetECWrapper implements LongCellSet {
    private LongHashSet set = new LongHashSet();

    public CellLongSetECWrapper() {
    }


    @Override
    public void put(long cell) {
        set.add(cell);
    }

    @Override
    public boolean contains(long cell) {
        return set.contains(cell);
    }

    @Override
    public void clear() {
        set.clear();
    }

    @Override
    public void forEach(LongConsumer action) {
        set.forEach(action::accept);
    }
}
