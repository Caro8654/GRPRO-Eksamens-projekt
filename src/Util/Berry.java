package Util;

import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;
import itumulator.simulator.Actor;

public class Berry implements NonBlocking, DynamicDisplayInformationProvider{
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.RED, "bush");

    }
    int ripe = 0;

    protected boolean hasBerries() {
        if(10 <= ripe) {
            return true;
        }
        return false;
    }


    @Override
    public void act(World world) {
        
    }
}
