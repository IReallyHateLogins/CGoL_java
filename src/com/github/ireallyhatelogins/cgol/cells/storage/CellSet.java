package com.github.ireallyhatelogins.cgol.cells.storage;

import com.github.ireallyhatelogins.cgol.cells.Cell;
import com.github.ireallyhatelogins.cgol.cells.CellStorage;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CellSet extends ObjectOpenHashSet<Cell> implements CellStorage {

    @Override
    public void put(Cell cell) {
        super.add(cell);
    }

    @Override
    public void remove(Cell cell) {
        super.remove(cell);
    }

    @Override
    public Cell get(Cell cell) {
        return super.get(cell);
    }

    @Override
    public boolean removeIf(Predicate<? super Cell> filter) {
        return super.removeIf(filter);
    }

    @Override
    public void forEach(Consumer<? super Cell> action) {
        super.forEach(action);
    }
}
