package dungeonmania.response.models;

import org.json.JSONObject;

public final class ItemResponse {
    private final String id;
    private final String type;

    public ItemResponse(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public ItemResponse(JSONObject r) {
        this.id = r.getString("id");
        this.type = r.getString("type");
    }

    public final String getType() {
        return type;
    }

    public final String getId() {
        return id;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("type", type);
        return json;
    }
}
