import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import Util.Grass;
import Util.Rabbit;
import Util.Burrow;

// FileParser omsætter en input-fil til en færdig verden med aktører og elementer
public class FileParser {

    //Den tager filens sti og returnerer et Program.
    public static Program load(String path) throws FileNotFoundException {
        Scanner s = new Scanner(new File(path));

        // Læser filens første linje (N, verdenens størrelse) og laver teksten om til et heltal
        int size = Integer.parseInt(s.nextLine());

        // Opretter simulatoren med en verden på size x size felter.
        Program p = new Program(size, 800, 750);
        // Henter verdenen ud af programmet, så aktører kan placeres i den
        World w = p.getWorld();

        Random r = new Random();

        // Gentager så længe der er flere linjer i filen
        while (s.hasNextLine()) {
            
            // Læser næste linje og fjerner mellemrum i begyndelsen og slutningen
            String line = s.nextLine().trim();

            // Deler linjen op ved mellemrum, fx bliver "rabbit 5" til ["rabbit", "5"].
            // \\s+ betyder "ét eller flere mellemrum"
            String[] input = line.split("\\s+");

            // Hvis linjen ikke har mindst to dele (fx en tom linje), springes den over
            if (input.length < 2) {
                continue;
            }

            // Første del er typen: grass, rabbit eller burrow
            String type = input[0];

            // Variabel til antallet, der skal tilføjes
            int amount;

            // Tjekker om andet led indeholder en bindestreg, altså om det er et interval som "10-20"
            if (input[1].contains("-")) {
                // Deler "10-20" op i "10" og "20" og laver dem til heltallene min og max
                String[] range = input[1].split("-");
                int min = Integer.parseInt(range[0]);
                int max = Integer.parseInt(range[1]);
                // Vælger et tilfældigt tal mellem min og max, begge inkluderet.
                // r.nextInt(max - min + 1) giver et tal fra 0 til max - min, og min lægges til
                amount = min + r.nextInt(max - min + 1);
            } else {
                // Ingen bindestreg betyder et fast tal, som laves om til et heltal
                amount = Integer.parseInt(input[1]);
            }
            
            if (type.equals("grass")) {
                for (int i = 0; i < amount; i++) {
                    // Find et ledigt, tilfældigt felt med hjælpemetoden
                    Location l = findFreeGroundTile(w, size, r);
                    // Placér et nyt Grass-objekt på feltet
                    w.setTile(l, new Grass());
                }

            } else if (type.equals("rabbit")) {
                for (int i = 0; i < amount; i++) {
                    // Laver en tilfældig position med x og y
                    Location l = new Location(r.nextInt(size), r.nextInt(size));

                    // Så længe feltet ikke er tomt (isTileEmpty er false), trækkes en ny tilfældig position. Løkken stopper, når et tomt felt er fundet
                    while (!w.isTileEmpty(l)) {
                        l = new Location(r.nextInt(size), r.nextInt(size));
                    }
                    // Placerer en ny kanin på det fundne felt
                    w.setTile(l, new Rabbit());
                }

            // Hvis typen er burrow (kaninhul): samme fremgangsmåde som for græs
            } else if (type.equals("burrow")) {
                for (int i = 0; i < amount; i++) {
                    Location l = findFreeGroundTile(w, size, r);
                    w.setTile(l, new Burrow());
                }
            }
        }

        // Når alle linjer er læst, returneres det færdige Program til Main
        return p;
    }


    // Privat hjælpemetode, som kun bruges inde i FileParser, finder et felt uden noget på jorden
    private static Location findFreeGroundTile(World w, int size, Random r) {
        // Trækker en tilfældig position
        Location l = new Location(r.nextInt(size), r.nextInt(size));

        // Så længe feltet allerede indeholder et NonBlocking-objekt (fx græs eller et hul), trækkes en ny position. Det sikrer, at to ting på jorden ikke placeres oven på hinanden
        while (w.containsNonBlocking(l)) {
            l = new Location(r.nextInt(size), r.nextInt(size));
        }
        // Returnerer den frie position
        return l;
    }


}