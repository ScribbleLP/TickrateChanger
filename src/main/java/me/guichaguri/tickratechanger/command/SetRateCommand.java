package me.guichaguri.tickratechanger.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import me.guichaguri.tickratechanger.TickrateChanger;
import me.guichaguri.tickratechanger.api.TickrateAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.EntityPlayer;

import static me.guichaguri.tickratechanger.command.TickrateCommand.chat;
import static me.guichaguri.tickratechanger.command.TickrateCommand.t;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.RED;

public class SetRateCommand {
    static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.argument("tickrate", FloatArgumentType.floatArg())
                .then(Commands.argument("where", StringArgumentType.string())
                        .executes(ctx -> setTickrate(
                                ctx.getSource(),
                                FloatArgumentType.getFloat(ctx, "tickrate"),
                                StringArgumentType.getString(ctx, "where")
                        ))
                )
                .executes(ctx -> setTickrate(
                        ctx.getSource(),
                        FloatArgumentType.getFloat(ctx, "tickrate"),
                        "all"
                ));
    }

    public static int setTickrate(CommandSource sender, Float tickrate, String where) {
        if ("all".equalsIgnoreCase(where)) {
            // Set the tickrate
            TickrateAPI.changeTickrate(tickrate);

            if (TickrateChanger.SHOW_MESSAGES) {
                chat(sender, t("tickratechanger.cmd.everything.success", GREEN, tickrate));
            }
        } else if ("client".equalsIgnoreCase(where)) {
            // Set client tickrate
            TickrateAPI.changeClientTickrate(tickrate);

            if (TickrateChanger.SHOW_MESSAGES) {
                chat(sender, t("tickratechanger.cmd.client.success", GREEN, tickrate));
            }

        } else if ("server".equalsIgnoreCase(where)) {
            // Set server tickrate
            TickrateAPI.changeServerTickrate(tickrate);

            if (TickrateChanger.SHOW_MESSAGES) {
                chat(sender, t("tickratechanger.cmd.server.success", GREEN, tickrate));
            }
        } else {
            // Set player tickrate
            EntityPlayer p = sender.getServer().getPlayerList().getPlayerByUsername(where);

            if (p == null) {
                chat(sender, t("tickratechanger.cmd.player.error", RED));
                return 1;
            }

            TickrateAPI.changeClientTickrate(p, tickrate);

            if (TickrateChanger.SHOW_MESSAGES) {
                chat(sender, t("tickratechanger.cmd.player.success", GREEN, p.getGameProfile().getName(), tickrate));
            }
        }

        return 0;
    }
}
