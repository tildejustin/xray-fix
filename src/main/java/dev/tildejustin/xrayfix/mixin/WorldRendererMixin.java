package dev.tildejustin.xrayfix.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.*;
import net.minecraft.util.profiler.Profiler;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render",at = @At("TAIL"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        if(client.player == null || client.player.noClip || camera.getFocusedEntity().world == null) return;
        Profiler profiler = client.getProfiler();
        profiler.swap("terrain");
        Matrix4f matrix4f2 = matrix4f.copy();
        Matrix4f modelMatrix = matrices.peek().getModel();
        matrix4f2.multiply(modelMatrix);
        matrix4f2.invert();
        Vec3d camPos = camera.getPos();
        Vector4f[] frustumCorners = new Vector4f[4];
        frustumCorners[0] = new Vector4f(-1, -1, -1.0F, 1.0F);
        frustumCorners[1] = new Vector4f(1, -1, -1.0F, 1.0F);
        frustumCorners[2] = new Vector4f(1, 1, -1.0F, 1.0F);
        frustumCorners[3] = new Vector4f(-1, 1, -1.0F, 1.0F);
        Vec3d[] cornerPos = new Vec3d[4];
        for (int i = 0; i < 4; ++i) {
            frustumCorners[i].transform(matrix4f2);
            frustumCorners[i].normalizeProjectiveCoordinates();
            cornerPos[i] = camPos.add(frustumCorners[i].getX(), frustumCorners[i].getY(), frustumCorners[i].getZ());
        }
        if(Double.isNaN(cornerPos[0].x)) return;
        double minX = Math.min(Math.min(cornerPos[0].x,cornerPos[1].x),Math.min(cornerPos[2].x,cornerPos[3].x));
        double maxX = Math.max(Math.max(cornerPos[0].x,cornerPos[1].x),Math.max(cornerPos[2].x,cornerPos[3].x));
        double minY = Math.min(Math.min(cornerPos[0].y,cornerPos[1].y),Math.min(cornerPos[2].y,cornerPos[3].y));
        double maxY = Math.max(Math.max(cornerPos[0].y,cornerPos[1].y),Math.max(cornerPos[2].y,cornerPos[3].y));
        double minZ = Math.min(Math.min(cornerPos[0].z,cornerPos[1].z),Math.min(cornerPos[2].z,cornerPos[3].z));
        double maxZ = Math.max(Math.max(cornerPos[0].z,cornerPos[1].z),Math.max(cornerPos[2].z,cornerPos[3].z));
        Set<BlockPos> visibleBlocks = getBlocks(minX,minY,minZ,maxX,maxY,maxZ,camera);
        if(visibleBlocks.isEmpty()) return;
        RenderSystem.disableTexture();
        RenderSystem.disableCull();
        RenderSystem.enablePolygonOffset();
        RenderSystem.polygonOffset(0.1f, 0.1f);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        for (BlockPos pos : visibleBlocks) {
            drawFilledBox(matrices,buffer, pos, camera.getPos(),0,0,0);
        }
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        RenderSystem.disablePolygonOffset();
        RenderSystem.polygonOffset(0,0);
        profiler.swap("entities");
    }

    @Unique
    private void drawFilledBox(MatrixStack matrices, VertexConsumer vertexConsumer, BlockPos pos, Vec3d camPos, float red, float green, float blue) {
        double x = pos.getX() - camPos.x;
        double y = pos.getY() - camPos.y;
        double z = pos.getZ() - camPos.z;
        Matrix4f matrix4f = matrices.peek().getModel();
        float x1 = (float) x;
        float y1 = (float) y;
        float z1 = (float) z;
        float x2 = (float) x+1;
        float y2 = (float) y+1;
        float z2 = (float) z+1;
        vertexConsumer.vertex(matrix4f, x1, y1, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y1, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y1, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y1, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y2, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y2, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y2, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y2, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y1, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y2, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y2, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y1, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y1, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y1, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y2, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y2, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y1, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y1, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y2, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x1, y2, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y1, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y2, z1).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y2, z2).color(red, green, blue,1f).next();
        vertexConsumer.vertex(matrix4f, x2, y1, z2).color(red, green, blue,1f).next();
    }

    @Unique
    private Set<BlockPos> getBlocks(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Camera camera) {
        Set<BlockPos> blocks = new HashSet<>();
        int x1 = MathHelper.floor(minX);
        int y1 = MathHelper.floor(minY);
        int z1 = MathHelper.floor(minZ);
        int x2 = MathHelper.floor(maxX);
        int y2 = MathHelper.floor(maxY);
        int z2 = MathHelper.floor(maxZ);
        for(int i = x1; i <= x2; ++i) {
            for (int j = y1; j <= y2; ++j) {
                for (int q = z1; q <= z2; ++q) {
                    BlockPos blockPos = new BlockPos(i, j, q);
                    BlockState blockState = client.world.getBlockState(blockPos);
                    if((blockState.isSolidBlock(camera.getFocusedEntity().world,blockPos) || blockState.getBlock() == Blocks.COMPOSTER)) {
                        for (Direction direction : Direction.values()) {
                            BlockPos surrounding = blockPos.offset(direction);
                            blockState = client.world.getBlockState(surrounding);
                            if((blockState.isSolidBlock(camera.getFocusedEntity().world,blockPos) || blockState.getBlock() == Blocks.COMPOSTER)) {
                                blocks.add(surrounding);
                            }
                        }
                    }
                }
            }
        }
        return blocks;
    }
}
