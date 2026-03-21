package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.tildejustin.xrayfix.ParticleCullingProvider;
import dev.tildejustin.xrayfix.ParticleManagerHelper;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @WrapWithCondition(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    public boolean onRenderParticle(Particle instance, VertexConsumer vertexConsumer, Camera camera, float v) {
        return !((ParticleCullingProvider) instance).xray_fix$shouldCull();
    }

    @Inject(method = "tickParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;tick()V", shift = At.Shift.AFTER))
    public void onTickParticle(Particle particle, CallbackInfo ci) {
        ((ParticleCullingProvider) particle).xray_fix$setCull(ParticleManagerHelper.shouldCull(particle));
    }

}
