package com.github.ireallyhatelogins.cgol.longs;

import java.util.function.Supplier;

public abstract class AbstractLongBasedLifeWithDiscard<T extends LongCellSet> extends AbstractLongBasedLife<T> {

    protected T discard;

    private byte[][] neighborGrid = {new byte[5], new byte[5], new byte[5], new byte[5], new byte[5]};

    public AbstractLongBasedLifeWithDiscard(int canvasHeight, int canvasWidth, Supplier<T> supplier) {
        super(canvasHeight, canvasWidth, supplier);
        discard = supplier.get();
    }

    @Override
    public void next() {
        super.next();
        discard.clear();
    }

    @Override
    protected void checkNeighbours(long cell) {
        resetGrid();
        super.checkNeighbours(cell);
    }

    protected void check(int x, int xShift, int y, int yShift) {
        long cell = LongCell.fromCoordinates(x + xShift, y + yShift);
        if (nextGeneration.contains(cell) || discard.contains(cell)) {
            return;
        }
        int count = 0;
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                int state = neighborGrid[i + xShift][j + yShift];
                if (state == -1) {
                    if (currentGeneration.contains(LongCell.fromCoordinates(x + xShift + (i - 2), y + yShift + (j - 2)))) {
                        neighborGrid[i + xShift][j + yShift] = 1;
                        count++;
                    } else {
                        neighborGrid[i + xShift][j + yShift] = 0;
                    }
                } else {
                    count += state;
                }
            }
        }
        if ((count == 3) || ((count == 4) && (neighborGrid[2 + xShift][2 + yShift] == 1))) {
            nextGeneration.put(cell);
        } else {
            discard.put(cell);
        }
    }

    private void resetGrid() {
        for (int i = 0; i < neighborGrid.length; i++) {
            for (int j = 0; j < neighborGrid.length; j++) {
                if (i == 2 && j == 2) {
                    neighborGrid[i][j] = 1;
                } else {
                    neighborGrid[i][j] = -1;
                }
            }
        }
    }

}
