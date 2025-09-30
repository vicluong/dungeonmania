package dungeonmania.entities.collectables;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Sword extends Entity implements InventoryItem, BattleItem {
    public static final double DEFAULT_ATTACK = 1;
    public static final double DEFAULT_ATTACK_SCALE_FACTOR = 1;
    public static final int DEFAULT_DURABILITY = 5;
    public static final double DEFAULT_DEFENCE = 0;
    public static final double DEFAULT_DEFENCE_SCALE_FACTOR = 1;

    private int durability;
    private double attack;

    public Sword(String id, Position position, double attack, int durability) {
        super(id, position);
        this.attack = attack;
        this.durability = durability;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void use(Game game) {
        durability--;
        if (durability <= 0)
            game.getPlayer().remove(this);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            attack,
            0,
            1,
            1));
    }

    @Override
    public int getDurability() {
        return durability;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("entityId", this.getId());
        json.put("type", "sword");
        json.put("durability", this.durability);
        return json;
    }
}
