package mech.mania.engine.log;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class NullBooleanFilter {
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Boolean)) {
            return true;
        }

        return false;
    }
}
