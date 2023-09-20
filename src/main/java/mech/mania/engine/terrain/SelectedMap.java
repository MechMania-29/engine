package mech.mania.engine.terrain;

public enum SelectedMap {
    MAIN("/maps/main.json");

    private final String value;

    SelectedMap(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
