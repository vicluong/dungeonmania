package dungeonmania.entities.enemies;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.util.Position;

public class Spider extends Enemy {
    private MovementStrategy strategy;
    public static final int DEFAULT_SPAWN_RATE = 0;
    public static final double DEFAULT_ATTACK = 5;
    public static final double DEFAULT_HEALTH = 10;
    private Position initPos;

    public Spider(
            String id,
            Position position,
            double health,
            double attack,
            Position initPos,
            int nextPositionElement) {
        super(id, position.asLayer(Entity.DOOR_LAYER + 1), health, attack);
        this.initPos = initPos;
        strategy = new CircularMovement(this, initPos, nextPositionElement);
        /**
         * Establish spider movement trajectory Spider moves as follows:
         * 8 1 2 10/12 1/9 2/8
         * 7 S 3 11 S 3/7
         * 6 5 4 B 5 4/6
         */
    };

    @Override
    public void move(Game game) {
        strategy.doMove(this, game);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("initPos", initPos.toJSON());
        json.put("nextPositionElement", ((CircularMovement) strategy).getNextPositionElement());
        return json;
    }
}
