import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import Util.Grass;
import Util.Rabbit;
import Util.Burrow;

public class Main {

    public static void main(String[] args) throws FileNotFoundException{
        // w.setTile(new Location(0, 0), new <MyClass>());

        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        //GROW GRASS
        try{
            Scanner s  = new Scanner(new File("week-1/tf1-1.txt"));
            int size = Integer.parseInt(s.nextLine());

            Program p = new Program(size, 800, 750);
            World w = p.getWorld();


            Random r = new Random();

            while(s.hasNextLine()) {
                String line = s.nextLine().trim();
                String[] input = line.split("\\s+");

                if (input.length < 2) {
                    continue;
                }

                String type = input[0];

                int amount;
                    if (input[1].contains("-") ) {
                        String[] range = input[1].split("-");
                        int min = Integer.parseInt(range[0]);
                        int max = Integer.parseInt(range[1]);
                        amount = min + r.nextInt(max - min + 1);
                    } else {
                        amount = Integer.parseInt(input[1]);
                    }

                if (type.equals("grass")) {
                    for (int i = 0; i < amount; i++) {
                        Location l = new Location(r.nextInt(size), r.nextInt(size));
                        while (w.containsNonBlocking(l)) {
                            l = new Location (r.nextInt(size), r.nextInt(size));
                        }
                        w.setTile(l, new Grass());
                    }
                } else if (type.equals("rabbit")) {
                    for (int i = 0; i < amount; i++) {
                        Location l = new Location(r.nextInt(size), r.nextInt(size));
                        while(!w.isTileEmpty(l)) {
                            l = new Location(r.nextInt(size), r.nextInt(size));
                        }
                        w.setTile(l, new Rabbit());
                    }
                } else if (type.equals("burrow")) {
                        for (int i = 0; i < amount; i++) {
                        Location l = new Location(r.nextInt(size), r.nextInt(size));
                        while(!w.isTileEmpty(l)) {
                            l = new Location(r.nextInt(size), r.nextInt(size));
                        }
                        w.setTile(l, new Burrow());
                    }
                }
            }
            
            s.close();  
            p.show();

            for (int i = 0; i < 200; i++) {
                p.simulate();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }
}