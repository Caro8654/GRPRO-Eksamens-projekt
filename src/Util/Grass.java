package Util;

import itumulator.world.NonBlocking;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;

import java.util.*;


public class Grass implements NonBlocking, Actor{
    
    @Override
    public void act(World world) {
        Random r = new Random();

        if(r.nextInt(30) != 0) {
            return;
        }

        Location here = world.getLocation(this);
        List<Location> free = new ArrayList<>();

        for (Location l : world.getSurroundingTiles(here)) {
            if (!world.containsNonBlocking(l)) {
                free.add(l);
            }
        }

        if (!free.isEmpty()) {
            Location l = free.get(r.nextInt(free.size()));
            world.setTile(l, new Grass());
        }
    }
}