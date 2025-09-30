package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class AlliedMovement implements MovementStrategy {
    private boolean attached = false;

    public boolean isAttached() {
        return attached;
    }

    @Override
    public void doMove(Enemy enemy, Game game) {
        GameMap map = game.getMap();
        Position playerPos = map.getPlayer().getPosition();
        Position enemyPos = enemy.getPosition();

        checkAttached(playerPos, enemyPos);

        if (attached) {
            map.moveTo(enemy, map.getPlayer().getPreviousDistinctPosition());
        } else {
            Position nextPos = map.dijkstraPathFind(enemyPos, playerPos, enemy);
            map.moveTo(enemy, nextPos);
            enemyPos = enemy.getPosition();
            checkAttached(playerPos, enemyPos);
        }
    }

    private void checkAttached(Position playerPos, Position enemyPos) {
        if (playerPos.getCardinallyAdjacentPositions().contains(enemyPos))
            attached = true;
    }
}
