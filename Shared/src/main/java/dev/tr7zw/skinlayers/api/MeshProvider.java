package dev.tr7zw.skinlayers.api;

import net.minecraft.client.player.AbstractClientPlayer;

public interface MeshProvider {

    /**
     * Returns the PlayerData for the given player.
     * 
     * @param abstractClientPlayerEntity
     * @param layer
     * @return
     */
    public PlayerData getPlayerMesh(AbstractClientPlayer abstractClientPlayerEntity);
    
}
