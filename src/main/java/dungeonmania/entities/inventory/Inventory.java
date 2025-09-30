package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Sunstone;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;

public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();

    public Inventory(JSONArray inventory, JSONObject config) {
        if (inventory != null) {
            EntityFactory e = new EntityFactory(config);
            inventory.forEach(i -> items.add((InventoryItem) e.createEntity((JSONObject) i)));
        }
    }

    public boolean add(InventoryItem item) {
        items.add(item);
        return true;
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables() {

        int wood = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        int sunstones = count(Sunstone.class);
        int swords = count(Sword.class);
        List<String> result = new ArrayList<>();

        if (wood >= 1 && arrows >= 3)
            result.add("bow");
        if (wood >= 2 && (treasure >= 1 || keys >= 1 || sunstones >= 1))
            result.add("shield");
        if ((wood >= 1 || arrows >= 2) && ((treasure >= 1 || keys >= 1) || sunstones >= 2) && sunstones >= 1)
            result.add("sceptre");
        if (swords >= 1 && sunstones >= 1)
            result.add("midnight_armour");
        return result;
    }

    public InventoryItem checkBuildCriteria(Player p, boolean remove, String entity, EntityFactory factory) {

        List<Wood> wood = getEntities(Wood.class);
        List<Arrow> arrows = getEntities(Arrow.class);
        List<Treasure> treasure = getEntities(Treasure.class);
        List<Key> keys = getEntities(Key.class);
        List<Sunstone> sunstones = getEntities(Sunstone.class);
        List<Sword> swords = getEntities(Sword.class);
        if (getBuildables().contains(entity)) {
            switch (entity) {
                case "bow":
                    if (remove) {
                        items.remove(wood.get(0));
                        items.remove(arrows.get(0));
                        items.remove(arrows.get(1));
                        items.remove(arrows.get(2));
                    }
                    return factory.buildBow(UUID.randomUUID().toString(), -1);
                case "shield":
                    if (remove) {
                        items.remove(wood.get(0));
                        items.remove(wood.get(1));
                        if (sunstones.size() < 1) {
                            if (treasure.size() >= 1) {
                                items.remove(treasure.get(0));
                            } else {
                                items.remove(keys.get(0));
                            }
                        }
                    }
                    return factory.buildShield(UUID.randomUUID().toString(), -1);
                case "sceptre":
                    if (remove) {
                        if (wood.size() >= 1) {
                            items.remove(wood.get(0));
                        } else {
                            items.remove(arrows.get(0));
                            items.remove(arrows.get(1));
                        }
                        if (sunstones.size() < 2) {
                            if (treasure.size() >= 1) {
                                items.remove(treasure.get(0));
                            } else {
                                items.remove(keys.get(0));
                            }
                        }
                        items.remove(sunstones.get(0));
                    }
                    return factory.buildSceptre(UUID.randomUUID().toString());
                case "midnight_armour":
                    if (remove) {
                        items.remove(sunstones.get(0));
                        items.remove(swords.get(0));
                    }
                    return factory.buildMidnightArmour(UUID.randomUUID().toString());
                default:
                    return null;
            }
        }
        return null;
    }

    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item)) count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId)) return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    public JSONArray toJSON() {
        JSONArray json = new JSONArray();
        items.stream().forEach(i -> json.put(i.toJSON()));
        return json;
    }

}
