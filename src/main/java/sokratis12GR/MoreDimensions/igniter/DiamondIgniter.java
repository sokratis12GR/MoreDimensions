package sokratis12GR.MoreDimensions.igniter;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.*;
import sokratis12GR.MoreDimensions.dimensions.DiamondDimension;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import java.util.Random;

public class DiamondIgniter{

public DiamondIgniter(){}

public Object instance;public void load(FMLInitializationEvent event){
ItemStack recStack = new ItemStack(DiamondDimension.block, 1);


GameRegistry.addShapelessRecipe(recStack, new Object[]{
new ItemStack(Blocks.diamond_block, 1), new ItemStack(Items.flint, 1), 
});}
public void generateNether(World world, Random random, int chunkX, int chunkZ){}
public void generateSurface(World world, Random random, int chunkX, int chunkZ){}
public int addFuel(ItemStack fuel){return 0;}
public void serverLoad(FMLServerStartingEvent event){}
public void preInit(FMLPreInitializationEvent event){}
public void registerRenderers(){}
}
