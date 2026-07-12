package com.novusmc.camprelay.model;

import java.util.UUID;

/**
 * Données personnelles d'un joueur pour CampRelay.
 */
public class PlayerData {

    private final UUID uuid;
    private RespawnMode respawnMode = RespawnMode.BED;
    private UUID selectedRelayId;
    private UUID lastCampRelayId;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public RespawnMode getRespawnMode() {
        return respawnMode;
    }

    public void setRespawnMode(RespawnMode respawnMode) {
        this.respawnMode = respawnMode;
    }

    public UUID getSelectedRelayId() {
        return selectedRelayId;
    }

    public void setSelectedRelayId(UUID selectedRelayId) {
        this.selectedRelayId = selectedRelayId;
    }

    public UUID getLastCampRelayId() {
        return lastCampRelayId;
    }

    public void setLastCampRelayId(UUID lastCampRelayId) {
        this.lastCampRelayId = lastCampRelayId;
    }
}
