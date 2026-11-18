package dev.tildejustin.xrayfix;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.*;
import net.minecraft.client.render.*;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;

import java.util.Set;

public class AntiXrayBlocks {
    private static final Set<Class<? extends Block>> SEE_THROUGH = ImmutableSet.of(AbstractGlassBlock.class, PaneBlock.class, SpawnerBlock.class, ScaffoldingBlock.class);
    private static final Set<Class<? extends Block>> NO_COLLISION_BLOCKERS = ImmutableSet.of(AbstractSignBlock.class, AbstractBannerBlock.class);
    // their inside faces all use cullface up with a block on top, might as well fix all of them
    public static final Set<Block> HOLLOW = ImmutableSet.of(Blocks.COMPOSTER, Blocks.CAULDRON, Blocks.HOPPER);

    public static boolean blocksView(BlockState state, BlockView world, BlockPos pos) {
        if (state.getRenderType() == BlockRenderType.INVISIBLE) return false;
        RenderLayer layer = RenderLayers.getBlockLayer(state);
        if (layer == RenderLayer.getTranslucent()) return false;
        Block block = state.getBlock();
        if (block instanceof LeavesBlock) return layer == RenderLayer.getSolid();
        if (SEE_THROUGH.stream().anyMatch(type -> type.isInstance(block))) return false;
        if (NO_COLLISION_BLOCKERS.stream().anyMatch(type -> type.isInstance(block))) return true;
        // otherwise tall grass, flowers and other walk-through blocks would black out the screen while standing in them
        return !state.getCollisionShape(world, pos).isEmpty();
    }

    public static Vec3d getFluidFogEye(BlockView world, Vec3d camera) {
        BlockPos cameraPos = new BlockPos(camera);
        BlockState state = world.getBlockState(cameraPos);
        if (!blocksView(state, world, cameraPos)) return null;
        for (Box box : state.getOutlineShape(world, cameraPos).getBoundingBoxes()) {
            box = box.offset(cameraPos);
            if (!box.contains(camera)) continue;
            Direction nearestFace = null;
            double nearestDistance = Double.MAX_VALUE;
            for (Direction face : Direction.values()) {
                Direction.Axis axis = face.getAxis();
                double distance = face.getDirection() == Direction.AxisDirection.POSITIVE ? box.getMax(axis) - camera.getComponentAlongAxis(axis) : camera.getComponentAlongAxis(axis) - box.getMin(axis);
                if (distance < nearestDistance) {
                    nearestFace = face;
                    nearestDistance = distance;
                }
            }
            if (nearestDistance > 0.2) return null;
            Vec3d outside = camera.add(Vec3d.of(nearestFace.getVector()).multiply(nearestDistance + 1e-3));
            BlockPos outsidePos = new BlockPos(outside);
            BlockState outsideState = world.getBlockState(outsidePos);
            if (blocksView(outsideState, world, outsidePos) && outsideState.getOutlineShape(world, outsidePos).getBoundingBoxes().stream().anyMatch(outsideBox -> outsideBox.offset(outsidePos).contains(outside))) return null;
            return world.getFluidState(outsidePos).isEmpty() ? null : outside;
        }
        return null;
    }
}
