//#if FABRIC
package dev.tr7zw.skinlayers;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.tr7zw.skinlayers.config.ConfigScreenProvider;

public class SkinLayersModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return ConfigScreenProvider.createConfigScreen(parent);
        };
    }

}
//#endif