package com.github.ireallyhatelogins.cgol.cells;

import java.util.function.Consumer;
import java.util.function.Predicate;

public interface CellStorage {

    void put(Cell cell);

    void remove(Cell cell);

    Cell get(Cell cell);

    boolean removeIf(Predicate<? super Cell> filter);

    void forEach(Consumer<? super Cell> action);

    void clear();
}
