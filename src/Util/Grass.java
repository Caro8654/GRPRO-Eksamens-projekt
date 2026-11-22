package Util;

import itumulator.world.NonBlocking;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.DisplayInformation;
import java.awt.Color;
import java.util.*;


// Græs er tre ting:
// - NonBlocking: andre aktører kan stå på samme felt som græsset (krav K1-1c)
// - Actor: græsset har en act-metode, som kaldes ved hvert simulationstrin (så det kan sprede sig)
// - DynamicDisplayInformationProvider: græsset leverer selv sit udseende til det grafiske vindue
public class Grass implements NonBlocking, Actor, DynamicDisplayInformationProvider{
    
    // Metode fra DynamicDisplayInformationProvider. @Override markerer, at den overskriver en metode fra interfacet
    @Override
    public DisplayInformation getInformation() {
        // Returnerer en grøn farve og navnet "grass". Navnet bruges til at finde et billede af græs,
        // og hvis billedet ikke kan findes, tegnes feltet i stedet med den grønne farve
        return new DisplayInformation(Color.GREEN, "grass");
    }

    // Metode fra Actor. Kaldes automatisk for hvert græsobjekt ved hvert simulate()
    @Override
    public void act(World world) {
        Random r = new Random();

        // r.nextInt(30) giver et tal fra 0 til 29. Kun når tallet er 0 (1 ud af 30 gange) fortsætter koden. 
        // Ellers afsluttes metoden med return, og græsset gør ingenting
        if(r.nextInt(30) != 0) {
            return;
        }

        // Finder græssets egen position i verdenen. this er det aktuelle græsobjekt
        Location here = world.getLocation(this);
        // Tom liste til de nabofelter.
        List<Location> free = new ArrayList<>();

        // Går igennem alle nabofelter rundt om græsset
        for (Location l : world.getSurroundingTiles(here)) {
            // Hvis feltet ikke allerede indeholder et NonBlocking-objekt (fx andet græs eller et hul), er det ledigt og tilføjes til listen
            if (!world.containsNonBlocking(l)) {
                free.add(l);
            }
        }

        // Kun hvis der er mindst ét ledigt nabofelt, går koden videre
        if (!free.isEmpty()) {
            // Vælger tilfældigt ét af de ledige felter fra listen
            Location l = free.get(r.nextInt(free.size()));
            // Placerer nyt græs på det valgte felt.
            world.setTile(l, new Grass());
        }
    }
}