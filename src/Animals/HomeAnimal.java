package Animals;

import Home.Homes;
import itumulator.world.World;

public abstract class HomeAnimal extends Animal {
    
    protected HomeAnimal(int maxAge, int adultAge) {
        super(maxAge, adultAge);
    }

    //Finder dyrets hjem
    protected abstract Homes getHome();

    // Har dyret et hjem
    protected boolean hasHome() {
        return getHome() != null;
    }

    protected boolean standsOnHome(World world) {
        if (!hasHome()) {
            return false;
        }
        return world.getLocation(this).equals(world.getLocation(getHome()));
    }

    protected void moveToHome(World world) {
        if (!hasHome() || standsOnHome(world)) {
            return;
        }
        moveTowards(world, world.getLocation(getHome()));
    }

}
