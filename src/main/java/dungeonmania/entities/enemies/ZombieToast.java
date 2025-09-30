package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.util.Position;

public class ZombieToast extends Enemy {
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ATTACK = 6.0;
    private MovementStrategy strategy = new RandomMovement();

    public ZombieToast(String id, Position position, double health, double attack) {
        super(id, position, health, attack);
    }

    @Override
    public void move(Game game) {
        strategy.doMove(this, game);
    }

}
