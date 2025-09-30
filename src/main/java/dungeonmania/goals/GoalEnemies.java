package dungeonmania.goals;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.enemies.ZombieToastSpawner;

public class GoalEnemies implements GoalType {
    private int target;

    public GoalEnemies(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        return game.getBattleFacade().getBattleResponses().size() >= target
            && game.countEntities(ZombieToastSpawner.class) == 0;
    }

    public String toString(Game game) {
        return ":enemies";
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("goal", "enemies");
        return json;
    }
}
