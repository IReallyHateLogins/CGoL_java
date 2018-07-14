package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedDraw;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSet;

public class LifeSetBufferedDraw extends AbstractCellBasedLifeBufferedDraw<CellSet> {
    public LifeSetBufferedDraw(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSet::new);
    }
}
