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


// kanin er 2 ting:
// - Actor: kaninen har en act-metode, som kaldes ved hvert simulationstrin 
// - DynamicDisplayInformationProvider: kaninen leverer selv sit udseende til det grafiske vindue
// - (Blocking: andre kan ikke stå på samme felt som kaninen, fordi den ikke implementerer NonBlocking)
public class Rabbit implements Actor, DynamicDisplayInformationProvider {
    private int age;
    private int energy;
    private int food;
    private int maxAge = 100;
    private Random random = new Random();
    private Burrow burrow;
    private boolean sleeping = false;
    private boolean removed = false;

    public Rabbit() {
        this.age = 0;
        this.energy = 100;
        this.food = 100;
        this.burrow = null;
    }

    // Metode fra DynamicDisplayInformationProvider. Vælger billede efter kaninens tilstand.
    // Alder 20 er grænsen mellem unge og voksne. Farven (grå) bruges, hvis billedet ikke findes
    @Override
    public DisplayInformation getInformation() {
        // Sover og er voksen
        if (sleeping && age >= 20) {
            return new DisplayInformation(Color.GRAY, "rabbit-sleeping");

        // Sover og er ung
        } else if (sleeping) {
            return new DisplayInformation(Color.GRAY, "rabbit-small-sleeping");

        // Vågen og ung
        } else if (age < 20) {
            return new DisplayInformation(Color.GRAY, "rabbit-small");

        // Vågen og voksen
        } else {
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        }
    }

    // act() bliver kaldt ved hvert skridt. Kaninen gør kun en ting pr. skridt.
    @Override
    public void act(World world) {
        // Tiden går: alder stiger med 1, mad og energi falder med 1
        age();
        food();
        energy();

        // bruger hjælpefunktion til at tjekke om det er nat eller dag
        sleeping = isSleeping(world);

        // Tjekker om kaninen dør (sult eller alderdom)
        if (die(world)) {
            // kaninen er død, stop her
            return;                 
        }

            
    // Hvis kaninen er nede i hullet (fjernet fra kortet), skal den ikke udføre de normale handlinger
    if (isRemoved()) {
        // Når det ikke længere er nat, prøver kaninen at komme op af hullet
       if (!sleeping) {
        wakeUp(world);      
    }
        return;
    }

    // Om natten: står kaninen på sit hul, fjernes den fra kortet (den sover i hullet). Ellers bevæger den sig mod hullet 
        if (sleeping) {
            if (standsOnBurrow(world)) {
                removeRabbit(world);
            } else {
                moveToBurrow(world);
            }
            return;
        } 

        // dag: gør én ting, i prioriteret rækkefølge
        // 1. Spis græs, hvis den kan
        if (eatGrass(world)) {
            return;
        }
        // 2. Hvis den ikke har et hul, er der 1/10 chance for, at den graver eller deler et hul
        if (!hasBurrow() && random.nextInt(10) == 0) {
            setBurrow(world);
            return;
        }
        // 3. Hvis den kan reproducere, er der 1/10 chance for, at den får en unge
        if (canReproduce(world) && random.nextInt(10) == 0) {
            giveBirth(world);
            return;
        }
        // 4. Ellers flytter den sig
        move(world);
    }

    // Alderen stiger med 1
    private void age() {
        age++;
    }

    // Maden falder med 1
    private void food() {
        food--;
    }

    // Energien falder med 1
    private void energy() {
        energy--;
    }

    // Kaninen sover, når det er nat i verdenen
    public boolean isSleeping(World world) {
        return world.isNight();
    }

    // Kaninen dør af sult eller alderdom. Returnerer true, hvis den døde.
    private boolean die(World world) {
        // Maden er brugt op, eller alderen har nået maksimum
        if (food <= 0 || age >= maxAge) {
            // delete fjerner kaninen helt fra verdenen
            world.delete(this);
            return true;
        }
        return false;
    }

    // Gør alderen tilgængelig for andre kaniner
    public int getAge() {
        return age;
    }

    // Flytter kaninen til et tilfældigt ledigt felt ved siden af sig
    private void move(World world) {
        // Finder kaninens position og de nabofelter, der er tomme
        Location current = world.getLocation(this);
        Set<Location> free = world.getEmptySurroundingTiles(current);

        // free indeholder de nabofelter, hvor der er plads til kaninen.
        // "!free.isEmpty()" betyder "hvis der er mindst ét ledigt nabofelt".
        // Er der ingen (kaninen er omringet), springes hele blokken over, og kaninen bliver stående
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

        // Tjekker om der ligger et NonBlocking-objekt på kaninens eget felt
        if (world.containsNonBlocking(current)) {
            Object thing = world.getNonBlocking(current);

            // Hvis det er græs: slet græsset, giv kaninen mad og returnér true
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

        // Går igennem alle nabofelter og kigger på det, der står på dem
        for (Location l : world.getSurroundingTiles(current)) {
            Object o = world.getTile(l);

            // Er der en kanin med alder 20 eller mere, kan kaninen reproducere
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

        // free indeholder de nabofelter, hvor der er plads til en unge.
        // "!free.isEmpty()" betyder "hvis der er mindst ét ledigt nabofelt".
        // Er der ingen, springes hele blokken over, og der bliver ikke født en unge dette trin
        if (!free.isEmpty()) {
            ArrayList<Location> options = new ArrayList<>(free);
            Location spot = options.get(random.nextInt(options.size()));
            world.setTile(spot, new Rabbit());
        }
    }

    // Ser om kaninen har et hul
    private boolean hasBurrow() {
        return burrow != null;
    }

    // Del et hul i nærheden, ellers grav et nyt
    private void setBurrow(World world) {
        Location current = world.getLocation(this);

        // Først kigger kaninen på nabofelterne. Er der et hul, bliver det kaninens hul (deling af hul), og metoden stopper
        for (Location l : world.getSurroundingTiles(current)) {
            if (world.containsNonBlocking(l) && world.getNonBlocking(l) instanceof Burrow) {
                burrow = (Burrow) world.getNonBlocking(l);
                return;
            }
        }

        // Findes der intet hul i nærheden, og kaninen står på græs, slettes græsset, så der bliver plads til et hul
        if (world.containsNonBlocking(current) && world.getNonBlocking(current) instanceof Grass) {
            world.delete(world.getNonBlocking(current));
        }

        // Hvis feltet nu ikke har et NonBlocking-objekt, graves et nyt hul på kaninens felt, og det bliver kaninens hul
        if (!world.containsNonBlocking(current)) {
            burrow = new Burrow();
            world.setTile(current, burrow);
        }
    }

   // Hopper direkte hen til hullet, hvis det er ledigt
    private void moveToBurrow(World world) {
    // Har kaninen intet hul, gør den ikke noget
    if (!hasBurrow()) {
        return;
    }

    // Finder kaninens position og hullets position
    Location here = world.getLocation(this);
    Location target = world.getLocation(burrow);

    // Allerede fremme
    if (here.equals(target)) {
        return;
    }

    // Hullet er ledigt: hop derhen
    if (world.isTileEmpty(target)) {
        world.move(this, target);
    }
    // Ellers står der en anden kanin på hullet, og så venter den til næste skridt
}

// Tager kaninen af kortet, når den står på sit hul om natten (den sover i hullet)  
    private void removeRabbit(World world) {
        if (!hasBurrow()) {
        // ingen hul → gør ingenting
        return;                 
    }
        
        Location here = world.getLocation(this);
        Location target = world.getLocation(burrow);

        // Står kaninen på hullet, sover og ikke allerede er fjernet, tages den af kortet med remove, og removed sættes til true
        if (here.equals(target) && sleeping && !isRemoved()) {
            world.remove(this);
            removed = true;
        }
    }

    // Fortæller, om kaninen er nede i hullet
    private boolean isRemoved() {
        return removed;
    }

    // Sætter kaninen tilbage på kortet ved hullet
    private void wakeUp(World world) {
        // Finder hullets position
        Location target = world.getLocation(burrow);

    // Er hullet ledigt, kommer kaninen op på hullet
        if (world.isTileEmpty(target)) {
            world.setTile(target, this);
            removed = false;
            return;
        }

    // Står der en anden kanin på hullet, kommer den op på et tilfældigt ledigt felt ved siden af
        Set<Location> freeTiles = world.getEmptySurroundingTiles(target);
            if (!freeTiles.isEmpty()) {
                ArrayList<Location> options = new ArrayList<>(freeTiles);
                Location spot = options.get(random.nextInt(options.size()));
                world.setTile(spot, this);
                removed = false;
            }
        // Ellers er alt omkring hullet optaget, og kaninen bliver nede og prøver igen næste skridt
    }

    // Returnerer true, hvis kaninen har et hul, og dens position er den samme som hullets
    private boolean standsOnBurrow(World world) {
        if (!hasBurrow()) {
            return false;
        } else {
            return world.getLocation(this).equals(world.getLocation(burrow));
        }
    }

}