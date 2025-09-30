package dungeonmania.entities.logic_entities;

import dungeonmania.util.Position;

public class LightBulbOff extends LightBulb {
    public LightBulbOff(String id, Position position, String logic) {
        super(id, position, logic);
        setActivated(false);
    }
}
