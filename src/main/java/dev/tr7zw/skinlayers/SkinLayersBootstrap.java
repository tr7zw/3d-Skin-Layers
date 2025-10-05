//#if FORGE
//$$package dev.tr7zw.skinlayers;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//$$import dev.tr7zw.transition.loader.ModLoaderUtil;
//$$
//$$@Mod("skinlayers3d")
//$$public class SkinLayersBootstrap {
//$$
//$$	public SkinLayersBootstrap(FMLJavaModLoadingContext context) {
//$$        ModLoaderUtil.setModLoadingContext(context);
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new SkinLayersMod().onInitialize());
//$$	}
//$$    public SkinLayersBootstrap() {
//$$        this(FMLJavaModLoadingContext.get());
//$$    }
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.skinlayers;
//$$
//$$import net.neoforged.fml.common.Mod;
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$import dev.tr7zw.transition.loader.ModLoaderEventUtil;
//$$import net.neoforged.api.distmarker.Dist;
//$$
//$$@Mod("skinlayers3d")
//$$public class SkinLayersBootstrap {
//$$
//$$	public SkinLayersBootstrap() {
//#if MC < 12109
//$$        if(FMLEnvironment.dist == Dist.CLIENT) {
//#else
//$$        if(FMLEnvironment.getDist() == Dist.CLIENT) {
//#endif
//$$                    ModLoaderEventUtil.registerClientSetupListener(() -> new SkinLayersMod().onInitialize());
//$$            }
//$$	}
//$$	
//$$}
//#endif