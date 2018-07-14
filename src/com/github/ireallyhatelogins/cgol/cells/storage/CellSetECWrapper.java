package com.github.ireallyhatelogins.cgol.cells.storage;

import com.github.ireallyhatelogins.cgol.cells.Cell;
import com.github.ireallyhatelogins.cgol.cells.CellStorage;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.impl.factory.Maps;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CellSetECWrapper implements CellStorage {

    MutableMap<Cell, Cell> map = Maps.mutable.empty();

    @Override
    public void put(Cell cell) {
        map.put(cell, cell);
    }

    @Override
    public void remove(Cell cell) {
        map.remove(cell);
    }

    @Override
    public Cell get(Cell cell) {
        return map.get(cell);
    }

    @Override
    public boolean removeIf(Predicate<? super Cell> filter) {
        return map.values().removeIf(filter);
    }

    @Override
    public void forEach(Consumer<? super Cell> action) {
        map.forEachKey(action::accept);
    }

    @Override
    public void clear() {
        map.clear();
    }
}
