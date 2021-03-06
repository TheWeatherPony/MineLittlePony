package com.minelittlepony.render.skull;

import com.minelittlepony.PonyConfig;
import com.minelittlepony.model.components.ModelDeadMau5Ears;
import com.minelittlepony.pony.data.Pony;
import com.minelittlepony.pony.data.PonyLevel;
import com.minelittlepony.render.PonySkull;
import com.minelittlepony.render.RenderPony;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.voxelmodpack.hdskins.HDSkinManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import javax.annotation.Nullable;

import static com.mojang.authlib.minecraft.MinecraftProfileTexture.*;

public class PlayerSkullRenderer extends PonySkull {

    private final ModelDeadMau5Ears deadMau5 = new ModelDeadMau5Ears();

    @Override
    public boolean canRender(PonyConfig config) {
        return config.getPonyLevel() != PonyLevel.HUMANS;
    }

    @Override
    public void preRender(boolean transparency) {
        GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);

        if (!transparency) {
            RenderPony.enableModelRenderProfile();
        }
    }

    @Override
    public ResourceLocation getSkinResource(@Nullable GameProfile profile) {
        deadMau5.setVisible(profile != null && "deadmau5".equals(profile.getName()));

        if (profile != null) {
            ResourceLocation skin = HDSkinManager.INSTANCE.getTextures(profile).get(Type.SKIN);
            if (skin != null && Pony.getBufferedImage(skin) != null) {
                return skin;
            }

            Minecraft minecraft = Minecraft.getMinecraft();
            Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

            if (map.containsKey(Type.SKIN)) {
                ResourceLocation loc = minecraft.getSkinManager().loadSkin(map.get(Type.SKIN), Type.SKIN);
                if (Pony.getBufferedImage(loc) != null) {
                    return loc;
                }
            }
            return DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getUUID(profile));

        }

        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    @Override
    public void render(float animateTicks, float rotation, float scale) {
        super.render(animateTicks, rotation, scale);
        deadMau5.render(null, animateTicks, 0, 0, rotation, 0, scale);
    }
}
