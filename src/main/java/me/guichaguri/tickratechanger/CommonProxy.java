package me.guichaguri.tickratechanger;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;

public class CommonProxy {
    public void preInit(){
        TickrateChanger.NETWORK = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(TickrateChanger.MODID, "tickratechanger"),
                () -> TickrateChanger.NETWORK_VERSION,
                TickrateChanger.NETWORK_VERSION::equals,
                TickrateChanger.NETWORK_VERSION::equals
        );

        TickrateMessageHandler handler = new TickrateMessageHandler();

        TickrateChanger.NETWORK.registerMessage(
                0,
                TickrateMessageHandler.TickrateMessage.class,
                TickrateMessageHandler.TickrateMessage::encode,
                TickrateMessageHandler.TickrateMessage::decode,
                handler::handle
        );

        //Reading out data from the Config file
        TickrateChanger.DEFAULT_TICKRATE=TickrateConfig.GENERAL.defaultTickrate.get().floatValue();
        TickrateChanger.MAX_TICKRATE=TickrateConfig.GENERAL.MAXtickrate.get().floatValue();
        TickrateChanger.MIN_TICKRATE=TickrateConfig.GENERAL.MINtickrate.get().floatValue();
        TickrateChanger.CHANGE_SOUND=TickrateConfig.GENERAL.ChangeSound.get().booleanValue();
        TickrateContainer.KEYS_AVAILABLE=TickrateConfig.GENERAL.ShowKeyBindings.get().booleanValue();
        TickrateChanger.SHOW_MESSAGES=TickrateConfig.GENERAL.ShowMessage.get().booleanValue();
    }

    public void postInit(){
    }
}
