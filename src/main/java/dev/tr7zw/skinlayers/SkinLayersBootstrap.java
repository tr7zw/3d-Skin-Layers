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
//$$import net.neoforged.api.distmarker.Dist;
//$$import net.neoforged.fml.DistExecutor;
//$$import net.neoforged.fml.common.Mod;
//$$
//$$@Mod("skinlayers3d")
//$$public class SkinLayersBootstrap {
//$$
//$$	public SkinLayersBootstrap() {
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SkinLayersMod::new);
//$$	}
//$$	
//$$}
//#endif