package com.github.ireallyhatelogins.cgol.list;

import com.github.ireallyhatelogins.cgol.AbstractLife;

import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;

public class LifeList extends AbstractLife {

    protected ArrayList<CellRow> cellList = new ArrayList<>();
    protected ArrayList<CellRow> previousCellList = cellList;     //not local for drawing
    protected boolean unsorted = true;                            //indicates if current cell list might be unordered
    protected CellRow currentLoadRow = null;                      //stores last used row while generating field to speed up the process


    protected class CellRow extends ArrayList<Integer> implements Comparable<CellRow>{
        protected int index;

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

        //likely unnecessary, but for safety sake
        if(unsorted){
            sortLists();
            unsorted = false;
            currentLoadRow = null; //if processing started, no need to have load buffer
        }

        previousCellList = cellList;
        cellList = new ArrayList<>();

        Iterator<CellRow> curIt = previousCellList.iterator();

        //setup
        CellRow lRow = null;                                        //left row
        CellRow cRow = null;                                        //center row
        CellRow rRow = curIt.next();                                //right row
        CellRow nextRow = curIt.hasNext() ? curIt.next() : null;    //next existing row
        int index = rRow.getIndex() - 1;                            //current index to check

        //main loop, exit when all rows processed
        while ((lRow != null) || (cRow != null) || (rRow != null))
        {
            //create new row, save if not empty
            CellRow newRow = nextRow(index, lRow, cRow, rRow);
            if (!newRow.isEmpty()) {
                cellList.add(newRow);
            }

            //setup next iteration
            index++;
            lRow = cRow;
            cRow = rRow;

            if(nextRow != null)
            {
                //leap through empty lines
                if((lRow == null) && (cRow == null)){
                    index = nextRow.index - 1;
                }

                //insert next line if appropriate
                if((nextRow.index - 1) == index){
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
    
    private CellRow nextRow(Integer col, CellRow lRow, CellRow cRow, CellRow rCol){
        CellRow newRow = new CellRow(col);
        boolean leftRowExists = lRow != null && !lRow.isEmpty();
        boolean centerRowExists = cRow != null && !cRow.isEmpty();
        boolean rightRowExists = rCol != null && !rCol.isEmpty();

        Iterator<Integer> centerIt;  //center column iterator
        Iterator<Integer> leftIt;    //left column iterator
        Iterator<Integer> rightIt;   //right column iterator

        byte leftBuffer = 0;         //buffer for left row cell state (dead or alive for current index +-1)
        byte centerBuffer = 0;       //buffer for center row cell state
        byte rightBuffer = 0;        //buffer for right row cell state

        Integer nextL = null;        //next existing left cell
        Integer nextC = null;        //next existing center cell
        Integer nextR = null;        //next existing right cell

        int lIndex;                  //current left index
        int cIndex;                  //current center index
        int rIndex;                  //current right index

        //init values
        if (leftRowExists){
            leftIt = lRow.iterator();
            leftBuffer = 1; //set first bit
            lIndex = leftIt.next() - 1; //actual index is left of first cell
            nextL =  leftIt.hasNext() ? leftIt.next() : null;
        } else {
            leftIt = Collections.emptyIterator();
            lIndex = Integer.MAX_VALUE; //important to work with min search
        }
        if (centerRowExists){
            centerIt = cRow.iterator();
            centerBuffer = 1; //set first bit
            cIndex = centerIt.next() - 1; //actual index is left of first cell
            nextC = centerIt.hasNext() ? centerIt.next() : null;
        } else {
            centerIt = Collections.emptyIterator();
            cIndex = Integer.MAX_VALUE; //important to work with min search
        }
        if(rightRowExists){
            rightIt = rCol.iterator();
            rightBuffer = 1; //set first bit
            rIndex = rightIt.next() - 1; //actual index is left of first cell
            nextR = rightIt.hasNext() ? rightIt.next() : null;
        } else {
            rightIt = Collections.emptyIterator();
            rIndex = Integer.MAX_VALUE; //important to work with min search
        }

        //start index selected from all
        int index = Integer.min(Integer.min(lIndex, cIndex), rIndex);

        //main loop, exit when all rows have no cells
        while (leftRowExists || rightRowExists || centerRowExists ) {
            int neighbourCount = 0;
            boolean leap = true; //if all rows effective position is above current, flag is set to skip unnecessary cells

            //left
            if (lIndex == index){

                //count
               switch (leftBuffer){
                   case 0b111: //three bytes
                       neighbourCount += 3;
                       break;
                   case 0b101: //any two bytes
                   case 0b011:
                   case 0b110:
                       neighbourCount += 2;
                       break;
                   case 0b100: //any one byte
                   case 0b010:
                   case 0b001:
                       neighbourCount += 1;
                       break;
                   default:
                       break;
               }

                //step
                lIndex++;
                leftBuffer = (byte) ((leftBuffer << 1) & 0b111); //shift + clear excess
                if (nextL != null){

                    //check for leap
                    if (leftBuffer == 0){
                        lIndex = nextL - 1;
                    } else {
                        leap = false;
                    }

                    //cycle next cell if it is next
                    if (lIndex == (nextL - 1)){
                        leftBuffer |= 1; //push first bit
                        nextL = leftIt.hasNext() ? leftIt.next() : null;
                    }
                } else {

                    //disable row if no cells left
                    if (leftBuffer == 0){
                        leftRowExists = false;
                        lIndex = Integer.MAX_VALUE; //important to work with min search
                    } else {
                        leap = false;
                    }

                }
            }

            //center
            if (cIndex == index){

                //count
                switch (centerBuffer){
                    case 0b111: //three bits
                        neighbourCount += 3;
                        break;
                    case 0b101: //any two bits
                    case 0b011:
                    case 0b110:
                        neighbourCount += 2;
                        break;
                    case 0b100: //any one bit
                    case 0b010:
                    case 0b001:
                        neighbourCount += 1;
                        break;
                    default:
                        break;
                }

                //step
                cIndex++;
                centerBuffer = (byte) ((centerBuffer << 1) & 0b111); //shift + clear excess
                if (nextC != null){

                    //check for leap
                    if (centerBuffer == 0){
                        cIndex = nextC - 1;
                    } else {
                        leap = false;
                    }

                    //cycle next cell if it is next
                    if (cIndex == (nextC - 1)){
                        centerBuffer |= 1;  //push first bit
                        nextC = centerIt.hasNext() ? centerIt.next() : null;
                    }
                } else {

                    //disable row if no cells left
                    if (centerBuffer == 0) {
                        centerRowExists = false;
                        cIndex = Integer.MAX_VALUE; //important to work with min search
                    } else {
                        leap = false;
                    }
                }
            }

            //right
            if (rIndex == index){

                //count
                switch (rightBuffer){
                    case 0b111: //three bytes
                        neighbourCount += 3;
                        break;
                    case 0b101: //any two bytes
                    case 0b011:
                    case 0b110:
                        neighbourCount += 2;
                        break;
                    case 0b100: //any one byte
                    case 0b010:
                    case 0b001:
                        neighbourCount += 1;
                        break;
                    default:
                        break;
                }

                //step
                rIndex++;
                rightBuffer = (byte) ((rightBuffer << 1) & 0b111); //shift + clear excess
                if (nextR != null){

                    //check for leap
                    if (rightBuffer == 0){
                        rIndex = nextR - 1;
                    } else {
                        leap = false;
                    }

                    //cycle next cell if it is next
                    if (rIndex == (nextR - 1)){
                        rightBuffer |= 1; //push first bit
                        nextR = rightIt.hasNext() ? rightIt.next() : null;
                    }
                } else {

                    //disable row if no cells left
                    if (rightBuffer == 0) {
                        rightRowExists = false;
                        rIndex = Integer.MAX_VALUE;
                    } else {
                        leap = false;
                    }
                }
            }

            //count, !!centre cell shifted to left centre cell or just none like left centre cell!!
            if (neighbourCount == 3 || (neighbourCount == 4 && (centerBuffer >= 0b100))) {
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

        //setting actual coordinates
        x = offsetX + x;
        y = offsetY + zHeight() - y;
        xLen = x + lineCount;

        //If row index is different, saving new row
        //!!If new row is duplicating, we are in trouble, but current methods are safe from this!!
        if((currentLoadRow == null) || (currentLoadRow.index != y)){
            currentLoadRow = new CellRow(y);
            cellList.add(currentLoadRow);
        }

        //adding all cells
        for (int i = x; i < xLen; i++) {
            currentLoadRow.add(i);
        }
        unsorted = true;
    }

    @Override
    public void clear() {
        cellList.clear();
        currentLoadRow = null;
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

    protected void sortLists(){
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
