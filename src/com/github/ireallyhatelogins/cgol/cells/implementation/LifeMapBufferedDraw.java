package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedDraw;
import com.github.ireallyhatelogins.cgol.cells.storage.CellMap;

public class LifeMapBufferedDraw extends AbstractCellBasedLifeBufferedDraw<CellMap> {
    public LifeMapBufferedDraw(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellMap::new);
    }
}
