package mech.mania.engine.log;

import java.util.List;

public record LogErrors(List<String> humanErrors, List<String> zombieErrors) {
}
