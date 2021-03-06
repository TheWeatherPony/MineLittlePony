package com.minelittlepony.render.layer;

import com.minelittlepony.model.ponies.ModelSkeletonPony;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.util.ResourceLocation;

public class LayerPonyStrayOverlay extends LayerOverlayBase<EntityStray> {

    public static final ResourceLocation STRAY_SKELETON_OVERLAY = new ResourceLocation("minelittlepony", "textures/entity/skeleton/stray_pony_overlay.png");

    private final ModelSkeletonPony overlayModel = new ModelSkeletonPony();

    public LayerPonyStrayOverlay(RenderLivingBase<?> render) {
        super(render);
        overlayModel.init(0, 0.25F);
    }

    @Override
    protected ModelBase getOverlayModel() {
        return overlayModel;
    }

    @Override
    protected ResourceLocation getOverlayTexture() {
        return STRAY_SKELETON_OVERLAY;
    }
}
