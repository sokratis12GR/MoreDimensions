package sokratis12GR.MoreDimensions.dimensions;

import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.relauncher.*;
import sokratis12GR.MoreDimensions.ConfigHandler;
import sokratis12GR.MoreDimensions.biomes.EmeraldWorld;
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
import net.minecraft.world.gen.structure.*;
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
//import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.*;
import net.minecraftforge.fml.common.eventhandler.Event.*;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;

public class EmeraldDimension
{

	public Object instance;
	public static int DIMID = 14;

	public static BlockTutorialPortal portal;
	public static ModTrigger block;

	static
	{

		portal = (BlockTutorialPortal) (new BlockTutorialPortal().setUnlocalizedName("emeraldDimension_portal"));
		block = (ModTrigger) (new ModTrigger()
				.setUnlocalizedName("emeraldDimension_trigger")/* .setTextureName("EmeraldIgnator") */);
		// Item.itemRegistry.addObject(437, "emeraldDimension_trigger", block);
	}

	public EmeraldDimension()
	{
	}

	public void load(FMLInitializationEvent event)
	{
		if (ConfigHandler.enableEmeraldDimension)
		{
			GameRegistry.registerBlock(portal, "emeraldDimension_portal");
			GameRegistry.registerItem(block, "emeraldDimension_trigger");
			DimensionManager.registerProviderType(DIMID, EmeraldDimension.WorldProviderMod.class, false);
			DimensionManager.registerDimension(DIMID, DIMID);

			if (event.getSide() == Side.CLIENT)
				Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(block, 0,
						new ModelResourceLocation("moredimensions:emeraldDimension_trigger", "inventory"));

			GameRegistry.addRecipe(new ItemStack(block, 1), new Object[]
			{ "XXX", "XXX", "X78", Character.valueOf('7'), new ItemStack(Blocks.emerald_block, 1),
					Character.valueOf('8'), new ItemStack(Items.flint, 1), });

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
			this.worldChunkMgr = new WorldChunkManagerHell(EmeraldWorld.biome, 0.0F);
			this.isHellWorld = true;
			this.hasNoSky = true;
			this.dimensionId = DIMID;
		}

		public String getInternalNameSuffix()
		{
			return "_emeraldDimension";
		}

		@SideOnly(Side.CLIENT)
		public Vec3 getFogColor(float par1, float par2)
		{
			return new Vec3(1.0D, 1.0D, 1.0D);
		}

		public IChunkProvider createChunkGenerator()
		{
			return new ChunkProviderModded(this.worldObj, this.worldObj.getSeed() - 20980);
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
			return "emeraldDimension";
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
									flag ? Blocks.emerald_block.getDefaultState() : Blocks.air.getDefaultState());
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
									flag ? Blocks.emerald_block.getDefaultState() : Blocks.air.getDefaultState());
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
								flag1 ? Blocks.emerald_block.getDefaultState() : iblockstate, 2);
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
			if (getBlock(par1World, par2 - 1, par3, par4) == Blocks.emerald_block
					|| getBlock(par1World, par2 + 1, par3, par4) == Blocks.emerald_block)
			{
				b0 = 1;
			}
			if (getBlock(par1World, par2, par3, par4 - 1) == Blocks.emerald_block
					|| getBlock(par1World, par2, par3, par4 + 1) == Blocks.emerald_block)
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
								if (j1 != Blocks.emerald_block)
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
			if (getBlock(par1World, par2, i1 - 1, par4) != Blocks.emerald_block)
			{
				par1World.setBlockToAir(new BlockPos(par2, par3, par4));
			} else
			{
				int j1;
				for (j1 = 1; j1 < 4 && getBlock(par1World, par2, i1 + j1, par4) == this; ++j1)
				{
					;
				}
				if (j1 == 3 && getBlock(par1World, par2, i1 + j1, par4) == Blocks.emerald_block)
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
						if ((getBlock(par1World, par2 + b0, par3, par4 + b1) != Blocks.emerald_block
								|| getBlock(par1World, par2 - b0, par3, par4 - b1) != this)
								&& (getBlock(par1World, par2 - b0, par3, par4 - b1) != Blocks.emerald_block
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
		private Random rand;
		private NoiseGeneratorOctaves field_147431_j;
		private NoiseGeneratorOctaves field_147432_k;
		private NoiseGeneratorOctaves field_147429_l;
		private NoiseGeneratorPerlin field_147430_m;
		/**
		 * A NoiseGeneratorOctaves used in generating terrain
		 */
		public NoiseGeneratorOctaves noiseGen5;
		/**
		 * A NoiseGeneratorOctaves used in generating terrain
		 */
		public NoiseGeneratorOctaves noiseGen6;
		public NoiseGeneratorOctaves mobSpawnerNoise;

		private StructureOceanMonument oceanMonumentGenerator;

		/**
		 * Reference to the World object.
		 */
		private World worldObj;
		/**
		 * are map structures going to be generated (e.g. strongholds)
		 */
		private final boolean mapFeaturesEnabled;
		private WorldType field_147435_p;
		private final double[] field_147434_q;
		private final float[] parabolicField;
		private double[] stoneNoise = new double[256];
		private MapGenBase caveGenerator = new MapGenCaves();
		/**
		 * Holds Stronghold Generator
		 */
		private MapGenStronghold strongholdGenerator = new MapGenStronghold();
		/**
		 * Holds Village Generator
		 */
		private MapGenVillage villageGenerator = new MapGenVillage();
		/**
		 * Holds Mineshaft Generator
		 */
		private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
		private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();
		/**
		 * Holds ravine generator
		 */
		private MapGenBase ravineGenerator = new MapGenRavine();
		/**
		 * The biomes that are used to generate the chunk
		 */
		private BiomeGenBase[] biomesForGeneration;
		double[] field_147427_d;
		double[] field_147428_e;
		double[] field_147425_f;
		double[] field_147426_g;
		int[][] field_73219_j = new int[32][32];

		{
			caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
			strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(strongholdGenerator, STRONGHOLD);
			villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(villageGenerator, VILLAGE);
			mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator, MINESHAFT);
			scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator,
					SCATTERED_FEATURE);
			ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, RAVINE);
		}

		public ChunkProviderModded(World par1World, long par2)
		{
			this.worldObj = par1World;
			this.mapFeaturesEnabled = false;
			this.field_147435_p = par1World.getWorldInfo().getTerrainType();
			this.oceanMonumentGenerator = new StructureOceanMonument();
			this.rand = new Random(par2);
			this.field_147431_j = new NoiseGeneratorOctaves(this.rand, 16);
			this.field_147432_k = new NoiseGeneratorOctaves(this.rand, 16);
			this.field_147429_l = new NoiseGeneratorOctaves(this.rand, 8);
			this.field_147430_m = new NoiseGeneratorPerlin(this.rand, 4);
			this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
			this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
			this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
			this.field_147434_q = new double[825];
			this.parabolicField = new float[25];

			for (int j = -2; j <= 2; ++j)
			{
				for (int k = -2; k <= 2; ++k)
				{
					float f = 10.0F / MathHelper.sqrt_float((float) (j * j + k * k) + 0.2F);
					this.parabolicField[j + 2 + (k + 2) * 5] = f;
				}
			}

			NoiseGenerator[] noiseGens =
			{ field_147431_j, field_147432_k, field_147429_l, field_147430_m, noiseGen5, noiseGen6, mobSpawnerNoise };
			noiseGens = TerrainGen.getModdedNoiseGenerators(par1World, this.rand, noiseGens);
			this.field_147431_j = (NoiseGeneratorOctaves) noiseGens[0];
			this.field_147432_k = (NoiseGeneratorOctaves) noiseGens[1];
			this.field_147429_l = (NoiseGeneratorOctaves) noiseGens[2];
			this.field_147430_m = (NoiseGeneratorPerlin) noiseGens[3];
			this.noiseGen5 = (NoiseGeneratorOctaves) noiseGens[4];
			this.noiseGen6 = (NoiseGeneratorOctaves) noiseGens[5];
			this.mobSpawnerNoise = (NoiseGeneratorOctaves) noiseGens[6];
		}

		public void func_147424_a(int p_147424_1_, int p_147424_2_, Block[] p_147424_3_)
		{
			byte b0 = 63;
			this.biomesForGeneration = this.worldObj.getWorldChunkManager()
					.getBiomesForGeneration(this.biomesForGeneration, p_147424_1_ * 4 - 2, p_147424_2_ * 4 - 2, 10, 10);
			this.func_147423_a(p_147424_1_ * 4, 0, p_147424_2_ * 4);

			for (int k = 0; k < 4; ++k)
			{
				int l = k * 5;
				int i1 = (k + 1) * 5;

				for (int j1 = 0; j1 < 4; ++j1)
				{
					int k1 = (l + j1) * 33;
					int l1 = (l + j1 + 1) * 33;
					int i2 = (i1 + j1) * 33;
					int j2 = (i1 + j1 + 1) * 33;

					for (int k2 = 0; k2 < 32; ++k2)
					{
						double d0 = 0.125D;
						double d1 = this.field_147434_q[k1 + k2];
						double d2 = this.field_147434_q[l1 + k2];
						double d3 = this.field_147434_q[i2 + k2];
						double d4 = this.field_147434_q[j2 + k2];
						double d5 = (this.field_147434_q[k1 + k2 + 1] - d1) * d0;
						double d6 = (this.field_147434_q[l1 + k2 + 1] - d2) * d0;
						double d7 = (this.field_147434_q[i2 + k2 + 1] - d3) * d0;
						double d8 = (this.field_147434_q[j2 + k2 + 1] - d4) * d0;

						for (int l2 = 0; l2 < 8; ++l2)
						{
							double d9 = 0.25D;
							double d10 = d1;
							double d11 = d2;
							double d12 = (d3 - d1) * d9;
							double d13 = (d4 - d2) * d9;

							for (int i3 = 0; i3 < 4; ++i3)
							{
								int j3 = i3 + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + l2;
								short short1 = 256;
								j3 -= short1;
								double d14 = 0.25D;
								double d16 = (d11 - d10) * d14;
								double d15 = d10 - d16;

								for (int k3 = 0; k3 < 4; ++k3)
								{
									if ((d15 += d16) > 0.0D)
									{
										p_147424_3_[j3 += short1] = Blocks.emerald_block;
									} else if (k2 * 8 + l2 < b0)
									{
										p_147424_3_[j3 += short1] = Blocks.emerald_block;
									} else
									{
										p_147424_3_[j3 += short1] = null;
									}
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

		/*
		 * public void func_180517_a(int p_180517_1_, int p_180517_2_, ChunkPrimer p_180517_3_, BiomeGenBase[] p_180517_4_) { ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, p_180517_1_, p_180517_2_, p_180517_3_, this.worldObj); MinecraftForge.EVENT_BUS.post(event); if (event.getResult() == Result.DENY) return;
		 * 
		 * double d0 = 0.03125D; this.stoneNoise = this.field_147430_m.func_151599_a(this.stoneNoise, (double)(p_180517_1_ * 16), (double)(p_180517_2_ * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);
		 * 
		 * for (int k = 0; k < 16; ++k) { for (int l = 0; l < 16; ++l) { BiomeGenBase biomegenbase = p_180517_4_[l + k * 16]; biomegenbase.genTerrainBlocks(this.worldObj, this.rand, p_180517_3_, p_180517_1_ * 16 + k, p_180517_2_ * 16 + l, this.stoneNoise[l + k * 16]); } } }
		 */

		/**
		 * loads or generates the chunk at the chunk location specified
		 */
		public Chunk loadChunk(int par1, int par2)
		{
			return this.provideChunk(par1, par2);
		}

		public List<?> func_177458_a(EnumCreatureType p_177458_1_, BlockPos p_177458_2_)
		{
			BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(p_177458_2_);

			if (this.mapFeaturesEnabled)
			{
				if (p_177458_1_ == EnumCreatureType.MONSTER
						&& this.scatteredFeatureGenerator.func_175798_a(p_177458_2_))
				{
					return this.scatteredFeatureGenerator.getScatteredFeatureSpawnList();
				}

				if (p_177458_1_ == EnumCreatureType.MONSTER
						&& this.oceanMonumentGenerator.func_175796_a(this.worldObj, p_177458_2_))
				{
					return this.oceanMonumentGenerator.func_175799_b();
				}
			}

			return biomegenbase.getSpawnableList(p_177458_1_);
		}

		public void func_180517_a(int p_180517_1_, int p_180517_2_, ChunkPrimer p_180517_3_, BiomeGenBase[] p_180517_4_)
		{
			ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, p_180517_1_,
					p_180517_2_, p_180517_3_, this.worldObj);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.getResult() == Result.DENY)
				return;

			double d0 = 0.03125D;
			this.stoneNoise = this.field_147430_m.func_151599_a(this.stoneNoise, (double) (p_180517_1_ * 16),
					(double) (p_180517_2_ * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

			for (int k = 0; k < 16; ++k)
			{
				for (int l = 0; l < 16; ++l)
				{
					BiomeGenBase biomegenbase = p_180517_4_[l + k * 16];
					biomegenbase.genTerrainBlocks(this.worldObj, this.rand, p_180517_3_, p_180517_1_ * 16 + k,
							p_180517_2_ * 16 + l, this.stoneNoise[l + k * 16]);
				}
			}
		}

		/**
		 * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the specified chunk from the map seed and chunk seed
		 */
		public Chunk provideChunk(int par1, int par2)
		{
			this.rand.setSeed((long) par1 * 341873128712L + (long) par2 * 132897987541L);
			Block[] ablock = new Block[65536];
			this.func_147424_a(par1, par2, ablock);
			ChunkPrimer chunkprimer = new ChunkPrimer();
			this.setBlocksInChunk(par1, par2, chunkprimer);
			this.biomesForGeneration = this.worldObj.getWorldChunkManager()
					.loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
			this.func_180517_a(par1, par2, chunkprimer, this.biomesForGeneration);

			Chunk chunk = new Chunk(this.worldObj, chunkprimer, par1, par2);
			byte[] abyte1 = chunk.getBiomeArray();

			for (int k = 0; k < abyte1.length; ++k)
			{
				abyte1[k] = (byte) this.biomesForGeneration[k].biomeID;
			}

			chunk.generateSkylightMap();
			return chunk;
		}

		public void setBlocksInChunk(int p_180518_1_, int p_180518_2_, ChunkPrimer p_180518_3_)
		{
			this.biomesForGeneration = this.worldObj.getWorldChunkManager()
					.getBiomesForGeneration(this.biomesForGeneration, p_180518_1_ * 4 - 2, p_180518_2_ * 4 - 2, 10, 10);
			this.func_147423_a(p_180518_1_ * 4, 0, p_180518_2_ * 4);

			for (int k = 0; k < 4; ++k)
			{
				int l = k * 5;
				int i1 = (k + 1) * 5;

				for (int j1 = 0; j1 < 4; ++j1)
				{
					int k1 = (l + j1) * 33;
					int l1 = (l + j1 + 1) * 33;
					int i2 = (i1 + j1) * 33;
					int j2 = (i1 + j1 + 1) * 33;

					for (int k2 = 0; k2 < 32; ++k2)
					{
						double d0 = 0.125D;
						double d1 = this.field_147434_q[k1 + k2];
						double d2 = this.field_147434_q[l1 + k2];
						double d3 = this.field_147434_q[i2 + k2];
						double d4 = this.field_147434_q[j2 + k2];
						double d5 = (this.field_147434_q[k1 + k2 + 1] - d1) * d0;
						double d6 = (this.field_147434_q[l1 + k2 + 1] - d2) * d0;
						double d7 = (this.field_147434_q[i2 + k2 + 1] - d3) * d0;
						double d8 = (this.field_147434_q[j2 + k2 + 1] - d4) * d0;

						for (int l2 = 0; l2 < 8; ++l2)
						{
							double d9 = 0.25D;
							double d10 = d1;
							double d11 = d2;
							double d12 = (d3 - d1) * d9;
							double d13 = (d4 - d2) * d9;

							for (int i3 = 0; i3 < 4; ++i3)
							{
								double d14 = 0.25D;
								double d16 = (d11 - d10) * d14;
								double d15 = d10 - d16;

								for (int j3 = 0; j3 < 4; ++j3)
								{
									if ((d15 += d16) > 0.0D)
									{
										p_180518_3_.setBlockState(k * 4 + i3, k2 * 8 + l2, j1 * 4 + j3,
												Blocks.emerald_ore.getDefaultState());
									} else if (k2 * 8 + l2 < 63)
									{
										p_180518_3_.setBlockState(k * 4 + i3, k2 * 8 + l2, j1 * 4 + j3,
												Blocks.emerald_block.getDefaultState());
									}
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

		private void func_147423_a(int p_147423_1_, int p_147423_2_, int p_147423_3_)
		{
			this.field_147426_g = this.noiseGen6.generateNoiseOctaves(this.field_147426_g, p_147423_1_, p_147423_3_, 5,
					5, 200.0D, 200.0D, 0.5D);
			this.field_147427_d = this.field_147429_l.generateNoiseOctaves(this.field_147427_d, p_147423_1_,
					p_147423_2_, p_147423_3_, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
			this.field_147428_e = this.field_147431_j.generateNoiseOctaves(this.field_147428_e, p_147423_1_,
					p_147423_2_, p_147423_3_, 5, 33, 5, 684.412D, 684.412D, 684.412D);
			this.field_147425_f = this.field_147432_k.generateNoiseOctaves(this.field_147425_f, p_147423_1_,
					p_147423_2_, p_147423_3_, 5, 33, 5, 684.412D, 684.412D, 684.412D);
			int l = 0;
			int i1 = 0;
			for (int j1 = 0; j1 < 5; ++j1)
			{
				for (int k1 = 0; k1 < 5; ++k1)
				{
					float f = 0.0F;
					float f1 = 0.0F;
					float f2 = 0.0F;
					byte b0 = 2;
					BiomeGenBase biomegenbase = this.biomesForGeneration[j1 + 2 + (k1 + 2) * 10];

					for (int l1 = -b0; l1 <= b0; ++l1)
					{
						for (int i2 = -b0; i2 <= b0; ++i2)
						{
							BiomeGenBase biomegenbase1 = this.biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
							float f3 = biomegenbase1.minHeight;
							float f4 = biomegenbase1.maxHeight;

							if (this.field_147435_p == WorldType.AMPLIFIED && f3 > 0.0F)
							{
								f3 = 1.0F + f3 * 2.0F;
								f4 = 1.0F + f4 * 4.0F;
							}

							float f5 = this.parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);

							if (biomegenbase1.minHeight > biomegenbase.minHeight)
							{
								f5 /= 2.0F;
							}

							f += f4 * f5;
							f1 += f3 * f5;
							f2 += f5;
						}
					}

					f /= f2;
					f1 /= f2;
					f = f * 0.9F + 0.1F;
					f1 = (f1 * 4.0F - 1.0F) / 8.0F;
					double d13 = this.field_147426_g[i1] / 8000.0D;

					if (d13 < 0.0D)
					{
						d13 = -d13 * 0.3D;
					}

					d13 = d13 * 3.0D - 2.0D;

					if (d13 < 0.0D)
					{
						d13 /= 2.0D;

						if (d13 < -1.0D)
						{
							d13 = -1.0D;
						}

						d13 /= 1.4D;
						d13 /= 2.0D;
					} else
					{
						if (d13 > 1.0D)
						{
							d13 = 1.0D;
						}

						d13 /= 8.0D;
					}

					++i1;
					double d12 = (double) f1;
					double d14 = (double) f;
					d12 += d13 * 0.2D;
					d12 = d12 * 8.5D / 8.0D;
					double d5 = 8.5D + d12 * 4.0D;

					for (int j2 = 0; j2 < 33; ++j2)
					{
						double d6 = ((double) j2 - d5) * 12.0D * 128.0D / 256.0D / d14;

						if (d6 < 0.0D)
						{
							d6 *= 4.0D;
						}

						double d7 = this.field_147428_e[l] / 512.0D;
						double d8 = this.field_147425_f[l] / 512.0D;
						double d9 = (this.field_147427_d[l] / 10.0D + 1.0D) / 2.0D;
						double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

						if (j2 > 29)
						{
							double d11 = (double) ((float) (j2 - 29) / 3.0F);
							d10 = d10 * (1.0D - d11) + -10.0D * d11;
						}

						this.field_147434_q[l] = d10;
						++l;
					}
				}
			}
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
			int k = par2 * 16;
			int l = par3 * 16;
			BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(k + 16, 0, l + 16));// warn
			this.rand.setSeed(this.worldObj.getSeed());
			long i1 = this.rand.nextLong() / 2L * 2L + 1L;
			long j1 = this.rand.nextLong() / 2L * 2L + 1L;
			this.rand.setSeed((long) par2 * i1 + (long) par3 * j1 ^ this.worldObj.getSeed());
			boolean flag = false;

			MinecraftForge.EVENT_BUS
					.post(new PopulateChunkEvent.Pre(par1IChunkProvider, worldObj, rand, par2, par3, flag));

			int k1;
			int l1;
			int i2;

			if (biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills && !flag
					&& this.rand.nextInt(4) == 0
					&& TerrainGen.populate(par1IChunkProvider, worldObj, rand, par2, par3, flag, LAKE))
			{
				k1 = k + this.rand.nextInt(16) + 8;
				l1 = this.rand.nextInt(256);
				i2 = l + this.rand.nextInt(16) + 8;
				(new WorldGenLakes(Blocks.emerald_block)).generate(this.worldObj, this.rand, new BlockPos(k1, l1, i2));
			}

			if (TerrainGen.populate(par1IChunkProvider, worldObj, rand, par2, par3, flag, LAVA) && !flag
					&& this.rand.nextInt(8) == 0)
			{
				k1 = k + this.rand.nextInt(16) + 8;
				l1 = this.rand.nextInt(this.rand.nextInt(248) + 8);
				i2 = l + this.rand.nextInt(16) + 8;

				if (l1 < 63 || this.rand.nextInt(10) == 0)
				{
					(new WorldGenLakes(Blocks.emerald_block)).generate(this.worldObj, this.rand,
							new BlockPos(k1, l1, i2));
				}
			}
			biomegenbase.decorate(this.worldObj, this.rand, new BlockPos(k, 0, l));
			SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, k + 8, l + 8, 16, 16, this.rand);
			k += 8;
			l += 8;

			MinecraftForge.EVENT_BUS
					.post(new PopulateChunkEvent.Post(par1IChunkProvider, worldObj, rand, par2, par3, flag));

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

		/*
		 * public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_) { return "Stronghold".equals(p_147416_2_) && this.strongholdGenerator != null ? this.strongholdGenerator.func_151545_a(p_147416_1_, p_147416_3_, p_147416_4_, p_147416_5_) : null; }
		 */

		public int getLoadedChunkCount()
		{
			return 0;
		}

		public BlockPos getStrongholdGen(World worldIn, String p_180513_2_, BlockPos p_180513_3_)
		{
			return null;
		}

		public void recreateStructures(Chunk c, int par1, int par2)
		{

		}

		public boolean func_177460_a(IChunkProvider p_177460_1_, Chunk p_177460_2_, int p_177460_3_, int p_177460_4_)
		{
			boolean flag = false;

			if (this.mapFeaturesEnabled && p_177460_2_.getInhabitedTime() < 3600L)
			{
				flag |= this.oceanMonumentGenerator.func_175794_a(this.worldObj, this.rand,
						new ChunkCoordIntPair(p_177460_3_, p_177460_4_));
			}

			return flag;
		}

		public Chunk provideChunk(BlockPos blockPosIn)
		{
			return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
		}
	}

	// helpers
	public static Block getBlock(IBlockAccess world, int i, int j, int k)
	{
		return world.getBlockState(new BlockPos(i, j, k)).getBlock();
	}

}
