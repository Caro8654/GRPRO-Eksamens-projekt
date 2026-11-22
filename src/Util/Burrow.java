package Util;

import itumulator.world.NonBlocking;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;

// Et kaninhul er to ting:
// - NonBlocking: dyr kan stå på hullet uden at der sker noget 
// - DynamicDisplayInformationProvider: hullet leverer selv sit udseende til det grafiske vindue
public class Burrow implements NonBlocking, DynamicDisplayInformationProvider{
    
    // Metode fra DynamicDisplayInformationProvider. @Override markerer, at den overskriver en metode fra interfacet
    @Override
    public DisplayInformation getInformation() {
        // Returnerer en sort farve og navnet "hole". Navnet bruges til at finde et billede af et hul,
        // og hvis billedet ikke kan findes, tegnes feltet i stedet med den sorte farve
        return new DisplayInformation(Color.BLACK, "hole");
    }
    
    
}
