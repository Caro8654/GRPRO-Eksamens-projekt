import java.io.FileNotFoundException;

import itumulator.executable.Program;

// Main er programmets startpunkt.
// den indlæser bare en input-fil, viser verdenen og kører simulationen
public class Main {

    public static void main(String[] args){
        
        try{
            //Kalder load i vores egen klasse FileParser. Den læser input-filen, opretter verdenen
            Program p = FileParser.load("week-1/t1-2cde.txt");

            // Åbner vinduet, hvor verdenen vises grafisk
            p.show();

            // Løkke, der kører 200 gange, altså 200 simulationstrin
            for (int i = 0; i < 200; i++) {
                p.simulate();
            }

        // Fanger fejlen, hvis input-filen ikke findes
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}