package com.novusmc.camprelay.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RespawnMenuHolder extends CrHolder {
    private final Map<Integer, UUID> slotToRelay = new HashMap<>();

    public Map<Integer, UUID> getSlotToRelay() {
        return slotToRelay;
    }
}
