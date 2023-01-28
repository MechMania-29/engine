package mech.mania.engine;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface Diffable {
    Map<String, JsonNode> diff(Object previous);
}
