package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.Game;

public class GoalAnd implements GoalType {
    private Goal goal1;
    private Goal goal2;

    public GoalAnd(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    public boolean achieved(Game game) {
        return goal1.achieved(game) && goal2.achieved(game);
    }

    public String toString(Game game) {
        return "(" + goal1.toString(game) + " AND " + goal2.toString(game) + ")";
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray subgoals = new JSONArray();
        json.put("goal", "AND");
        subgoals.put(goal1.toJSON());
        subgoals.put(goal2.toJSON());
        json.put("subgoals", subgoals);
        return json;
    }
}
