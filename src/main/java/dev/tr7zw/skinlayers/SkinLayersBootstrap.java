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
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.skinlayers;
//$$
//$$import net.neoforged.fml.common.Mod;
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$import dev.tr7zw.transition.loader.ModLoaderEventUtil;
//$$
//$$@Mod("skinlayers3d")
//$$public class SkinLayersBootstrap {
//$$
//$$	public SkinLayersBootstrap() {
//$$            if (FMLEnvironment.dist.isClient()){
//$$                    ModLoaderEventUtil.registerClientSetupListener(() -> new SkinLayersMod().onInitialize());
//$$            }
//$$	}
//$$	
//$$}
//#endif