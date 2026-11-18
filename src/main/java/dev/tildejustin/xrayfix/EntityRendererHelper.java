package dev.tildejustin.xrayfix;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.*;

public class EntityRendererHelper {
    // WorldRenderer#render, in entityRenderDispatcher.shouldRender loop
    public static boolean countedIfRendered(Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();

        if (entity.hasPassengerDeep(client.player)) return false;
        return (entity != camera.getFocusedEntity()
                || camera.isThirdPerson()
                || camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity) camera.getFocusedEntity()).isSleeping()) && (!(entity instanceof ClientPlayerEntity)
                || camera.getFocusedEntity() == entity);
    }
}
