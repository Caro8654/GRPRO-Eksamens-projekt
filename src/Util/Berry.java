package Util;

import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;
import itumulator.simulator.Actor;

public class Berry implements NonBlocking, DynamicDisplayInformationProvider, Actor{
    int ripe = 0;
    int maxRipe = 10;

    @Override
    public void act(World world) {
        if(ripe < maxRipe) {
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
        return ripe >= maxRipe;
    }

    public void eatBerrie() {
        ripe = 0;
    }

    
}
