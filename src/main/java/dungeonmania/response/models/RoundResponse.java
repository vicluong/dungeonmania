package dungeonmania.response.models;

import org.json.JSONObject;

public class RoundResponse {
    private double deltaPlayerHealth;
    private double deltaEnemyHealth;

    public RoundResponse(double deltaPlayerHealth, double deltaEnemyHealth) {
        this.deltaPlayerHealth = deltaPlayerHealth;
        this.deltaEnemyHealth = deltaEnemyHealth;
    }

    public RoundResponse(JSONObject r) {
        this.deltaEnemyHealth = r.getDouble("deltaEnemyHealth");
        this.deltaPlayerHealth = r.getDouble("deltaPlayerHealth");
    }

    public double getDeltaCharacterHealth() {
        return deltaPlayerHealth;
    }

    public double getDeltaEnemyHealth() {
        return deltaEnemyHealth;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("deltaPlayerHealth", deltaPlayerHealth);
        json.put("deltaEnemyHealth", deltaEnemyHealth);
        return json;
    }

}
