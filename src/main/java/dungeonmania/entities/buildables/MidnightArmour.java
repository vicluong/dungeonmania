package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class MidnightArmour extends Buildable {

    private int attack;
    private int defence;

    public MidnightArmour(String id, int attack, int defence) {
        super(id, null);
        this.attack = attack;
        this.defence = defence;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
                0,
                attack,
                defence,
                1,
                1));
    }

    @Override
    public void use(Game game) {
        return;
    }

    @Override
    public int getDurability() {
        return -1;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("entityId", this.getId());
        json.put("type", "midnight_armour");
        return json;
    }
}
