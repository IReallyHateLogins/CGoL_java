package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLife;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSet;

public class LifeSet extends AbstractCellBasedLife<CellSet> {
    public LifeSet(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSet::new);
    }
}
