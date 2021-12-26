package dev.tr7zw.skinlayers;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tweaker implements ITweaker {

    private ArrayList<String> args = new ArrayList<>();
    private boolean isRunningOptifine = Launch.classLoader.getTransformers().stream()
            .anyMatch(p -> p.getClass().getName().toLowerCase(Locale.ENGLISH).contains("optifine"));


    @Override public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        //this.args.addAll(args);
        //addArg("gameDir", gameDir);
        //addArg("assetsDir", assetsDir);
        //addArg("version", profile);
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

        if (isRunningOptifine) {
            environment.setObfuscationContext("notch"); // Switch's to notch mappings
        }

        if (environment.getObfuscationContext() == null) {
            environment.setObfuscationContext("notch"); // Switch's to notch mappings
        }

        environment.setSide(MixinEnvironment.Side.CLIENT);

    }

    @Override public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override public String[] getLaunchArguments() {
        System.out.println(args);
        return isRunningOptifine ? new String[0] : args.toArray(new String[]{});
    }
    
    private void addArg(String label, Object value) {
        args.add("--" + label);
        args.add(value instanceof String ? (String) value : value instanceof File ? ((File) value).getAbsolutePath() : ".");
    }

}
