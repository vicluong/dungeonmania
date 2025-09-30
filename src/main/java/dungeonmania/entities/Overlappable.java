package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface Overlappable {
    public void onOverlap(GameMap map, Entity entity);
}
