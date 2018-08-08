package com.voxelmodpack.hdskins.skins;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.util.Session;

public abstract class AbstractSkinServer implements SkinServer {

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, SkinUpload upload) {
        return CallableFutures.asyncFailableFuture(() -> {
            return doUpload(session, upload);
        }, HDSkinManager.skinUploadExecutor);
    }

    @Override
    public CompletableFuture<MinecraftTexturesPayload> getPreviewTextures(GameProfile profile) {
        return CallableFutures.asyncFailableFuture(() -> doGetPreviewTextures(profile), HDSkinManager.skinDownloadExecutor);
    }

    protected MinecraftTexturesPayload doGetPreviewTextures(GameProfile profile) throws AuthenticationException, IOException {
        return loadProfileData(profile);
    }

    protected abstract SkinUploadResponse doUpload(Session session, SkinUpload upload) throws AuthenticationException, IOException;

}
