package me.guichaguri.tickratechanger;

import me.guichaguri.tickratechanger.api.TickrateAPI;
import me.guichaguri.tickratechanger.command.TickrateCommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author Guilherme Chaguri
 */
public class TickrateMessageHandler {
    public void handle(TickrateMessage msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            EntityPlayerMP player = context.getSender();

            if (!player.hasPermissionLevel(TickrateChanger.COMMAND.getRequiredPermissionLevel())) return;
        }

        float tickrate = msg.getTickrate();

        if (tickrate < TickrateChanger.MIN_TICKRATE) {
            TickrateChanger.LOGGER.info("Tickrate forced to change from " + tickrate + " to " +
                    TickrateChanger.MIN_TICKRATE + ", because the value is too low" +
                    " (You can change the minimum tickrate in the config file)");
            tickrate = TickrateChanger.MIN_TICKRATE;
        } else if (tickrate > TickrateChanger.MAX_TICKRATE) {
            TickrateChanger.LOGGER.info("Tickrate forced to change from " + tickrate + " to " +
                    TickrateChanger.MAX_TICKRATE + ", because the value is too high" +
                    " (You can change the maximum tickrate in the config file)");
            tickrate = TickrateChanger.MAX_TICKRATE;
        }

        if (EffectiveSide.get() == LogicalSide.CLIENT) {
            TickrateChanger.INSTANCE.updateClientTickrate(tickrate, TickrateChanger.SHOW_MESSAGES);
        } else {
            TickrateAPI.changeTickrate(tickrate, TickrateChanger.SHOW_MESSAGES);

            if (TickrateChanger.SHOW_MESSAGES) {
                context.getSender().sendMessage(TickrateCommand.successTickrateMsg(tickrate));
            }
        }
    }

    public static class TickrateMessage {
        private float tickrate;

        public TickrateMessage(float tickrate) {
            this.tickrate = tickrate;
        }

        public static TickrateMessage decode(PacketBuffer buf) {
            return new TickrateMessage(buf.readFloat());
        }

        public static void encode(TickrateMessage message, PacketBuffer buf) {
            buf.writeFloat(message.tickrate);
        }

        public float getTickrate() {
            return tickrate;
        }
    }
}
