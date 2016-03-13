package sokratis12GR.MoreDimensions.biomes;
import net.minecraftforge.fml.common.event.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.*;
import net.minecraft.init.*;
import java.util.Random;

public class ObsidianWorld {

public static BiomeGenobsidianWorld biome = new BiomeGenobsidianWorld();

public Object instance;

public ObsidianWorld(){}

public void load(FMLInitializationEvent event){
BiomeDictionary.registerBiomeType(biome, BiomeDictionary.Type.FOREST);
BiomeManager.addSpawnBiome(biome);
BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(biome, 0));
}

public void generateNether(World world, Random random, int chunkX, int chunkZ){}
public void generateSurface(World world, Random random, int chunkX, int chunkZ){}
public void registerRenderers(){}
public int addFuel(ItemStack fuel){
	return 0;
}
public void serverLoad(FMLServerStartingEvent event){}
public void preInit(FMLPreInitializationEvent event){}

static class BiomeGenobsidianWorld extends BiomeGenBase
{
	public BiomeGenobsidianWorld()
    {
        super(52);
        setBiomeName("Obsidian World");
        topBlock = Blocks.obsidian.getDefaultState();
        fillerBlock = Blocks.obsidian.getDefaultState();
        theBiomeDecorator.generateLakes = false;
	theBiomeDecorator.treesPerChunk = 0;
	theBiomeDecorator.flowersPerChunk = 0;
	theBiomeDecorator.grassPerChunk = 0;
	theBiomeDecorator.deadBushPerChunk = 0;
	theBiomeDecorator.mushroomsPerChunk = 0;
	theBiomeDecorator.reedsPerChunk = 0;
	theBiomeDecorator.cactiPerChunk = 0;
   	theBiomeDecorator.sandPerChunk = 0;
   	rainfall = 0.0F;
   	setHeight(new BiomeGenBase.Height(0.1F, 0.3F));
   	
   	
this.spawnableMonsterList.clear();
this.spawnableCreatureList.clear();
this.spawnableWaterCreatureList.clear();
this.spawnableCaveCreatureList.clear();

    }

    
    
}

}