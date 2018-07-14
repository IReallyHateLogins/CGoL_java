package com.github.ireallyhatelogins.cgol.cells;

/**
 * Cell to use as search object
 */
final class MutableCell extends Cell {

    MutableCell(int x, int y) {
        super(x, y);
    }

    /**
     * Change coordinates, use only in objects dedicated for search
     * @param x - new x coordinate
     * @param y - new y coordinate
     */
    void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
