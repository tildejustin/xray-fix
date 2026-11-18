package dev.tildejustin.xrayfix.mixin;

import dev.tildejustin.xrayfix.AntiXrayBlocks;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow private BlockView area;
    @Shadow private Entity focusedEntity;
    @Shadow private Vec3d pos;
    @Shadow @Final private Vector3f horizontalPlane;

    @Inject(method = "clipToSpace", at = @At("RETURN"), cancellable = true)
    private void castCenterRay(double distance, CallbackInfoReturnable<Double> cir) {
        double vanillaDistance = cir.getReturnValue();
        Vec3d vanillaCamera = new Vec3d(pos.x - horizontalPlane.getX() * vanillaDistance, pos.y - horizontalPlane.getY() * vanillaDistance, pos.z - horizontalPlane.getZ() * vanillaDistance);
        if (AntiXrayBlocks.getFluidFogEye(area, vanillaCamera) != null) return;
        HitResult hit = area.rayTrace(new RayTraceContext(pos, vanillaCamera, RayTraceContext.ShapeType.VISUAL, RayTraceContext.FluidHandling.NONE, focusedEntity));
        if (hit.getType() != HitResult.Type.MISS) cir.setReturnValue(Math.min(vanillaDistance, Math.max(0, hit.getPos().distanceTo(pos) - 0.01)));
    }
}
