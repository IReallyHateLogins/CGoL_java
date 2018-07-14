package com.github.ireallyhatelogins.cgol.longs.implementation;

import com.github.ireallyhatelogins.cgol.longs.AbstractLongBasedLife;
import com.github.ireallyhatelogins.cgol.longs.storage.CellLongSetFU;

public class LifeLongSetFU extends AbstractLongBasedLife<CellLongSetFU> {
    public LifeLongSetFU(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellLongSetFU::new);
    }
}
