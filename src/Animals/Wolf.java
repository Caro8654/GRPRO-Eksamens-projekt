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

    }

    @Override
    protected boolean eat(World world){
        return false;
    }

    @Override 
    protected Animal addBaby() {
        return new Wolf();
    }
}
