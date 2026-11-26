package Util;

import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;
import itumulator.simulator.Actor;

public class Berry implements NonBlocking, DynamicDisplayInformationProvider, Actor{
    int ripe = 0;
    
    @Override
    public void act(World world) {
        if(ripe < 10) {
            ripe++;
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if(hasBerries()) {
            return new DisplayInformation(Color.RED, "bush-berries");
        }
        return new DisplayInformation(Color.GREEN, "bush");
    }

    public boolean hasBerries() {
        return ripe >= 10;
    }

    public void hasBeenEaten() {
        if(hasBerries()) {
            ripe = 0;
        }
    }
}
