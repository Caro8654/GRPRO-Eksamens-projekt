package Animals;

import java.awt.Color;


import itumulator.executable.DisplayInformation;
import itumulator.world.World;
import itumulator.world.Location;

public class Wolf extends Animal{
    public Wolf() {
        super(100, 20);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.GRAY, "wolf");
    }

    @Override 
    public void act(World world) {
        // Tiden går: alder stiger med 1, mad og energi falder med 1
        super.ageEnergy();
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
        }
    }
    return false;
}



    @Override 
    protected Animal addBaby() {
        return new Wolf();
    }
}
