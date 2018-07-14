package com.github.ireallyhatelogins.cgol.longs;

import com.github.ireallyhatelogins.cgol.AbstractLife;

import java.util.Random;
import java.util.function.LongConsumer;
import java.util.function.Supplier;

public abstract class AbstractLongBasedLife<T extends LongCellSet> extends AbstractLife {
    protected T currentGeneration;
    protected T nextGeneration;

    private Supplier<T> supplier;

    public AbstractLongBasedLife(int canvasHeight, int canvasWidth, Supplier<T> supplier) {
        super(canvasHeight, canvasWidth);
        this.supplier = supplier;
        currentGeneration = supplier.get();
        nextGeneration = supplier.get();
    }

    public void randomize() {
        Random rnd = new Random();
        for (int i = offsetX; i < zWidth(); ++i) {
            for (int j = offsetY; j < zHeight(); ++j) {
                if ((rnd.nextInt() % 12) > 8) {
                    nextGeneration.put(LongCell.fromCoordinates(i, j));
                }
            }
        }
    }

    @Override
    public void clear() {
        currentGeneration.clear();
        nextGeneration.clear();
    }

    @Override
    public void next() {
        currentGeneration = nextGeneration;
        nextGeneration = supplier.get();
        currentGeneration.forEach((LongConsumer) this::checkNeighbours);
    }

    @Override
    public void draw() {
        int startX = offsetX;
        int startY = offsetY;
        int endX = startX + zWidth();
        int endY = startY + zHeight();

        if (isDualMode()) {
            currentGeneration.forEach((LongConsumer) e -> {
                if (LongCell.inRange(e, startX, startY, endX, endY)) {
                    prevPoint(LongCell.getX(e) - startX, LongCell.getY(e) - startY);
                }
            });
        }
        nextGeneration.forEach((LongConsumer) e -> {
            if (LongCell.inRange(e, startX, startY, endX, endY)) {
                currentPoint(LongCell.getX(e) - startX, LongCell.getY(e) - startY);
            }
        });
    }

    @Override
    protected void add(int x, int y, int lineCount) {
        int xLen;

        x = offsetX + x;
        y = offsetY + zHeight() - y;
        xLen = x + lineCount;
        //trying to overcome overflows, likely unnecessary, but still
        for (int i = x; i != xLen; i++) {
            nextGeneration.put(LongCell.fromCoordinates(i, y));
        }
    }

    protected void checkNeighbours(long cell) {
        int x = LongCell.getX(cell);
        int y = LongCell.getY(cell);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                check(x, i, y, j);
            }
        }
    }

    protected void check(int x, int xShift, int y, int yShift) {
        long cell = LongCell.fromCoordinates(x + xShift, y + yShift);
        if (nextGeneration.contains(cell)) {
            return;
        }
        int count = 0;
        boolean alive = false;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (currentGeneration.contains(LongCell.fromCoordinates(x + xShift + i, y + yShift + j))) {
                    if (i == 0 && j == 0) {
                        alive = true;
                    } else {
                        count++;
                    }
                }

            }
        }
        if ((count == 3) || ((count == 2) && (alive))) {
            nextGeneration.put(cell);
        }
    }


}