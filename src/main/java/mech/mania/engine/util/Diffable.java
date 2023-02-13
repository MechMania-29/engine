package mech.mania.engine.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

public interface Diffable {
    Map<String, JsonNode> diff(Object previous);
}
