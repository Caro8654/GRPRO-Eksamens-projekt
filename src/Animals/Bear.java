package Animals;

import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.world.World;
import itumulator.world.Location;

public class Bear extends Animal{
    
    public Bear() {
        super(100, 20);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLUE, "bear");
    }

    @Override 
    public void act(World world) {
        // Tiden går: alder stiger med 1, mad og energi falder med 1
        super.ageEnergy();
        foodEnergy();

        updateSleeping(world);

        // Tjekker om bjørnen dør (sult eller alderdom)
        if (die(world)) {
            // bjørnen er død, stop her
            return;
        }

        move(world);
    }

    @Override
    protected boolean eat(World world) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eat'");
    }

    @Override
    protected Animal addBaby() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addBaby'");
    }

    

    
}
