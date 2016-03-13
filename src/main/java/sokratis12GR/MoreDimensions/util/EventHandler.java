package sokratis12GR.MoreDimensions.util;

import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sokratis12GR.MoreDimensions.MoreDimensions;
import sokratis12GR.MoreDimensions.ConfigHandler;

public class EventHandler {

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(MoreDimensions.MODID)) {
            ConfigHandler.syncConfig();
            MoreDimensions.logger.info(TextHelper.localize("info." + MoreDimensions.MODID + ".console.config.refresh"));
        }
    }
}