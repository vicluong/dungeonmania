package dungeonmania.entities.logic_entities;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class LightBulb extends LogicalEntity {
    public LightBulb(String id, Position position, String logic) {
        super(id, position);
        setLogic(logic);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("logic", getLogic());
        return json;
    }
}
