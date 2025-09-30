package dungeonmania.entities;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Exit extends Entity {
    public Exit(String id, Position position) {
        super(id, position.asLayer(Entity.ITEM_LAYER));
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
