package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface Destroyable {
    public void onDestroy(GameMap gameMap);
}
