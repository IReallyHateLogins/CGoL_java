package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLife;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSetBig;

public class LifeSetBig extends AbstractCellBasedLife<CellSetBig> {
    public LifeSetBig(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSetBig::new);
    }
}
