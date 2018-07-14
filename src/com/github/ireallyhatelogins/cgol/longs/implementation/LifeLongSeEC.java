package com.github.ireallyhatelogins.cgol.longs.implementation;

import com.github.ireallyhatelogins.cgol.longs.AbstractLongBasedLife;
import com.github.ireallyhatelogins.cgol.longs.storage.CellLongSetECWrapper;

public class LifeLongSeEC extends AbstractLongBasedLife<CellLongSetECWrapper> {
    public LifeLongSeEC(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellLongSetECWrapper::new);
    }
}
