package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLifeBufferedChanges;
import com.github.ireallyhatelogins.cgol.cells.storage.CellSet;

public class LifeSetBufferedChanges extends AbstractCellBasedLifeBufferedChanges<CellSet> {
    public LifeSetBufferedChanges(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellSet::new);
    }
}
