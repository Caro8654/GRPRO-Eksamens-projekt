package Util;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class Rabbit implements Actor, DynamicDisplayInformationProvider {
    private int age;
    private int energy;
    private int food;
    private int maxAge = 100;
    private Random random = new Random();
    private Burrow burrow;
    private boolean sleeping = false;

    public Rabbit() {
        this.age = 0;
        this.energy = 100;
        this.food = 100;
        this.burrow = null;
    }

    @Override
    public DisplayInformation getInformation() {
        if (sleeping && age >= 20) {
            return new DisplayInformation(Color.GRAY, "rabbit-sleeping");
        } else if (sleeping) {
            return new DisplayInformation(Color.GRAY, "rabbit-small-sleeping");
        } else if (age < 20) {
            return new DisplayInformation(Color.GRAY, "rabbit-small");
        } else {
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        }
    }

    // act() bliver kaldt ved hvert skridt. Kaninen gør kun én ting pr. skridt.
    @Override
    public void act(World world) {
        age();
        food();
        energy();
        sleeping = isSleeping(world);

        if (die(world)) {
            return;                 // kaninen er død, stop her
        }

        if (sleeping) {
            moveToBurrow(world);    // nat: søg mod hullet
            return;
        }

        // dag: gør én ting, i prioriteret rækkefølge
        if (eatGrass(world)) {
            return;
        }
        if (!hasBurrow() && random.nextInt(10) == 0) {
            setBurrow(world);
            return;
        }
        if (canReproduce(world) && random.nextInt(10) == 0) {
            giveBirth(world);
            return;
        }
        move(world);
    }

    private void age() {
        age++;
    }

    private void food() {
        food--;
    }

    private void energy() {
        energy--;
    }

    private boolean isSleeping(World world) {
        return world.isNight();
    }

    // Kaninen dør af sult eller alderdom. Returnerer true, hvis den døde.
    private boolean die(World world) {
        if (food <= 0 || age >= maxAge) {
            world.delete(this);
            return true;
        }
        return false;
    }

    public int getAge() {
        return age;
    }

    // Flytter kaninen til et tilfældigt ledigt felt ved siden af sig
    private void move(World world) {
        Location current = world.getLocation(this);
        Set<Location> free = world.getEmptySurroundingTiles(current);

        if (!free.isEmpty()) {
            ArrayList<Location> options = new ArrayList<>(free);
            Location next = options.get(random.nextInt(options.size()));
            world.move(this, next);
        }
    }

    // Giver kaninen mere mad
    public void eat() {
        food = food + 10;
    }

    // Spiser græs, hvis kaninen står på noget. Returnerer true, hvis den spiste.
    private boolean eatGrass(World world) {
        Location current = world.getLocation(this);

        if (world.containsNonBlocking(current)) {
            Object thing = world.getNonBlocking(current);
            if (thing instanceof Grass) {
                world.delete(thing);
                eat();
                return true;
            }
        }
        return false;
    }

    // Er der en voksen kanin ved siden af?
    private boolean canReproduce(World world) {
        Location current = world.getLocation(this);

        for (Location l : world.getSurroundingTiles(current)) {
            Object o = world.getTile(l);
            if (o instanceof Rabbit && ((Rabbit) o).getAge() >= 20) {
                return true;
            }
        }
        return false;
    }

    // Placerer en unge på et tilfældigt ledigt nabofelt
    private void giveBirth(World world) {
        Location current = world.getLocation(this);
        Set<Location> free = world.getEmptySurroundingTiles(current);

        if (!free.isEmpty()) {
            ArrayList<Location> options = new ArrayList<>(free);
            Location spot = options.get(random.nextInt(options.size()));
            world.setTile(spot, new Rabbit());
        }
    }

    private boolean hasBurrow() {
        return burrow != null;
    }

    // Del et hul i nærheden, ellers grav et nyt
    private void setBurrow(World world) {
        Location current = world.getLocation(this);

        for (Location l : world.getSurroundingTiles(current)) {
            if (world.containsNonBlocking(l) && world.getNonBlocking(l) instanceof Burrow) {
                burrow = (Burrow) world.getNonBlocking(l);
                return;
            }
        }

        if (world.containsNonBlocking(current) && world.getNonBlocking(current) instanceof Grass) {
            world.delete(world.getNonBlocking(current));
        }

        if (!world.containsNonBlocking(current)) {
            burrow = new Burrow();
            world.setTile(current, burrow);
        }
    }

    // Går ét skridt mod hullet, hvis feltet er ledigt
    private void moveToBurrow(World world) {
        if (!hasBurrow()) {
            return;
        }

        Location here = world.getLocation(this);
        Location target = world.getLocation(burrow);

        if (here.equals(target)) {
            return;
        }

        int dx = Integer.compare(target.getX(), here.getX());
        int dy = Integer.compare(target.getY(), here.getY());
        Location next = new Location(here.getX() + dx, here.getY() + dy);

        if (world.isTileEmpty(next)) {
            world.move(this, next);
        }

        if (here.equals(target)) {
            world.remove(this);
        }
    }
}