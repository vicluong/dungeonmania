package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class TargetedMovement implements MovementStrategy {

    @Override
    public void doMove(Enemy enemy, Game game) {
        GameMap map = game.getMap();
        Position nextPos = map.dijkstraPathFind(enemy.getPosition(), map.getPlayer().getPosition(), enemy);
        map.moveTo(enemy, nextPos);
    }
}
