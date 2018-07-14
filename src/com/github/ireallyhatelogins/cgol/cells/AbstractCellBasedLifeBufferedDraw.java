package com.github.ireallyhatelogins.cgol.cells;

import java.util.ArrayDeque;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractCellBasedLifeBufferedDraw<T extends CellStorage> extends AbstractCellBasedLife<T> {

    protected int startX = 0;
    protected int endX = 0;
    protected int startY = 0;
    protected int endY = 0;

    protected ArrayDeque<Cell> toDraw = new ArrayDeque<>();

    Consumer<Cell> drawAction = e -> {
        if (e.isCurrentGeneration()) {
            currentPoint(e.getX() - startX, e.getY() - startY);
        } else if (isDualMode()) {
            prevPoint(e.getX() - startX, e.getY() - startY);
        }
    };

    public AbstractCellBasedLifeBufferedDraw(int canvasHeight, int canvasWidth, Supplier<T> supplier) {
        super(canvasHeight, canvasWidth, supplier);
    }

    @Override
    public void next() {
        startX = offsetX;
        startY = offsetY;
        endX = startX + zWidth();
        endY = startY + zHeight();

        toDraw.clear();
        cells.removeIf(e -> {
            if (e.cycle()) {
                changedCells.add(e);
            }
            if (e.inRange(startX, startY, endX, endY)) {
                if (isDualMode() || e.isSeeded()) {
                    toDraw.add(e);
                }
            }
            return e.isInvalidGhost();
        });
        for (Cell cell : changedCells) {
            if (cell.isCurrentGeneration()) {
                addNeighbours(cell);
            } else {
                removeNeighbours(cell);
            }
        }
        changedCells.clear();
    }

    @Override
    public void draw() {
        if ((this.startX == offsetX) && (this.startY == offsetY) && (this.endX == startX + zWidth()) && (this.endY == startY + zHeight())) {
            toDraw.forEach(drawAction);
            return;
        }

        startX = offsetX;
        startY = offsetY;
        endX = startX + zWidth();
        endY = startY + zHeight();

        cells.forEach(e -> {
            if (e.inRange(startX, startY, endX, endY)) {
                toDraw.add(e);
                drawAction.accept(e);
            }
        });
    }

}
