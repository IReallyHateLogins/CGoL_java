package com.github.ireallyhatelogins.cgol.cells.storage;

import com.github.ireallyhatelogins.cgol.cells.Cell;
import com.github.ireallyhatelogins.cgol.cells.CellStorage;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CellMap extends HashMap<Cell, Cell> implements CellStorage {

    @Override
    public void put(Cell cell) {
        super.put(cell, cell);
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
        return super.keySet().removeIf(filter);
    }

    @Override
    public void forEach(Consumer<? super Cell> action) {
        super.keySet().forEach(action);
    }
}
