package com.github.ireallyhatelogins.cgol.matrix;

public class LifeMatrixTorus extends LifeMatrix {

    //storage for first line
    private byte[] firstLine;

    public LifeMatrixTorus(int height, int width) {
        this(height, width, height, width);
    }

    public LifeMatrixTorus(int height, int width, int canvasHeight, int canvasWidth) {
        super(height, width, canvasHeight, canvasWidth);
        firstLine = new byte[height];
        unbound = true;
    }

    protected byte getBlockSum(int i, int j) {
        //OOB checks
        if (j >= height) {
            j = 0;
        }
        return (byte) (((i == 0 ? field[width - 1][j] : field[i - 1][j]) >>> 1)
                + (field[i][j] >>> 1)
                + (((i + 1) >= width ? firstLine[j] : field[i + 1][j]) >>> 1));
    }//hope it will be inline

    protected boolean seedCell(byte cell, int seed) {
        byte n = (byte) (seed + (seed >>> 8) + (seed >>> 16));
        return (n == 3 || ((cell == 2) && (n == 4)));
    }//hope it will be inline

    protected int initRow(int x) {
        return (((int) getBlockSum(x, height - 1)) << 8) | getBlockSum(x, 0);
    }

    @Override
    public void next() {

        //Storing first line
        System.arraycopy(field[0], 0, firstLine, 0, height);
        super.next();
    }

    @Override
    public void draw() {
        int shiftX = trunkX(offsetX);
        int shiftY = trunkY(offsetY);
        int drawHeight = shiftY + zHeight();
        int drawWidth = shiftX + zWidth();
        for (int i = shiftX; i < drawWidth; ++i) {
            for (int j = shiftY; j < drawHeight; ++j) {
                if (field[trunkX(i)][trunkY(j)] == 2) {
                    currentPoint(i - shiftX, j - shiftY);
                } else if (isDualMode() && (field[trunkX(i)][trunkY(j)] == 1)) {
                    prevPoint(i - shiftX, j - shiftY);
                }
            }
        }
    }

    /**
     * Transforms number to be within width borders
     * @param x - num to trunk
     * @return - trunked number
     */
    private int trunkX(int x) {
        return (x >= 0) ? x % width : width - Math.abs(x) % width;
    }

    /**
     * Transforms number to be within height borders
     * @param y - num to trunk
     * @return - trunked number
     */
    private int trunkY(int y) {
        return (y >= 0) ? y % height : height - Math.abs(y) % height;
    }

}

