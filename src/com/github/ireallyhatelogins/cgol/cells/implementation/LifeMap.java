package com.github.ireallyhatelogins.cgol.cells.implementation;

import com.github.ireallyhatelogins.cgol.cells.AbstractCellBasedLife;
import com.github.ireallyhatelogins.cgol.cells.storage.CellMap;

public class LifeMap extends AbstractCellBasedLife<CellMap> {
    public LifeMap(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellMap::new);
    }
}
