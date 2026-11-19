import java.io.FileNotFoundException;

import itumulator.executable.Program;


public class Main {

    public static void main(String[] args) throws FileNotFoundException{
        
        try{
            Program p = FileParser.load("week-1/tf1-1.txt");

            p.show();

            for (int i = 0; i < 200; i++) {
                p.simulate();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}