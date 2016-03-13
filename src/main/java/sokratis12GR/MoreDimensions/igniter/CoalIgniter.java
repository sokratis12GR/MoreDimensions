package sokratis12GR.MoreDimensions.igniter;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sokratis12GR.MoreDimensions.dimensions.CoalDimension;

public class CoalIgniter{

public CoalIgniter(){}

public Object instance;public void load(FMLInitializationEvent event){
ItemStack recStack = new ItemStack(CoalDimension.block, 1);


GameRegistry.addShapelessRecipe(recStack, new Object[]{
new ItemStack(Blocks.coal_block, 1), new ItemStack(Items.flint, 1), 
});}
public void generateNether(World world, Random random, int chunkX, int chunkZ){}
public void generateSurface(World world, Random random, int chunkX, int chunkZ){}
public int addFuel(ItemStack fuel){return 0;}
public void serverLoad(FMLServerStartingEvent event){}
public void preInit(FMLPreInitializationEvent event){}
public void registerRenderers(){}
}
