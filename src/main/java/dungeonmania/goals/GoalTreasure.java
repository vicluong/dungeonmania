package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.collectables.Sunstone;
import dungeonmania.entities.collectables.Treasure;

public class GoalTreasure implements GoalType {
    private int target;

    public GoalTreasure(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        return game.getInitialTreasureCount() - game.getMap().getEntities(Treasure.class).size()
        >= target - game.getPlayer().getInventory().count(Sunstone.class);
    }

    public String toString(Game game) {
        return ":treasure";
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("goal", "treasure");
        return json;
    }
}
