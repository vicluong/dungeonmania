package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class Bow extends Buildable  {

    private int durability;

    public Bow(String id, int durability) {
        super(id, null);
        this.durability = durability;
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
            0,
            0,
            2,
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
        json.put("durability", this.getDurability());
        json.put("type", "bow");
        return json;
    }
}
