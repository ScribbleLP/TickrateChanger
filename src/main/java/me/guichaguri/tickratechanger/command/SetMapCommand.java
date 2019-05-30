package me.guichaguri.tickratechanger.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import me.guichaguri.tickratechanger.TickrateChanger;
import me.guichaguri.tickratechanger.api.TickrateAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import static me.guichaguri.tickratechanger.command.TickrateCommand.chat;
import static me.guichaguri.tickratechanger.command.TickrateCommand.t;
import static net.minecraft.util.text.TextFormatting.*;

public class SetMapCommand {
    static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("setmap")
                .then(Commands
                        .argument("tickrate", FloatArgumentType.floatArg())
                        .executes(ctx -> setMapTickrate(ctx.getSource(), FloatArgumentType.getFloat(ctx, "tickrate")))
                );
    }

    public static int setMapTickrate(CommandSource sender, Float tickrate) {
        TickrateAPI.changeMapTickrate(tickrate);
        TickrateAPI.changeTickrate(tickrate);

        if(TickrateChanger.SHOW_MESSAGES) {
            chat(sender, t("tickratechanger.cmd.map.success", GREEN, tickrate));
        }

        return 0;
    }
}
