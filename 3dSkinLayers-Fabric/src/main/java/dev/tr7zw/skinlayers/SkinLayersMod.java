package dev.tr7zw.skinlayers;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

public class SkinLayersMod extends SkinLayersModBase implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        this.onInitialize();
    }

    @Override
    public void initModloader() {
        ClientTickEvents.START_CLIENT_TICK.register(e ->
        {
            this.clientTick();
        });
        KeyBindingHelper.registerKeyBinding(keybind);
    }

}
