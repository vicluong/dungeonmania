package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;

public class Goal {
    private GoalType goalType;

    public Goal(GoalType goalType) {
        this.goalType = goalType;
    }

    /**
     * @return true if the goal has been achieved, false otherwise
     */
    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        return goalType.achieved(game);
    }

    public String toString(Game game) {
        if (this.achieved(game)) return "";
        return goalType.toString(game);
    }

    public JSONObject toJSON() {
        return goalType.toJSON();
    }

}
