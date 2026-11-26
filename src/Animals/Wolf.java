package Animals;

import java.awt.Color;
import java.util.Random;

import itumulator.executable.DisplayInformation;
import itumulator.world.World;
import itumulator.world.Location;
import Home.Homes;
import Home.WolfHole;
import Util.Grass;

public class Wolf extends HomeAnimal {
    private WolfPack pack;
    private Random random = new Random();

    public Wolf(WolfPack pack) {
        super(100, 20);
        this.pack = pack;
        pack.addMember(this);
    }

    @Override
    public DisplayInformation getInformation() {
        // Sover og er voksen
        if (isAsleep() && isAdult()) {
            return new DisplayInformation(Color.GRAY, "wolf-sleeping");

            // Sover og er ung
        } else if (isAsleep() && !isAdult()) {
            return new DisplayInformation(Color.GRAY, "wolf-small-sleeping");

            // Vågen og ung
        } else if (!isAdult()) {
            return new DisplayInformation(Color.GRAY, "wolf-small");

            // Vågen og voksen
        } else {
            return new DisplayInformation(Color.GRAY, "wolf");
        }
    }

    @Override
    public void act(World world) {
        // Tiden går: alder stiger med 1, mad og energi falder med 1
        ageEnergy();
        foodEnergy();

        updateSleeping(world);

        // Tjekker om kaninen dør (sult eller alderdom)
        if (die(world)) {
            // kaninen er død, stop her
            return;
        }

        if (eat(world)) {
            return;
        }

        if (!hasHome() && random.nextInt(10) == 0) {
            buildWolfHole(world);
            return;
        }

        if (!isAsleep()) {
            Location friend = nearestPackMember(world);

            if (friend != null && distance(world.getLocation(this), friend) > 2) {
                moveTowards(world, friend);
            } else {
                move(world);
            }
        }
    }

    @Override
    protected boolean eat(World world) {
        Location current = world.getLocation(this);

        // Går alle felter rundt om ulven igennem
        for (Location l : world.getSurroundingTiles(current)) {
            // getTile giver det, der står på feltet (null hvis feltet er tomt)
            Object thing = world.getTile(l);

            if (thing instanceof Rabbit) {
                world.delete(thing);
                addFoodEnergy();
                return true;
            }
        }
        return false;
    }

    @Override
    protected Animal addBaby() {
        return new Wolf(pack);
    }

    // Finder nærmeste ulv i flokken (som er på kortet), eller null
    private Location nearestPackMember(World world) {
        Location here = world.getLocation(this);
        Location nearest = null;
        for (Wolf other : pack.getMembers()) {
            if (other != this && world.isOnTile(other)) {
                Location l = world.getLocation(other);
                if (nearest == null || distance(here, l) < distance(here, nearest)) {
                    nearest = l;
                }
            }
        }
        return nearest;
    }

    // --------------------------- Ulvens hjem ----------------------

    private void buildWolfHole(World world) {
        Location current = world.getLocation(this);

        if (world.containsNonBlocking(current) && world.getNonBlocking(current) instanceof Grass) {
            world.delete(world.getNonBlocking(current));
        }

        // Hvis feltet nu ikke har et NonBlocking-objekt, graves et nyt hul på kaninens
        // felt, og det bliver kaninens hul
        if (!world.containsNonBlocking(current)) {
            WolfHole wHole = new WolfHole();
            world.setTile(current, wHole);
            pack.setWolfHole(wHole);
        }
    }

    @Override
    protected Homes getHome() {
        return pack.getWolfHole();
    }
}
