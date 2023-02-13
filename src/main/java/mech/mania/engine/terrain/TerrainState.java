package mech.mania.engine.terrain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.util.Diffable;
import mech.mania.engine.util.Position;

import java.util.HashMap;
import java.util.Map;

public class TerrainState implements Cloneable, Diffable {
    private final String id;
    private final String imageId;
    private final Position position;

    public TerrainState(String id, String imageId, Position position) {
        this.id = id;
        this.imageId = imageId;
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public String getImageId() {
        return imageId;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public Map<String, JsonNode> diff(Object previous) {
        if (this == previous) {
            return null;
        }
        TerrainState previousTerrainState = null;

        if (previous != null) {
            if (!(previous instanceof TerrainState ts)) {
                return null;
            }

            previousTerrainState = ts;

            if (!previousTerrainState.id.equals(id)) {
                return null;
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, JsonNode> diff = new HashMap<>();

        if (previousTerrainState == null || position != previousTerrainState.position) {
            diff.put("position", mapper.valueToTree(position));
        }

        if (previousTerrainState == null || imageId != previousTerrainState.imageId) {
            diff.put("imageId", mapper.valueToTree(imageId));
        }

        if (diff.isEmpty()) {
            return null;
        }

        return diff;
    }

    @Override
    public TerrainState clone() {
        try {
            return (TerrainState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
