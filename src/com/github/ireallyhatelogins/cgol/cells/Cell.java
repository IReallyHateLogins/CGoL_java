package com.github.ireallyhatelogins.cgol.cells;

public class Cell {
    private static final byte NEIGHBOR_COUNT = 0x1F;     // stores neighbour count
    private static final byte GENERATION_FLAG = 0x40;    // set if previous generation
    private static final byte CHANGED_FLAG = 0x20;       // set in pending if there will be change on cycle
    private static final byte SEEDED_FLAG = -0x80;       // set for non-ghost sells; e.g. 0x80.. I want unsigned types...
    private static final byte MIN_NEIGHBOURS = 0;        // for safety
    private static final byte MAX_NEIGHBOURS = 8;

    protected int x;
    protected int y;

    private byte flags = 0;                              // start as ghost cell without neighbours


    Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Cell(int x, int y, boolean seeded) {
        this(x, y);
        if (seeded) {
            seed();
        }
    }

    final int getX() {
        return x;
    }

    final int getY() {
        return y;
    }

    /**
     * Access neighbour count
     * @return - neighbor count
     */
    final int getN() {
        return flags & NEIGHBOR_COUNT;
    }

    /**
     * Increase neighbor count by 1
     */
    final void incNeighbour() {
        if (getN() < MAX_NEIGHBOURS) {
            flags++;
        }
    }

    /**
     * Decrease neighbor count by 1
     */
    final void decNeighbour() {
        if (getN() > MIN_NEIGHBOURS) {
            flags--;
        }
    }

    /**
     * Is cell displayed? (e.g. of current o previous generation)
     * @return - seeded flag
     */
    final boolean isSeeded() {
        return (flags & SEEDED_FLAG) != 0;
    }

    /**
     * Is cell of current generation?
     * @return - generation flag
     */
    final boolean isCurrentGeneration() {
        return (flags & GENERATION_FLAG) != 0;
    }

    /**
     * Is this cell a ghost with zero neighbours?
     * @return - invalid state
     */
    final boolean isInvalidGhost() {
        return flags == 0;
    }

    /**
     * Bring cell to life
     */
    final void seed() {
        flags |= SEEDED_FLAG | GENERATION_FLAG;
    }

    /**
     * Reset generation flag
     */
    final void setPreviousGeneration() {
        flags &= ~GENERATION_FLAG;
    }

    /**
     * Reset seeded flag(does nit touch generation flag, should be reset together
     */
    final void setGhost() {
        flags &= ~SEEDED_FLAG;
    }

    /**
     * Set flag to signal potential change
     */
    final void setChangedFlag() {
        flags |= CHANGED_FLAG;
    }

    /**
     * Reset changed flag
     */
    final void resetChangedFlag() {
        flags &= ~CHANGED_FLAG;
    }

    /**
     * Check changed flag
     * @return - flag state
     */
    final boolean isChanged() {
        return (flags & CHANGED_FLAG) != 0;
    }

    /**
     * Progress cell to next generation, affecting it's state. Sets flags.
     * @return - is change affects neighboring cells
     */
    final boolean cycle() {//life cycle of cell
        boolean curGen = isCurrentGeneration();
        int neighbours = getN();
        resetChangedFlag();

        if ((neighbours == 3) || (curGen && (neighbours == 2))) {
            seed();
            return !curGen;
        } else if (curGen) {
            setPreviousGeneration();
            return true;
        } else {
            setGhost();
        }
        return false;
    }

    /**
     * Check if state is going to be changed in cycle
     * @return - will state change on cycle
     */
    final boolean pendingChange() {
        boolean curGen = isCurrentGeneration();
        int neighbours = getN();

        if ((neighbours == 3) && !curGen) {
            return true;
        } else if (curGen || (neighbours != 2)) {
            return true;
        }
        return isSeeded();
    }

    /**
     * Check if cell is to be displayed within this range, wrapping through overflow if necessary
     * @param startX - left X limit
     * @param startY - left Y limit
     * @param endX - right X limit
     * @param endY - right Y limit
     * @return - is cell in range
     */
    final boolean inRange(int startX, int startY, int endX, int endY) {
        int x;
        int y;

        if (!isSeeded()) {
            return false;
        }

        x = getX();
        y = getY();

        //make it a giant torus...
        //checking x
        if (endX >= startX) {
            if ((x < startX) || (x > endX)) {
                return false;
            }
        } else {
            if ((x < startX) && (x > endX)) {
                return false;
            }
        }

        //checking y
        if (endY >= startY) {
            if ((y < startY) || (y > endY)) {
                return false;
            }
        } else {
            if ((y < startY) && (y > endY)) {
                return false;
            }
        }

        return true;
    }

    @Override
    final public int hashCode() {
        return ((x & 0xffff) | ((y << 16) & 0xffff0000));
    }

    @Override
    final public boolean equals(Object obj) {
        if (!(obj instanceof Cell)) {
            return false;
        }
        Cell other = (Cell) obj;
        return (x == other.x) && (y == other.y);
    }
}

