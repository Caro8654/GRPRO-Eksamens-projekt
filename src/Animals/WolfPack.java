package Animals;

import java.util.ArrayList;
import Home.WolfHole;

public class WolfPack {
    private ArrayList<Wolf> wolfPack = new ArrayList<>();
    private WolfHole wolfHole;

    public void addMember(Wolf wolf) {
        wolfPack.add(wolf);
    }

    public void removeMember(Wolf wolf) {
        wolfPack.remove(wolf);
    }

    public ArrayList<Wolf> getMembers() {
        return wolfPack;
    }

    public WolfHole getWolfHole() {
        return wolfHole;
    }

    public void setWolfHole(WolfHole wolfHole) {
        this.wolfHole = wolfHole;
    }
}