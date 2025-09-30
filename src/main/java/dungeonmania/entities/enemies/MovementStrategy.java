package dungeonmania.entities.enemies;

import dungeonmania.Game;

public interface MovementStrategy {
    public void doMove(Enemy enemy, Game game);
}
