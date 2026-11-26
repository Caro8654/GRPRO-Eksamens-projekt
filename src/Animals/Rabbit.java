package Animals;

import java.awt.Color;
import java.util.Random;

import Home.Burrow;
import Home.Homes;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import Util.Grass;

// kanin er 2 ting:
// - Actor: kaninen har en act-metode, som kaldes ved hvert simulationstrin 
// - DynamicDisplayInformationProvider: kaninen leverer selv sit udseende til det grafiske vindue
// - (Blocking: andre kan ikke stå på samme felt som kaninen, fordi den ikke implementerer NonBlocking)
public class Rabbit extends HomeAnimal {
    private Burrow burrow;
    private Random random = new Random();

    public Rabbit() {
        // maxAge = 100, voksen fra alder 20
        super(100, 20);
        this.burrow = null;
    }

    // Metode fra DynamicDisplayInformationProvider. Vælger billede efter kaninens
    // tilstand.
    // Farven (grå) bruges, hvis billedet ikke findes

    @Override
    public DisplayInformation getInformation() {
        // Sover og er voksen
        if (isAsleep() && isAdult()) {
            return new DisplayInformation(Color.GRAY, "rabbit-sleeping");

            // Sover og er ung
        } else if (isAsleep() && !isAdult()) {
            return new DisplayInformation(Color.GRAY, "rabbit-small-sleeping");

            // Vågen og ung
        } else if (!isAdult()) {
            return new DisplayInformation(Color.GRAY, "rabbit-small");

            // Vågen og voksen
        } else {
            return new DisplayInformation(Color.GRAY, "rabbit-large");
        }
    }

    // ------------------- act() bliver kaldt ved hvert skridt. Kaninen gør kun en ting pr. skridt. ------------------------------
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

        // Hvis kaninen er nede i hullet (fjernet fra kortet), skal den ikke udføre de
        // normale handlinger
        if (isRemoved()) {
            // Når det ikke længere er nat, prøver kaninen at komme op af hullet
            if (!isAsleep()) {
                wakeUp(world);
            }
            return;
        }

        // Om natten: står kaninen på sit hul, fjernes den fra kortet (den sover i
        // hullet). Ellers bevæger den sig mod hullet
        if (isAsleep()) {
            if (standsOnHome(world)) {
                removeAnimal(world);
            } else {
                moveToHome(world);
            }
            return;
        }

        // dag: gør én ting, i prioriteret rækkefølge
        // 1. Spis græs, hvis den kan
        if (eat(world)) {
            return;
        }
        // 2. Hvis den ikke har et hul, er der 1/10 chance for, at den graver eller
        // deler et hul
        if (!hasHome() && random.nextInt(10) == 0) {
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

    // ------------------------------- Kaninen spiser ------------------------------------------

    @Override
    // Spiser græs, hvis kaninen står på noget. Returnerer true, hvis den spiste.
    protected boolean eat(World world) {
        Location current = world.getLocation(this);

        // Tjekker om der ligger et NonBlocking-objekt på kaninens eget felt
        if (world.containsNonBlocking(current)) {
            Object thing = world.getNonBlocking(current);

            // Hvis det er græs: slet græsset, giv kaninen mad og returnér true
            if (thing instanceof Grass) {
                world.delete(thing);
                addFoodEnergy();
                return true;
            }
        }
        return false;
    }

    // ------------------------------- ALT DER HAR MED DYRETS HJEM AT GØRE ------------------------------------


    public Burrow getBurrow() {
        return burrow;
    }

    // Del et hul i nærheden, ellers grav et nyt
    public void setBurrow(World world) {
        Location current = world.getLocation(this);

        // Først kigger kaninen på nabofelterne. Er der et hul, bliver det kaninens hul
        // (deling af hul), og metoden stopper
        for (Location l : world.getSurroundingTiles(current)) {
            if (world.containsNonBlocking(l) && world.getNonBlocking(l) instanceof Burrow) {
                burrow = (Burrow) world.getNonBlocking(l);
                return;
            }
        }

        // Findes der intet hul i nærheden, og kaninen står på græs, slettes græsset, så
        // der bliver plads til et hul
        if (world.containsNonBlocking(current) && world.getNonBlocking(current) instanceof Grass) {
            world.delete(world.getNonBlocking(current));
        }

        // Hvis feltet nu ikke har et NonBlocking-objekt, graves et nyt hul på kaninens
        // felt, og det bliver kaninens hul
        if (!world.containsNonBlocking(current)) {
            burrow = new Burrow();
            world.setTile(current, burrow);
        }
    }
    
    @Override
    protected Homes getHome() {
        return burrow;
    }

    // ---------------------------- ALT MED DYRETS EKSISTENS I VERDENEN ----------------------------------

    @Override
    protected Animal addBaby() {
        return new Rabbit();
    }

    // Tager kaninen af kortet, når den står på sit hul om natten (den sover i
    // hullet)
    protected void removeAnimal(World world) {
        if (standsOnHome(world) && isAsleep() && !isRemoved()) {
            world.remove(this);
            setRemoved(true);
        }
    }      

    // Sætter kaninen tilbage på kortet ved hullet
    private void wakeUp(World world) {
        // Finder hullets position
        Location target = world.getLocation(burrow);

        // Er hullet ledigt, kommer kaninen op på hullet
        if (world.isTileEmpty(target)) {
            world.setTile(target, this);
            setRemoved(false);
            return;
        }

        // Står der en anden kanin på hullet, kommer den op på et tilfældigt ledigt felt
        // ved siden af
        // Ellers er alt omkring hullet optaget, og kaninen bliver nede og prøver igen
        // næste skridt

        Location spot = randomFreeTile(world, target);
        if (spot != null) {
            world.setTile(spot, this);
            setRemoved(false);
        }
    }

}