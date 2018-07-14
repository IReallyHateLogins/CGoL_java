package com.github.ireallyhatelogins.cgol.cells;

import java.util.function.Supplier;

public abstract class AbstractCellBasedLifeBufferedChanges<T extends CellStorage> extends AbstractCellBasedLifeBufferedDraw<T> {

    protected T toChange;

    public AbstractCellBasedLifeBufferedChanges(int canvasHeight, int canvasWidth, Supplier<T> supplier) {
        super(canvasHeight, canvasWidth, supplier);
        toChange = supplier.get();
    }

    public void add(int x, int y) {
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
        if (cell.pendingChange()) {
            toChange.put(cell);
        }
    }

    protected void addNeighbour(int x, int y) {
        searchCell.setCoordinates(x, y);
        Cell cell = cells.get(searchCell);
        if (cell != null) {
            cell.incNeighbour();
            if (cell.pendingChange()) {
                if (!cell.isChanged()) {
                    cell.setChangedFlag();
                    toChange.put(cell);
                }
            } else {
                if (cell.isChanged()) {
                    cell.resetChangedFlag();
                    toChange.remove(cell);
                }
            }
        } else {
            cell = new Cell(x, y);
            cell.incNeighbour();
            cells.put(cell);
        }
    }

    protected void removeNeighbour(int x, int y) {
        searchCell.setCoordinates(x, y);
        Cell cell = cells.get(searchCell);
        if (cell != null) {
            cell.decNeighbour();
            if (cell.pendingChange()) {
                if (!cell.isChanged()) {
                    cell.setChangedFlag();
                    toChange.put(cell);
                }
            } else {
                if (cell.isChanged()) {
                    cell.resetChangedFlag();
                    toChange.remove(cell);
                }
            }
        }
    }

    public void next() {

        startX = offsetX;
        startY = offsetY;
        endX = startX + zWidth();
        endY = startY + zHeight();

        toChange.removeIf(e -> {
            if (e.cycle()) {
                changedCells.add(e);
            }
            if (e.inRange(startX, startY, endX, endY)) {
                if (isDualMode() || e.isCurrentGeneration()) {
                    toDraw.add(e);
                }
            }
            if (e.isInvalidGhost()) {
                cells.remove(e);
                return true;
            }
            return !e.isSeeded() || e.isCurrentGeneration();
        });
        toDraw.removeIf(e -> !e.inRange(startX, startY, endX, endY));
        for (Cell cell : changedCells) {
            if (cell.isCurrentGeneration()) {
                addNeighbours(cell);
            } else {
                removeNeighbours(cell);
            }
        }
        changedCells.clear();
    }

}
