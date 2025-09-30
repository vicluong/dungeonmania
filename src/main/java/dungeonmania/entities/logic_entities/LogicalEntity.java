package dungeonmania.entities.logic_entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class LogicalEntity extends Entity implements Activatable {
    private String logic;
    private boolean isActivated = false;

    public LogicalEntity(String id, Position position) {
        super(id, position);
    }

    public void activate(GameMap map) {
        Map<Entity, Integer> sourceEntities = new HashMap<Entity, Integer>();
        int connectedConductors = 0;

        List<Entity> adjEntities = getAdjacentActivatables(map);

        for (Entity adjEntity : adjEntities) {
            if (adjEntity instanceof Conductor) {
                for (Entity sourceEntity : ((Conductor) adjEntity).getSourceEntities()) {
                    sourceEntities.merge(sourceEntity, 1, ((a, b) -> a + b));
                }
                if (!((Conductor) adjEntity).getSourceEntities().isEmpty()) {
                    connectedConductors++;
                }
            }
        }

        switch (logic) {
            case "and":
                setActivated(connectedConductors >= 2);
                break;
            case "or":
                setActivated(connectedConductors >= 1);
                break;
            case "xor":
                setActivated(connectedConductors == 1);
                break;
            case "co_and":
                boolean check = false;
                for (Integer value : sourceEntities.values()) {
                    if (value >= 2)
                        check = true;
                }
                setActivated(check);
            default:
                return;
        }
    }

    public List<Entity> getAdjacentActivatables(GameMap map) {
        List<Entity> adjEntities = new ArrayList<>();

        List<Position> cardinalAdjacent = this.getPosition().getCardinallyAdjacentPositions();
        for (Position adjacent : cardinalAdjacent) {
            List<Entity> entities = map.getEntities(adjacent);
            for (Entity adjEntity : entities) {
                if (adjEntity instanceof Activatable) {
                    adjEntities.add(adjEntity);
                }
            }
        }

        return adjEntities;
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        this.isActivated = activated;
    }

    public String getLogic() {
        return logic;
    }

    public void setLogic(String logic) {
        this.logic = logic;
    }
}
