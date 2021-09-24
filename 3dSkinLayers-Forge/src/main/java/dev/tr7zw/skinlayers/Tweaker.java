package dev.tr7zw.skinlayers;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tweaker implements ITweaker {

    private ArrayList<String> args = new ArrayList<>();
    private boolean isRunningOptifine = false;

    @Override public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        this.args.addAll(args);

    }

    @Override public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        MixinBootstrap.init();

        MixinEnvironment environment = MixinEnvironment.getDefaultEnvironment();
        Mixins.addConfiguration("3dskinlayers.mixins.json");

        if (isRunningOptifine || environment.getObfuscationContext() == null) {
            environment.setObfuscationContext("searge");
        }else {
            environment.setObfuscationContext("searge");
        }

        environment.setSide(MixinEnvironment.Side.CLIENT);

    }

    @Override public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override public String[] getLaunchArguments() {
        return new String[0];
    }

}
