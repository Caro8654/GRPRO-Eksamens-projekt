package Util;

import itumulator.world.NonBlocking;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;

public class Burrow implements NonBlocking, DynamicDisplayInformationProvider{
    
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.BLACK, "hole");
    }
    
    public void burrowLocation() {

    }
}
