package dev.tildejustin.xrayfix.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @ModifyExpressionValue(method = "renderHand", at = @At(value = "FIELD", target = "Lnet/minecraft/client/options/GameOptions;perspective:I"))
    private int doPerspectiveCheckLater(int original) {
        return 0;
    }
}
