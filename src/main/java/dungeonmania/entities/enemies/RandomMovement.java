package dungeonmania.entities.enemies;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class RandomMovement implements MovementStrategy {

    @Override
    public void doMove(Enemy enemy, Game game) {
        Random randGen = new Random();
        Position nextPos;
        GameMap map = game.getMap();
        List<Position> pos = enemy.getPosition().getCardinallyAdjacentPositions();
        pos = pos
            .stream()
            .filter(p -> map.canMoveTo(enemy, p)).collect(Collectors.toList());
        if (pos.size() == 0) {
            nextPos = enemy.getPosition();
            game.getMap().moveTo(enemy, nextPos);
        } else {
            nextPos = pos.get(randGen.nextInt(pos.size()));
            game.getMap().moveTo(enemy, nextPos);
        }

    }
}
