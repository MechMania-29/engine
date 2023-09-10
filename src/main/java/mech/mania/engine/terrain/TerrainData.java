package mech.mania.engine.terrain;

public record TerrainData(TerrainType type, String imageId, boolean canShootThrough, int health) {
}
