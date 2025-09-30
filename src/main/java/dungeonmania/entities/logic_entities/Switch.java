package dungeonmania.entities.logic_entities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.entities.MoveAwayable;
import dungeonmania.entities.Overlappable;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Conductor implements Overlappable, MoveAwayable {
    private boolean activated;
    private List<Bomb> bombs = new ArrayList<>();

    public Switch(String id, Position position, boolean activated) {
        super(id, position.asLayer(Entity.ITEM_LAYER));
        this.activated = activated;
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (activated) {
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = true;
            bombs.stream().forEach(b -> b.notify(map));
            activate(map, this, activated);
            changeLightBulb(map);
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            activated = false;
            activate(map, this, activated);
            changeLightBulb(map);
        }
    }

    public void changeLightBulb(GameMap map) {
        List<LightBulbOff> lightBulbsOff = map.getEntities(LightBulbOff.class);
        List<LightBulbOn> changedAlready = new ArrayList<>();

        for (LightBulbOff lightBulbOff : lightBulbsOff) {
            if (lightBulbOff.isActivated()) {
                LightBulbOn newLightBulbOn = new LightBulbOn(lightBulbOff.getId(),
                                                             lightBulbOff.getPosition(),
                                                             lightBulbOff.getLogic());
                map.addEntity(newLightBulbOn);
                map.destroyEntity(lightBulbOff);
                changedAlready.add(newLightBulbOn);
            }
        }

        List<LightBulbOn> lightBulbsOn = map.getEntities(LightBulbOn.class);
        for (LightBulbOn lightBulbOn : lightBulbsOn) {
            if (!lightBulbOn.isActivated() && !changedAlready.contains(lightBulbOn)) {
                map.addEntity(new LightBulbOff(lightBulbOn.getId(), lightBulbOn.getPosition(), lightBulbOn.getLogic()));
                map.destroyEntity(lightBulbOn);
            }
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("activated", activated);
        return json;
    }
}
