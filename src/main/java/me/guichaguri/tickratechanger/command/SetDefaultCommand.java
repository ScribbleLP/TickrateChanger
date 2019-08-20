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

public class SetDefaultCommand {
    static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("setdefault")
                .then(Commands
                        .argument("tickrate", FloatArgumentType.floatArg())
                        .executes(ctx -> setDefaultTickrateUpdate(ctx.getSource(), FloatArgumentType.getFloat(ctx, "tickrate")))
                        .then(Commands
                                .literal("--dont-save")
                                .executes(ctx -> setDefaultTickrateDontUpdate(ctx.getSource(), FloatArgumentType.getFloat(ctx.getLastChild(), "tickrate"))))

                );
    }

    public static int setDefaultTickrateUpdate(CommandSource sender, Float tickrate) {
        TickrateAPI.changeDefaultTickrate(tickrate, true);
        TickrateAPI.changeTickrate(tickrate);

        if (TickrateChanger.SHOW_MESSAGES) {
            chat(sender, t("tickratechanger.cmd.default.success", GREEN, tickrate));
        }

        return 0;
    }

    public static int setDefaultTickrateDontUpdate(CommandSource sender, Float tickrate) {
        TickrateAPI.changeDefaultTickrate(tickrate, false);
        TickrateAPI.changeTickrate(tickrate);

        if (TickrateChanger.SHOW_MESSAGES) {
            chat(sender, t("tickratechanger.cmd.default.success", GREEN, tickrate));
        }
        return 0;
    }
}
