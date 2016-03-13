package sokratis12GR.MoreDimensions;

import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sokratis12GR.MoreDimensions.MoreDimensions;
import sokratis12GR.MoreDimensions.CommonProxy;
import sokratis12GR.MoreDimensions.biomes.CoalWorld;
import sokratis12GR.MoreDimensions.biomes.DiamondWorld;
import sokratis12GR.MoreDimensions.biomes.EmeraldWorld;
import sokratis12GR.MoreDimensions.biomes.GlassWorld;
import sokratis12GR.MoreDimensions.biomes.GoldWorld;
import sokratis12GR.MoreDimensions.biomes.IronWorld;
import sokratis12GR.MoreDimensions.biomes.LapisWorld;
import sokratis12GR.MoreDimensions.biomes.ObsidianWorld;
import sokratis12GR.MoreDimensions.biomes.QuartzWorld;
import sokratis12GR.MoreDimensions.biomes.RedstoneWorld;
import sokratis12GR.MoreDimensions.biomes.SnowWorld;
import sokratis12GR.MoreDimensions.biomes.VoidWorld;
import sokratis12GR.MoreDimensions.biomes.WoodWorld;
import sokratis12GR.MoreDimensions.dimensions.CoalDimension;
import sokratis12GR.MoreDimensions.dimensions.DiamondDimension;
import sokratis12GR.MoreDimensions.dimensions.EmeraldDimension;
import sokratis12GR.MoreDimensions.dimensions.GlassDimension;
import sokratis12GR.MoreDimensions.dimensions.GoldDimension;
import sokratis12GR.MoreDimensions.dimensions.IronDimension;
import sokratis12GR.MoreDimensions.dimensions.LapisDimension;
import sokratis12GR.MoreDimensions.dimensions.ObsidianDimension;
import sokratis12GR.MoreDimensions.dimensions.QuartzDimension;
import sokratis12GR.MoreDimensions.dimensions.RedstoneDimension;
import sokratis12GR.MoreDimensions.dimensions.SnowDimension;
import sokratis12GR.MoreDimensions.dimensions.VoidDimension;
import sokratis12GR.MoreDimensions.dimensions.WoodDimension;
import sokratis12GR.MoreDimensions.igniter.CoalIgniter;
import sokratis12GR.MoreDimensions.igniter.DiamondIgniter;
import sokratis12GR.MoreDimensions.igniter.EmeraldIgniter;
import sokratis12GR.MoreDimensions.igniter.GoldIgniter;
import sokratis12GR.MoreDimensions.igniter.IronIgniter;
import sokratis12GR.MoreDimensions.igniter.LapisIgniter;
import sokratis12GR.MoreDimensions.igniter.ObsidianIgniter;
import sokratis12GR.MoreDimensions.igniter.RedstoneIgniter;
import sokratis12GR.MoreDimensions.igniter.VoidIgniter;
import sokratis12GR.MoreDimensions.igniter.WoodIgniter;
import sokratis12GR.MoreDimensions.util.TextHelper;

import java.io.File;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = MoreDimensions.MODID, name = MoreDimensions.MODNAME, version = MoreDimensions.VERSION, dependencies = MoreDimensions.DEPEND, guiFactory = MoreDimensions.GUIFACTORY)
public class MoreDimensions implements IWorldGenerator, IFuelHandler
{

	public static final String MODNAME = "MoreDimensions";
	public static final String MODID = "moredimensions";
	public static final String CHANNEL = "MoreDimensions";
	public static final String DEPEND = "";
	public static final String VERSION = "1.5";
	public static final String CLIENTPROXY = "sokratis12GR.MoreDimensions.ClientProxy";
	public static final String COMMONPROXY = "sokratis12GR.MoreDimensions.CommonProxy";
	public static final String GUIFACTORY = "sokratis12GR.MoreDimensions.client.gui.ConfigGuiFactory";

	@SidedProxy(clientSide = MoreDimensions.CLIENTPROXY, serverSide = MoreDimensions.COMMONPROXY)
	public static CommonProxy proxy;
	public static Logger logger = LogManager.getLogger(MoreDimensions.MODNAME);

	@Instance(MODID)
	public static MoreDimensions instance;
	private static File configDir;

	public static File getConfigDir()
	{
		return configDir;
	}

	LapisWorld LapisWorld = new LapisWorld();
	LapisDimension LapisDimension = new LapisDimension();
	CoalWorld CoalWorld = new CoalWorld();
	CoalDimension CoalDimension = new CoalDimension();
	RedstoneWorld RedstoneWorld = new RedstoneWorld();
	RedstoneDimension RedstoneDimension = new RedstoneDimension();
	IronWorld IronWorld = new IronWorld();
	IronDimension IronDimension = new IronDimension();
	GoldWorld GoldWorld = new GoldWorld();
	GoldDimension GoldDimension = new GoldDimension();
	EmeraldWorld EmeraldWorld = new EmeraldWorld();
	EmeraldDimension EmeraldDimension = new EmeraldDimension();
	DiamondWorld DiamondWorld = new DiamondWorld();
	DiamondDimension DiamondDimension = new DiamondDimension();
	WoodWorld WoodWorld = new WoodWorld();
	WoodDimension WoodDimension = new WoodDimension();
	VoidWorld VoidWorld = new VoidWorld();
	VoidDimension VoidDimension = new VoidDimension();
	SnowWorld SnowWorld = new SnowWorld();
	SnowDimension SnowDimension = new SnowDimension();
	QuartzWorld QuartzWorld = new QuartzWorld();
	QuartzDimension QuartzDimension = new QuartzDimension();
	GlassWorld GlassWorld = new GlassWorld();
	GlassDimension GlassDimension = new GlassDimension();
	ObsidianWorld ObsidianWorld = new ObsidianWorld();
	LapisIgniter LapisIgniter = new LapisIgniter();
	CoalIgniter CoalIgniter = new CoalIgniter();
	RedstoneIgniter RedstoneIgniter = new RedstoneIgniter();
	IronIgniter IronIgniter = new IronIgniter();
	GoldIgniter GoldIgniter = new GoldIgniter();
	DiamondIgniter DiamondIgniter = new DiamondIgniter();
	EmeraldIgniter EmeraldIgniter = new EmeraldIgniter();
	WoodIgniter WoodIgniter = new WoodIgniter();
	VoidIgniter VoidIgniter = new VoidIgniter();
	ObsidianDimension ObsidianDimension = new ObsidianDimension();
	ObsidianIgniter ObsidianIgniter = new ObsidianIgniter();

	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator,
			IChunkProvider chunkProvider)
	{

		chunkX = chunkX * 16;
		chunkZ = chunkZ * 16;
		if (world.provider.getDimensionId() == -1)
			LapisWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			LapisWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			LapisDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			LapisDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			CoalWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			CoalWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			CoalDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			CoalDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			RedstoneWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			RedstoneWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			RedstoneDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			RedstoneDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			IronWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			IronWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			IronDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			IronDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			GoldWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			GoldWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			GoldDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			GoldDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			EmeraldWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			EmeraldWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			EmeraldDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			EmeraldDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			DiamondWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			DiamondWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			DiamondDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			DiamondDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			WoodWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			WoodWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			WoodDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			WoodDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			VoidWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			VoidWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			VoidDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			VoidDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			SnowWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			SnowWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			SnowDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			SnowDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			QuartzWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			QuartzWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			QuartzDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			QuartzDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			GlassWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			GlassWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			GlassDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			GlassDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			ObsidianWorld.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			ObsidianWorld.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			LapisIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			LapisIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			CoalIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			CoalIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			RedstoneIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			RedstoneIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			IronIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			IronIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			GoldIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			GoldIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			DiamondIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			DiamondIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			EmeraldIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			EmeraldIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			WoodIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			WoodIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			VoidIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			VoidIgniter.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			ObsidianDimension.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			ObsidianDimension.generateSurface(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == -1)
			ObsidianIgniter.generateNether(world, random, chunkX, chunkZ);
		if (world.provider.getDimensionId() == 0)
			ObsidianIgniter.generateSurface(world, random, chunkX, chunkZ);

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{

		GameRegistry.registerFuelHandler(this);
		GameRegistry.registerWorldGenerator(this, 1);
		MinecraftForge.EVENT_BUS.register(new GlobalEventsMoreDimensions());
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		LapisWorld.load(event);
		LapisDimension.load(event);
		CoalWorld.load(event);
		CoalDimension.load(event);
		RedstoneWorld.load(event);
		RedstoneDimension.load(event);
		IronWorld.load(event);
		IronDimension.load(event);
		GoldWorld.load(event);
		GoldDimension.load(event);
		EmeraldWorld.load(event);
		EmeraldDimension.load(event);
		DiamondWorld.load(event);
		DiamondDimension.load(event);
		WoodWorld.load(event);
		WoodDimension.load(event);
		VoidWorld.load(event);
		VoidDimension.load(event);
		SnowWorld.load(event);
		SnowDimension.load(event);
		QuartzWorld.load(event);
		QuartzDimension.load(event);
		GlassWorld.load(event);
		GlassDimension.load(event);
		ObsidianWorld.load(event);
		LapisIgniter.load(event);
		CoalIgniter.load(event);
		RedstoneIgniter.load(event);
		IronIgniter.load(event);
		GoldIgniter.load(event);
		DiamondIgniter.load(event);
		EmeraldIgniter.load(event);
		WoodIgniter.load(event);
		VoidIgniter.load(event);
		ObsidianDimension.load(event);
		ObsidianIgniter.load(event);

	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		LapisWorld.serverLoad(event);
		LapisDimension.serverLoad(event);
		CoalWorld.serverLoad(event);
		CoalDimension.serverLoad(event);
		RedstoneWorld.serverLoad(event);
		RedstoneDimension.serverLoad(event);
		IronWorld.serverLoad(event);
		IronDimension.serverLoad(event);
		GoldWorld.serverLoad(event);
		GoldDimension.serverLoad(event);
		EmeraldWorld.serverLoad(event);
		EmeraldDimension.serverLoad(event);
		DiamondWorld.serverLoad(event);
		DiamondDimension.serverLoad(event);
		WoodWorld.serverLoad(event);
		WoodDimension.serverLoad(event);
		VoidWorld.serverLoad(event);
		VoidDimension.serverLoad(event);
		SnowWorld.serverLoad(event);
		SnowDimension.serverLoad(event);
		QuartzWorld.serverLoad(event);
		QuartzDimension.serverLoad(event);
		GlassWorld.serverLoad(event);
		GlassDimension.serverLoad(event);
		ObsidianWorld.serverLoad(event);
		LapisIgniter.serverLoad(event);
		CoalIgniter.serverLoad(event);
		RedstoneIgniter.serverLoad(event);
		IronIgniter.serverLoad(event);
		GoldIgniter.serverLoad(event);
		DiamondIgniter.serverLoad(event);
		EmeraldIgniter.serverLoad(event);
		WoodIgniter.serverLoad(event);
		VoidIgniter.serverLoad(event);
		ObsidianDimension.serverLoad(event);
		ObsidianIgniter.serverLoad(event);
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		LapisWorld.instance = MoreDimensions.instance;
		LapisDimension.instance = MoreDimensions.instance;
		CoalWorld.instance = MoreDimensions.instance;
		CoalDimension.instance = MoreDimensions.instance;
		RedstoneWorld.instance = MoreDimensions.instance;
		RedstoneDimension.instance = MoreDimensions.instance;
		IronWorld.instance = MoreDimensions.instance;
		IronDimension.instance = MoreDimensions.instance;
		GoldWorld.instance = MoreDimensions.instance;
		GoldDimension.instance = MoreDimensions.instance;
		EmeraldWorld.instance = MoreDimensions.instance;
		EmeraldDimension.instance = MoreDimensions.instance;
		DiamondWorld.instance = MoreDimensions.instance;
		DiamondDimension.instance = MoreDimensions.instance;
		WoodWorld.instance = MoreDimensions.instance;
		WoodDimension.instance = MoreDimensions.instance;
		VoidWorld.instance = MoreDimensions.instance;
		VoidDimension.instance = MoreDimensions.instance;
		SnowWorld.instance = MoreDimensions.instance;
		SnowDimension.instance = MoreDimensions.instance;
		QuartzWorld.instance = MoreDimensions.instance;
		QuartzDimension.instance = MoreDimensions.instance;
		GlassWorld.instance = MoreDimensions.instance;
		GlassDimension.instance = MoreDimensions.instance;
		ObsidianWorld.instance = MoreDimensions.instance;
		LapisIgniter.instance = MoreDimensions.instance;
		CoalIgniter.instance = MoreDimensions.instance;
		RedstoneIgniter.instance = MoreDimensions.instance;
		IronIgniter.instance = MoreDimensions.instance;
		GoldIgniter.instance = MoreDimensions.instance;
		DiamondIgniter.instance = MoreDimensions.instance;
		EmeraldIgniter.instance = MoreDimensions.instance;
		WoodIgniter.instance = MoreDimensions.instance;
		VoidIgniter.instance = MoreDimensions.instance;
		ObsidianDimension.instance = MoreDimensions.instance;
		ObsidianIgniter.instance = MoreDimensions.instance;
		LapisWorld.preInit(event);
		LapisDimension.preInit(event);
		CoalWorld.preInit(event);
		CoalDimension.preInit(event);
		RedstoneWorld.preInit(event);
		RedstoneDimension.preInit(event);
		IronWorld.preInit(event);
		IronDimension.preInit(event);
		GoldWorld.preInit(event);
		GoldDimension.preInit(event);
		EmeraldWorld.preInit(event);
		EmeraldDimension.preInit(event);
		DiamondWorld.preInit(event);
		DiamondDimension.preInit(event);
		WoodWorld.preInit(event);
		WoodDimension.preInit(event);
		VoidWorld.preInit(event);
		VoidDimension.preInit(event);
		SnowWorld.preInit(event);
		SnowDimension.preInit(event);
		QuartzWorld.preInit(event);
		QuartzDimension.preInit(event);
		GlassWorld.preInit(event);
		GlassDimension.preInit(event);
		ObsidianWorld.preInit(event);
		LapisIgniter.preInit(event);
		CoalIgniter.preInit(event);
		RedstoneIgniter.preInit(event);
		IronIgniter.preInit(event);
		GoldIgniter.preInit(event);
		DiamondIgniter.preInit(event);
		EmeraldIgniter.preInit(event);
		WoodIgniter.preInit(event);
		VoidIgniter.preInit(event);
		ObsidianDimension.preInit(event);
		ObsidianIgniter.preInit(event);
		configDir = new File(event.getModConfigurationDirectory() + "/" + MoreDimensions.MODID);
		configDir.mkdirs();
		ConfigHandler.init(new File(configDir.getPath(), MoreDimensions.MODID + ".cfg"));
		proxy.registerRenderers(this);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		logger.info(TextHelper.localize("info." + MoreDimensions.MODID + ".console.load.init"));
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		logger.info(TextHelper.localize("info." + MoreDimensions.MODID + ".console.load.postInit"));
	}

	public static class GuiHandler implements IGuiHandler
	{
		@Override
		public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
		{
			return null;
		}

		@Override
		public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
		{
			return null;
		}
	}

	@Override
	public int getBurnTime(ItemStack fuel)
	{
		// TODO Auto-generated method stub
		return 0;
	}

}