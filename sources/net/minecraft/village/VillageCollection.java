package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData
{
    private World worldObj;
    /**
     * This is a black hole. You can add data to this list through a public interface, but you can't query that
     * information in any way and it's not used internally either.
     */
    private final List villagerPositionsList = Lists.newArrayList();
    private final List newDoors = Lists.newArrayList();
    private final List villageList = Lists.newArrayList();
    private int tickCounter;
    private static final String __OBFID = "CL_00001635";

    public VillageCollection(String name)
    {
        super(name);
    }

    public VillageCollection(World worldIn)
    {
        super(fileNameForProvider(worldIn.provider));
        this.worldObj = worldIn;
        this.markDirty();
    }

    public void setWorldsForAll(World worldIn)
    {
        this.worldObj = worldIn;
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();
            village.setWorld(worldIn);
        }
    }

    public void addToVillagerPositionList(BlockPos pos)
    {
        if (this.villagerPositionsList.size() <= 64)
        {
            if (!this.positionInList(pos))
            {
                this.villagerPositionsList.add(pos);
            }
        }
    }

    /**
     * Runs a single tick for the village collection
     */
    public void tick()
    {
        ++this.tickCounter;
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();
            village.tick(this.tickCounter);
        }

        this.removeAnnihilatedVillages();
        this.dropOldestVillagerPosition();
        this.addNewDoorsToVillageOrCreateVillage();

        if (this.tickCounter % 400 == 0)
        {
            this.markDirty();
        }
    }

    private void removeAnnihilatedVillages()
    {
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();

            if (village.isAnnihilated())
            {
                iterator.remove();
                this.markDirty();
            }
        }
    }

    /**
     * Get a list of villages.
     */
    public List getVillageList()
    {
        return this.villageList;
    }

    public Village getNearestVillage(BlockPos doorBlock, int radius)
    {
        Village village = null;
        double d0 = 3.4028234663852886E38D;
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village1 = (Village)iterator.next();
            double d1 = village1.getCenter().distanceSq(doorBlock);

            if (d1 < d0)
            {
                float f = (float)(radius + village1.getVillageRadius());

                if (d1 <= (double)(f * f))
                {
                    village = village1;
                    d0 = d1;
                }
            }
        }

        return village;
    }

    private void dropOldestVillagerPosition()
    {
        if (!this.villagerPositionsList.isEmpty())
        {
            this.addDoorsAround((BlockPos)this.villagerPositionsList.remove(0));
        }
    }

    private void addNewDoorsToVillageOrCreateVillage()
    {
        for (int i = 0; i < this.newDoors.size(); ++i)
        {
            VillageDoorInfo villagedoorinfo = (VillageDoorInfo)this.newDoors.get(i);
            Village village = this.getNearestVillage(villagedoorinfo.getDoorBlockPos(), 32);

            if (village == null)
            {
                village = new Village(this.worldObj);
                this.villageList.add(village);
                this.markDirty();
            }

            village.addVillageDoorInfo(villagedoorinfo);
        }

        this.newDoors.clear();
    }

    private void addDoorsAround(BlockPos central)
    {
        byte b0 = 16;
        byte b1 = 4;
        byte b2 = 16;

        for (int i = -b0; i < b0; ++i)
        {
            for (int j = -b1; j < b1; ++j)
            {
                for (int k = -b2; k < b2; ++k)
                {
                    BlockPos blockpos1 = central.add(i, j, k);

                    if (this.isWoodDoor(blockpos1))
                    {
                        VillageDoorInfo villagedoorinfo = this.checkDoorExistence(blockpos1);

                        if (villagedoorinfo == null)
                        {
                            this.addToNewDoorsList(blockpos1);
                        }
                        else
                        {
                            villagedoorinfo.func_179849_a(this.tickCounter);
                        }
                    }
                }
            }
        }
    }

    /**
     * returns the VillageDoorInfo if it exists in any village or in the newDoor list, otherwise returns null
     */
    private VillageDoorInfo checkDoorExistence(BlockPos doorBlock)
    {
        Iterator iterator = this.newDoors.iterator();
        VillageDoorInfo villagedoorinfo;

        do
        {
            if (!iterator.hasNext())
            {
                iterator = this.villageList.iterator();
                VillageDoorInfo villagedoorinfo1;

                do
                {
                    if (!iterator.hasNext())
                    {
                        return null;
                    }

                    Village village = (Village)iterator.next();
                    villagedoorinfo1 = village.getExistedDoor(doorBlock);
                }
                while (villagedoorinfo1 == null);

                return villagedoorinfo1;
            }

            villagedoorinfo = (VillageDoorInfo)iterator.next();
        }
        while (villagedoorinfo.getDoorBlockPos().getX() != doorBlock.getX() || villagedoorinfo.getDoorBlockPos().getZ() != doorBlock.getZ() || Math.abs(villagedoorinfo.getDoorBlockPos().getY() - doorBlock.getY()) > 1);

        return villagedoorinfo;
    }

    private void addToNewDoorsList(BlockPos doorBlock)
    {
        EnumFacing enumfacing = BlockDoor.getFacing(this.worldObj, doorBlock);
        EnumFacing enumfacing1 = enumfacing.getOpposite();
        int i = this.countBlocksCanSeeSky(doorBlock, enumfacing, 5);
        int j = this.countBlocksCanSeeSky(doorBlock, enumfacing1, i + 1);

        if (i != j)
        {
            this.newDoors.add(new VillageDoorInfo(doorBlock, i < j ? enumfacing : enumfacing1, this.tickCounter));
        }
    }

    /**
     * Check five blocks in the direction. The centerPos will not be checked.
     */
    private int countBlocksCanSeeSky(BlockPos centerPos, EnumFacing direction, int limitation)
    {
        int j = 0;

        for (int k = 1; k <= 5; ++k)
        {
            if (this.worldObj.canSeeSky(centerPos.offset(direction, k)))
            {
                ++j;

                if (j >= limitation)
                {
                    return j;
                }
            }
        }

        return j;
    }

    private boolean positionInList(BlockPos pos)
    {
        Iterator iterator = this.villagerPositionsList.iterator();
        BlockPos blockpos1;

        do
        {
            if (!iterator.hasNext())
            {
                return false;
            }

            blockpos1 = (BlockPos)iterator.next();
        }
        while (!blockpos1.equals(pos));

        return true;
    }

    private boolean isWoodDoor(BlockPos doorPos)
    {
        Block block = this.worldObj.getBlockState(doorPos).getBlock();
        return block instanceof BlockDoor ? block.getMaterial() == Material.wood : false;
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(NBTTagCompound nbt)
    {
        this.tickCounter = nbt.getInteger("Tick");
        NBTTagList nbttaglist = nbt.getTagList("Villages", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            Village village = new Village();
            village.readVillageDataFromNBT(nbttagcompound1);
            this.villageList.add(village);
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("Tick", this.tickCounter);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.villageList.iterator();

        while (iterator.hasNext())
        {
            Village village = (Village)iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            village.writeVillageDataToNBT(nbttagcompound1);
            nbttaglist.appendTag(nbttagcompound1);
        }

        nbt.setTag("Villages", nbttaglist);
    }

    public static String fileNameForProvider(WorldProvider provider)
    {
        return "villages" + provider.getInternalNameSuffix();
    }
}