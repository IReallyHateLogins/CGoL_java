package com.github.ireallyhatelogins.cgol.longs.storage;

import com.github.ireallyhatelogins.cgol.longs.LongCell;
import com.github.ireallyhatelogins.cgol.longs.LongCellSet;
import it.unimi.dsi.fastutil.longs.LongHash;
import it.unimi.dsi.fastutil.longs.LongOpenCustomHashSet;

import java.util.function.LongConsumer;

public class CellLongSetFU extends LongOpenCustomHashSet implements LongCellSet {

    public CellLongSetFU() {
        super(Short.MAX_VALUE, FAST_LOAD_FACTOR, new LongHash.Strategy() {
            @Override
            public int hashCode(long l) {
                return LongCell.hashCode(l);
            }

            @Override
            public boolean equals(long l, long l1) {
                return LongCell.equals(l, l1);
            }
        });
    }

    @Override
    public void put(long cell) {
        super.add(cell);
    }

    @Override
    public void forEach(LongConsumer action) {
        super.forEach(action);
    }
}
