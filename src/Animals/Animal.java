package Animals;

import java.util.ArrayList;
import java.util.Set;
import java.util.Random;

import itumulator.world.World;
import itumulator.world.Location;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;

public abstract class Animal implements Actor, DynamicDisplayInformationProvider {
    protected int age;
    protected int energy;
    protected int food;
    protected int maxAge;
    protected Random random = new Random();
    protected boolean removed;
    protected int adultAge;
    protected boolean sleeping;

    protected Animal(int maxAge, int adultAge) {
        this.age = 0;
        this.energy = 100;
        this.food = 100;
        this.maxAge = maxAge;
        this.adultAge = adultAge;
        this.sleeping = false;
        this.removed = false;
    }

    // Alderen stiger med 1
    public void age() {
        age++;
    }

    // Gør alderen tilgængelig for andre dyr
    public int getAge() {
        return age;
    }

    // Maden falder med 1
    public void food() {
        food--;
    }

    // Energien falder med 1
    public void energy() {
        energy--;
    }

    // Dyret sover, når det er nat i verdenen
    public boolean isSleeping(World world) {
        return world.isNight();
    }

    public boolean isAdult() {
        return age >= adultAge;
    }

    // Tjekker om dyrene kan spise, og gør så dyrene spiser maden
    protected abstract boolean eat(World world);

    // Giver dyrene mere mad
    public void addFood() {
        food = food + 10;
    }

    // ---------------------------------- ALT OMHANDLENDE LOKATION, BEVÆGELSE OG EKSISTENS ---------------------------------------

    // Flytter dyret til et tilfældigt ledigt felt ved siden af sig
    public void move(World world) {
        Location next = randomFreeTile(world, world.getLocation(this));
        if (next != null) {
            world.move(this, next);
        }
    }

    // Returnerer et tilfældigt ledigt felt ved siden af location, eller null hvis der ikke er noget
    protected Location randomFreeTile(World world, Location location) {
        Set<Location> free = world.getEmptySurroundingTiles(location);
        if (free.isEmpty()) {
            return null;
        }
        ArrayList<Location> options = new ArrayList<>(free);
        return options.get(random.nextInt(options.size()));
    }

    // Dyret dør af sult eller alderdom. Returnerer true, hvis den døde.
    public boolean die(World world) {
        // Maden er brugt op, eller alderen har nået maksimum
        if (food <= 0 || age >= maxAge) {
            // delete fjerner dyret helt fra verdenen
            world.delete(this);
            return true;
        }
        return false;
    }

    // Fortæller, om dyret er nede i hullet og dermed midlertidigt forsvundet fra hvad vi kan se
    public boolean isRemoved() {
        return removed;
    }

    // ---------------------- ALT MED REPRODUCERE OG NYE UNGER ------------------------------------------

    public boolean canReproduce(World world) {
        Location current = world.getLocation(this);

        for (Location l : world.getSurroundingTiles(current)) {
            Object o = world.getTile(l);

            // Samme klasse som dyret selv, og voksen
            if (o != null && o.getClass() == this.getClass() && ((Animal) o).isAdult()) {
                return true;
            }
        }
        return false;
    }

    // Placerer en unge på et tilfældigt ledigt nabofelt.
    // Er der ingen ledige felter, bliver der ikke født en unge dette trin
    public void giveBirth(World world) {
        Location spot = randomFreeTile(world, world.getLocation(this));
        if (spot != null) {
            world.setTile(spot, addBaby());
        }
    }

    protected abstract Animal addBaby();

}