package dev.tildejustin.xrayfix.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tildejustin.xrayfix.AntiXrayBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;

    @SuppressWarnings("deprecation")
    @Inject(method = "render",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderChunkDebugInfo(Lnet/minecraft/client/render/Camera;)V"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        if(client.player == null || client.player.noClip || camera.getFocusedEntity().world == null) return;
        Matrix4f viewProjection = matrix4f.copy();
        viewProjection.multiply(matrices.peek().getModel());
        Matrix4f inverse = viewProjection.copy();
        inverse.invert();
        Vec3d camPos = camera.getPos();
        Vector4f[] frustumCorners = new Vector4f[4];
        frustumCorners[0] = new Vector4f(-1, -1, -1.0F, 1.0F);
        frustumCorners[1] = new Vector4f(1, -1, -1.0F, 1.0F);
        frustumCorners[2] = new Vector4f(1, 1, -1.0F, 1.0F);
        frustumCorners[3] = new Vector4f(-1, 1, -1.0F, 1.0F);
        Vec3d[] cornerPos = new Vec3d[4];
        for (int i = 0; i < 4; ++i) {
            frustumCorners[i].transform(inverse);
            frustumCorners[i].normalizeProjectiveCoordinates();
            cornerPos[i] = camPos.add(frustumCorners[i].getX(), frustumCorners[i].getY(), frustumCorners[i].getZ());
        }
        if(Double.isNaN(cornerPos[0].x)) return;
        double minX = Math.min(camPos.x, Math.min(Math.min(cornerPos[0].x,cornerPos[1].x),Math.min(cornerPos[2].x,cornerPos[3].x)));
        double maxX = Math.max(camPos.x, Math.max(Math.max(cornerPos[0].x,cornerPos[1].x),Math.max(cornerPos[2].x,cornerPos[3].x)));
        double minY = Math.min(camPos.y, Math.min(Math.min(cornerPos[0].y,cornerPos[1].y),Math.min(cornerPos[2].y,cornerPos[3].y)));
        double maxY = Math.max(camPos.y, Math.max(Math.max(cornerPos[0].y,cornerPos[1].y),Math.max(cornerPos[2].y,cornerPos[3].y)));
        double minZ = Math.min(camPos.z, Math.min(Math.min(cornerPos[0].z,cornerPos[1].z),Math.min(cornerPos[2].z,cornerPos[3].z)));
        double maxZ = Math.max(camPos.z, Math.max(Math.max(cornerPos[0].z,cornerPos[1].z),Math.max(cornerPos[2].z,cornerPos[3].z)));
        Set<BlockPos> nearBlocks = getBlocks(minX,minY,minZ,maxX,maxY,maxZ);
        if(nearBlocks.isEmpty()) return;

        List<List<float[]>> shadows = new ArrayList<>();
        for (BlockPos pos : nearBlocks) {
            double relX = pos.getX() - camPos.x;
            double relY = pos.getY() - camPos.y;
            double relZ = pos.getZ() - camPos.z;
            client.world.getBlockState(pos).getOutlineShape(client.world, pos).forEachBox((boxMinX, boxMinY, boxMinZ, boxMaxX, boxMaxY, boxMaxZ) -> {
                List<float[]> shadow = getShadow(viewProjection, relX + boxMinX, relY + boxMinY, relZ + boxMinZ, relX + boxMaxX, relY + boxMaxY, relZ + boxMaxZ);
                if (shadow != null) shadows.add(shadow);
            });
        }
        if (shadows.isEmpty()) return;

        RenderSystem.disableTexture();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, VertexFormats.POSITION_COLOR);
        for (List<float[]> shadow : shadows) {
            float[] first = shadow.get(0);
            for (int corner = 1; corner < shadow.size() - 1; corner++) {
                float[] current = shadow.get(corner), next = shadow.get(corner + 1);
                buffer.vertex(first[0], first[1], 0).color(0, 0, 0, 1f).next();
                buffer.vertex(current[0], current[1], 0).color(0, 0, 0, 1f).next();
                buffer.vertex(next[0], next[1], 0).color(0, 0, 0, 1f).next();
            }
        }
        tessellator.draw();
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL11.GL_PROJECTION);
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(GL11.GL_MODELVIEW);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.enableTexture();
    }

    // screen area covered by the part of the box between the eye and the near plane, as a convex polygon
    @Unique
    private static List<float[]> getShadow(Matrix4f viewProjection, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        if (minX <= 0 && maxX >= 0 && minY <= 0 && maxY >= 0 && minZ <= 0 && maxZ >= 0) {
            return Arrays.asList(new float[]{-1, -1}, new float[]{1, -1}, new float[]{1, 1}, new float[]{-1, 1});
        }
        Vector4f[] corners = new Vector4f[8];
        for (int corner = 0; corner < 8; corner++) {
            corners[corner] = new Vector4f((float) ((corner & 1) == 0 ? minX : maxX), (float) ((corner & 2) == 0 ? minY : maxY), (float) ((corner & 4) == 0 ? minZ : maxZ), 1);
            corners[corner].transform(viewProjection);
        }
        // in clip space w is the view depth and z + w crosses zero at the near plane
        float minDepth = 1e-4f;
        List<float[]> points = new ArrayList<>();
        for (Vector4f corner : corners) {
            if (corner.getW() >= minDepth && corner.getZ() + corner.getW() <= 0) points.add(project(corner));
        }
        for (int corner = 0; corner < 8; corner++) {
            for (int axisBit = 1; axisBit < 8; axisBit <<= 1) {
                if ((corner & axisBit) != 0) continue;
                Vector4f start = corners[corner], end = corners[corner | axisBit];
                float startPastNear = start.getZ() + start.getW(), endPastNear = end.getZ() + end.getW();
                if ((startPastNear < 0) != (endPastNear < 0)) {
                    Vector4f crossing = lerp(start, end, startPastNear / (startPastNear - endPastNear));
                    if (crossing.getW() >= minDepth) points.add(project(crossing));
                }
                float startPastEye = start.getW() - minDepth, endPastEye = end.getW() - minDepth;
                if ((startPastEye < 0) != (endPastEye < 0)) {
                    Vector4f crossing = lerp(start, end, startPastEye / (startPastEye - endPastEye));
                    if (crossing.getZ() + crossing.getW() <= 0) points.add(project(crossing));
                }
            }
        }
        if (points.size() < 3) return null;
        List<float[]> hull = convexHull(points);
        return hull.size() < 3 ? null : hull;
    }

    @Unique
    private static Vector4f lerp(Vector4f start, Vector4f end, float fraction) {
        return new Vector4f(
                start.getX() + (end.getX() - start.getX()) * fraction,
                start.getY() + (end.getY() - start.getY()) * fraction,
                start.getZ() + (end.getZ() - start.getZ()) * fraction,
                start.getW() + (end.getW() - start.getW()) * fraction);
    }

    @Unique
    private static float[] project(Vector4f clipPos) {
        return new float[]{clipPos.getX() / clipPos.getW(), clipPos.getY() / clipPos.getW()};
    }

    @Unique
    private static List<float[]> convexHull(List<float[]> points) {
        points.sort(Comparator.<float[]>comparingDouble(point -> point[0]).thenComparingDouble(point -> point[1]));
        float[][] hull = new float[points.size() * 2][];
        int hullSize = 0;
        for (float[] point : points) {
            while (hullSize >= 2 && cross(hull[hullSize - 2], hull[hullSize - 1], point) <= 0) hullSize--;
            hull[hullSize++] = point;
        }
        for (int i = points.size() - 2, upperStart = hullSize + 1; i >= 0; i--) {
            float[] point = points.get(i);
            while (hullSize >= upperStart && cross(hull[hullSize - 2], hull[hullSize - 1], point) <= 0) hullSize--;
            hull[hullSize++] = point;
        }

        return Arrays.asList(hull).subList(0, hullSize - 1);
    }

    @Unique
    private static float cross(float[] origin, float[] first, float[] second) {
        return (first[0] - origin[0]) * (second[1] - origin[1]) - (first[1] - origin[1]) * (second[0] - origin[0]);
    }

    @Unique
    private Set<BlockPos> getBlocks(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Set<BlockPos> blocks = new HashSet<>();
        int x1 = MathHelper.floor(minX);
        int y1 = MathHelper.floor(minY);
        int z1 = MathHelper.floor(minZ);
        int x2 = MathHelper.floor(maxX);
        int y2 = MathHelper.floor(maxY);
        int z2 = MathHelper.floor(maxZ);
        for(int x = x1; x <= x2; ++x) {
            for (int y = y1; y <= y2; ++y) {
                for (int z = z1; z <= z2; ++z) {
                    BlockPos blockPos = new BlockPos(x, y, z);
                    BlockState blockState = client.world.getBlockState(blockPos);
                    if(AntiXrayBlocks.blocksView(blockState, client.world, blockPos)) {
                        blocks.add(blockPos);
                    }
                }
            }
        }
        return blocks;
    }
}
