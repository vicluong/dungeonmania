package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;

public class GoalOr implements GoalType {
    private Goal goal1;
    private Goal goal2;

    public GoalOr(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    public boolean achieved(Game game) {
        return goal1.achieved(game) || goal2.achieved(game);
    }

    public String toString(Game game) {
        if (achieved(game)) return "";
        else return "(" + goal1.toString(game) + " OR " + goal2.toString(game) + ")";
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray subgoals = new JSONArray();
        json.put("goal", "OR");
        subgoals.put(goal1.toJSON());
        subgoals.put(goal2.toJSON());
        json.put("subgoals", subgoals);
        return json;
    }
}
