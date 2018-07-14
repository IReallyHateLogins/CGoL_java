package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedDraw;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSetECWrapper;

public class LifeSetECBufferedDraw extends AbstractCellBasedLifeBufferedDraw<CellSetECWrapper> {
    public LifeSetECBufferedDraw(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSetECWrapper::new);
    }
}
