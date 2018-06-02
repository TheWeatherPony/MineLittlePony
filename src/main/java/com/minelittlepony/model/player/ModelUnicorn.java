package com.minelittlepony.model.player;

import com.minelittlepony.model.components.UnicornHorn;
import com.minelittlepony.render.PonyRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

import com.minelittlepony.model.capabilities.IModelUnicorn;

import static com.minelittlepony.model.PonyModelConstants.*;

/**
 * Used for both unicorns and alicorns since there's no logical way to keep them distinct and not duplicate stuff.
 */
public class ModelUnicorn extends ModelEarthPony implements IModelUnicorn {

    public PonyRenderer unicornArmRight;
    public PonyRenderer unicornArmLeft;

    public UnicornHorn horn;

    public ModelUnicorn(boolean smallArms) {
        super(smallArms);
    }

    @Override
    public void init(float yOffset, float stretch) {
        super.init(yOffset, stretch);
        horn = new UnicornHorn(this, yOffset, stretch);
    }

    @Override
    protected void rotateLegsOnGround(float move, float swing, float ticks, Entity entity) {
        super.rotateLegsOnGround(move, swing, ticks, entity);

        unicornArmRight.rotateAngleY = 0;
        unicornArmLeft.rotateAngleY = 0;
    }

    @Override
    protected void adjustLegs(float move, float swing, float ticks) {
        super.adjustLegs(move, swing, ticks);

        unicornArmLeft.rotateAngleZ = 0;
        unicornArmRight.rotateAngleZ = 0;

        unicornArmLeft.rotateAngleX = 0;
        unicornArmRight.rotateAngleX = 0;
    }

    @Override
    protected void holdItem(float swing) {
        if (canCast()) {
            boolean both = leftArmPose == ArmPose.ITEM && rightArmPose == ArmPose.ITEM;

            alignArmForAction(unicornArmLeft, leftArmPose, both, swing);
            alignArmForAction(unicornArmRight, rightArmPose, both, swing);
        } else {
            super.holdItem(swing);
        }
    }

    @Override
    protected void swingItem(Entity entity) {
        EnumHandSide mainSide = getMainHand(entity);

        if (canCast() && getArmPoseForSide(mainSide) != ArmPose.EMPTY) {
            if (swingProgress > -9990.0F && !isSleeping) {
                swingArm(getUnicornArmForSide(mainSide));
            }
        } else {
            super.swingItem(entity);
        }
    }

    @Override
    protected void swingArms(float ticks) {
        if (isSleeping) return;

        if (canCast()) {
            float cos = MathHelper.cos(ticks * 0.09F) * 0.05F + 0.05F;
            float sin = MathHelper.sin(ticks * 0.067F) * 0.05F;

            if (rightArmPose != ArmPose.EMPTY) {
                unicornArmRight.rotateAngleZ += cos;
                unicornArmRight.rotateAngleX += sin;
            }

            if (leftArmPose != ArmPose.EMPTY) {
                unicornArmLeft.rotateAngleZ += cos;
                unicornArmLeft.rotateAngleX += sin;
            }
        } else {
            super.swingArms(ticks);
        }
    }

    @Override
    public PonyRenderer getUnicornArmForSide(EnumHandSide side) {
        return side == EnumHandSide.LEFT ? unicornArmLeft : unicornArmRight;
    }

    @Override
    public boolean canCast() {
        return metadata.hasMagic();
    }

    @Override
    public boolean isCasting() {
        return rightArmPose != ArmPose.EMPTY || leftArmPose != ArmPose.EMPTY;
    }

    @Override
    public int getMagicColor() {
        return metadata.getGlowColor();
    }

    @Override
    protected void sneakLegs() {
        super.sneakLegs();
        unicornArmRight.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
        unicornArmLeft.rotateAngleX += SNEAK_LEG_X_ROTATION_ADJUSTMENT;
    }

    @Override
    protected void aimBow(ArmPose leftArm, ArmPose rightArm, float ticks) {
        if (canCast()) {
            if (rightArm == ArmPose.BOW_AND_ARROW) aimBowPony(unicornArmRight, ticks);
            if (leftArm == ArmPose.BOW_AND_ARROW) aimBowPony(unicornArmLeft, ticks);
        } else {
            super.aimBow(leftArm, rightArm, ticks);
        }
    }

    @Override
    protected void renderHead(Entity entity, float move, float swing, float ticks, float headYaw, float headPitch, float scale) {
        super.renderHead(entity, move, swing, ticks, headYaw, headPitch, scale);

        if (canCast()) {
            horn.render(scale);
            if (isCasting()) {
                horn.renderMagic(getMagicColor(), scale);
            }
        }
    }

    @Override
    protected void initLegTextures() {
        super.initLegTextures();
        unicornArmLeft = new PonyRenderer(this, 40, 32).size(64, 64);
        unicornArmRight = new PonyRenderer(this, 40, 32).size(64, 64);
    }

    @Override
    protected void initLegPositions(float yOffset, float stretch) {
        super.initLegPositions(yOffset, stretch);
        float armY = THIRDP_ARM_CENTRE_Y - 6;
        float armZ = THIRDP_ARM_CENTRE_Z - 2;

        unicornArmLeft .box(FIRSTP_ARM_CENTRE_X - 2, armY, armZ, 4, 12, 4, stretch + .25F).around(5, yOffset + 2, 0);
        unicornArmRight.box(FIRSTP_ARM_CENTRE_X - 2, armY, armZ, 4, 12, 4, stretch + .25F).around(-5, yOffset + 2, 0);
    }
}