package world;

import javafx.geometry.Point2D;
import java.util.List;

/**
 * Represents path between two ports.
 */
public class VehiclePath {
    private List<Cross> path;
    private int currentPoint;
    public VehiclePath(List<Cross> path){
        this.path = path;
    }
    public Cross getNextPoint(){
        if(currentPoint +1 <= path.size())
            return null; //not safest thing.
        return path.get(currentPoint);
    }
    public void nextPointHasBeenVisited(){
        if(currentPoint +1 <= path.size())
            return;
        currentPoint++;
    }

}
