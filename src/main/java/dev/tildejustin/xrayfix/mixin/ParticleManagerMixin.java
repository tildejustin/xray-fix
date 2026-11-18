package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import dev.tildejustin.xrayfix.ParticleManagerHelper;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @WrapWithCondition(method = "renderParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/Particle;buildGeometry(Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/Camera;F)V"))
    public boolean onRenderParticle(Particle instance, VertexConsumer vertexConsumer, Camera camera, float v) {
        return !ParticleManagerHelper.shouldCull(instance);
    }
}
