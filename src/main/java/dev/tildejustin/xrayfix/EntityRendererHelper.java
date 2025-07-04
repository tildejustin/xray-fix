package dev.tildejustin.xrayfix;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.*;

public class EntityRendererHelper {
    // WorldRenderer#render, in entityRenderDispatcher.shouldRender loop
    public static boolean otherwiseRendered(Entity entity) {
        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        return entity.hasPassengerDeep(MinecraftClient.getInstance().player) && (entity != camera.getFocusedEntity()
                || camera.isThirdPerson()
                || camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity) camera.getFocusedEntity()).isSleeping());
    }
}
