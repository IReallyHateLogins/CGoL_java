package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedChanges;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSetBig;

public class LifeSetBigBufferedChanges extends AbstractCellBasedLifeBufferedChanges<CellSetBig> {
    public LifeSetBigBufferedChanges(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSetBig::new);
    }
}
