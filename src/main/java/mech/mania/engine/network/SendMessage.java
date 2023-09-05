package mech.mania.engine.network;

import com.fasterxml.jackson.databind.JsonNode;

public record SendMessage(boolean isZombie, SendMessageType type, JsonNode message) {}
