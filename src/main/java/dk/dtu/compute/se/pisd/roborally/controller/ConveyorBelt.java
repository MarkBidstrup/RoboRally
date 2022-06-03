package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;
// @author Golbas Haidari
public class ConveyorBelt extends FieldAction {

    private Heading heading;
    private String color;
    public void setColor(String color){
        this.color=color;
    }

    public String getColor(){
        return color;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    //@author Golbas Haidari
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // create fa object from FieldAction class, and if space has conveyorBelt assign it to fa, otherwise assign null to the fa
        FieldAction fa = space.getActions().stream().filter((FieldAction obj)-> obj.getClass().getSimpleName().equals("ConveyorBelt")).findAny().orElse(null);
        ConveyorBelt cb= (ConveyorBelt) fa;
        if(cb != null ){
            try {
                // find the space's neighbour and assign it to the target
                Space target= gameController.board.getNeighbour(space, cb.getHeading());
                if(cb.getColor().equals("BLUE")){
                    // find the target's neighbour and assign it to the target
                    target=gameController.board.getNeighbour(target, cb.getHeading());
                }
                //move the space's player to the target
                gameController.moveToSpace(space.getPlayer(), target, cb.getHeading());
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

}