package com.github.ireallyhatelogins.cgol.longs;

import java.util.function.LongConsumer;

public interface LongCellSet {
    void put(long cell);

    boolean contains(long cell);

    void clear();

    void forEach(LongConsumer action);


}
