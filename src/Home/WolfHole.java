package Home;

import itumulator.executable.DisplayInformation;

import java.awt.Color;

public class WolfHole extends Homes {
    
    @Override
    public DisplayInformation getInformation() {
        // Returnerer en sort farve og navnet "hole". Navnet bruges til at finde et billede af et hul,
        // og hvis billedet ikke kan findes, tegnes feltet i stedet med den sorte farve
        return new DisplayInformation(Color.BLACK, "hole");
    }
}
