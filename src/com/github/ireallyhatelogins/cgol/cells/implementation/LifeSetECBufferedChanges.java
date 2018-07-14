package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedChanges;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSetECWrapper;

public class LifeSetECBufferedChanges extends AbstractCellBasedLifeBufferedChanges<CellSetECWrapper> {
    public LifeSetECBufferedChanges(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSetECWrapper::new);
    }
}
