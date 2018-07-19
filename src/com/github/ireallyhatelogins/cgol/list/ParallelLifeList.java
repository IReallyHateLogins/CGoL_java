package com.github.ireallyhatelogins.cgol.list;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class ParallelLifeList extends LifeList {

    protected class ParallelRowProcessor implements Callable<CellRow> {
        Integer rowIndex;
        CellRow lRow;
        CellRow cRow;
        CellRow rRow;

        public  ParallelRowProcessor(Integer rowIndex, CellRow lRow, CellRow cRow, CellRow rRow){
            this.rowIndex = rowIndex;
            this.lRow = lRow;
            this.cRow = cRow;
            this.rRow = rRow;
        }

        @Override
        public CellRow call() {
            CellRow newRow = new CellRow(rowIndex);
            boolean leftRowExists = lRow != null && !lRow.isEmpty();
            boolean centerRowExists = cRow != null && !cRow.isEmpty();
            boolean rightRowExists = rRow != null && !rRow.isEmpty();

            Iterator<Integer> centerIt;  //center row iterator
            Iterator<Integer> leftIt;    //left row iterator
            Iterator<Integer> rightIt;   //right row iterator

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
                rightIt = rRow.iterator();
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
    }

    public ParallelLifeList(int canvasHeight, int canvasWidth) {
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
        ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        //setup
        CellRow lRow = null;                                        //left row
        CellRow cRow = null;                                        //center row
        CellRow rRow = curIt.next();                                //right row
        CellRow nextRow = curIt.hasNext() ? curIt.next() : null;    //next existing row
        int index = rRow.getIndex() - 1;                            //current index to check

        List<Future<CellRow>> futureList = new ArrayList<>();

        //main loop, exit when all rows processed
        while ((lRow != null) || (cRow != null) || (rRow != null))
        {

            //parallel processing
            futureList.add(service.submit(new ParallelRowProcessor(index, lRow, cRow, rRow)));

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

        service.shutdown();
        futureList.forEach(e -> {
            try {
                CellRow row = e.get();
                if(!row.isEmpty()) {
                    cellList.add(row);
                }
            } catch (InterruptedException | ExecutionException e1) {

                //!! LATER !!
                e1.printStackTrace();
            }
        });
        while (!service.isTerminated()){
            //!!await, no timer yet, might add later with awaitTermination!!
        }
    }
}
