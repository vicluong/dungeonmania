package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface MoveAwayable {
    public void onMovedAway(GameMap map, Entity entity);
}
