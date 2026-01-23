package dev.tildejustin.xrayfix.mixin;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.tildejustin.xrayfix.mixin.accessor.*;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Unique
    private static final Set<Block> ILLEGAL_BLOCKS = Sets.newHashSet(Blocks.COMPOSTER);

    @Unique
    private static EntityPose currPose = EntityPose.STANDING, lastPose = EntityPose.STANDING;


    @SuppressWarnings("deprecation")
    @Inject(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;pop()V", shift = At.Shift.AFTER))
    public void onRenderSight(MatrixStack matrices, Camera camera, float tickDelta, CallbackInfo ci) {
        if (this.client.player != null && this.client.player.noClip) return;

        BlockState target = null;
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        ClientPlayerEntity player = this.client.player;
        System.out.println(camera.getFocusedEntity().getStandingEyeHeight() + " " + ((CameraAccessor) camera).getCameraY());
        if (player != null && currPose != player.getPose()) {
            lastPose = currPose;
            currPose = player.getPose();
        }
        System.out.println(currPose.name() + " " + lastPose.name());
        if (player != null && (player.getPose() == EntityPose.SWIMMING || lastPose == EntityPose.SWIMMING) && ((CameraAccessor) camera).getCameraY() - camera.getFocusedEntity().getStandingEyeHeight() > 1e-4) {
            for (int i = 0; i < 8; i++) {
                double x = camera.getPos().getX() + (i % 2 - 0.5F) * camera.getFocusedEntity().getWidth() * 0.8F;
                double y = camera.getPos().getY() + ((i >> 1) % 2 - 0.5F) * 0.1F;
                double z = camera.getPos().getZ() + ((i >> 2) % 2 - 0.5F) * camera.getFocusedEntity().getWidth() * 0.8F;
                mutable.set(x, y, z);
                BlockState blockState = camera.getFocusedEntity().world.getBlockState(mutable);
                if (blockState.getRenderType() != BlockRenderType.INVISIBLE && blockState.shouldBlockVision(camera.getFocusedEntity().world, mutable)) {
                    target = blockState;
                    break;
                }
            }
        }

        BlockState cameraInside = camera.getFocusedEntity().world.getBlockState(new BlockPos(camera.getPos()));
        if (ILLEGAL_BLOCKS.contains(cameraInside.getBlock())) target = cameraInside;

        if (target != null) {
            RenderSystem.disableAlphaTest();
            InGameOverlayRendererAccessor.callRenderInWallOverlay(this.client, this.client.getBlockRenderManager().getModels().getSprite(target), matrices);
            RenderSystem.enableAlphaTest();
        }
    }
}
