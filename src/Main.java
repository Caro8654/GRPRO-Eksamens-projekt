import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import Util.Grass;

public class Main {

    public static void main(String[] args) throws FileNotFoundException{
        // w.setTile(new Location(0, 0), new <MyClass>());

        // p.setDisplayInformation(<MyClass>.class, new DisplayInformation(<Color>, "<ImageName>"));

        //GROW GRASS
        try{
            Scanner s  = new Scanner(new File("week-1/t1-1a.txt"));
            int size = Integer.parseInt(s.nextLine());

            Program p = new Program(size, 800, 75);
            World w = p.getWorld();
            p.setDisplayInformation(Grass.class, new DisplayInformation(Color.GREEN, "grass"));

            Random r = new Random();

            while(s.hasNextLine()) {
                String[] input = s.nextLine().split(" ");
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
            }

            s.close();  
            p.show();
        }

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        

    }

}