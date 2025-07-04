package dev.tildejustin.xrayfix.mixin;

import dev.tildejustin.xrayfix.CameraExtension;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExtension {
    @Unique
    private double firstPersonY;

    @Shadow
    public abstract Vec3d getPos();

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = At.Shift.AFTER))
    private void save1stPersonYPos(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        this.firstPersonY = this.getPos().y;
    }

    @Override
    public double xray_fix$getFirstPersonY() {
        return firstPersonY;
    }
}
