package dev.tildejustin.xrayfix.mixin;

import dev.tildejustin.xrayfix.AntiXrayBlocks;
import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true)
    private static void drawHollowInside(BlockState state, BlockView world, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (facing == Direction.UP && AntiXrayBlocks.HOLLOW.contains(state.getBlock())) cir.setReturnValue(true);
    }
}
