package Util;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;


public class Rabbit implements Actor, DynamicDisplayInformationProvider{
    private int age;
    private int energy;
    private int food;
    private int maxAge = 100;
    private Random random = new Random();
    private Burrow burrow;
    private boolean sleeping = false;

    public Rabbit(){
        this.age = 0;
        this.energy = 100;
        this.food = 100;
        this.burrow = null;
    }

    @Override
    public DisplayInformation getInformation() {
        if (sleeping && age>= 20) {
            return new DisplayInformation(Color.GRAY, "rabbit-sleeping");
        } else if (sleeping) {
            return new DisplayInformation(Color.GRAY, "rabbit-small-sleeping");
        } else if (age >= 0 && age < 20) {
            return new DisplayInformation(Color.GRAY, "rabbit-small");
        } else {
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        }
    }

    // act() bliver kaldt af simulatoren ved hvert skridt. Her kaldes alle ting kaninen gør.
    @Override
    public void act(World world) {
        age++;      // kaninen bliver ældre
        food--;     // mister 1 food pr. skridt
        energy--;   // den mister også lidt energi, når den bliver ældre
        sleeping = world.isNight();

            // Kaninen dør, hvis den er sulten (food = 0) eller for gammel
        if (food <= 0 || age >= maxAge) {
            world.delete(this); // fjerner kaninen fra verdenen
            return; // stop her, ellers fortsætter koden for en død kanin
        }

        // Om dagen: spis græs, hvis der er noget, ellers gå rundt
        if (world.isDay()) {
            if (eatGrass(world)) { // spiste, færdig for denne gang
            } else if (hasBurrow(world) == false && random.nextInt(10) == 0) {
                setBurrow(world);
            } else if (canReproduce(world) && random.nextInt(10) == 0) {
                giveBirth(world);
            } else {
                move(world);
            } 
        }

        if (world.isNight()) {
            moveToBurrow(world);
        }
    }

    private int getAge(){
        return age;
    }

    // Giver kaninen mere mad. Bliver kaldt, når den har spist græs.
    public void eat() {
        food = food + 10;
    }

    // Flytter kaninen til et tilfældigt ledigt felt ved siden af sig
    private void move(World world) {
        Location current = world.getLocation(this);                  // hvor står kaninen nu?
        Set<Location> free = world.getEmptySurroundingTiles(current); // ledige felter ved siden af kaninen

        // Kun hvis der er mindst et ledigt felt (ellers står kaninen bare stille)
        if (!free.isEmpty()) {
            ArrayList<Location> options = new ArrayList<>(free);      // lav sættet om til en liste, så vi kan vælge ved et tal
            Location next = options.get(random.nextInt(options.size())); // vælg et tilfældigt felt fra listen
            world.move(this, next);                                   // flyt kaninen derhen
        }
    }

        // Spiser græs, hvis kaninen står oven på noget.
        // Returnerer true, hvis den spiste, og false, hvis der ikke var græs.
    private boolean eatGrass(World world) {
        Location current = world.getLocation(this);   // kaninens felt

        // Ligger der noget ikke-blokerende (fx græs) på feltet?
        if (world.containsNonBlocking(current)) {
            Object thing = world.getNonBlocking(current); // hent det, der ligger der
        
            if (thing instanceof Grass) {                 // er det faktisk græs?
            world.delete(thing);   // græsset bliver spist og forsvinder
            eat();                 // kaninens food stiger
            return true;           // ja, kaninen spiste
            }
        }
    
        return false;   // der var intet græs at spise
    }

    private boolean canReproduce(World world) {
        Location current = world.getLocation(this);
        Set<Location> neighbours = world.getSurroundingTiles(current);

        for (Location l : neighbours) {
            Object o = world.getTile(l);
            if (o instanceof Rabbit && ((Rabbit) o).getAge() >= 20) {
                return true;
            }
        }

        return false;
    }

    private void giveBirth(World world) {
        Location current = world.getLocation(this);
        Set<Location> free = world.getEmptySurroundingTiles(current);

        if (!free.isEmpty()) {
            ArrayList<Location> options = new ArrayList<>(free);
            Location spot = options.get(random.nextInt(options.size()));
            world.setTile(spot, new Rabbit());
        }
    }

    private boolean hasBurrow(World world) {
        if (this.burrow == null) {
            return false;
        } else {
            return true;
        }
    }

    private void setBurrow(World world) {
        Location current = world.getLocation(this);

        // 1. Er der et hul ved siden af? Så del det.
        for (Location l : world.getSurroundingTiles(current)) {
            if (world.containsNonBlocking(l) && world.getNonBlocking(l) instanceof Burrow) {
                burrow = (Burrow) world.getNonBlocking(l);
                return;
            }
        }

        // 2. Ellers: grav et nyt hul, hvor kaninen står
        if (world.containsNonBlocking(current) && world.getNonBlocking(current) instanceof Grass) {
            world.delete(world.getNonBlocking(current));   // fjern græs, så der er plads
        }

        if (!world.containsNonBlocking(current)) {
            burrow = new Burrow();
            world.setTile(current, burrow);
        }
    }

    private void moveToBurrow(World world) {
        if (hasBurrow(world)) {
            Location target = world.getLocation(this.burrow);
            world.move(this, target);
        }
    }
}