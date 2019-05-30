package me.guichaguri.tickratechanger;

import java.io.File;

import me.guichaguri.tickratechanger.command.TickrateCommand;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Guilherme Chaguri
 */
public class TickrateChanger {

    public static TickrateChanger INSTANCE;
    public static Logger LOGGER = LogManager.getLogger("Tickrate Changer");
    public static SimpleChannel NETWORK;
    public static final String NETWORK_VERSION = "1";
    public static TickrateCommand COMMAND = null;
    public static File CONFIG_FILE = null;

    public static final String MODID = "tickratechanger";
    public static final String VERSION = "2.0";

    public static final String GAME_RULE = "tickrate";

    // Default tickrate - can be changed in the config file
    public static float DEFAULT_TICKRATE = 20;
    // Stored client-side tickrate
    public static float TICKS_PER_SECOND = 20;
    // Server-side tickrate in miliseconds
    public static long MILISECONDS_PER_TICK = 50L;
    // Sound speed
    public static float GAME_SPEED = 1;
    // Min Tickrate
    public static float MIN_TICKRATE = 0.1F;
    // Max Tickrate
    public static float MAX_TICKRATE = 1000;
    // Show Messages
    public static boolean SHOW_MESSAGES = true;
    // Change sound speed
    public static boolean CHANGE_SOUND = true;

    public TickrateChanger() {
        INSTANCE = this;
    }

    public void updateClientTickrate(float tickrate, boolean log) {
        if(log) LOGGER.info("Updating client tickrate to " + tickrate);

        TICKS_PER_SECOND = tickrate;
        if(CHANGE_SOUND) GAME_SPEED = tickrate / 20F;

        Minecraft mc = Minecraft.getInstance();

        mc.timer.tickLength = 1000F / tickrate;
    }

    public void updateServerTickrate(float tickrate, boolean log) {
        if(log) LOGGER.info("Updating server tickrate to " + tickrate);

        MILISECONDS_PER_TICK = (long)(1000L / tickrate);
    }
}
