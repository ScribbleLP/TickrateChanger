package me.guichaguri.tickratechanger.api;

import me.guichaguri.tickratechanger.TickrateChanger;
import me.guichaguri.tickratechanger.TickrateConfig;
import me.guichaguri.tickratechanger.TickrateMessageHandler.TickrateMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * @author Guilherme Chaguri
 */
public class TickrateAPI {

    /**
     * Let you change the client & server tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeTickrate(float ticksPerSecond) {
        changeTickrate(ticksPerSecond, TickrateChanger.SHOW_MESSAGES);
    }

    /**
     * Let you change the client & server tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     *
     * @param ticksPerSecond Tickrate to be set
     * @param log            If should send console logs
     */
    public static void changeTickrate(float ticksPerSecond, boolean log) {
        changeServerTickrate(ticksPerSecond, log);
        changeClientTickrate(ticksPerSecond, log);
    }


    /**
     * Let you change the server tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeServerTickrate(float ticksPerSecond) {
        changeServerTickrate(ticksPerSecond, TickrateChanger.SHOW_MESSAGES);
    }

    /**
     * Let you change the server tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     *
     * @param ticksPerSecond Tickrate to be set
     * @param log            If should send console logs
     */
    public static void changeServerTickrate(float ticksPerSecond, boolean log) {
        TickrateChanger.INSTANCE.updateServerTickrate(ticksPerSecond, log);
    }

    /**
     * Let you change the all clients tickrate
     * Can be called either from server-side or client-side
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeClientTickrate(float ticksPerSecond) {
        changeClientTickrate(ticksPerSecond, TickrateChanger.SHOW_MESSAGES);
    }

    /**
     * Let you change the all clients tickrate
     * Can be called either from server-side or client-side
     *
     * @param ticksPerSecond Tickrate to be set
     * @param log            If should send console logs
     */
    public static void changeClientTickrate(float ticksPerSecond, boolean log) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server != null && server.getPlayerList() != null) { // Is a server or singleplayer
            for (EntityPlayerMP p : server.getPlayerList().getPlayers()) {
                changeClientTickrate(p, ticksPerSecond, log);
            }
        } else { // Is in menu or a player connected in a server. We can say this is client.
            changeClientTickrate(null, ticksPerSecond, log);
        }
    }

    /**
     * Let you change the all clients tickrate
     * Can be called either from server-side or client-side.
     * Will only take effect in the client-side if the player is Minecraft.thePlayer
     *
     * @param player         The Player
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeClientTickrate(EntityPlayer player, float ticksPerSecond) {
        changeClientTickrate(player, ticksPerSecond, TickrateChanger.SHOW_MESSAGES);
    }

    /**
     * Let you change the all clients tickrate
     * Can be called either from server-side or client-side.
     * Will only take effect in the client-side if the player is Minecraft.thePlayer
     *
     * @param player         The Player
     * @param ticksPerSecond Tickrate to be set
     * @param log            If should send console logs
     */
    public static void changeClientTickrate(EntityPlayer player, float ticksPerSecond, boolean log) {
        if ((player == null) || (player.world.isRemote)) { // Client
            if (EffectiveSide.get() != LogicalSide.CLIENT) return;

            if ((player != null) && (player != Minecraft.getInstance().player)) return;

            TickrateChanger.INSTANCE.updateClientTickrate(ticksPerSecond, log);
        } else { // Server
            TickrateChanger.NETWORK.send(
                    PacketDistributor.PLAYER.with(() -> (EntityPlayerMP) player),
                    new TickrateMessage(ticksPerSecond)
            );
        }
    }

    /**
     * Let you change the default tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     * This will not update the tickrate from the server/clients.
     *
     * @param ticksPerSecond Tickrate to be set
     * @param save           If will be saved in the config file
     */
    public static void changeDefaultTickrate(float ticksPerSecond, boolean save) {
        TickrateChanger.DEFAULT_TICKRATE = ticksPerSecond;

        if (save) {
            TickrateConfig.setValueAndSave(TickrateConfig.ClientConfig, "DefaultTickrate", ticksPerSecond);
        }
    }

    /**
     * Let you change the map tickrate
     * Can only be called from server-side. Can also be called from client-side if is singleplayer.
     * This will not update the tickrate from the server/clients
     *
     * @param ticksPerSecond Tickrate to be set
     */
    public static void changeMapTickrate(float ticksPerSecond) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        World world = server.getWorld(DimensionType.OVERWORLD);

        world.getGameRules().setOrCreateGameRule(TickrateChanger.GAME_RULE, ticksPerSecond + "", server);
    }

    /**
     * Only returns the real tickrate if you call the method server-side or in singleplayer
     *
     * @return The server tickrate or the client server tickrate if it doesn't have access to the real tickrate.
     */
    public static float getServerTickrate() {
        return 1000F / TickrateChanger.MILISECONDS_PER_TICK;
    }

    /**
     * Can only be called in the client-side
     *
     * @return The client tickrate
     */
    public static float getClientTickrate() {
        return TickrateChanger.TICKS_PER_SECOND;
    }

    /**
     * Can only be called in the server-side or singleplayer
     *
     * @return The map tickrate or the server tickrate if it doesn't have a map tickrate.
     */
    public static float getMapTickrate() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        GameRules rules = server.getWorld(DimensionType.OVERWORLD).getGameRules();

        GameRules.Value ruleValue = rules.get(TickrateChanger.GAME_RULE);

        if (ruleValue != null) {
            String ruleValueString = ruleValue.getString();

            return Float.parseFloat(ruleValueString);
        }

        return getServerTickrate();
    }

    /**
     * Checks if the tickrate is valid
     *
     * @param ticksPerSecond Tickrate to be checked
     * @deprecated Used to check if the tickrate > 0, but I decided to let any tickrate be valid according to the config limits
     */
    @Deprecated
    public static boolean isValidTickrate(float ticksPerSecond) {
        return true;
    }

    /**
     * Processes IMC messages
     * Send an IMC message using FMLInterModComms.sendMessage
     *
     * @hide
     */
//    TODO
//    public static void processIMC(InterModComms.IMCMessage msg) {
//        String type, player = null;
//        float tickrate;
//        boolean save = false;
//
//        if (msg.) {
//
//            NBTTagCompound nbt = msg.getNBTValue();
//
//            type = nbt.hasKey("type") ? nbt.getString("type") : "all";
//            tickrate = nbt.hasKey("tickrate") ? nbt.getFloat("tickrate") : TickrateChanger.DEFAULT_TICKRATE;
//            player = nbt.hasKey("player") ? nbt.getString("player") : null;
//            save = nbt.hasKey("save") && nbt.getBoolean("save");
//
//        } else if (msg.isStringMessage()) {
//
//            type = "all";
//            tickrate = Float.parseFloat(msg.getStringValue());
//
//        } else {
//            return;
//        }
//
//        if (type.equalsIgnoreCase("client")) {
//            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
//            if (player != null && server != null && server.getPlayerList() != null) {
//                EntityPlayer pl = server.getPlayerList().getPlayerByUsername(player);
//                changeClientTickrate(pl, tickrate, false);
//            } else {
//                changeClientTickrate(tickrate, false);
//            }
//        } else if (type.equalsIgnoreCase("server")) {
//            changeServerTickrate(tickrate, false);
//        } else if (type.equalsIgnoreCase("map")) {
//            changeMapTickrate(tickrate);
//        } else if (type.equalsIgnoreCase("default")) {
//            changeDefaultTickrate(tickrate, save);
//        } else {
//            changeTickrate(tickrate, false);
//        }
//    }
}
