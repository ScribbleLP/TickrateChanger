package me.guichaguri.tickratechanger;

import me.guichaguri.tickratechanger.TickrateMessageHandler.TickrateMessage;
import me.guichaguri.tickratechanger.api.TickrateAPI;
import me.guichaguri.tickratechanger.command.TickrateCommand;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
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
import net.minecraftforge.fml.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

/**
 * @author Guilherme Chaguri
 */
@Mod(TickrateChanger.MODID)
public class TickrateContainer {
    public static final Logger logger = LogManager.getLogger("TickrateChanger");
    public static boolean KEYS_AVAILABLE = false;

    public static KeyBinding SWITCH_SPEED_KEYBIND = null;
    private long lastKeyInputTime = 0;

    public TickrateContainer() {
        logger.info("Initializing TickrateContainer!");
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TickrateConfig.ConfigSpec);
        TickrateConfig.loadConfig(TickrateConfig.ConfigSpec, FMLPaths.CONFIGDIR.get().resolve("tickratechanger-common.toml"));
        TickrateChanger.DEFAULT_TICKRATE=TickrateConfig.GENERAL.defaultTickrate.get().floatValue();

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        final IEventBus eventBus = MinecraftForge.EVENT_BUS;

        modEventBus.addListener(this::preInit);
        modEventBus.addListener(this::postInit);
        modEventBus.addListener(this::onClientModStart);
        modEventBus.addListener(this::onServerModStart);

        eventBus.addListener(this::onServerStart);
        eventBus.addListener(this::onChat);
        eventBus.addListener(this::onKey);
        eventBus.addListener(this::onConnect);

        new TickrateChanger();
    }

    public void preInit(FMLCommonSetupEvent event) {
        TickrateChanger.NETWORK = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(TickrateChanger.MODID, "tickratechanger"),
                () -> TickrateChanger.NETWORK_VERSION,
                TickrateChanger.NETWORK_VERSION::equals,
                TickrateChanger.NETWORK_VERSION::equals
        );

        TickrateMessageHandler handler = new TickrateMessageHandler();

        TickrateChanger.NETWORK.registerMessage(
                0,
                TickrateMessage.class,
                TickrateMessage::encode,
                TickrateMessage::decode,
                handler::handle
        );

//        TODO
        SWITCH_SPEED_KEYBIND = new KeyBinding(TickrateChanger.MODID + ".key.switchspeed", GLFW.GLFW_KEY_Z, "key.categories." + TickrateChanger.MODID);

//        TODO
//        cfg.save();
    }

    public void postInit(InterModProcessEvent event) {
        TickrateAPI.changeTickrate(TickrateChanger.DEFAULT_TICKRATE);
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
        KEYS_AVAILABLE = true;
        ClientRegistry.registerKeyBinding(SWITCH_SPEED_KEYBIND);
    }

    public void onServerModStart(FMLDedicatedServerSetupEvent event) {
        KEYS_AVAILABLE = false;
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

    public void onKey(InputEvent.KeyInputEvent event) {
        if (!KEYS_AVAILABLE) return;

        float tickrate;

        if (InputMappings.isKeyDown(GLFW.GLFW_KEY_1)) {
            tickrate = 5;
        } else if (InputMappings.isKeyDown(GLFW.GLFW_KEY_2)) {
            tickrate = 10;
        } else if (InputMappings.isKeyDown(GLFW.GLFW_KEY_3)) {
            tickrate = 15;
        } else if (InputMappings.isKeyDown(GLFW.GLFW_KEY_4)) {
            tickrate = 20;
        } else if (InputMappings.isKeyDown(GLFW.GLFW_KEY_5)) {
            tickrate = 30;
        } else if (InputMappings.isKeyDown(GLFW.GLFW_KEY_6)) {
            tickrate = 60;
        } else if (InputMappings.isKeyDown(GLFW.GLFW_KEY_7)) {
            tickrate = 100;
        } else {
            return;
        }

        // Cooldown. 0.1 real life second to prevent spam
        if (lastKeyInputTime > System.currentTimeMillis() - 150) return;
        lastKeyInputTime = System.currentTimeMillis();

        TickrateChanger.NETWORK.sendToServer(new TickrateMessage(tickrate));
    }

}
