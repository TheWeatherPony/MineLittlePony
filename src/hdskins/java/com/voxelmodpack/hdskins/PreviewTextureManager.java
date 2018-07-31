package com.voxelmodpack.hdskins;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.skins.CallableFutures;

import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

/**
 * Manager for fetching preview textures. This ensures that multiple calls
 * to the skin server aren't done when fetching preview textures.
 */
public class PreviewTextureManager {

    private final GameProfile profile;

    private Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = null;

    PreviewTextureManager(GameProfile profile) {
        this.profile = profile;
    }

    public CompletableFuture<PreviewTexture> getPreviewTexture(ResourceLocation location, MinecraftProfileTexture.Type type, ResourceLocation def, @Nullable SkinAvailableCallback callback) {
        return CallableFutures.asyncFailableFuture(() ->
            loadPreviewTexture(location, type, def, callback)
        , HDSkinManager.skinDownloadExecutor);
    }

    @Nullable
    private PreviewTexture loadPreviewTexture(ResourceLocation location, MinecraftProfileTexture.Type type, ResourceLocation def, @Nullable SkinAvailableCallback callback) throws AuthenticationException, IOException {
        if (textures == null) {
            textures = HDSkinManager.INSTANCE.getGatewayServer().getPreviewTextures(profile);
        }

        if (!textures.containsKey(type)) {
            return null;
        }

        MinecraftProfileTexture texture = textures.get(type);

        IImageBuffer buffer = type != MinecraftProfileTexture.Type.SKIN ? null : new ImageBufferDownloadHD().withCallback(() -> {
            if (callback != null) {
                callback.skinAvailable(type, location, new MinecraftProfileTexture(texture.getUrl(), Maps.newHashMap()));
            }
        });

        PreviewTexture skinTexture = new PreviewTexture(texture, def, buffer);

        TextureLoader.loadTexture(location, skinTexture);

        return skinTexture;
    }
}
