package sokratis12GR.MoreDimensions.client.gui;

import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import sokratis12GR.MoreDimensions.ConfigHandler;
import sokratis12GR.MoreDimensions.MoreDimensions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.config.ConfigElement;
import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends GuiConfig {

    public ConfigGui(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(parentScreen), MoreDimensions.MODID, false, false, StatCollector.translateToLocal("config.MoreDimensions.title"));
    }

    private static List<IConfigElement> getConfigElements(GuiScreen parent) {
        List<IConfigElement> list = new ArrayList<IConfigElement>();

        // adds sections declared in ConfigHandler. toLowerCase() is used because the configuration class automatically does this, so must we.
        list.add(new ConfigElement(ConfigHandler.config.getCategory("Dimensions".toLowerCase())));

        return list;
    }
}