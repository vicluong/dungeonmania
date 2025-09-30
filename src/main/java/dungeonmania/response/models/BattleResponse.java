package dungeonmania.response.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public final class BattleResponse {
    private final String enemy;
    private final double initialPlayerHealth;
    private final double initialEnemyHealth;
    private final List<ItemResponse> battleItems;
    private final List<RoundResponse> rounds;

    public BattleResponse() {
        this.initialPlayerHealth = 0;
        this.initialEnemyHealth = 0;
        this.enemy = "";
        this.battleItems = new ArrayList<>();
        this.rounds = new ArrayList<>();
    }

    public BattleResponse(String enemy, List<RoundResponse> rounds, List<ItemResponse> battleItems,
    double initialPlayerHealth, double initialEnemyHealth) {
        this.initialPlayerHealth = initialPlayerHealth;
        this.initialEnemyHealth = initialEnemyHealth;
        this.enemy = enemy;
        this.rounds = rounds;
        this.battleItems = battleItems;
    }

    public BattleResponse(JSONObject battle) {
        this.initialPlayerHealth = battle.getDouble("initialPlayerHealth");
        this.initialEnemyHealth = battle.getDouble("initialEnemyHealth");
        this.enemy = battle.getString("enemy");
        this.battleItems = new ArrayList<>();
        this.rounds = new ArrayList<>();
        battle.getJSONArray("rounds").forEach(r -> this.rounds.add(new RoundResponse((JSONObject) r)));
        battle.getJSONArray("battleItems").forEach(r -> this.battleItems.add(new ItemResponse((JSONObject) r)));
    }

    public final String getEnemy() {
        return enemy;
    }

    public final double getInitialPlayerHealth() {
        return initialPlayerHealth;
    }

    public final double getInitialEnemyHealth() {
        return initialEnemyHealth;
    }

    public final List<RoundResponse> getRounds() {
        return rounds;
    }

    public final List<ItemResponse> getBattleItems() {
        return battleItems;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("enemy", enemy);
        json.put("initialPlayerHealth", initialPlayerHealth);
        json.put("initialEnemyHealth", initialEnemyHealth);
        JSONArray items = new JSONArray();
        JSONArray roundList = new JSONArray();
        battleItems.stream().forEach(bi -> items.put(bi.toJSON()));
        rounds.stream().forEach(r -> roundList.put(r.toJSON()));
        json.put("battleItems", items);
        json.put("rounds", roundList);
        return json;
    }
}
