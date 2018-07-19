package com.github.ireallyhatelogins.cgol.list;

import com.github.ireallyhatelogins.cgol.AbstractLife;

import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;

public class LifeList extends AbstractLife {

    private ArrayList<CellRow> cellList = new ArrayList<>();
    private ArrayList<CellRow> previousCellList = cellList;
    private boolean unsorted = true;
    private CellRow currentLoadColumn = null;
    
    

    private class CellRow extends ArrayList<Integer> implements Comparable<CellRow>{
        private int index;

        CellRow(int index){
            super();
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public int compareTo(CellRow o) {
            return Integer.compare(this.index, o.index);
        }
    }

    public LifeList(int canvasHeight, int canvasWidth) {
        super(canvasHeight, canvasWidth);
    }

    @Override
    protected void next(){

        //skip on empty
        if(cellList.isEmpty()){
            return;
        }

        if(unsorted){
            sortLists();
            unsorted = false;
        }

        previousCellList = cellList;
        cellList = new ArrayList<>();

        Iterator<CellRow> curIt = previousCellList.iterator();

        //setup
        CellRow lRow = null;                                        //left Column
        CellRow cRow = null;                                        //center Column
        CellRow rRow = curIt.next();                                //right Column
        CellRow nextRow = curIt.hasNext() ? curIt.next() : null;    //next existing column
        int index = rRow.getIndex() - 1;                               //current index to check

        while (lRow != null || cRow != null || rRow != null)
        {
            CellRow newColumn = nextColumn(index, lRow, cRow, rRow);
            if (!newColumn.isEmpty()) {
                cellList.add(newColumn);
            }

            //setup next iteration
            index++;
            lRow = cRow;
            cRow = rRow;

            if(nextRow != null)
            {
                //leap through empty lines
                if(lRow == null && cRow == null){
                    index = nextRow.index - 1;
                }

                //insert next line if appropriate
                if(nextRow.index - 1 == index){
                    rRow = nextRow;
                    nextRow = curIt.hasNext() ? curIt.next() : null;
                } else {
                    rRow = null;
                }
            } else {
                rRow = null;
            }
        }
    }
    
    private CellRow nextColumn(Integer col, CellRow lRow, CellRow cRow, CellRow rCol){
        CellRow newRow = new CellRow(col);
        boolean leftRowExists = lRow != null && !lRow.isEmpty();
        boolean centerRowExists = cRow != null && !cRow.isEmpty();
        boolean rightRowExists = rCol != null && !rCol.isEmpty();

        Iterator<Integer> centerIt; //center column iterator
        Iterator<Integer> leftIt;   //left column iterator
        Iterator<Integer> rightIt;  //right column iterator

        boolean lb = false;          //left back
        boolean cc = false;          //left center
        boolean cf = false;          //left front
        boolean cb = false;          //center back
        boolean lc = false;          //center center
        boolean lf = false;          //center front
        boolean rb = false;          //right back
        boolean rc = false;          //right center
        boolean rf = false;          //right front

        Integer nextL = null;        //next existing left cell
        Integer nextC = null;        //next existing center cell
        Integer nextR = null;        //next existing right cell

        int lIndex;                  //current left index
        int cIndex;                  //current center index
        int rIndex;                  //current right index

        //init values
        if (leftRowExists){
            leftIt = lRow.iterator();
            lf = true;
            lIndex = leftIt.next() - 1;
            nextL =  leftIt.hasNext() ? leftIt.next() : null;
        } else {
            leftIt = Collections.emptyIterator();
            lIndex = Integer.MAX_VALUE;
        }
        if (centerRowExists){
            centerIt = cRow.iterator();
            cf = true;
            cIndex = centerIt.next() - 1;
            nextC = centerIt.hasNext() ? centerIt.next() : null;
        } else {
            centerIt = Collections.emptyIterator();
            cIndex = Integer.MAX_VALUE;
        }
        if(rightRowExists){
            rightIt = rCol.iterator();
            rf = true;
            rIndex = rightIt.next() - 1;
            nextR = rightIt.hasNext() ? rightIt.next() : null;
        } else {
            rightIt = Collections.emptyIterator();
            rIndex = Integer.MAX_VALUE;
        }

        int index = Integer.min(Integer.min(lIndex, cIndex), rIndex);

        //sync
        while (leftRowExists || rightRowExists || centerRowExists ) {
            int sum = 0;
            boolean leap = true;

            //left
            if (lIndex == index){

                //count
                if (lb){
                    sum++;
                }
                if (lc){
                    sum++;
                }
                if (lf){
                    sum++;
                }

                //step
                lIndex++;
                lb = lc;
                lc = lf;
                if (nextL != null){

                    //leap
                    if (!lb  && !lc){
                        lIndex = nextL - 1;
                    } else {
                        leap = false;
                    }
                    if(lIndex == nextL - 1){
                        lf = true;
                        nextL = leftIt.hasNext() ? leftIt.next() : null;
                    } else {
                        lf = false;
                    }
                } else {
                    leap = false;
                    if(!lc && !lb){
                        leftRowExists = false;
                        lIndex = Integer.MAX_VALUE;
                    } else {
                        lf = false;
                    }
                }
            }

            //center
            if (cIndex == index){
                //count
                if (cb){
                    sum++;
                }
                if (cc){
                    sum++;
                }
                if (cf){
                    sum++;
                }

                //step
                cIndex++;
                cb = cc;
                cc = cf;
                if (nextC != null){

                    //leap
                    if (!cb && !cc){
                        cIndex = nextC - 1;
                    } else {
                        leap = false;
                    }
                    if(cIndex == nextC - 1){
                        cf = true;
                        nextC = centerIt.hasNext() ? centerIt.next() : null;
                    } else {
                        cf = false;
                    }
                } else {
                    leap = false;
                    if (!cc && !cb) {
                        centerRowExists = false;
                        cIndex = Integer.MAX_VALUE;
                    } else {
                        cf = false;
                    }
                }
            }

            //right
            if (rIndex == index){
                //count
                if (rb){
                    sum++;
                }
                if (rc){
                    sum++;
                }
                if (rf){
                    sum++;
                }

                //step
                rIndex++;
                rb = rc;
                rc = rf;
                if (nextR != null){

                    //leap
                    if (!rb && !rc){
                        rIndex = nextR - 1;
                    } else {
                        leap = false;
                    }
                    if(rIndex == nextR - 1){
                        rf = true;
                        nextR = rightIt.hasNext() ? rightIt.next() : null;
                    } else {
                        rf = false;
                    }
                } else {
                    leap = false;
                    if (!rc && !rb) {
                        rightRowExists = false;
                        rIndex = Integer.MAX_VALUE;
                    } else {
                        rf = false;
                    }
                }
            }

            //count, !!cc shifted to cb or just none like the cb!!
            if (sum == 3 || (sum == 4 && cb)) {
                newRow.add(index);
            }

            //leap if needed
            if (leap) {
                index = Integer.min(Integer.min(lIndex, cIndex), rIndex);
            } else {
                index++;
            }

        }
        return newRow;
    }

    @Override
    protected void draw() {
        int startX = offsetX;
        int startY = offsetY;
        int endX = startX + zWidth();
        int endY = startY + zHeight();

        if(isDualMode()) {
            previousCellList.forEach(e -> {
                if((e.index >= startY) && (e.index <= endY))
                    e.forEach(ee -> {
                    if ((ee >= startX) && (ee <= endX)) {
                        prevPoint(ee - startX, e.index - startY);
                    }
                });
            });
        }
        cellList.forEach(e -> {
            if((e.index >= startY) && (e.index <= endY))
                e.forEach(ee -> {
                    if ((ee >= startX) && (ee <= endX)) {
                        currentPoint(ee - startX, e.index - startY);
                    }
                });
        });
    }

    @Override
    protected void add(int x, int y, int lineCount) {

        //just a bit unsafe if input file is broken...
        int xLen;

        x = offsetX + x;
        y = offsetY + zHeight() - y;
        xLen = x + lineCount;


        if(currentLoadColumn == null || currentLoadColumn.index != y){
            currentLoadColumn = new CellRow(y);
            cellList.add(currentLoadColumn);
        }

        for (int i = x; i < xLen; i++) {
            currentLoadColumn.add(i);
        }
        unsorted = true;
    }

    @Override
    public void clear() {
        cellList.clear();
        currentLoadColumn = null;
    }

    @Override
    public void randomize() {
        Random rnd = new Random();
        for (int i = offsetY; i < zHeight(); ++i) {
            CellRow row = new CellRow(i);
            for (int j = offsetX; j < zWidth(); ++j) {
                if ((rnd.nextInt() % 12) > 8) {
                    row.add(j);
                }
            }
            if (!row.isEmpty()){
                cellList.add(row);
            }
        }

    }

    private void sortLists(){
        cellList.removeIf(e -> {
            if(e.isEmpty()){
                return true;
            }
            e.sort(Integer::compareTo);
            return false;
        });
        cellList.sort(CellRow::compareTo);
    }
}
