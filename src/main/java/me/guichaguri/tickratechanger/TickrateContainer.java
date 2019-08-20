package me.guichaguri.tickratechanger;

import me.guichaguri.tickratechanger.api.TickrateAPI;
import me.guichaguri.tickratechanger.command.TickrateCommand;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Guilherme Chaguri
 */
@Mod(TickrateChanger.MODID)
public class TickrateContainer {
    public static final Logger logger = LogManager.getLogger("TickrateChanger");
    public static boolean KEYS_AVAILABLE = false;


    private final CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public static KeyBinding SWITCH_SPEED_KEYBIND = null;


    public TickrateContainer() {
        logger.info("Initializing TickrateContainer!");

        //Config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TickrateConfig.ConfigSpec);
        TickrateConfig.loadConfig(TickrateConfig.ConfigSpec, FMLPaths.CONFIGDIR.get().resolve("tickratechanger-common.toml"));

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus eventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::postInit);
        modEventBus.addListener(this::onClientModStart);
        modEventBus.addListener(this::onServerModStart);
        modEventBus.addListener(this::configGet);

        eventBus.addListener(this::onServerStart);
        eventBus.addListener(this::onChat);
        eventBus.addListener(this::onConnect);

        new TickrateChanger();
    }

    public void preInit(FMLCommonSetupEvent event) {
        proxy.preInit();
    }

    public void postInit(InterModProcessEvent event) {
        proxy.postInit();
    }

//    TODO
//    public void imc(IMCEvent event) {
//        for(IMCMessage msg : event.getMessages()) {
//            if(!msg.key.equalsIgnoreCase("tickrate")) continue;
//
//            try {
//                TickrateAPI.processIMC(msg);
//            } catch(Exception ex) {}
//        }
//    }

    public void onServerStart(FMLServerStartingEvent event) {
        TickrateChanger.COMMAND = new TickrateCommand(event.getCommandDispatcher());
    }

    public void onClientModStart(FMLClientSetupEvent event) {
        if(KEYS_AVAILABLE) {
            ClientRegistry.registerKeyBinding(ClientProxy.KEY_5);
            ClientRegistry.registerKeyBinding(ClientProxy.KEY_10);
            ClientRegistry.registerKeyBinding(ClientProxy.KEY_15);
            ClientRegistry.registerKeyBinding(ClientProxy.KEY_20);
            ClientRegistry.registerKeyBinding(ClientProxy.KEY_40);
            ClientRegistry.registerKeyBinding(ClientProxy.KEY_60);
            ClientRegistry.registerKeyBinding(ClientProxy.KEY_100);
        }
    }

    public void onServerModStart(FMLDedicatedServerSetupEvent event) {
    }

    public  void configGet(ModConfig.ModConfigEvent ev){
        TickrateConfig.setModConfig(ev.getConfig());
    }
//    TODO
//    public void onDisconnect(ClientDisconnectionFromServerEvent event) {
//        TickrateAPI.changeServerTickrate(TickrateChanger.DEFAULT_TICKRATE);
//        TickrateAPI.changeClientTickrate(null, TickrateChanger.DEFAULT_TICKRATE);
//    }
//
//    public void onConnect(ClientConnectedToServerEvent event) {
//        if(event.isLocal()) {
//            float tickrate = TickrateChanger.DEFAULT_TICKRATE;
//
//            try {
//                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
//
//                if(server != null) {
//                    GameRules rules = server.getWorld(DimensionType.OVERWORLD).getGameRules();
//
//                    GameRules.Value ruleValue = rules.get(TickrateChanger.GAME_RULE);
//
//                    if (ruleValue != null) {
//                        String ruleValueString = ruleValue.getString();
//
//                        tickrate = Float.parseFloat(ruleValueString);
//                    }
//                }
//            } catch(Exception ex) {
//                ex.printStackTrace();
//            }
//
//            TickrateAPI.changeServerTickrate(tickrate);
//            TickrateAPI.changeClientTickrate(null, tickrate);
//        } else {
//            TickrateAPI.changeClientTickrate(null, 20F);
//        }
//    }

    public void onChat(ClientChatReceivedEvent event) {
        ITextComponent message = event.getMessage();

        if (message instanceof TextComponentTranslation) {

            TextComponentTranslation t = (TextComponentTranslation) message;
            if (t.getKey().equals("tickratechanger.show.clientside")) {
                event.setMessage(TickrateCommand.clientTickrateMsg());
            }

        }
    }

    public void onConnect(PlayerLoggedInEvent event) {
        if (EffectiveSide.get() == LogicalSide.SERVER) {
            float tickrate = TickrateChanger.DEFAULT_TICKRATE;

            try {
                MinecraftServer server = event.getPlayer().getServer();

                if (server != null) {
                    GameRules rules = server.getWorld(DimensionType.OVERWORLD).getGameRules();

                    GameRules.Value ruleValue = rules.get(TickrateChanger.GAME_RULE);

                    if (ruleValue != null) {
                        String ruleValueString = ruleValue.getString();

                        tickrate = Float.parseFloat(ruleValueString);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            TickrateAPI.changeClientTickrate(event.getPlayer(), tickrate);
        }
    }
}
