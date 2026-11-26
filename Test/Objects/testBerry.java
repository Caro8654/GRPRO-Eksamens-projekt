package Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import itumulator.world.Location;
import itumulator.world.World;
import Util.Berry;

class TestBerry {

private World world;
private Location center;
private Berry berry;

@BeforeEach
void setUp() {
    world = new World(10);
    center = new Location(5, 5);
    world.setCurrentLocation(center);
    berry = new Berry();
    world.setTile(center, berry);
}
@Test
void berryRipeTest() {
    assertFalse(berry.hasBerries());
    for (int i = 0; i < 10; i++) {
        berry.act(world);
    }

    assertTrue(berry.hasBerries());
}

@Test
void berryHasBeenEaten() {
    for (int i = 0; i < 10; i++) {
        berry.act(world);
    }

    assertTrue(berry.hasBerries());

    berry.eatBerrie();

    assertFalse(berry.hasBerries());
}

@Test
void berryRegrows() {
    for (int i = 0; i < 10; i++) {
        berry.act(world);
    }

    assertTrue(berry.hasBerries());

    berry.eatBerrie();

    assertFalse(berry.hasBerries());

    for (int i = 0; i < 10; i++) {
        berry.act(world);
    }
    assertTrue(berry.hasBerries());
}
}
