package dev.tildejustin.xrayfix.mixin;

import dev.tildejustin.xrayfix.AntiXrayBlocks;
import me.jellysquid.mods.sodium.client.render.occlusion.BlockOcclusionCache;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = BlockOcclusionCache.class, remap = false)
public abstract class BlockOcclusionCacheMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private void drawHollowInside(BlockState state, BlockView world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (facing == Direction.UP && AntiXrayBlocks.HOLLOW.contains(state.getBlock())) cir.setReturnValue(true);
    }
}
