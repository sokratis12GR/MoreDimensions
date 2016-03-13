package sokratis12GR.MoreDimensions;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ConfigHandler
{

	public static Configuration config;

	// Settings
	public static boolean enableCoalDimension;
	public static boolean enableLapisDimension;
	public static boolean enableRedstoneDimension;
	public static boolean enableGoldDimension;
	public static boolean enableIronDimension;
	public static boolean enableDiamondDimension;
	public static boolean enableEmeraldDimension;
	public static boolean enableGlassDimension;
	public static boolean enableWoodDimension;
	public static boolean enableQuartzDimension;
	public static boolean enableSnowDimension;
	public static boolean enableObsidianDimension;
	public static boolean enableVoidDimension;

	public static void init(File file)
	{
		config = new Configuration(file);
		syncConfig();
	}

	public static void syncConfig()
	{
		String category;

		category = "Dimensions";
		config.setCategoryLanguageKey("Dimensions", "config.dimensions");
		enableCoalDimension = config.getBoolean("enableCoalDimension", category, true,
				"Enable/Disable The Coal Dimension");
		enableLapisDimension = config.getBoolean("enableLapisDimension", category, true,
				"Enable/Disable The Lapis Dimension");
		enableRedstoneDimension = config.getBoolean("enableRedstoneDimension", category, true,
				"Enable/Disable The Redstone Dimension");
		enableGoldDimension = config.getBoolean("enableGoldDimension", category, true,
				"Enable/Disable The Gold Dimension");
		enableIronDimension = config.getBoolean("enableIronDimension", category, true,
				"Enable/Disable The Iron Dimension");
		enableDiamondDimension = config.getBoolean("enableDiamondDimension", category, true,
				"Enable/Disable The Diamond Dimension");
		enableEmeraldDimension = config.getBoolean("enableEmeraldDimension", category, true,
				"Enable/Disable The Emerald Dimension");
		enableGlassDimension = config.getBoolean("enableGlassDimension", category, true,
				"Enable/Disable The Glass Dimension");
		enableWoodDimension = config.getBoolean("enableWoodDimension", category, true,
				"Enable/Disable The Wood Dimension");
		enableQuartzDimension = config.getBoolean("enableQuartzDimension", category, true,
				"Enable/Disable The Quartz Dimension");
		enableSnowDimension = config.getBoolean("enableSnowDimension", category, true,
				"Enable/Disable The Snow Dimension");
		enableObsidianDimension = config.getBoolean("enableObsidianDimension", category, true,
				"Enable/Disable The Obsidian Dimension");
		enableVoidDimension = config.getBoolean("enableVoidDimension", category, true,
				"Enable/Disable The Void Dimension");

		if (config.hasChanged())
			config.save();
	}
}