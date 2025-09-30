package dungeonmania.entities.logic_entities;

import dungeonmania.util.Position;

public class LightBulbOn extends LightBulb {
    public LightBulbOn(String id, Position position, String logic) {
        super(id, position, logic);
        setActivated(false);
    }
}
