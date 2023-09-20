package mech.mania.engine.terrain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mech.mania.engine.Engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MapLoader {
    public static List<List<Character>> LoadMap(SelectedMap map) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream is = Engine.class.getResourceAsStream(map.getValue());
        return  objectMapper.readValue(is, new TypeReference<>() {});
    }
}
