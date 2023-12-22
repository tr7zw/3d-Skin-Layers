package dev.tr7zw.skinlayers.mixin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.platform.NativeImage;

import dev.tr7zw.skinlayers.accessor.HttpTextureAccessor;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;

@Mixin(HttpTexture.class)
public abstract class HttpTextureMixin extends AbstractTexture implements HttpTextureAccessor {

    @Shadow
    private File file;

    @Override
    public NativeImage getImage() throws FileNotFoundException {
        if (this.file != null && this.file.isFile()) {
            FileInputStream fileInputStream = new FileInputStream(this.file);
            NativeImage nativeImage = load(fileInputStream);
            return nativeImage;
        }
        return null;
    }

    @Shadow
    public abstract NativeImage load(InputStream inputStream);

}
