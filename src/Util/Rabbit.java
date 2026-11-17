package Util;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class Rabbit implements Actor{
    private int age;
    private int energy;
    private int food;
    private int maxAge = 100;
    private Random random = new Random();

    public Rabbit(){
        this.age = 0;
        this.energy = 100;
        this.food = 100;
        
    }

    // act() bliver kaldt af simulatoren ved hvert skridt. Her kaldes alle ting kaninen gør.
    @Override
    public void act(World world) {
    age++;      // kaninen bliver ældre
    food--;     // mister 1 food pr. skridt
    energy--;   // den mister også lidt energi, når den bliver ældre

    // Kaninen dør, hvis den er sulten (food = 0) eller for gammel
    if (food <= 0 || age >= maxAge) {
        world.delete(this); // fjerner kaninen fra verdenen
        return; // stop her, ellers fortsætter koden for en død kanin
    }

    // Om dagen: spis græs, hvis der er noget, ellers gå rundt
    if (world.isDay()) {
        if (!eatGrass(world)) {   // eatGrass returnerer true, hvis kaninen kunne spise noget
    move(world);          // ellers (false) flytter kaninen sig
}
        }
    }

    // Giver kaninen mere mad. Bliver kaldt, når den har spist græs.
    public void eat() {
        food++;
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

    // Spiser græs, hvis kanine står oven på noget.
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

}