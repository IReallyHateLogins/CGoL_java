package local.main;

import com.github.ireallyhatelogins.cgol.*;
import com.github.ireallyhatelogins.cgol.list.LifeList;

import java.lang.String;
import java.io.*;


public class Main {

    private static final boolean LOAD = true;

    public static void main(String[] args) {
        int height = 1000;
        int width = 1900;
        AbstractLife basic = new LifeList(height, width);
        if(LOAD) {
            try {
                FileReader reader = new FileReader("rle/Caterpillar.rle");
                BufferedReader bReader = new BufferedReader(reader);
                basic.load(bReader);
                bReader.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }else {
            basic.randomize();
        }

        basic.enablePreviousGeneration();
        basic.enableStagnate();

        while (true) {
            basic.drawNext();
            //basic.enableStagnate();
        }
    }
}
