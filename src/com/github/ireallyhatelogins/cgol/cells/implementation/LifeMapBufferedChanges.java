package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedChanges;
import com.github.ireallyhatelogins.cgol.cells.storage.CellMap;

public class LifeMapBufferedChanges extends AbstractCellBasedLifeBufferedChanges<CellMap> {
    public LifeMapBufferedChanges(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellMap::new);
    }
}
