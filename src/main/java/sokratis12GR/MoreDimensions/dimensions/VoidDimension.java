package sokratis12GR.MoreDimensions.dimensions;

import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.relauncher.*;
import sokratis12GR.MoreDimensions.ConfigHandler;
import sokratis12GR.MoreDimensions.biomes.VoidWorld;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.client.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.terraingen.*;
import net.minecraft.init.*;
import java.util.*;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.eventhandler.Event.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class VoidDimension
{

	public Object instance;
	public static int DIMID = 23;

	public static BlockTutorialPortal portal;
	public static ModTrigger block;

	static
	{

		portal = (BlockTutorialPortal) (new BlockTutorialPortal().setUnlocalizedName("voidDimension_portal"));
		block = (ModTrigger) (new ModTrigger()
				.setUnlocalizedName("voidDimension_trigger")/* .setTextureName("VoidIgnator") */);
		// Item.itemRegistry.addObject(440, "voidDimension_trigger", block);
	}

	public VoidDimension()
	{
	}

	public void load(FMLInitializationEvent event)
	{
		if (ConfigHandler.enableVoidDimension)
		{
			GameRegistry.registerBlock(portal, "voidDimension_portal");
			GameRegistry.registerItem(block, "voidDimension_trigger");
			DimensionManager.registerProviderType(DIMID, VoidDimension.WorldProviderMod.class, false);
			DimensionManager.registerDimension(DIMID, DIMID);

			if (event.getSide() == Side.CLIENT)
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(block, 0,
						new ModelResourceLocation("moredimensions:voidDimension_trigger", "inventory"));

			GameRegistry.addRecipe(new ItemStack(block, 1), new Object[]
			{ "XXX", "XXX", "X78", Character.valueOf('7'), new ItemStack(Items.feather, 1), Character.valueOf('8'),
					new ItemStack(Items.flint, 1), });

		}
	}

	public void registerRenderers()
	{
	}

	public void generateNether(World world, Random random, int chunkX, int chunkZ)
	{
	}

	public void generateSurface(World world, Random random, int chunkX, int chunkZ)
	{
	}

	public int addFuel(ItemStack fuel)
	{
		return 0;
	}

	public void serverLoad(FMLServerStartingEvent event)
	{
	}

	public void preInit(FMLPreInitializationEvent event)
	{
	}

	public static class WorldProviderMod extends WorldProvider
	{

		public void registerWorldChunkManager()
		{
			this.worldChunkMgr = new WorldChunkManagerHell(VoidWorld.biome, 0.0F);
			this.isHellWorld = true;
			this.hasNoSky = true;
			this.dimensionId = DIMID;
		}

		public String getInternalNameSuffix()
		{
			return "_voidDimension";
		}

		@SideOnly(Side.CLIENT)
		public Vec3 getFogColor(float par1, float par2)
		{
			return new Vec3(1.0D, 1.0D, 1.0D);
		}

		public IChunkProvider createChunkGenerator()
		{
			return new ChunkProviderModded(this.worldObj, this.worldObj.getSeed() - 21414);
		}

		public boolean isSurfaceWorld()
		{
			return false;
		}

		public boolean canCoordinateBeSpawn(int par1, int par2)
		{
			return false;
		}

		public boolean canRespawnHere()
		{
			return false;
		}

		@SideOnly(Side.CLIENT)
		public boolean doesXZShowFog(int par1, int par2)
		{
			return false;
		}

		public String getDimensionName()
		{
			return "voidDimension";
		}

		@Override
		protected void generateLightBrightnessTable()
		{
			float f = 0.5F;
			for (int i = 0; i <= 15; ++i)
			{
				float f1 = 1.0F - (float) i / 15.0F;
				this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
			}
		}

	}

	public static class TeleporterDimensionMod extends Teleporter
	{

		private final WorldServer worldServerInstance;
		/** A private Random() function in Teleporter */
		private final Random random;
		/** Stores successful portal placement locations for rapid lookup. */
		private final LongHashMap destinationCoordinateCache = new LongHashMap();
		/**
		 * A list of valid keys for the destinationCoordainteCache. These are based on the X & Z of the players initial location.
		 */
		private final List<Long> destinationCoordinateKeys = com.google.common.collect.Lists.newArrayList();

		public TeleporterDimensionMod(WorldServer worldIn)
		{
			super(worldIn);
			this.worldServerInstance = worldIn;
			this.random = new Random(worldIn.getSeed());
		}

		public void placeInPortal(Entity entityIn, float rotationYaw)
		{
			if (this.worldServerInstance.provider.getDimensionId() != 1)
			{
				if (!this.placeInExistingPortal(entityIn, rotationYaw))
				{
					this.makePortal(entityIn);
					this.placeInExistingPortal(entityIn, rotationYaw);
				}
			} else
			{
				int i = MathHelper.floor_double(entityIn.posX);
				int j = MathHelper.floor_double(entityIn.posY) - 1;
				int k = MathHelper.floor_double(entityIn.posZ);
				byte b0 = 1;
				byte b1 = 0;

				for (int l = -2; l <= 2; ++l)
				{
					for (int i1 = -2; i1 <= 2; ++i1)
					{
						for (int j1 = -1; j1 < 3; ++j1)
						{
							int k1 = i + i1 * b0 + l * b1;
							int l1 = j + j1;
							int i2 = k + i1 * b1 - l * b0;
							boolean flag = j1 < 0;
							this.worldServerInstance.setBlockState(new BlockPos(k1, l1, i2),
									flag ? Blocks.end_stone.getDefaultState() : Blocks.air.getDefaultState());
						}
					}
				}

				entityIn.setLocationAndAngles((double) i, (double) j, (double) k, entityIn.rotationYaw, 0.0F);
				entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0D;
			}
		}

		public boolean placeInExistingPortal(Entity entityIn, float p_180620_2_)
		{
			double d0 = -1.0D;
			int i = MathHelper.floor_double(entityIn.posX);
			int j = MathHelper.floor_double(entityIn.posZ);
			boolean flag1 = true;
			Object object = BlockPos.ORIGIN;
			long k = ChunkCoordIntPair.chunkXZ2Int(i, j);

			if (this.destinationCoordinateCache.containsItem(k))
			{
				Teleporter.PortalPosition portalposition = (Teleporter.PortalPosition) this.destinationCoordinateCache
						.getValueByKey(k);
				d0 = 0.0D;
				object = portalposition;
				portalposition.lastUpdateTime = this.worldServerInstance.getTotalWorldTime();
				flag1 = false;
			} else
			{
				BlockPos blockpos4 = new BlockPos(entityIn);

				for (int l = -128; l <= 128; ++l)
				{
					BlockPos blockpos1;

					for (int i1 = -128; i1 <= 128; ++i1)
					{
						for (BlockPos blockpos = blockpos4.add(l,
								this.worldServerInstance.getActualHeight() - 1 - blockpos4.getY(), i1); blockpos
										.getY() >= 0; blockpos = blockpos1)
						{
							blockpos1 = blockpos.down();

							if (this.worldServerInstance.getBlockState(blockpos).getBlock() == portal)
							{
								while (this.worldServerInstance.getBlockState(blockpos1 = blockpos.down())
										.getBlock() == portal)
								{
									blockpos = blockpos1;
								}

								double d1 = blockpos.distanceSq(blockpos4);

								if (d0 < 0.0D || d1 < d0)
								{
									d0 = d1;
									object = blockpos;
								}
							}
						}
					}
				}
			}

			if (d0 >= 0.0D)
			{
				if (flag1)
				{
					this.destinationCoordinateCache.add(k, new Teleporter.PortalPosition((BlockPos) object,
							this.worldServerInstance.getTotalWorldTime()));
					this.destinationCoordinateKeys.add(Long.valueOf(k));
				}

				double d4 = (double) ((BlockPos) object).getX() + 0.5D;
				double d5 = (double) ((BlockPos) object).getY() + 0.5D;
				double d6 = (double) ((BlockPos) object).getZ() + 0.5D;
				EnumFacing enumfacing = null;

				if (this.worldServerInstance.getBlockState(((BlockPos) object).west()).getBlock() == portal)
				{
					enumfacing = EnumFacing.NORTH;
				}

				if (this.worldServerInstance.getBlockState(((BlockPos) object).east()).getBlock() == portal)
				{
					enumfacing = EnumFacing.SOUTH;
				}

				if (this.worldServerInstance.getBlockState(((BlockPos) object).north()).getBlock() == portal)
				{
					enumfacing = EnumFacing.EAST;
				}

				if (this.worldServerInstance.getBlockState(((BlockPos) object).south()).getBlock() == portal)
				{
					enumfacing = EnumFacing.WEST;
				}

				EnumFacing enumfacing1 = EnumFacing.getHorizontal(entityIn.getTeleportDirection());

				if (enumfacing != null)
				{
					EnumFacing enumfacing2 = enumfacing.rotateYCCW();
					BlockPos blockpos2 = ((BlockPos) object).offset(enumfacing);
					boolean flag2 = this.func_180265_a(blockpos2);
					boolean flag3 = this.func_180265_a(blockpos2.offset(enumfacing2));

					if (flag3 && flag2)
					{
						object = ((BlockPos) object).offset(enumfacing2);
						enumfacing = enumfacing.getOpposite();
						enumfacing2 = enumfacing2.getOpposite();
						BlockPos blockpos3 = ((BlockPos) object).offset(enumfacing);
						flag2 = this.func_180265_a(blockpos3);
						flag3 = this.func_180265_a(blockpos3.offset(enumfacing2));
					}

					float f6 = 0.5F;
					float f1 = 0.5F;

					if (!flag3 && flag2)
					{
						f6 = 1.0F;
					} else if (flag3 && !flag2)
					{
						f6 = 0.0F;
					} else if (flag3)
					{
						f1 = 0.0F;
					}

					d4 = (double) ((BlockPos) object).getX() + 0.5D;
					d5 = (double) ((BlockPos) object).getY() + 0.5D;
					d6 = (double) ((BlockPos) object).getZ() + 0.5D;
					d4 += (double) ((float) enumfacing2.getFrontOffsetX() * f6
							+ (float) enumfacing.getFrontOffsetX() * f1);
					d6 += (double) ((float) enumfacing2.getFrontOffsetZ() * f6
							+ (float) enumfacing.getFrontOffsetZ() * f1);
					float f2 = 0.0F;
					float f3 = 0.0F;
					float f4 = 0.0F;
					float f5 = 0.0F;

					if (enumfacing == enumfacing1)
					{
						f2 = 1.0F;
						f3 = 1.0F;
					} else if (enumfacing == enumfacing1.getOpposite())
					{
						f2 = -1.0F;
						f3 = -1.0F;
					} else if (enumfacing == enumfacing1.rotateY())
					{
						f4 = 1.0F;
						f5 = -1.0F;
					} else
					{
						f4 = -1.0F;
						f5 = 1.0F;
					}

					double d2 = entityIn.motionX;
					double d3 = entityIn.motionZ;
					entityIn.motionX = d2 * (double) f2 + d3 * (double) f5;
					entityIn.motionZ = d2 * (double) f4 + d3 * (double) f3;
					entityIn.rotationYaw = p_180620_2_ - (float) (enumfacing1.getHorizontalIndex() * 90)
							+ (float) (enumfacing.getHorizontalIndex() * 90);
				} else
				{
					entityIn.motionX = entityIn.motionY = entityIn.motionZ = 0.0D;
				}

				entityIn.setLocationAndAngles(d4, d5, d6, entityIn.rotationYaw, entityIn.rotationPitch);
				return true;
			} else
			{
				return false;
			}
		}

		private boolean func_180265_a(BlockPos p_180265_1_)
		{
			return !this.worldServerInstance.isAirBlock(p_180265_1_)
					|| !this.worldServerInstance.isAirBlock(p_180265_1_.up());
		}

		public boolean makePortal(Entity p_85188_1_)
		{

			byte b0 = 16;
			double d0 = -1.0D;
			int i = MathHelper.floor_double(p_85188_1_.posX);
			int j = MathHelper.floor_double(p_85188_1_.posY);
			int k = MathHelper.floor_double(p_85188_1_.posZ);
			int l = i;
			int i1 = j;
			int j1 = k;
			int k1 = 0;
			int l1 = this.random.nextInt(4);
			int i2;
			double d1;
			int k2;
			double d2;
			int i3;
			int j3;
			int k3;
			int l3;
			int i4;
			int j4;
			int k4;
			int l4;
			int i5;
			double d3;
			double d4;

			for (i2 = i - b0; i2 <= i + b0; ++i2)
			{
				d1 = (double) i2 + 0.5D - p_85188_1_.posX;

				for (k2 = k - b0; k2 <= k + b0; ++k2)
				{
					d2 = (double) k2 + 0.5D - p_85188_1_.posZ;
					label271:

					for (i3 = this.worldServerInstance.getActualHeight() - 1; i3 >= 0; --i3)
					{
						if (this.worldServerInstance.isAirBlock(new BlockPos(i2, i3, k2)))
						{
							while (i3 > 0 && this.worldServerInstance.isAirBlock(new BlockPos(i2, i3 - 1, k2)))
							{
								--i3;
							}

							for (j3 = l1; j3 < l1 + 4; ++j3)
							{
								k3 = j3 % 2;
								l3 = 1 - k3;

								if (j3 % 4 >= 2)
								{
									k3 = -k3;
									l3 = -l3;
								}

								for (i4 = 0; i4 < 3; ++i4)
								{
									for (j4 = 0; j4 < 4; ++j4)
									{
										for (k4 = -1; k4 < 4; ++k4)
										{
											l4 = i2 + (j4 - 1) * k3 + i4 * l3;
											i5 = i3 + k4;
											int j5 = k2 + (j4 - 1) * l3 - i4 * k3;

											if (k4 < 0
													&& !this.worldServerInstance.getBlockState(new BlockPos(l4, i5, j5))
															.getBlock().getMaterial().isSolid()
													|| k4 >= 0 && !this.worldServerInstance
															.isAirBlock(new BlockPos(l4, i5, j5)))
											{
												continue label271;
											}
										}
									}
								}

								d3 = (double) i3 + 0.5D - p_85188_1_.posY;
								d4 = d1 * d1 + d3 * d3 + d2 * d2;

								if (d0 < 0.0D || d4 < d0)
								{
									d0 = d4;
									l = i2;
									i1 = i3;
									j1 = k2;
									k1 = j3 % 4;
								}
							}
						}
					}
				}
			}

			if (d0 < 0.0D)
			{
				for (i2 = i - b0; i2 <= i + b0; ++i2)
				{
					d1 = (double) i2 + 0.5D - p_85188_1_.posX;

					for (k2 = k - b0; k2 <= k + b0; ++k2)
					{
						d2 = (double) k2 + 0.5D - p_85188_1_.posZ;
						label219:

						for (i3 = this.worldServerInstance.getActualHeight() - 1; i3 >= 0; --i3)
						{
							if (this.worldServerInstance.isAirBlock(new BlockPos(i2, i3, k2)))
							{
								while (i3 > 0 && this.worldServerInstance.isAirBlock(new BlockPos(i2, i3 - 1, k2)))
								{
									--i3;
								}

								for (j3 = l1; j3 < l1 + 2; ++j3)
								{
									k3 = j3 % 2;
									l3 = 1 - k3;

									for (i4 = 0; i4 < 4; ++i4)
									{
										for (j4 = -1; j4 < 4; ++j4)
										{
											k4 = i2 + (i4 - 1) * k3;
											l4 = i3 + j4;
											i5 = k2 + (i4 - 1) * l3;

											if (j4 < 0
													&& !this.worldServerInstance.getBlockState(new BlockPos(k4, l4, i5))
															.getBlock().getMaterial().isSolid()
													|| j4 >= 0 && !this.worldServerInstance
															.isAirBlock(new BlockPos(k4, l4, i5)))
											{
												continue label219;
											}
										}
									}

									d3 = (double) i3 + 0.5D - p_85188_1_.posY;
									d4 = d1 * d1 + d3 * d3 + d2 * d2;

									if (d0 < 0.0D || d4 < d0)
									{
										d0 = d4;
										l = i2;
										i1 = i3;
										j1 = k2;
										k1 = j3 % 2;
									}
								}
							}
						}
					}
				}
			}

			int k5 = l;
			int j2 = i1;
			k2 = j1;
			int l5 = k1 % 2;
			int l2 = 1 - l5;

			if (k1 % 4 >= 2)
			{
				l5 = -l5;
				l2 = -l2;
			}

			if (d0 < 0.0D)
			{
				i1 = MathHelper.clamp_int(i1, 70, this.worldServerInstance.getActualHeight() - 10);
				j2 = i1;

				for (i3 = -1; i3 <= 1; ++i3)
				{
					for (j3 = 1; j3 < 3; ++j3)
					{
						for (k3 = -1; k3 < 3; ++k3)
						{
							l3 = k5 + (j3 - 1) * l5 + i3 * l2;
							i4 = j2 + k3;
							j4 = k2 + (j3 - 1) * l2 - i3 * l5;
							boolean flag = k3 < 0;
							this.worldServerInstance.setBlockState(new BlockPos(l3, i4, j4),
									flag ? Blocks.end_stone.getDefaultState() : Blocks.air.getDefaultState());
						}
					}
				}
			}

			IBlockState iblockstate = portal.getDefaultState().withProperty(BlockPortal.AXIS,
					l5 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);

			for (j3 = 0; j3 < 4; ++j3)
			{
				for (k3 = 0; k3 < 4; ++k3)
				{
					for (l3 = -1; l3 < 4; ++l3)
					{
						i4 = k5 + (k3 - 1) * l5;
						j4 = j2 + l3;
						k4 = k2 + (k3 - 1) * l2;
						boolean flag1 = k3 == 0 || k3 == 3 || l3 == -1 || l3 == 3;
						this.worldServerInstance.setBlockState(new BlockPos(i4, j4, k4),
								flag1 ? Blocks.end_stone.getDefaultState() : iblockstate, 2);
					}
				}

				for (k3 = 0; k3 < 4; ++k3)
				{
					for (l3 = -1; l3 < 4; ++l3)
					{
						i4 = k5 + (k3 - 1) * l5;
						j4 = j2 + l3;
						k4 = k2 + (k3 - 1) * l2;
						this.worldServerInstance.notifyNeighborsOfStateChange(new BlockPos(i4, j4, k4),
								this.worldServerInstance.getBlockState(new BlockPos(i4, j4, k4)).getBlock());
					}
				}
			}

			return true;
		}

		/**
		 * called periodically to remove out-of-date portal locations from the cache list. Argument par1 is a WorldServer.getTotalWorldTime() value.
		 */
		public void removeStalePortalLocations(long p_85189_1_)
		{
			if (p_85189_1_ % 100L == 0L)
			{
				Iterator<Long> iterator = this.destinationCoordinateKeys.iterator();
				long j = p_85189_1_ - 600L;

				while (iterator.hasNext())
				{
					Long olong = (Long) iterator.next();
					Teleporter.PortalPosition portalposition = (Teleporter.PortalPosition) this.destinationCoordinateCache
							.getValueByKey(olong.longValue());

					if (portalposition == null || portalposition.lastUpdateTime < j)
					{
						iterator.remove();
						this.destinationCoordinateCache.remove(olong.longValue());
					}
				}
			}
		}

		public class PortalPosition extends BlockPos
		{
			/** The worldtime at which this PortalPosition was last verified */
			public long lastUpdateTime;

			public PortalPosition(BlockPos pos, long p_i45747_3_)
			{
				super(pos.getX(), pos.getY(), pos.getZ());
				this.lastUpdateTime = p_i45747_3_;
			}
		}

	}

	/// FIRE BLOCK

	static class BlockFireMod extends Block
	{

		protected BlockFireMod()
		{
			super(Material.ground);
		}

		public void onBlockAdded(World par1World, int par2, int par3, int par4)
		{
			/* TutorialPortal.tryToCreatePortal(par1World, par2, par3, par4); */
		}

	}// fire block end

	/// PORTAL BLOCK

	public static class BlockTutorialPortal extends Block
	{

		public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, new EnumFacing.Axis[]
		{ EnumFacing.Axis.X, EnumFacing.Axis.Z });

		public BlockTutorialPortal()
		{
			super(Material.portal);
			this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Z));
			this.setTickRandomly(true);
			this.setHardness(-1.0F);
			this.setLightLevel(0.75F);
		}

		public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
		{
			return null;
		}

		public boolean isOpaqueCube()
		{
			return false;
		}

		public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
		{
			EnumFacing.Axis axis = (EnumFacing.Axis) worldIn.getBlockState(pos).getValue(AXIS);
			float f = 0.125F;
			float f1 = 0.125F;

			if (axis == EnumFacing.Axis.X)
			{
				f = 0.5F;
			}

			if (axis == EnumFacing.Axis.Z)
			{
				f1 = 0.5F;
			}

			this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f1, 0.5F + f, 1.0F, 0.5F + f1);
		}

		public static int getMetaForAxis(EnumFacing.Axis axis)
		{
			return axis == EnumFacing.Axis.X ? 1 : (axis == EnumFacing.Axis.Z ? 2 : 0);
		}

		public boolean isFullCube()
		{
			return false;
		}

		public int getMetaFromState(IBlockState state)
		{
			return getMetaForAxis((EnumFacing.Axis) state.getValue(AXIS));
		}

		public IBlockState getStateFromMeta(int meta)
		{
			return this.getDefaultState().withProperty(AXIS, (meta & 3) == 2 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
		}

		/**
		 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
		 */
		/**
		 * Checks to see if this location is valid to create a portal and will return True if it does. Args: world, x, y, z
		 */
		public boolean tryToCreatePortal(World par1World, int par2, int par3, int par4)
		{
			byte b0 = 0;
			byte b1 = 0;
			if (getBlock(par1World, par2 - 1, par3, par4) == Blocks.end_stone
					|| getBlock(par1World, par2 + 1, par3, par4) == Blocks.end_stone)
			{
				b0 = 1;
			}
			if (getBlock(par1World, par2, par3, par4 - 1) == Blocks.end_stone
					|| getBlock(par1World, par2, par3, par4 + 1) == Blocks.end_stone)
			{
				b1 = 1;
			}
			if (b0 == b1)
			{
				return false;
			} else
			{
				if (getBlock(par1World, par2 - b0, par3, par4 - b1) == Blocks.air)
				{
					par2 -= b0;
					par4 -= b1;
				}
				int l;
				int i1;
				for (l = -1; l <= 2; ++l)
				{
					for (i1 = -1; i1 <= 3; ++i1)
					{
						boolean flag = l == -1 || l == 2 || i1 == -1 || i1 == 3;
						if (l != -1 && l != 2 || i1 != -1 && i1 != 3)
						{
							Block j1 = getBlock(par1World, par2 + b0 * l, par3 + i1, par4 + b1 * l);
							if (flag)
							{
								if (j1 != Blocks.end_stone)
								{
									return false;
								}
							}
							/*
							 * else if (j1 != 0 && j1 != Main.TutorialFire.blockID) { return false; }
							 */
						}
					}
				}
				for (l = 0; l < 2; ++l)
				{
					for (i1 = 0; i1 < 3; ++i1)
					{
						IBlockState iblockstate = this.getDefaultState().withProperty(BlockPortal.AXIS,
								b0 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);
						par1World.setBlockState(new BlockPos(par2 + b0 * l, par3 + i1, par4 + b1 * l), iblockstate, 3);
					}
				}
				return true;
			}
		}

		/**
		 * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are their own) Args: x, y, z, neighbor blockID
		 */
		public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block neighborBlock)
		{

			int par2 = pos.getX();
			int par3 = pos.getY();
			int par4 = pos.getZ();

			byte b0 = 0;
			byte b1 = 1;
			if (getBlock(par1World, par2 - 1, par3, par4) == this || getBlock(par1World, par2 + 1, par3, par4) == this)
			{
				b0 = 1;
				b1 = 0;
			}
			int i1;
			for (i1 = par3; getBlock(par1World, par2, i1 - 1, par4) == this; --i1)
			{
				;
			}
			if (getBlock(par1World, par2, i1 - 1, par4) != Blocks.end_stone)
			{
				par1World.setBlockToAir(new BlockPos(par2, par3, par4));
			} else
			{
				int j1;
				for (j1 = 1; j1 < 4 && getBlock(par1World, par2, i1 + j1, par4) == this; ++j1)
				{
					;
				}
				if (j1 == 3 && getBlock(par1World, par2, i1 + j1, par4) == Blocks.end_stone)
				{
					boolean flag = getBlock(par1World, par2 - 1, par3, par4) == this
							|| getBlock(par1World, par2 + 1, par3, par4) == this;
					boolean flag1 = getBlock(par1World, par2, par3, par4 - 1) == this
							|| getBlock(par1World, par2, par3, par4 + 1) == this;
					if (flag && flag1)
					{
						par1World.setBlockToAir(new BlockPos(par2, par3, par4));
					} else
					{
						if ((getBlock(par1World, par2 + b0, par3, par4 + b1) != Blocks.end_stone
								|| getBlock(par1World, par2 - b0, par3, par4 - b1) != this)
								&& (getBlock(par1World, par2 - b0, par3, par4 - b1) != Blocks.end_stone
										|| getBlock(par1World, par2 + b0, par3, par4 + b1) != this))
						{
							par1World.setBlockToAir(new BlockPos(par2, par3, par4));
						}
					}
				} else
				{
					par1World.setBlockToAir(new BlockPos(par2, par3, par4));
				}
			}
		}

		@SideOnly(Side.CLIENT)
		public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
		{
			EnumFacing.Axis axis = null;
			IBlockState iblockstate = worldIn.getBlockState(pos);

			if (worldIn.getBlockState(pos).getBlock() == this)
			{
				axis = (EnumFacing.Axis) iblockstate.getValue(AXIS);

				if (axis == null)
				{
					return false;
				}

				if (axis == EnumFacing.Axis.Z && side != EnumFacing.EAST && side != EnumFacing.WEST)
				{
					return false;
				}

				if (axis == EnumFacing.Axis.X && side != EnumFacing.SOUTH && side != EnumFacing.NORTH)
				{
					return false;
				}
			}

			boolean flag = worldIn.getBlockState(pos.west()).getBlock() == this
					&& worldIn.getBlockState(pos.west(2)).getBlock() != this;
			boolean flag1 = worldIn.getBlockState(pos.east()).getBlock() == this
					&& worldIn.getBlockState(pos.east(2)).getBlock() != this;
			boolean flag2 = worldIn.getBlockState(pos.north()).getBlock() == this
					&& worldIn.getBlockState(pos.north(2)).getBlock() != this;
			boolean flag3 = worldIn.getBlockState(pos.south()).getBlock() == this
					&& worldIn.getBlockState(pos.south(2)).getBlock() != this;
			boolean flag4 = flag || flag1 || axis == EnumFacing.Axis.X;
			boolean flag5 = flag2 || flag3 || axis == EnumFacing.Axis.Z;
			return flag4 && side == EnumFacing.WEST ? true
					: (flag4 && side == EnumFacing.EAST ? true
							: (flag5 && side == EnumFacing.NORTH ? true : flag5 && side == EnumFacing.SOUTH));
		}

		@SideOnly(Side.CLIENT)
		public EnumWorldBlockLayer getBlockLayer()
		{
			return EnumWorldBlockLayer.TRANSLUCENT;
		}

		protected BlockState createBlockState()
		{
			return new BlockState(this, new IProperty[]
			{ AXIS });
		}

		// @SideOnly(Side.CLIENT)
		/**
		 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates. Args: blockAccess, x, y, z, side
		 */

		/*
		 * public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) { if (getBlock(par1IBlockAccess,par2, par3, par4) == this) { return false; } else { boolean flag = getBlock(par1IBlockAccess,par2 - 1, par3, par4) == this && getBlock(par1IBlockAccess,par2 - 2, par3, par4) != this; boolean flag1 = getBlock(par1IBlockAccess,par2 + 1, par3, par4) == this && getBlock(par1IBlockAccess,par2 + 2, par3, par4) != this; boolean flag2 = getBlock(par1IBlockAccess,par2, par3, par4 - 1) == this && getBlock(par1IBlockAccess,par2, par3, par4 - 2) != this; boolean flag3 = getBlock(par1IBlockAccess,par2, par3, par4 + 1) == this && getBlock(par1IBlockAccess,par2, par3, par4 + 2) != this; boolean flag4 = flag || flag1; boolean flag5 = flag2 || flag3; return
		 * flag4 && par5 == 4 ? true : (flag4 && par5 == 5 ? true : (flag5 && par5 == 2 ? true : flag5 && par5 == 3)); } }
		 */
		/**
		 * Returns the quantity of items to drop on block destruction.
		 */
		public int quantityDropped(Random par1Random)
		{
			return 0;
		}

		/**
		 * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
		 */
		// @Override
		public void onEntityCollidedWithBlock(World par1World, BlockPos pos, IBlockState state, Entity par5Entity)
		{

			pos.getX();
			pos.getY();
			pos.getZ();

			// par1World.createExplosion((Entity)null, par2, par3, par4, 0.004F, true);
			if (par5Entity.ridingEntity == null && par5Entity.riddenByEntity == null
					&& par5Entity instanceof EntityPlayerMP)
			{

				EntityPlayerMP thePlayer = (EntityPlayerMP) par5Entity;
				if (thePlayer.timeUntilPortal > 0)
				{
					thePlayer.timeUntilPortal = 10;
				} else if (thePlayer.dimension != DIMID)
				{
					thePlayer.timeUntilPortal = 10;
					thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, DIMID,
							new TeleporterDimensionMod(thePlayer.mcServer.worldServerForDimension(DIMID)));
				} else
				{
					thePlayer.timeUntilPortal = 10;
					thePlayer.mcServer.getConfigurationManager().transferPlayerToDimension(thePlayer, 0,
							new TeleporterDimensionMod(thePlayer.mcServer.worldServerForDimension(0)));
				}
			}
		}

		@SideOnly(Side.CLIENT)
		/**
		 * A randomly called display update to be able to add particles or other items for display
		 */
		public void randomDisplayTick(World par1World, BlockPos pos, IBlockState state, Random par5Random)
		{

			int par2 = pos.getX();
			int par3 = pos.getY();
			int par4 = pos.getZ();

			if (par5Random.nextInt(100) == 0)
			{
				par1World.playSound((double) par2 + 0.5D, (double) par3 + 0.5D, (double) par4 + 0.5D, "portal.portal",
						0.5F, par5Random.nextFloat() * 0.4F + 0.8F, false);
			}
			for (int l = 0; l < 4; ++l)
			{
				double d0 = (double) ((float) par2 + par5Random.nextFloat());
				double d1 = (double) ((float) par3 + par5Random.nextFloat());
				double d2 = (double) ((float) par4 + par5Random.nextFloat());
				double d3 = 0.0D;
				double d4 = 0.0D;
				double d5 = 0.0D;
				int i1 = par5Random.nextInt(2) * 2 - 1;
				d3 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;
				d4 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;
				d5 = ((double) par5Random.nextFloat() - 0.5D) * 0.5D;
				if (getBlock(par1World, par2 - 1, par3, par4) != this
						&& getBlock(par1World, par2 + 1, par3, par4) != this)
				{
					d0 = (double) par2 + 0.5D + 0.25D * (double) i1;
					d3 = (double) (par5Random.nextFloat() * 2.0F * (float) i1);
				} else
				{
					d2 = (double) par4 + 0.5D + 0.25D * (double) i1;
					d5 = (double) (par5Random.nextFloat() * 2.0F * (float) i1);
				}
				par1World.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
			}
		}

		@SideOnly(Side.CLIENT)
		/**
		 * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
		 */
		public int idPicked(World par1World, int par2, int par3, int par4)
		{
			return 0;
		}
	}

	// portal block

	public static class ModTrigger extends Item
	{
		public ModTrigger()
		{
			super();
			this.maxStackSize = 1;
			setMaxDamage(64);
			setCreativeTab(CreativeTabs.tabTools);
		}

		@Override
		public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos,
				EnumFacing side, float par8, float par9, float par10)
		// public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
		{

			int par4 = pos.getX();
			int par5 = pos.getY();
			int par6 = pos.getZ();

			int par7 = side.getIndex();

			if (par7 == 0)
			{
				par5--;
			}
			if (par7 == 1)
			{
				par5++;
			}
			if (par7 == 2)
			{
				par6--;
			}
			if (par7 == 3)
			{
				par6++;
			}
			if (par7 == 4)
			{
				par4--;
			}
			if (par7 == 5)
			{
				par4++;
			}
			if (!par2EntityPlayer.canPlayerEdit(new BlockPos(par4, par5, par6), side, par1ItemStack))
			{
				return false;
			}
			Block i1 = getBlock(par3World, par4, par5, par6);
			if (i1 == Blocks.air)
			{
				par3World.playSoundEffect(par4 + 0.5D, par5 + 0.5D, par6 + 0.5D, "fire.ignite", 1.0F,
						itemRand.nextFloat() * 0.4F + 0.8F);
				portal.tryToCreatePortal(par3World, par4, par5, par6);
			}
			par1ItemStack.damageItem(1, par2EntityPlayer);
			return true;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static class ChunkProviderModded implements IChunkProvider
	{

		private Random endRNG;
		private NoiseGeneratorOctaves noiseGen1;
		private NoiseGeneratorOctaves noiseGen2;
		private NoiseGeneratorOctaves noiseGen3;
		public NoiseGeneratorOctaves noiseGen4;
		public NoiseGeneratorOctaves noiseGen5;
		private World endWorld;
		private double[] densities;

		/**
		 * The biomes that are used to generate the chunk
		 */
		private BiomeGenBase[] biomesForGeneration;
		double[] noiseData1;
		double[] noiseData2;
		double[] noiseData3;
		double[] noiseData4;
		double[] noiseData5;
		int[][] field_73203_h = new int[32][32];

		public ChunkProviderModded(World par1World, long par2)
		{
			this.endWorld = par1World;
			this.endRNG = new Random(par2);
			this.noiseGen1 = new NoiseGeneratorOctaves(this.endRNG, 16);
			this.noiseGen2 = new NoiseGeneratorOctaves(this.endRNG, 16);
			this.noiseGen3 = new NoiseGeneratorOctaves(this.endRNG, 8);
			this.noiseGen4 = new NoiseGeneratorOctaves(this.endRNG, 10);
			this.noiseGen5 = new NoiseGeneratorOctaves(this.endRNG, 16);

			NoiseGenerator[] noiseGens =
			{ noiseGen1, noiseGen2, noiseGen3, noiseGen4, noiseGen5 };
			noiseGens = TerrainGen.getModdedNoiseGenerators(par1World, this.endRNG, noiseGens);
			this.noiseGen1 = (NoiseGeneratorOctaves) noiseGens[0];
			this.noiseGen2 = (NoiseGeneratorOctaves) noiseGens[1];
			this.noiseGen3 = (NoiseGeneratorOctaves) noiseGens[2];
			this.noiseGen4 = (NoiseGeneratorOctaves) noiseGens[3];
			this.noiseGen5 = (NoiseGeneratorOctaves) noiseGens[4];
		}

		private int chunkX = 0, chunkZ = 0;

		public void func_180519_a(ChunkPrimer p_180519_1_)
		{
			ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, chunkX,
					chunkZ, p_180519_1_, this.endWorld);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.getResult() == Result.DENY)
				return;
			for (int i = 0; i < 16; ++i)
			{
				for (int j = 0; j < 16; ++j)
				{
					byte b0 = 1;
					int k = -1;
					IBlockState iblockstate = Blocks.air.getDefaultState();
					IBlockState iblockstate1 = Blocks.air.getDefaultState();

					for (int l = 127; l >= 0; --l)
					{
						IBlockState iblockstate2 = p_180519_1_.getBlockState(i, l, j);

						if (iblockstate2.getBlock().getMaterial() == Material.air)
						{
							k = -1;
						} else if (iblockstate2.getBlock() == Blocks.stone)
						{
							if (k == -1)
							{
								if (b0 <= 0)
								{
									iblockstate = Blocks.air.getDefaultState();
									iblockstate1 = Blocks.air.getDefaultState();
								}

								k = b0;

								if (l >= 0)
								{
									p_180519_1_.setBlockState(i, l, j, iblockstate);
								} else
								{
									p_180519_1_.setBlockState(i, l, j, iblockstate1);
								}
							} else if (k > 0)
							{
								--k;
								p_180519_1_.setBlockState(i, l, j, iblockstate1);
							}
						}
					}
				}
			}
		}

		public void func_147420_a(int p_147420_1_, int p_147420_2_, Block[] p_147420_3_, BiomeGenBase[] p_147420_4_)
		{
			byte b0 = 2;
			int k = b0 + 1;
			byte b1 = 33;
			int l = b0 + 1;
			this.densities = this.initializeNoiseField(this.densities, p_147420_1_ * b0, 0, p_147420_2_ * b0, k, b1, l);

			for (int i1 = 0; i1 < b0; ++i1)
			{
				for (int j1 = 0; j1 < b0; ++j1)
				{
					for (int k1 = 0; k1 < 32; ++k1)
					{
						double d0 = 0.25D;
						double d1 = this.densities[((i1 + 0) * l + j1 + 0) * b1 + k1 + 0];
						double d2 = this.densities[((i1 + 0) * l + j1 + 1) * b1 + k1 + 0];
						double d3 = this.densities[((i1 + 1) * l + j1 + 0) * b1 + k1 + 0];
						double d4 = this.densities[((i1 + 1) * l + j1 + 1) * b1 + k1 + 0];
						double d5 = (this.densities[((i1 + 0) * l + j1 + 0) * b1 + k1 + 1] - d1) * d0;
						double d6 = (this.densities[((i1 + 0) * l + j1 + 1) * b1 + k1 + 1] - d2) * d0;
						double d7 = (this.densities[((i1 + 1) * l + j1 + 0) * b1 + k1 + 1] - d3) * d0;
						double d8 = (this.densities[((i1 + 1) * l + j1 + 1) * b1 + k1 + 1] - d4) * d0;

						for (int l1 = 0; l1 < 4; ++l1)
						{
							double d9 = 0.125D;
							double d10 = d1;
							double d11 = d2;
							double d12 = (d3 - d1) * d9;
							double d13 = (d4 - d2) * d9;

							for (int i2 = 0; i2 < 8; ++i2)
							{
								int j2 = i2 + i1 * 8 << 11 | 0 + j1 * 8 << 7 | k1 * 4 + l1;
								short short1 = 128;
								double d14 = 0.125D;
								double d15 = d10;
								double d16 = (d11 - d10) * d14;

								for (int k2 = 0; k2 < 8; ++k2)
								{
									Block block = null;

									if (d15 > 0.0D)
									{
										block = Blocks.air;
									}

									p_147420_3_[j2] = block;
									j2 += short1;
									d15 += d16;
								}

								d10 += d12;
								d11 += d13;
							}

							d1 += d5;
							d2 += d6;
							d3 += d7;
							d4 += d8;
						}
					}
				}
			}
		}

		public List<?> func_177458_a(EnumCreatureType p_177458_1_, BlockPos p_177458_2_)
		{
			return this.endWorld.getBiomeGenForCoords(p_177458_2_).getSpawnableList(p_177458_1_);
		}

		public BlockPos getStrongholdGen(World worldIn, String p_180513_2_, BlockPos p_180513_3_)
		{
			return null;
		}

		public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
		{
			return false;
		}

		/**
		 * loads or generates the chunk at the chunk location specified
		 */
		public Chunk loadChunk(int par1, int par2)
		{
			return this.provideChunk(par1, par2);
		}

		public void func_180520_a(int p_180520_1_, int p_180520_2_, ChunkPrimer p_180520_3_)
		{
			byte b0 = 2;
			int k = b0 + 1;
			byte b1 = 33;
			int l = b0 + 1;
			this.densities = this.initializeNoiseField(this.densities, p_180520_1_ * b0, 0, p_180520_2_ * b0, k, b1, l);

			for (int i1 = 0; i1 < b0; ++i1)
			{
				for (int j1 = 0; j1 < b0; ++j1)
				{
					for (int k1 = 0; k1 < 32; ++k1)
					{
						double d0 = 0.25D;
						double d1 = this.densities[((i1 + 0) * l + j1 + 0) * b1 + k1 + 0];
						double d2 = this.densities[((i1 + 0) * l + j1 + 1) * b1 + k1 + 0];
						double d3 = this.densities[((i1 + 1) * l + j1 + 0) * b1 + k1 + 0];
						double d4 = this.densities[((i1 + 1) * l + j1 + 1) * b1 + k1 + 0];
						double d5 = (this.densities[((i1 + 0) * l + j1 + 0) * b1 + k1 + 1] - d1) * d0;
						double d6 = (this.densities[((i1 + 0) * l + j1 + 1) * b1 + k1 + 1] - d2) * d0;
						double d7 = (this.densities[((i1 + 1) * l + j1 + 0) * b1 + k1 + 1] - d3) * d0;
						double d8 = (this.densities[((i1 + 1) * l + j1 + 1) * b1 + k1 + 1] - d4) * d0;

						for (int l1 = 0; l1 < 4; ++l1)
						{
							double d9 = 0.125D;
							double d10 = d1;
							double d11 = d2;
							double d12 = (d3 - d1) * d9;
							double d13 = (d4 - d2) * d9;

							for (int i2 = 0; i2 < 8; ++i2)
							{
								double d14 = 0.125D;
								double d15 = d10;
								double d16 = (d11 - d10) * d14;

								for (int j2 = 0; j2 < 8; ++j2)
								{
									IBlockState iblockstate = null;

									if (d15 > 0.0D)
									{
										iblockstate = Blocks.air.getDefaultState();
									}

									int k2 = i2 + i1 * 8;
									int l2 = l1 + k1 * 4;
									int i3 = j2 + j1 * 8;
									p_180520_3_.setBlockState(k2, l2, i3, iblockstate);
									d15 += d16;
								}

								d10 += d12;
								d11 += d13;
							}

							d1 += d5;
							d2 += d6;
							d3 += d7;
							d4 += d8;
						}
					}
				}
			}
		}

		/**
		 * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the specified chunk from the map seed and chunk seed
		 */
		public Chunk provideChunk(int x, int z)
		{
			chunkX = x;
			chunkZ = z;
			this.endRNG.setSeed((long) x * 341873128712L + (long) z * 132897987541L);
			ChunkPrimer chunkprimer = new ChunkPrimer();
			this.biomesForGeneration = this.endWorld.getWorldChunkManager()
					.loadBlockGeneratorData(this.biomesForGeneration, x * 16, z * 16, 16, 16);
			this.func_180520_a(x, z, chunkprimer);
			this.func_180519_a(chunkprimer);
			Chunk chunk = new Chunk(this.endWorld, chunkprimer, x, z);
			byte[] abyte = chunk.getBiomeArray();

			for (int k = 0; k < abyte.length; ++k)
			{
				abyte[k] = (byte) this.biomesForGeneration[k].biomeID;
			}

			chunk.generateSkylightMap();
			return chunk;
		}

		public Chunk provideChunk(BlockPos blockPosIn)
		{
			return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
		}

		/**
		 * generates a subset of the level's terrain data. Takes 7 arguments: the [empty] noise array, the position, and the size.
		 */
		private double[] initializeNoiseField(double[] par1ArrayOfDouble, int par2, int par3, int par4, int par5,
				int par6, int par7)
		{
			ChunkProviderEvent.InitNoiseField event = new ChunkProviderEvent.InitNoiseField(this, par1ArrayOfDouble,
					par2, par3, par4, par5, par6, par7);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.getResult() == Result.DENY)
				return event.noisefield;

			if (par1ArrayOfDouble == null)
			{
				par1ArrayOfDouble = new double[par5 * par6 * par7];
			}

			double d0 = 684.412D;
			double d1 = 684.412D;
			this.noiseData4 = this.noiseGen4.generateNoiseOctaves(this.noiseData4, par2, par4, par5, par7, 1.121D,
					1.121D, 0.5D);
			this.noiseData5 = this.noiseGen5.generateNoiseOctaves(this.noiseData5, par2, par4, par5, par7, 200.0D,
					200.0D, 0.5D);
			d0 *= 2.0D;
			this.noiseData1 = this.noiseGen3.generateNoiseOctaves(this.noiseData1, par2, par3, par4, par5, par6, par7,
					d0 / 80.0D, d1 / 160.0D, d0 / 80.0D);
			this.noiseData2 = this.noiseGen1.generateNoiseOctaves(this.noiseData2, par2, par3, par4, par5, par6, par7,
					d0, d1, d0);
			this.noiseData3 = this.noiseGen2.generateNoiseOctaves(this.noiseData3, par2, par3, par4, par5, par6, par7,
					d0, d1, d0);
			int k1 = 0;
			int l1 = 0;

			for (int i2 = 0; i2 < par5; ++i2)
			{
				for (int j2 = 0; j2 < par7; ++j2)
				{
					double d2 = (this.noiseData4[l1] + 256.0D) / 512.0D;

					if (d2 > 1.0D)
					{
						d2 = 1.0D;
					}

					double d3 = this.noiseData5[l1] / 8000.0D;

					if (d3 < 0.0D)
					{
						d3 = -d3 * 0.3D;
					}

					d3 = d3 * 3.0D - 2.0D;
					float f = (float) (i2 + par2 - 0) / 1.0F;
					float f1 = (float) (j2 + par4 - 0) / 1.0F;
					float f2 = 100.0F - MathHelper.sqrt_float(f * f + f1 * f1) * 8.0F;

					if (f2 > 80.0F)
					{
						f2 = 80.0F;
					}

					if (f2 < -100.0F)
					{
						f2 = -100.0F;
					}

					if (d3 > 1.0D)
					{
						d3 = 1.0D;
					}

					d3 /= 8.0D;
					d3 = 0.0D;

					if (d2 < 0.0D)
					{
						d2 = 0.0D;
					}

					d2 += 0.5D;
					d3 = d3 * (double) par6 / 16.0D;
					++l1;
					double d4 = (double) par6 / 2.0D;

					for (int k2 = 0; k2 < par6; ++k2)
					{
						double d5 = 0.0D;
						double d6 = ((double) k2 - d4) * 8.0D / d2;

						if (d6 < 0.0D)
						{
							d6 *= -1.0D;
						}

						double d7 = this.noiseData2[k1] / 512.0D;
						double d8 = this.noiseData3[k1] / 512.0D;
						double d9 = (this.noiseData1[k1] / 10.0D + 1.0D) / 2.0D;

						if (d9 < 0.0D)
						{
							d5 = d7;
						} else if (d9 > 1.0D)
						{
							d5 = d8;
						} else
						{
							d5 = d7 + (d8 - d7) * d9;
						}

						d5 -= 8.0D;
						d5 += (double) f2;
						byte b0 = 2;
						double d10;

						if (k2 > par6 / 2 - b0)
						{
							d10 = (double) ((float) (k2 - (par6 / 2 - b0)) / 64.0F);

							if (d10 < 0.0D)
							{
								d10 = 0.0D;
							}

							if (d10 > 1.0D)
							{
								d10 = 1.0D;
							}

							d5 = d5 * (1.0D - d10) + -3000.0D * d10;
						}

						b0 = 8;

						if (k2 < b0)
						{
							d10 = (double) ((float) (b0 - k2) / ((float) b0 - 1.0F));
							d5 = d5 * (1.0D - d10) + -30.0D * d10;
						}

						par1ArrayOfDouble[k1] = d5;
						++k1;
					}
				}
			}

			return par1ArrayOfDouble;
		}

		/**
		 * Checks to see if a chunk exists at x, y
		 */
		public boolean chunkExists(int par1, int par2)
		{
			return true;
		}

		/**
		 * Populates chunk with ores etc etc
		 */
		public void populate(IChunkProvider par1IChunkProvider, int par2, int par3)
		{
			BlockFalling.fallInstantly = true;

			MinecraftForge.EVENT_BUS
					.post(new PopulateChunkEvent.Pre(par1IChunkProvider, endWorld, endWorld.rand, par2, par3, false));

			MinecraftForge.EVENT_BUS
					.post(new PopulateChunkEvent.Post(par1IChunkProvider, endWorld, endWorld.rand, par2, par3, false));

			BlockFalling.fallInstantly = false;
		}

		/**
		 * Two modes of operation: if passed true, save all Chunks in one go. If passed false, save up to two chunks. Return true if all chunks have been saved.
		 */
		public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate)
		{
			return true;
		}

		/**
		 * Save extra data not associated with any Chunk. Not saved during autosave, only during world unload. Currently unimplemented.
		 */
		public void saveExtraData()
		{
		}

		/**
		 * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
		 */
		public boolean unloadQueuedChunks()
		{
			return false;
		}

		/**
		 * Returns if the IChunkProvider supports saving.
		 */
		public boolean canSave()
		{
			return true;
		}

		/**
		 * Converts the instance data to a readable string.
		 */
		public String makeString()
		{
			return "RandomLevelSource";
		}

		/**
		 * Returns a list of creatures of the specified type that can spawn at the given location.
		 */
		public List<?> getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4)
		{
			BiomeGenBase biomegenbase = this.endWorld.getBiomeGenForCoords(new BlockPos(par2, 0, par4));
			return biomegenbase.getSpawnableList(par1EnumCreatureType);
		}

		public void recreateStructures(Chunk p_180514_1_, int p_180514_2_, int p_180514_3_)
		{
		}

		public int getLoadedChunkCount()
		{
			return 0;
		}

		public void recreateStructures(int par1, int par2)
		{
		}
	}

	// helpers
	public static Block getBlock(IBlockAccess world, int i, int j, int k)
	{
		return world.getBlockState(new BlockPos(i, j, k)).getBlock();
	}

}
