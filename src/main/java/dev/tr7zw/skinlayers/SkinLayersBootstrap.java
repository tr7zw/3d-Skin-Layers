//#if FORGE
//$$package dev.tr7zw.skinlayers;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$
//$$@Mod("skinlayers3d")
//$$public class SkinLayersBootstrap {
//$$
//$$	public SkinLayersBootstrap() {
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SkinLayersMod::new);
//$$	}
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.skinlayers;
//$$
//$$import net.neoforged.fml.common.Mod;
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$
//$$@Mod("skinlayers3d")
//$$public class SkinLayersBootstrap {
//$$
//$$	public SkinLayersBootstrap() {
//$$		if (FMLEnvironment.dist.isClient()) new SkinLayersMod();
//$$	}
//$$	
//$$}
//#endif
