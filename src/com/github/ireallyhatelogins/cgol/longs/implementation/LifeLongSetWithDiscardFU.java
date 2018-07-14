package com.github.ireallyhatelogins.cgol.longs.implementation;

import com.github.ireallyhatelogins.cgol.longs.AbstractLongBasedLifeWithDiscard;
import com.github.ireallyhatelogins.cgol.longs.storage.CellLongSetFU;

public class LifeLongSetWithDiscardFU extends AbstractLongBasedLifeWithDiscard<CellLongSetFU> {
    public LifeLongSetWithDiscardFU(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellLongSetFU::new);
    }
}
