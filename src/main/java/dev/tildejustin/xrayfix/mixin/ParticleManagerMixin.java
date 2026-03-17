package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tildejustin.xrayfix.ParticleCullingProvider;
import dev.tildejustin.xrayfix.ParticleManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Shadow protected ClientWorld world;

    @SuppressWarnings("MixinExtrasOperationParameters")
    @WrapOperation(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    public void onRenderParticle(Particle instance, VertexConsumer vertexConsumer, Camera camera, float v, Operation<Void> original) {
        if (!((ParticleCullingProvider) instance).xray_fix$shouldCull()) {
            original.call(instance, vertexConsumer, camera, v);
        }
    }

    @Inject(method = "tickParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;tick()V", shift = At.Shift.AFTER))
    public void onTickParticle(Particle particle, CallbackInfo ci) {
        ((ParticleCullingProvider) particle).xray_fix$setCull(ParticleManagerHelper.shouldCull(this.world, particle, MinecraftClient.getInstance().gameRenderer.getCamera()));
    }

}
