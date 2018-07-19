package com.github.ireallyhatelogins.cgol;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;


/**
 * Abstract class, using StdDraw to display game of life
 * In subclasses implement storage, draw and next methods
 */
public abstract class AbstractLife {
    private static final int UNSIGNED = 0x7FFFFFFF;

    private static final int STATUS_W = 120;
    private static final int FRAME_H = 35;
    private static final int STATUS_H = (int) (FRAME_H * 1.5);
    private static final int FPS_H = FRAME_H / 2;
    private static final double Z_COEFF = 0.05;
    private static final double OFFSET_COEFF = 0.01;
    private static final double DEFAULT_Z_MAX = 3;
    private static final double DEFAULT_Z_MIN = Z_COEFF;
    private static final double DEFAULT_Z_RADIUS = 0.5;

    private static final int PAUSE_KEY = ' ';
    private static final int UP_KEY = 'w';
    private static final int DOWN_KEY = 's';
    private static final int LEFT_KEY = 'a';
    private static final int RIGHT_KEY = 'd';
    private static final int ZOOM_IN_KEY = '+';
    private static final int ZOOM_OUT_KEY = '-';

    private static final String S_RUNNING = "Running";
    private static final String S_PAUSED = "Paused";
    private static final String S_LOADING = "Loading";
    private static final String S_UNDEF = "NAN";

    //offset for displayed field, left-low point
    protected int offsetX = 0;
    protected int offsetY = 0;
    protected boolean unbound = true;

    private int canvasHeight;
    private int canvasWidth;

    //coordinates for status information
    private double statusX;
    private double fpsY;
    private double frameY;
    private double statusY;

    //zoom parameters
    private double zRadius = DEFAULT_Z_RADIUS;
    private double zMax = DEFAULT_Z_MAX;
    private double zMin = DEFAULT_Z_MIN;

    private enum SimulationState {
        PAUSED,
        LOADING,
        RUNNING
    }
    private enum BrushState {
        CURR, PREV, UNDETERMINED
    }

    private BrushState brushState = BrushState.UNDETERMINED;

    private boolean dualMode = false;
    private boolean doNext = false;

    public AbstractLife(int canvasHeight, int canvasWidth) {
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;

        statusX = canvasWidth - STATUS_W;
        frameY = canvasHeight - FRAME_H;
        fpsY = canvasHeight - FPS_H;
        statusY = canvasHeight - STATUS_H;

        StdDraw.setCanvasSize(canvasWidth, canvasHeight);
        StdDraw.setXscale(0, canvasWidth);
        StdDraw.setYscale(0, canvasHeight);

        StdDraw.enableDoubleBuffering();
    }

    /**
     * Randomizes cell states in implementation
     * Override in subclasses
     */
    public void randomize() {
    }

    /**
     * Enables draw of previous generation on canvas
     */
    public final void enablePreviousGeneration() {
        dualMode = true;
    }

    /**
     * Disables draw of previous generation on canvas
     */
    public final void disablePreviousGeneration() {
        dualMode = false;
    }

    /**
     * Disables next generation calculation
     */
    public final void enableStagnate() {
        doNext = false;
    }

    /**
     * Enables next generation calculation
     */
    public final void disableStagnate() {
        doNext = true;
    }

    /**
     * Loads cell state from rle life files
     * @param reader - buffer to read from
     * @throws IOException
     */
    public final void load(BufferedReader reader) throws IOException {
        int input;
        int x = 0;
        int y = 0;
        int count = 0;
        boolean numRead = false;

        clear();
        displayStats(0, SimulationState.LOADING);
        StdDraw.show();
        loadCycle:
        while (true) {
            input = reader.read();
            if (Character.isDigit(input)) {//num
                if (numRead) {
                    count = count * 10 + Character.digit(input, 10);
                } else {
                    count = Character.digit(input, 10);
                    numRead = true;
                }
                continue;
            } else if (!numRead) {
                count = 1;
            }
            switch (input) {
                case 'o': // alive
                    add(x, y, count);

                    /*FALLING THROUGH*/
                case 'b': // dead
                    x += count;
                    break;
                case '$': // EOL
                    y += count;
                    x = 0;
                    break;
                case '!': // EOF
                    break loadCycle;
                case '\r':

                    /*FALLING THROUGH*/
                case '\n':
                    continue; //prevent line skips?
                default: // skip
                    reader.readLine(); // test
                    continue; // leave num?
            }
            numRead = false;
        }
    }

    /**
     * Function to run in loop for life to work
     */
    public final void step() {
        long tStart = System.currentTimeMillis();
        processKeys();
        StdDraw.clear(Color.white);
        if (doNext) {
            next();
        }
        draw();
        displayStats(System.currentTimeMillis() - tStart, (doNext ? SimulationState.RUNNING : SimulationState.PAUSED));
        StdDraw.show();
        brushState = BrushState.UNDETERMINED;
    }

    /**
     * Clears all alive cells,
     * Should be overridden
     */
    public abstract void clear();

    /**
     * Method to calculate next generation
     * Should be overridden
     */
    protected abstract void next();

    /**
     * Method to draw current state
     *  Should be overridden and
     *  use currentPoint and previousPoint
     *  to draw, current canvas scope can be obtained
     *  from offsets and zHeight/zWidth
     */
    protected abstract void draw();

    /**
     * Used in load method,
     * Override to add line of cells from x and y
     * @param x - start x coordinate
     * @param y - start y coordinate
     * @param lineCount - amount of cells to add, from x to x + lineCount
     */
    protected abstract void add(int x, int y, int lineCount);

    /**
     * Set zoom limits to not zoom out more than height and width
     * @param height - filed height
     * @param width - field width
     */
    protected void setLimits(int height, int width) {
        zRadius = Math.max(((double) canvasHeight / height) / 2, ((double) canvasWidth / width) / 2);
//        if(zRadius <= 0.0){
//            throw
//        }
        zMin = zRadius;
        zMax = Math.max(DEFAULT_Z_MAX, zRadius * 5);
    }

    /**
     * Access whether or not previous cells are displayed
     * @return - whether or not previous cells are displayed
     */
    protected final boolean isDualMode() {
        return dualMode;
    }

    /**
     * Override for custom borders of your field
     * @return - right border of your field
     */
    protected int xUpper() {
        return Integer.MAX_VALUE;
    }

    /**
     * Override for custom borders of your field
     * @return - left border of your
     */
    protected int xLower() {
        return Integer.MIN_VALUE;
    }

    /**
     * Override for custom borders of your field
     * @return - upper border of your field
     */
    protected int yUpper() {
        return Integer.MAX_VALUE;
    }

    /**
     * Override for custom borders of your field
     * @return - lower border of your field
     */
    protected int yLower() {
        return Integer.MIN_VALUE;
    }

    /**
     * Display current generation point
     * @param x - x coordinate
     * @param y - y coordinate
     */
    protected final void currentPoint(int x, int y) {
        if (brushState != BrushState.CURR) {
            StdDraw.setPenColor(dualMode ? Color.blue : Color.black);
        }
        point(x, y);
    }

    /**
     * Display previous generation point
     * @param x - x coordinate
     * @param y - y coordinate
     */
    protected final void prevPoint(int x, int y) {
        if (brushState != BrushState.PREV) {
            StdDraw.setPenColor(Color.red);
        }
        point(x, y);
    }

    /**
     * Current filed height, with current zoom level
     * @return - current field height
     */
    protected final int zHeight() {
        return (int) (canvasHeight / zRadius) >>> 1;
    }

    /**
     * Current field width, with current zoom level
     * @return - current field width
     */
    protected final int zWidth() {
        return (int) (canvasWidth / zRadius) >>> 1;
    }

    /**
     * Wrapped draw function, draws point using current zoom level
     * @param x - x coordinate
     * @param y - y coordinate
     */
    private void point(int x, int y) {
        StdDraw.filledSquare((((x << 1) & UNSIGNED) * zRadius) + zRadius, (((y << 1) & UNSIGNED) * zRadius) + zRadius, zRadius);
    }

    /**
     * Method that process pressed keys to change zoom, offset and play/pause
     */
    private void processKeys() {
        int x = 0;
        int y = 0;
        int z = 0;
        boolean space = false;

        while (StdDraw.hasNextKeyTyped()) {
            int c = StdDraw.nextKeyTyped();
            switch (c) {
                case PAUSE_KEY:
                    space = true;
                    break;
                case UP_KEY: //
                    ++y;
                    break;
                case LEFT_KEY:
                    --x;
                    break;
                case DOWN_KEY:
                    --y;
                    break;
                case RIGHT_KEY:
                    ++x;
                    break;
                case ZOOM_IN_KEY:
                    ++z;
                    break;
                case ZOOM_OUT_KEY:
                    --z;
                    break;
            }
        }

        if (space) { // play/pause
            doNext = !doNext;
        }

        zoom(z); //apply zoom first
        moveX(x);
        moveY(y);
    }

    /**
     * Method to change zoom level
     * @param count - total amount of zoom change, positive for zooming in, negative for zooming out
     */
    private void zoom(int count) {
        double newRadius;

        if (count == 0) {
            return;
        }

        newRadius = zRadius + count * Z_COEFF;

        // Checking zoom limits
        if (newRadius < zMin) {
            newRadius = zMin;
        } else if (newRadius > zMax) {
            newRadius = zMax;
        }

        //Correcting position to current zoom level, centring on middle point
        if (newRadius != zRadius) {
            int xMid = (zWidth() / 2) + offsetX;
            int yMid = (zHeight() / 2) + offsetY;
            zRadius = newRadius;
            int halfHeight = zHeight() / 2;
            int halfWidth = zWidth() / 2;
            offsetX = xMid - halfWidth;
            offsetY = yMid - halfHeight;
        }
    }

    /**
     * Change offset of x coordinate
     * @param x - amount of change, positive for left shift, negative for right shift
     */
    private void moveX(int x) {

        //calculating new offset position
        int xShift = offsetX + (x == 0 ? 0 : 1 + (int) (x * canvasWidth * OFFSET_COEFF));

        //if no bounds, apply
        if (unbound) {
            offsetX = xShift;
            return;
        }

        //if has bounds, check them
        if (xShift >= xLower()) {
            if ((xShift + zWidth()) <= xUpper()) {
                offsetX = xShift;
            } else {
                offsetX = xUpper() - zWidth();
            }
        } else {
            offsetX = xLower();
        }
    }

    /**
     * Change offset of y coordinate
     * @param y - amount of change, positive for up shift, negative for down shift
     */
    private void moveY(int y) {

        //calculating new offset position
        int yShift = offsetY + (y == 0 ? 0 : 1 + (int) (y * canvasHeight * OFFSET_COEFF));

        //if no bounds, apply
        if (unbound) {
            offsetY = yShift;
            return;
        }

        //if has bounds, check them
        if (yShift >= yLower()) {
            if ((yShift + zHeight()) <= yUpper()) {
                offsetY = yShift;
            } else {
                offsetY = yUpper() - zHeight();
            }
        } else {
            offsetY = yLower();
        }
    }

    /**
     * function to display statistics
     * @param tFrame - frame time
     * @param simulationState - state of simulation
     */
    private void displayStats(long tFrame, SimulationState simulationState) {
        String time = "frame: " + tFrame + "ms";
        String fps = "fps: " + String.format("%.2f", 1000.0 / tFrame);
        String status;
        switch (simulationState) {
            case PAUSED:
                status = S_PAUSED;
                break;
            case LOADING:
                status = S_LOADING;
                break;
            case RUNNING:
                status = S_RUNNING;
                break;
            default:
                status = S_UNDEF;
                break;
        }
        StdDraw.setPenColor(Color.black);
        StdDraw.textLeft(statusX, frameY, time);
        StdDraw.textLeft(statusX, fpsY, fps); // round/format
        StdDraw.textLeft(statusX, statusY, status); // round/format
    }

}
