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

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        FieldAction fa = space.getActions().stream().filter((FieldAction obj)-> obj.getClass().getSimpleName().equals("ConveyorBelt")).findAny().orElse(null);
        ConveyorBelt cb= (ConveyorBelt) fa;
        if(cb != null ){
            try {
                Space target= gameController.board.getNeighbour(space, cb.getHeading());
                if(cb.getColor().equals("BLUE")){
                    target=gameController.board.getNeighbour(target, cb.getHeading());
                }
                gameController.moveToSpace(space.getPlayer(), target, cb.getHeading());
                return true;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

}