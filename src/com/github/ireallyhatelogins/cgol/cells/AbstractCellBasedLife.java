package com.github.ireallyhatelogins.cgol.cells;

import java.util.ArrayDeque;
import java.util.Random;
import java.util.function.Supplier;

import com.github.ireallyhatelogins.cgol.AbstractLife;

public abstract class AbstractCellBasedLife<T extends CellStorage> extends AbstractLife {

    protected T cells;

    protected ArrayDeque<Cell> changedCells = new ArrayDeque<>();

    protected MutableCell searchCell = new MutableCell(0, 0);

    public AbstractCellBasedLife(int canvasHeight, int canvasWidth, Supplier<T> supplier) {
        super(canvasHeight, canvasWidth);
        cells = supplier.get();
    }

    @Override
    public void randomize() {
        Random rnd = new Random();
        for (int i = offsetX; i < zWidth(); ++i) {
            for (int j = offsetY; j < zHeight(); ++j) {
                if ((rnd.nextInt() % 12) > 8) {
                    add(i, j);
                }
            }
        }
    }

    @Override
    public void clear() {
        cells.clear();
    }

    @Override
    public void next() {
        cells.removeIf(e -> {
            if (e.cycle()) {
                changedCells.add(e);
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
        int startX = offsetX;
        int startY = offsetY;
        int endX = startX + zWidth();
        int endY = startY + zHeight();

        cells.forEach(e -> {
            if (e.inRange(startX, startY, endX, endY)) {
                if (e.isCurrentGeneration()) {
                    currentPoint(e.getX() - startX, e.getY() - startY);
                } else if (isDualMode()) {
                    prevPoint(e.getX() - startX, e.getY() - startY);
                }
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
            add(i, y);
        }
    }

    protected void add(int x, int y) {
        searchCell.setCoordinates(x, y);
        Cell cell = cells.get(searchCell);
        if (cell == null) {
            cell = new Cell(x, y, true);
            cells.put(cell);
            addNeighbours(cell);
        } else if (!cell.isCurrentGeneration()) {
            cell.seed();
            addNeighbours(cell);
        }
    }

    protected void addNeighbours(Cell cell) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i == 0) && (j == 0)) {
                    continue;
                }
                addNeighbour(cell.getX() + i, cell.getY() + j);
            }
        }
    }

    protected void addNeighbour(int x, int y) {
        searchCell.setCoordinates(x, y);
        Cell cell = cells.get(searchCell);
        if (cell != null) {
            cell.incNeighbour();
        } else {
            cell = new Cell(x, y);
            cell.incNeighbour();
            cells.put(cell);
        }
    }

    protected void removeNeighbours(Cell cell) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i == 0) && (j == 0)) {
                    continue;
                }
                removeNeighbour(cell.getX() + i, cell.getY() + j);
            }
        }
    }

    protected void removeNeighbour(int x, int y) {
        searchCell.setCoordinates(x, y);
        Cell cell = cells.get(searchCell);
        if (cell != null) {
            cell.decNeighbour();
        }
    }

}
