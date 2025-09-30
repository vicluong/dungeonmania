package dungeonmania.entities.collectables.potions;

import org.json.JSONObject;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.util.Position;

public class InvincibilityPotion extends Potion {
    public static final int DEFAULT_DURATION = 8;

    public InvincibilityPotion(String id, Position position, int duration) {
        super(id, position, duration);
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                0,
                0,
                1,
                1,
                true,
                true));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("entityId", this.getId());
        json.put("type", "invincibility_potion");
        return json;
    }
}
