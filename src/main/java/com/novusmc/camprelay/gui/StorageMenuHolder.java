package com.novusmc.camprelay.gui;

import java.util.UUID;

public class StorageMenuHolder extends CrHolder {
    private final UUID owner;

    public StorageMenuHolder(UUID owner) {
        this.owner = owner;
    }

    public UUID getOwner() {
        return owner;
    }
}
