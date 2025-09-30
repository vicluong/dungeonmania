package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.logic_entities.Switch;

public class GoalBoulders implements GoalType {
    public GoalBoulders() {
    }

    public boolean achieved(Game game) {
        return game.getMap().getEntities(Switch.class).stream().allMatch(s -> s.isActivated());
    }

    public String toString(Game game) {
        return ":boulders";
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("goal", "boulders");
        return json;
    }
}
