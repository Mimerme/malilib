package fi.dy.masa.malilib.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class PositionUtils
{
    public static final EnumFacing[] ALL_DIRECTIONS = new EnumFacing[] { EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };
    public static final EnumFacing[] HORIZONTAL_DIRECTIONS = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };
    public static final EnumFacing[] VERTICAL_DIRECTIONS = new EnumFacing[] { EnumFacing.DOWN, EnumFacing.UP };
    public static final Mirror[] MIRROR_VALUES = Mirror.values();
    public static final Rotation[] ROTATION_VALUES = Rotation.values();

    public static final int SIZE_BITS_X = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
    public static final int SIZE_BITS_Z = SIZE_BITS_X;
    public static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
    public static final long BITMASK_X = (1L << SIZE_BITS_X) - 1L;
    public static final long BITMASK_Y = (1L << SIZE_BITS_Y) - 1L;
    public static final long BITMASK_Z = (1L << SIZE_BITS_Z) - 1L;
    public static final int BIT_SHIFT_Z = 0;
    public static final int BIT_SHIFT_Y = SIZE_BITS_Z;
    public static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;

    public static long blockPosToLong(int x, int y, int z)
    {
        return (((long) x & BITMASK_X) << BIT_SHIFT_X) | (((long) y & BITMASK_Y) << BIT_SHIFT_Y) | (((long) z & BITMASK_Z) << BIT_SHIFT_Z);
    }

    public static int unpackX(long packedPos)
    {
        return (int) (packedPos << (64 - BIT_SHIFT_X - SIZE_BITS_X) >> (64 - SIZE_BITS_X));
    }

    public static int unpackY(long packedPos)
    {
        return (int) (packedPos << (64 - BIT_SHIFT_Y - SIZE_BITS_Y) >> (64 - SIZE_BITS_Y));
    }

    public static int unpackZ(long packedPos)
    {
        return (int) (packedPos << (64 - BIT_SHIFT_Z - SIZE_BITS_Z) >> (64 - SIZE_BITS_Z));
    }

    public static int getPackedChunkRelativePosition(BlockPos pos)
    {
        return (pos.getY() << 8) | ((pos.getZ() & 0xF) << 4) | (pos.getX() & 0xF);
    }

    public static long getPackedAbsolutePosition(long chunkPos, int chunkRelativeBlockPos)
    {
        int chunkX = (int) chunkPos;
        int chunkZ = (int) (chunkPos >> 32L);
        int x = (chunkX << 4) + (chunkRelativeBlockPos & 0xF);
        int y = chunkRelativeBlockPos >> 8;
        int z = (chunkZ << 4) + ((chunkRelativeBlockPos >> 4) & 0xF);

        return blockPosToLong(x, y, z);
    }

    public static ChunkPos chunkPosFromLong(long posLong)
    {
        return new ChunkPos((int) posLong, (int) (posLong >> 32));
    }

    public static BlockPos getMinCorner(BlockPos pos1, BlockPos pos2)
    {
        return new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
    }

    public static BlockPos getMaxCorner(BlockPos pos1, BlockPos pos2)
    {
        return new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }

    public static boolean isPositionInsideArea(BlockPos pos, BlockPos posMin, BlockPos posMax)
    {
        return pos.getX() >= posMin.getX() && pos.getX() <= posMax.getX() &&
               pos.getY() >= posMin.getY() && pos.getY() <= posMax.getY() &&
               pos.getZ() >= posMin.getZ() && pos.getZ() <= posMax.getZ();
    }

    public static Rotation cycleRotation(Rotation rotation, boolean reverse)
    {
        int ordinal = rotation.ordinal();

        if (reverse)
        {
            ordinal = ordinal == 0 ? ROTATION_VALUES.length - 1 : ordinal - 1;
        }
        else
        {
            ordinal = ordinal >= ROTATION_VALUES.length - 1 ? 0 : ordinal + 1;
        }

        return ROTATION_VALUES[ordinal];
    }

    public static Mirror cycleMirror(Mirror mirror, boolean reverse)
    {
        int ordinal = mirror.ordinal();

        if (reverse)
        {
            ordinal = ordinal == 0 ? MIRROR_VALUES.length - 1 : ordinal - 1;
        }
        else
        {
            ordinal = ordinal >= MIRROR_VALUES.length - 1 ? 0 : ordinal + 1;
        }

        return MIRROR_VALUES[ordinal];
    }

    public static EnumFacing cycleDirection(EnumFacing direction, boolean reverse)
    {
        int index = direction.getIndex();

        if (reverse)
        {
            index = index == 0 ? 5 : index - 1;
        }
        else
        {
            index = index >= 5 ? 0 : index + 1;
        }

        return EnumFacing.byIndex(index);
    }

    /**
     * Returns the closest direction the given entity is looking towards,
     * with a vertical/pitch threshold of 60 degrees.
     */
    public static EnumFacing getClosestLookingDirection(Entity entity)
    {
        return getClosestLookingDirection(entity, 60);
    }

    /**
     * Returns the closest direction the given entity is looking towards.
     * @param verticalThreshold the pitch threshold to return the up or down facing instead of horizontals
     */
    public static EnumFacing getClosestLookingDirection(Entity entity, float verticalThreshold)
    {
        if (entity.rotationPitch >= verticalThreshold)
        {
            return EnumFacing.DOWN;
        }
        else if (entity.rotationPitch <= -verticalThreshold)
        {
            return EnumFacing.UP;
        }

        return entity.getHorizontalFacing();
    }

    /**
     * Returns the closest side direction to the entity's yaw facing that is 90 degrees from the entity's forward direction
     */
    public static EnumFacing getClosestSideDirection(Entity entity)
    {
        EnumFacing forwardDirection = entity.getHorizontalFacing();
        float entityYaw = ((entity.rotationYaw % 360.0F) + 360.0F) % 360.0F;
        float forwardYaw = forwardDirection.getHorizontalAngle();

        if (entityYaw < forwardYaw || (forwardYaw == 0.0F && entityYaw > 270.0F))
        {
            return forwardDirection.rotateYCCW();
        }
        else
        {
            return forwardDirection.rotateY();
        }
    }

    /**
     * Returns the closest block position directly infront of the
     * given entity that is not colliding with it.
     */
    public static BlockPos getPositionInfrontOfEntity(Entity entity)
    {
        return getPositionInfrontOfEntity(entity, 60);
    }

    /**
     * Returns the closest block position directly infront of the
     * given entity that is not colliding with it.
     */
    public static BlockPos getPositionInfrontOfEntity(Entity entity, float verticalThreshold)
    {
        BlockPos pos = new BlockPos(entity.posX, entity.posY, entity.posZ);

        if (entity.rotationPitch >= verticalThreshold)
        {
            return pos.down();
        }
        else if (entity.rotationPitch <= -verticalThreshold)
        {
            return new BlockPos(entity.posX, Math.ceil(entity.getEntityBoundingBox().maxY), entity.posZ);
        }

        double y = Math.floor(entity.posY + entity.getEyeHeight());

        switch (entity.getHorizontalFacing())
        {
            case EAST:
                return new BlockPos((int) Math.ceil( entity.posX + entity.width / 2),     (int) y, (int) Math.floor(entity.posZ));
            case WEST:
                return new BlockPos((int) Math.floor(entity.posX - entity.width / 2) - 1, (int) y, (int) Math.floor(entity.posZ));
            case SOUTH:
                return new BlockPos((int) Math.floor(entity.posX), (int) y, (int) Math.ceil( entity.posZ + entity.width / 2)    );
            case NORTH:
                return new BlockPos((int) Math.floor(entity.posX), (int) y, (int) Math.floor(entity.posZ - entity.width / 2) - 1);
            default:
        }

        return pos;
    }

    /**
     * Get the rotation that will go from facingOriginal to facingRotated, if possible.
     * If it's not possible to rotate between the given facings
     * (at least one of them is vertical, but they are not the same), then null is returned.
     */
    @Nullable
    public static Rotation getRotation(EnumFacing directionFrom, EnumFacing directionTo)
    {
        if (directionFrom == directionTo)
        {
            return Rotation.NONE;
        }

        if (directionFrom.getAxis() == EnumFacing.Axis.Y || directionTo.getAxis() == EnumFacing.Axis.Y)
        {
            return null;
        }

        if (directionTo == directionFrom.getOpposite())
        {
            return Rotation.CLOCKWISE_180;
        }

        return directionTo == directionFrom.rotateY() ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90;
    }

    /**
     * Returns the hit vector at the center point of the given side/face of the given block position.
     */
    public static Vec3d getHitVecCenter(BlockPos basePos, EnumFacing facing)
    {
        int x = basePos.getX();
        int y = basePos.getY();
        int z = basePos.getZ();

        switch (facing)
        {
            case UP:    return new Vec3d(x + 0.5, y + 1  , z + 0.5);
            case DOWN:  return new Vec3d(x + 0.5, y      , z + 0.5);
            case NORTH: return new Vec3d(x + 0.5, y + 0.5, z      );
            case SOUTH: return new Vec3d(x + 0.5, y + 0.5, z + 1  );
            case WEST:  return new Vec3d(x      , y + 0.5, z      );
            case EAST:  return new Vec3d(x + 1  , y + 0.5, z + 1);
            default:    return new Vec3d(x, y, z);
        }
    }

    /**
     * Returns the part of the block face the player is currently targeting.
     * The block face is divided into four side segments and a center segment.
     */
    public static HitPart getHitPart(EnumFacing originalSide, EnumFacing playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        Vec3d positions = getHitPartPositions(originalSide, playerFacingH, pos, hitVec);
        double posH = positions.x;
        double posV = positions.y;
        double offH = Math.abs(posH - 0.5d);
        double offV = Math.abs(posV - 0.5d);

        if (offH > 0.25d || offV > 0.25d)
        {
            if (offH > offV)
            {
                return posH < 0.5d ? HitPart.LEFT : HitPart.RIGHT;
            }
            else
            {
                return posV < 0.5d ? HitPart.BOTTOM : HitPart.TOP;
            }
        }
        else
        {
            return HitPart.CENTER;
        }
    }

    private static Vec3d getHitPartPositions(EnumFacing originalSide, EnumFacing playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        double x = hitVec.x - pos.getX();
        double y = hitVec.y - pos.getY();
        double z = hitVec.z - pos.getZ();
        double posH = 0;
        double posV = 0;

        switch (originalSide)
        {
            case DOWN:
            case UP:
                switch (playerFacingH)
                {
                    case NORTH:
                        posH = x;
                        posV = 1.0d - z;
                        break;
                    case SOUTH:
                        posH = 1.0d - x;
                        posV = z;
                        break;
                    case WEST:
                        posH = 1.0d - z;
                        posV = 1.0d - x;
                        break;
                    case EAST:
                        posH = z;
                        posV = x;
                        break;
                    default:
                }

                if (originalSide == EnumFacing.DOWN)
                {
                    posV = 1.0d - posV;
                }

                break;
            case NORTH:
            case SOUTH:
                posH = originalSide.getAxisDirection() == AxisDirection.POSITIVE ? x : 1.0d - x;
                posV = y;
                break;
            case WEST:
            case EAST:
                posH = originalSide.getAxisDirection() == AxisDirection.NEGATIVE ? z : 1.0d - z;
                posV = y;
                break;
        }

        return new Vec3d(posH, posV, 0);
    }

    /**
     * Returns the direction the targeted part of the targeting overlay is pointing towards.
     */
    public static EnumFacing getTargetedDirection(EnumFacing side, EnumFacing playerFacingH, BlockPos pos, Vec3d hitVec)
    {
        Vec3d positions = getHitPartPositions(side, playerFacingH, pos, hitVec);
        double posH = positions.x;
        double posV = positions.y;
        double offH = Math.abs(posH - 0.5d);
        double offV = Math.abs(posV - 0.5d);

        if (offH > 0.25d || offV > 0.25d)
        {
            if (side.getAxis() == EnumFacing.Axis.Y)
            {
                if (offH > offV)
                {
                    return posH < 0.5d ? playerFacingH.rotateYCCW() : playerFacingH.rotateY();
                }
                else
                {
                    if (side == EnumFacing.DOWN)
                    {
                        return posV > 0.5d ? playerFacingH.getOpposite() : playerFacingH;
                    }
                    else
                    {
                        return posV < 0.5d ? playerFacingH.getOpposite() : playerFacingH;
                    }
                }
            }
            else
            {
                if (offH > offV)
                {
                    return posH < 0.5d ? side.rotateY() : side.rotateYCCW();
                }
                else
                {
                    return posV < 0.5d ? EnumFacing.DOWN : EnumFacing.UP;
                }
            }
        }

        return side;
    }

    /**
     * Adjusts the (usually ray traced) position so that the provided entity
     * will not clip inside the presumable block side.
     */
    public static Vec3d adjustPositionToSideOfEntity(Vec3d pos, Entity entity, EnumFacing side)
    {
        double x = pos.x;
        double y = pos.y;
        double z = pos.z;

        if (side == EnumFacing.DOWN)
        {
            y -= entity.height;
        }
        else if (side.getAxis().isHorizontal())
        {
            x += side.getXOffset() * (entity.width / 2 + 1.0E-4D);
            z += side.getZOffset() * (entity.width / 2 + 1.0E-4D);
        }

        return new Vec3d(x, y, z);
    }

    public enum HitPart
    {
        CENTER,
        LEFT,
        RIGHT,
        BOTTOM,
        TOP;
    }
}
