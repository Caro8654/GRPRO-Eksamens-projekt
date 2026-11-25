package Animals;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import itumulator.world.Location;
import itumulator.world.World;
import Util.Grass;
import Util.Burrow;

class TestRabbit {
    private World world;
    private Location center;
    private Rabbit rabbit;

    @BeforeEach
    void setUp() {
        world = new World(10);
        center = new Location(5, 5);
        world.setCurrentLocation(center);
        rabbit = new Rabbit();
        world.setTile(center, rabbit);
    }

    @Test
    void rabbitEatsGrass() {
        Grass grass = new Grass();
        world.setTile(center, grass);
        rabbit.act(world);

        assertFalse(world.contains(grass), "Kaninen har ikke spist græsset");
    }

    @Test
    void rabbitDies() {
        for (int i = 0; i < 100 && world.contains(rabbit); i++) {
            rabbit.act(world);
        }

        assertFalse(world.contains(rabbit), "Kaninen er ikke død");
    }

    @Test
    void rabbitMoves() {
        boolean moved = false;
        for (int i = 0; i < 90 && world.contains(rabbit); i++) {
            rabbit.act(world);

            if (!world.getLocation(rabbit).equals(center)) {
                moved = true;
                break;
            }
        }

        assertTrue(moved, "Kaninen bevæger sig ikke");
    }

    @Test
    void rabbitDigsBurrow() {
        boolean dug = false;

        for (int i = 0; i < 90 && world.contains(rabbit); i++) {
            rabbit.act(world);

            if (countBurrows() > 0) {
                dug = true;
                break;
            }
        }

        assertTrue(dug, "Kaninen har ikke gravet et hul efter 100 skridt");
    }

    private int countBurrows() {
        int count = 0;
        int size = world.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Location l = new Location(x, y);
                if (world.containsNonBlocking(l) && world.getNonBlocking(l) instanceof Burrow) {
                    count++;
                }
            }
        }

        return count;
    }

    @Test
    void rabbitSharesNearbyBurrow() {
        Burrow burrow = new Burrow();
        world.setTile(new Location(6, 5), burrow); // hul på nabofeltet

        rabbit.setBurrow(world);

        assertSame(burrow, rabbit.getBurrow(), "Kaninen burde dele hullet ved siden af");
        assertEquals(1, countBurrows(), "Kaninen burde ikke have gravet et nyt hul");
    }

    @Test
    void rabbitGoesIntoBurrowAtNight() {
        rabbit.setBurrow(world);

        world.setNight();
        rabbit.act(world);

        assertTrue(rabbit.isRemoved(), "Kaninen er ikke gået ned i sit hul");
    }

    @Test
    void rabbitWakesUpWhenDay() {
        for (int i = 0; i < 90 && countBurrows() == 0; i++) {
            rabbit.act(world);
        }

        world.setNight();
        rabbit.act(world);

        assertTrue(rabbit.isRemoved(), "Kaninen er ikke gået ned i sit hul, så testen kan ikke foretages");

        world.setDay();
        rabbit.act(world);

        assertFalse(rabbit.isRemoved(), "Kaninen er ikke vågnet om dagen");
    }

    @Test
    void rabbitCanReproduceWithAdultNeighbour() {
        Rabbit rabbit2 = new Rabbit();
        world.setTile(new Location(0, 0), rabbit2);
        for (int i = 0; i < 20; i++) {
            rabbit2.act(world);
        }

        world.remove(rabbit2);
        world.setTile(new Location(6, 5), rabbit2);

        assertTrue(rabbit.canReproduce(world), "Kaninen burde kunne formere sig med en voksen nabo");
    }

    //Unhappy test
    @Test
    void rabbitCannotReproduceWithBabyNeighbour() {
        Rabbit rabbit2 = new Rabbit();
        world.setTile(new Location(6, 5), rabbit2);

        assertFalse(rabbit.canReproduce(world), "Kaninen burde ikke kunne formere sig med en baby nabo");
    }

    @Test
    void rabbitGivesBirth() {
        rabbit.giveBirth(world);

        assertEquals(2, countRabbits(), "Kaninen bø have født en unge, så der er 2 kaniner");
    }

    private int countRabbits() {
        int count = 0;
        int size = world.getSize();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Location l = new Location(x, y);
                if (world.getTile(l) instanceof Rabbit) {
                    count++;
                }
            }
        }

        return count;
    }
}