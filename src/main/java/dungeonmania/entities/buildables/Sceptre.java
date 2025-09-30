package dungeonmania.entities.buildables;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;

public class Sceptre extends Buildable {

    public static final int DEFAULT_DURATION = 1;
    private int mindControlDuration;

    public Sceptre(String id, int mindControlDuration) {
        super(id, null);
        this.mindControlDuration = mindControlDuration;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return origin;
    }

    @Override
    public void use(Game game) {
        return;
    }

    @Override
    public int getDurability() {
        return 0;
    }

    public int getMindControlDuration() {
        return this.mindControlDuration;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("entityId", this.getId());
        json.put("type", "sceptre");
        return json;
    }
}
