package dungeonmania.entities;

import dungeonmania.map.GameMap;

import org.json.JSONObject;

import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Sunstone;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.util.Position;

public class Door extends Entity implements Overlappable {
    private boolean open = false;
    private int number;

    public Door(String id, Position position, int number, boolean open) {
        super(id, position.asLayer(Entity.DOOR_LAYER));
        this.number = number;
        this.open = open;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (open || entity instanceof Spider) {
            return true;
        }
        return (entity instanceof Player && hasKey((Player) entity));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (!(entity instanceof Player))
            return;

        Player player = (Player) entity;
        if (player.useKey())
            open();
    }

    private boolean hasKey(Player player) {
        Inventory inventory = player.getInventory();
        Key key = inventory.getFirst(Key.class);
        Sunstone sunstone = inventory.getFirst(Sunstone.class);
        return (sunstone != null) || (key != null && key.getnumber() == number);
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("key", this.number);
        json.put("isOpen", isOpen());
        return json;
    }
}
