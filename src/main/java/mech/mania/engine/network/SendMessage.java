package mech.mania.engine.network;

import com.fasterxml.jackson.databind.JsonNode;
import mech.mania.engine.GamePhase;

public record SendMessage(boolean isZombie, GamePhase phase, JsonNode message) {}
