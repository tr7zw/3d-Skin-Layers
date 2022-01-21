//package dev.tr7zw.skinlayers.mixin;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//
//import dev.tr7zw.skinlayers.accessor.SkullModelAccessor;
//import net.minecraft.client.model.SkullModel;
//import net.minecraft.client.model.geom.ModelPart;
//
//@Mixin(SkullModel.class)
//public class SkullModelMixin implements SkullModelAccessor {
//
//    @Shadow
//    private ModelPart head;
//    
//    @Override
//    public void showHat(boolean val) {
//        head.getAllParts().forEach(part -> {
//            if(part != head) { // is the hat, not the head
//                part.visible = val;
//            }
//        });
//    }
//
//}
