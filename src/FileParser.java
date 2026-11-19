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

public class FileParser {

    public static Program load(String path) throws FileNotFoundException {
        Scanner s = new Scanner(new File(path));
        int size = Integer.parseInt(s.nextLine());

        Program p = new Program(size, 800, 750);
        World w = p.getWorld();

        Random r = new Random();

        while (s.hasNextLine()) {
            String line = s.nextLine().trim();
            String[] input = line.split("\\s+");

            if (input.length < 2) {
                continue;
            }

            String type = input[0];

            int amount;
            if (input[1].contains("-")) {
                String[] range = input[1].split("-");
                int min = Integer.parseInt(range[0]);
                int max = Integer.parseInt(range[1]);
                amount = min + r.nextInt(max - min + 1);
            } else {
                amount = Integer.parseInt(input[1]);
            }
            
            if (type.equals("grass")) {
                for (int i = 0; i < amount; i++) {
                    Location l = findFreeGroundTile(w, size, r);
                    w.setTile(l, new Grass());
                }

            } else if (type.equals("rabbit")) {
                for (int i = 0; i < amount; i++) {
                    Location l = new Location(r.nextInt(size), r.nextInt(size));
                    while (!w.isTileEmpty(l)) {
                        l = new Location(r.nextInt(size), r.nextInt(size));
                    }
                    w.setTile(l, new Rabbit());
                }
            } else if (type.equals("burrow")) {
                for (int i = 0; i < amount; i++) {
                    Location l = findFreeGroundTile(w, size, r);
                    w.setTile(l, new Burrow());
                }
            }
        }

        return p;
    }

    // Hjælpefunktioner, klar til brug når I vil forenkle løkken

    // Til græs og huller: finder et felt uden noget på jorden
    private static Location findFreeGroundTile(World w, int size, Random r) {
        Location l = new Location(r.nextInt(size), r.nextInt(size));
        while (w.containsNonBlocking(l)) {
            l = new Location(r.nextInt(size), r.nextInt(size));
        }
        return l;
    }


}