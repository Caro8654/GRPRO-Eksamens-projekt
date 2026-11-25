import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import itumulator.world.Location;
import itumulator.world.World;
import Util.Grass;

class testGrass {

    @Test
    void grassSpread() {
        int size = 10;
        World w = new World(10);
        Grass grass = new Grass();
        Location start = new Location(2,2);
        w.setTile(start, grass);
        w.setCurrentLocation(start);

        for (int i = 0; i < 100; i++) {
            grass.act(w);
        }

        assertTrue(countGrass(w, size) > 1, "Græsset burde have spredt sig efter 100 skridt");
    }

    private int countGrass(World w, int size) {
        int count = 0;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Location l = new Location(x,y);
                if (w.containsNonBlocking(l) && w.getNonBlocking(l) instanceof Grass) {
                    count++;
                }
            }
        }

        return count;
    }
}