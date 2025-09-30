package dungeonmania.entities.logic_entities;

import org.json.JSONObject;

import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends LogicalEntity {
    public SwitchDoor(String id, Position position, String logic) {
        super(id, position);
        setLogic(logic);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return isActivated();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("logic", getLogic());
        return json;
    }
}
