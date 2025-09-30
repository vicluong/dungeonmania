package dungeonmania.entities;

import dungeonmania.entities.logic_entities.Conductor;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wire extends Conductor {
    public Wire(String id, Position position) {
        super(id, position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }
}
