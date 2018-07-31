package com.voxelmodpack.hdskins.skins;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.util.Strings;

import com.google.gson.JsonParseException;
import com.google.gson.annotations.Expose;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.voxelmodpack.hdskins.HDSkinManager;

import net.minecraft.util.Session;

public abstract class AbstractSkinServer implements SkinServer {

    @Expose
    protected final String address;

    public AbstractSkinServer(String address) {
        this.address = address;
    }

    @Override
    public CompletableFuture<SkinUploadResponse> uploadSkin(Session session, SkinUpload upload) {
        return CallableFutures.asyncFailableFuture(() -> {
            return doUpload(session, upload);
        }, HDSkinManager.skinUploadExecutor);
    }

    public Map<Type, MinecraftProfileTexture> getProfileTextures(GameProfile profile) throws AuthenticationException, IOException {
        MinecraftTexturesPayload payload = loadProfileData(profile);

        if (payload != null && payload.getTextures() != null) {
            return payload.getTextures();
        }

        return Collections.emptyMap();
    }

    public Map<Type, MinecraftProfileTexture> getPreviewTextures(GameProfile profile) throws AuthenticationException, IOException {
        return getProfileTextures(profile);
    }

    @Override
    public void validate() throws JsonParseException {
        if (Strings.isBlank(address)) {
            throw new JsonParseException("Address was not specified.");
        }
    }

    protected abstract MinecraftTexturesPayload loadProfileData(GameProfile profile) throws IOException;

    protected abstract SkinUploadResponse doUpload(Session session, SkinUpload upload) throws AuthenticationException, IOException;

    @Override
    public String toString() {
        return new IndentedToStringStyle.Builder(this)
                .append("address", address)
                .build();
    }
}
