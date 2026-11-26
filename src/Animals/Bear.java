package Animals;

import java.awt.Color;

import java.util.Random;
import itumulator.executable.DisplayInformation;
import itumulator.world.World;
import itumulator.world.Location;
import Util.Grass;
import Util.Berry;

public class Bear extends Animal{
    
    private Location territoryCenter;
    private Random random = new Random();


    public Bear() {
        super(100, 20);
    }

    @Override
    public DisplayInformation getInformation() {
    
        if (isAsleep() && isAdult()) {
            return new DisplayInformation(Color.GRAY, "bear-sleeping");

            // Sover og er ung
        } else if (isAsleep() && !isAdult()) {
            return new DisplayInformation(Color.GRAY, "bear-small-sleeping");

            // Vågen og ung
        } else if (!isAdult()) {
            return new DisplayInformation(Color.GRAY, "bear-small");

            // Vågen og voksen
        } else {
            return new DisplayInformation(Color.GRAY, "bear");
        }
    }

    @Override 
    public void act(World world) {
        // Tiden går: alder stiger med 1, mad og energi falder med 1
        ageEnergy();
        foodEnergy();

        updateSleeping(world);

        // Tjekker om bjørnen dør (sult eller alderdom)
        if (die(world)) {
            // bjørnen er død, stop her
            return;
        }


        if (territoryCenter == null) {
        territoryCenter = world.getLocation(this);
        }

    if (world.isNight()){
            moveHome(world);
            return;
        }

        if (eat(world)) {
            return;
        }
        
        if (canReproduce(world) && random.nextInt(10) == 0) {
            giveBirth(world);
            return;
        }

        move(world);
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
        } else if (thing instanceof Wolf) {
            world.delete(thing);
            addFoodEnergy();
            return true;
        } else if (thing instanceof Grass) {
            world.delete(thing);
            addFoodEnergy();
            return true;
        } else if (thing instanceof Berry && ((Berry) thing).hasBerries()) {
            ((Berry) thing).eatBerrie();
            addFoodEnergy();
            return true;
        } 
    }
    return false;
}

private boolean hasHome (World world){
        // Har bjørnen intet territorium, gør den ikke noget
        if (territoryCenter == null) {
            return false;
        }
        return true;
    }


private void moveHome (World world) {
        // Har bjørnen intet territorium, gør den ikke noget
        if (!hasHome(world)) {
            return;
        }

        // Finder bjørnens
        Location here = world.getLocation(this);

        // Allerede fremme
        if (here.equals(territoryCenter)) {
            return;
        }

        // Hullet er ledigt: hop derhen
        if (world.isTileEmpty(territoryCenter)) {
            world.move(this, territoryCenter);
        }
        // Ellers står der en anden kanin på hullet, og så venter den til næste skridt
    }



    @Override
    protected Animal addBaby() {
        return new Bear();
    }

    

    
}
