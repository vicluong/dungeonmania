package dungeonmania.entities.enemies;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.entities.SwampTile;
import dungeonmania.util.Position;

public class CircularMovement implements MovementStrategy {
    private int nextPositionElement = 1;
    private boolean forward = true;
    private List<Position> movementTrajectory;

    public CircularMovement(Enemy enemy, Position initPos, int nextPositionElement) {
        movementTrajectory = initPos.getAdjacentPositions();
        this.nextPositionElement = nextPositionElement;
    };

    private void updateNextPosition(Enemy enemy, Game game) {
        for (Entity e : game.getMap().getEntities(enemy.getPosition())) {
            if (e instanceof SwampTile && ((SwampTile) e).isStuck(enemy) && ((SwampTile) e).getTick(enemy) != 0)
                return;
        }

        if (forward) {
            nextPositionElement++;
            if (nextPositionElement == 8)
                nextPositionElement = 0;
        } else {
            nextPositionElement--;
            if (nextPositionElement == -1)
                nextPositionElement = 7;
        }
    }

    @Override
    public void doMove(Enemy enemy, Game game) {
        Position nextPos = movementTrajectory.get(nextPositionElement);
        List<Entity> entities = game.getMap().getEntities(nextPos);
        if (entities != null && entities.size() > 0 && entities.stream().anyMatch(e -> e instanceof Boulder)) {
            forward = !forward;
            updateNextPosition(enemy, game);
            updateNextPosition(enemy, game);
        }
        nextPos = movementTrajectory.get(nextPositionElement);
        entities = game.getMap().getEntities(nextPos);
        if (entities == null
                || entities.size() == 0
                || entities.stream().allMatch(e -> e.canMoveOnto(game.getMap(), enemy))) {
            game.getMap().moveTo(enemy, nextPos);
            updateNextPosition(enemy, game);
        }
    }

    public int getNextPositionElement() {
        return nextPositionElement;
    }
}
