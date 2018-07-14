package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLife;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSetECWrapper;

public class LifeSetEC extends AbstractCellBasedLife<CellSetECWrapper> {
    public LifeSetEC(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSetECWrapper::new);
    }
}
