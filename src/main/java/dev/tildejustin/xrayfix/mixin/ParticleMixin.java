package dev.tildejustin.xrayfix.mixin;

import dev.tildejustin.xrayfix.ParticleCullingProvider;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Particle.class)
public class ParticleMixin implements ParticleCullingProvider {

    @Unique
    private boolean cull = true;

    @Override
    public boolean xray_fix$shouldCull() {
        return this.cull;
    }

    @Override
    public void xray_fix$setCull(boolean cull) {
        this.cull = cull;
    }
}
