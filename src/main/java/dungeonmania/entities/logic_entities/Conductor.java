package dungeonmania.entities.logic_entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Conductor extends Entity implements Activatable {
    private List<Entity> sourceEntities = new ArrayList<>();

    public Conductor(String id, Position position) {
        super(id, position);
    }

    public void activate(GameMap map, Entity sourceEntity, boolean circuitActivated) {
        if (!sourceEntities.contains(sourceEntity) && circuitActivated) {
            sourceEntities.add(sourceEntity);
        } else if (sourceEntities.contains(sourceEntity) && !circuitActivated) {
            sourceEntities.remove(sourceEntity);
        } else if (sourceEntities.contains(sourceEntity) == circuitActivated) {
            return;
        }

        List<Entity> adjEntities = getAdjacentActivatables(map);
        for (Entity adjEntity : adjEntities) {
            if (adjEntity instanceof Conductor) {
                ((Conductor) adjEntity).activate(map, sourceEntity, circuitActivated);
            } else if (adjEntity instanceof LogicalEntity) {
                ((LogicalEntity) adjEntity).activate(map);
            }
        }
    }

    public List<Entity> getAdjacentActivatables(GameMap map) {
        List<Entity> adjEntities = new ArrayList<>();

        List<Position> cardinalAdjacent = this.getPosition().getCardinallyAdjacentPositions();
        for (Position adjacent : cardinalAdjacent) {
            List<Entity> adjEntity = map.getEntities(adjacent)
                                        .stream()
                                        .filter(entity -> entity instanceof Activatable)
                                        .collect(Collectors.toList());

            adjEntities.addAll(adjEntity);
        }

        return adjEntities;
    }

    public List<Entity> getSourceEntities() {
        return sourceEntities;
    }
}
