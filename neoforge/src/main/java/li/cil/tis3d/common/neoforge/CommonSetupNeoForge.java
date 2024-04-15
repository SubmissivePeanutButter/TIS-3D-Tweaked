package li.cil.tis3d.common.neoforge;

import li.cil.tis3d.api.API;
import li.cil.tis3d.common.CommonSetup;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = API.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonSetupNeoForge {
    @SubscribeEvent
    public static void handleCommonSetup(final FMLCommonSetupEvent event) {
        CommonSetup.run();
    }
}
