package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Gear extends FieldAction{ // @author Xiao Chen
    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        FieldAction fa = space.getActions().stream().filter((FieldAction obj)-> obj.getClass().getSimpleName().equals("Gear")).findAny().orElse(null);
        Gear gear = (Gear) fa;
        if(gear != null ){
            try {
                Player player = space.getPlayer();
                if(gear.getHeading() == Heading.WEST){
                    player.setHeading(player.getHeading().next());
                }
                else
                    player.setHeading(player.getHeading().prev());
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

}
