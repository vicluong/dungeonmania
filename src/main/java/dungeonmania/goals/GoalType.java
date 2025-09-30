package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;

public interface GoalType {
    public abstract boolean achieved(Game game);
    public abstract String toString(Game game);
    public JSONObject toJSON();
}
