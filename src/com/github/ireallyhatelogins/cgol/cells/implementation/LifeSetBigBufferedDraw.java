package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedDraw;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSetBig;

public class LifeSetBigBufferedDraw extends AbstractCellBasedLifeBufferedDraw<CellSetBig> {
    public LifeSetBigBufferedDraw(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSetBig::new);
    }
}
