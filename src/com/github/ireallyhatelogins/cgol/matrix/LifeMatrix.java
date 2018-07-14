package com.github.ireallyhatelogins.cgol.matrix;

import com.github.ireallyhatelogins.cgol.AbstractLife;

import java.util.Arrays;
import java.util.Random;

/**
 * Abstract life with matrix storage and borders
 */
public class LifeMatrix extends AbstractLife {
    protected int height;
    protected int width;

    protected byte[][] field;
    protected byte[] prevLine;
    protected byte[] line;

    /**
     * Create life with height and widths equal with canvas
     * @param height - height for field and canvas
     * @param width - width for field and canvas
     */
    public LifeMatrix(int height, int width) {
        this(height, width, height, width);
    }

    /**
     * Create life with different height and widths for field and canvas
     * @param height - height for field
     * @param width - width for field
     * @param canvasHeight - height for canvas
     * @param canvasWidth - width for canvas
     */
    public LifeMatrix(int height, int width, int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth);
        super.setLimits(height, width);

        this.height = height;
        this.width = width;
        field = new byte[width][];
        for (int i = 0; i < width; ++i) {
            field[i] = new byte[height];
        }

        prevLine = new byte[width];
        line = new byte[width];

        clear();

        //set display to be bound
        unbound = false;
    }

    @Override
    public void randomize() {
        Random rnd = new Random();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                field[i][j] = (byte) ((rnd.nextInt() % 12) > 8 ? 2 : 0);
            }
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < width; ++i) {
            Arrays.fill(field[i], (byte) 0);
        }
    }

    /**
     * Get amount of alive cells in this point, one to the left and one to the right of it
     * @param i - x coordinate
     * @param j - y coordinate
     * @return - amount of alive cells
     */
    protected byte getBlockSum(int i, int j) {

        //OOB checks
        if (j >= height) {
            return 0;
        }
        return (byte) ((i == 0 ? 0 : field[i - 1][j] >>> 1)
                + (field[i][j] >>> 1)
                + ((i + 1) <= width ? 0 : field[i + 1][j] >>> 1));
    }

    /**
     * Check if cell with this seed will be alive or dead
     * @param cell - cell state
     * @param seed - cell seed info, seed is amount of neighbours, stored as 3 block sums,
     *             each in separate byte from 3-rd to 1-st LB
     * @return - whether or not cell is alive
     */
    protected boolean seedCell(byte cell, int seed) {
        byte n = (byte) ((seed + (seed >>> 8) + (seed >>> 16)));
        return ((n == 3) || ((cell == 2) && (n == 4)));
    }

    /**
     * Method that gets seed for first element in line,
     * Exists to be overridden in torus subclass
     * @param x - row index
     * @return - initial seed
     */
    protected int initRow(int x) {
        return getBlockSum(x, 0);
    }

    @Override
    protected int xUpper() {
        return width;
    }

    @Override
    protected int xLower() {
        return 0;
    }

    @Override
    protected int yUpper() {
        return height;
    }

    @Override
    protected int yLower() {
        return 0;
    }

    @Override
    public void next() {

        //seed, by left shifting by one byte partially preserves states of cells for next cell in line
        int shiftCounter;
        byte[] tmp;
        for (int i = 0; i < width; ++i) {
            byte[] currentLine = field[i];

            //init seed
            shiftCounter = initRow(i);

            for (int j = 0; j < height; ++j) {

                //shift seed obe byte and get next sum in LB
                shiftCounter = (shiftCounter << 8) | getBlockSum(i, j + 1);

                //set sell as alive or degrade.
                line[j] = (byte) (seedCell(currentLine[j], shiftCounter) ? 2 : currentLine[j] >>> 1);
            }
            if (i > 0)
            {
                //Swap if has prevLine
                tmp = field[i - 1];
                field[i - 1] = prevLine;
                prevLine = tmp;
            }

            //Save line as prev
            tmp = prevLine;
            prevLine = line;
            line = tmp;
        }

        //Swap last line
        tmp = field[width - 1];
        field[width - 1] = prevLine;
        prevLine = tmp;
    }

    @Override
    public void draw() {
        int shiftX = offsetX;
        int shiftY = offsetY;
        int drawHeight = shiftY + zHeight();
        int drawWidth = shiftX + zWidth();
        for (int i = shiftX; i < drawWidth; ++i) {
            for (int j = shiftY; j < drawHeight; ++j) {

                //if current generation
                if (field[i][j] == 2) {
                    currentPoint(i - shiftX, j - shiftY);
                }

                //else if previous generation
                else if (isDualMode() && (field[i][j] == 1)) {
                    prevPoint(i - shiftX, j - shiftY);
                }
            }
        }
    }

    @Override
    protected void add(int x, int y, int lineCount) {
        int xLen = x + lineCount;
        y = height - y - 1;

        if ((y >= height) || (xLen > width)) {

            //if OOB, just skip
            return;
        }
        for (int i = x; i < xLen; i++) {
            field[i][y] = 2;
        }
    }
}
