package dev.tr7zw.skinlayers;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class SkinLayersModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return SkinLayersModBase.instance.createConfigScreen(parent);
        };
    }  
    
}
