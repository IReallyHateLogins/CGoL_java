package com.github.ireallyhatelogins.cgol.longs.implementation;

import com.github.ireallyhatelogins.cgol.longs.AbstractLongBasedLifeWithDiscard;
import com.github.ireallyhatelogins.cgol.longs.storage.CellLongSetECWrapper;

public class LifeLongSeWithDiscardEC extends AbstractLongBasedLifeWithDiscard<CellLongSetECWrapper> {
    public LifeLongSeWithDiscardEC(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth, CellLongSetECWrapper::new);
    }
}
