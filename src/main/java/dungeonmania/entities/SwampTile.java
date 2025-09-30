package dungeonmania.entities;

import java.util.HashMap;
import java.util.Map;

import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwampTile extends Entity implements Overlappable {
    private Map<Entity, Integer> stuckEntities = new HashMap<>();
    private int movementFactor;

    public SwampTile(String id, Position position, int movementFactor) {
        super(id, position);
        this.movementFactor = movementFactor;
    }

    public boolean isStuck(Entity entity) {
        return stuckEntities.containsKey(entity);
    }

    public int getTick(Entity entity) {
        return stuckEntities.get(entity);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (!isAffected(map, entity)) {
            return;
        }

        if (movementFactor == 0) {
            return;
        } else if (!stuckEntities.containsKey(entity)) {
            stuckEntities.put(entity, 0);
        } else if (stuckEntities.get(entity) == movementFactor) {
            stuckEntities.remove(entity);
        } else {
            int value = stuckEntities.get(entity) + 1;
            stuckEntities.put(entity, value);
        }
    }

    public boolean isAffected(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            return false;
        } else if (entity instanceof Mercenary && ((Mercenary) entity).isAllied()
                   && ((Mercenary) entity).isAttached()) {
            if (stuckEntities.containsKey(entity)) {
                stuckEntities.remove(entity);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

}
