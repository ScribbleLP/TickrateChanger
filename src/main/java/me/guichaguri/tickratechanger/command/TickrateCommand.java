package me.guichaguri.tickratechanger.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.guichaguri.tickratechanger.TickrateChanger;
import me.guichaguri.tickratechanger.api.TickrateAPI;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.GameRules;
import net.minecraft.world.dimension.DimensionType;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * @author Guilherme Chaguri
 */
public class TickrateCommand {
    public TickrateCommand(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmd = dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("tickrate")
                        .requires((source) -> source.hasPermissionLevel(getRequiredPermissionLevel()))
                        .then(Commands.literal("help").executes(source -> {
                            showHelp(source.getSource());
                            return 0;
                        }))
                        .then(SetDefaultCommand.register())
                        .then(SetMapCommand.register())
                        .then(SetRateCommand.register())
                        .executes(source -> {
                            showInfo(source.getSource().getServer(), source.getSource());
                            return 0;
                        })
        );

        dispatcher.register(
                LiteralArgumentBuilder.<CommandSource>literal("trc")
                .requires((source) -> source.hasPermissionLevel(getRequiredPermissionLevel()))
                .redirect(cmd)
        );
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    private void showInfo(MinecraftServer server, CommandSource sender) {
        chat(sender, t("tickratechanger.info.server", WHITE), t("tickratechanger.info.value", GREEN, TickrateAPI.getServerTickrate()));

        try {
            GameRules rules = server.getWorld(DimensionType.OVERWORLD).getGameRules();

            GameRules.Value ruleValue = rules.get(TickrateChanger.GAME_RULE);

            if(ruleValue != null) {
                String ruleValueString = ruleValue.getString();

                float tickrate = Float.parseFloat(ruleValueString);
                chat(sender, t("tickratechanger.info.map", WHITE), t("tickratechanger.info.value", GREEN, tickrate));
            }
        } catch(Exception ex) {
            // Invalid map tickrate
        }

        chat(sender, t("tickratechanger.info.default", WHITE), t("tickratechanger.info.value", YELLOW, TickrateChanger.DEFAULT_TICKRATE));
        chat(sender);
        chat(sender, c("/tickrate <ticks per second> [all/server/client/", AQUA), c("playername", DARK_AQUA), c("]", AQUA));
        chat(sender, c("/tickrate setdefault <ticks per second> [--dontsave, --dontupdate]", AQUA));
        chat(sender, c("/tickrate setmap <ticks per second> [--dontupdate]", AQUA));
        chat(sender);
        chat(sender, t("tickratechanger.info.help", RED, WHITE + "/tickrate help" + RED));
    }

    private void showHelp(CommandSource sender) {
        chat(sender, c(" * * Tickrate Changer * * ", DARK_PURPLE, BOLD), c("by ", GRAY, ITALIC), c("Guichaguri", WHITE, ITALIC));
        chat(sender, t("tickratechanger.help.desc", GREEN));

        chat(sender, c("/tickrate 20", t("tickratechanger.help.command.1", GREEN), GRAY));
        chat(sender, c("/tickrate 20 server", t("tickratechanger.help.command.2", GREEN), GRAY));
        chat(sender, c("/tickrate 20 client", t("tickratechanger.help.command.3", GREEN), GRAY));
        chat(sender, c("/tickrate 20 Notch", t("tickratechanger.help.command.4", GREEN), GRAY));
        chat(sender, c("/tickrate setdefault 20", t("tickratechanger.help.command.5", GREEN), GRAY));
        chat(sender, c("/tickrate setdefault 20 --dontsave", t("tickratechanger.help.command.6", GREEN), GRAY));
        chat(sender, c("/tickrate setdefault 20 --dontupdate", t("tickratechanger.help.command.7", GREEN), GRAY));
        chat(sender, c("/tickrate setdefault 20 --dontsave --dontupdate", t("tickratechanger.help.command.8", GREEN), GRAY));
        chat(sender, c("/tickrate setmap 20", t("tickratechanger.help.command.9", GREEN), GRAY));
        chat(sender, c("/tickrate setmap 20 --dontupdate", t("tickratechanger.help.command.10", GREEN), GRAY));

        chat(sender, c(" * * * * * * * * * * * * * * ", DARK_PURPLE, BOLD));
    }

    public static ITextComponent clientTickrateMsg() {
        ITextComponent msg = new TextComponentString("");
        msg.appendSibling(t("tickratechanger.info.client", WHITE));
        msg.appendText(" ");
        msg.appendSibling(t("tickratechanger.info.value", GREEN, TickrateAPI.getClientTickrate()));
        return msg;
    }

    public static ITextComponent successTickrateMsg(float ticksPerSecond) {
        return t("tickratechanger.cmd.everything.success", GREEN, ticksPerSecond);
    }

    static void chat(CommandSource sender, ITextComponent... comps) {
        ITextComponent top;
        if(comps.length == 1) {
            top = comps[0];
        } else {
            top = new TextComponentString("");
            for(ITextComponent c : comps) {
                top.appendSibling(c);
                top.appendText(" ");
            }
        }
        sender.sendFeedback(top, false);
    }

    static ITextComponent t(String langKey, TextFormatting formatting, Object ... data) {
        ITextComponent comp = new TextComponentTranslation(langKey, data);
        comp.setStyle(comp.getStyle().setColor(formatting));
        return comp;
    }

    static TextComponentString c(String s, ITextComponent hover, TextFormatting ... formattings) {
        TextComponentString txt = c(s, formattings);
        txt.setStyle(txt.getStyle().setHoverEvent(new HoverEvent(Action.SHOW_TEXT, hover)));
        return txt;
    }

    static TextComponentString c(String s, TextFormatting ... formattings) {
        TextComponentString comp = new TextComponentString(s);
        Style style = comp.getStyle();
        for(TextFormatting f : formattings) {
            if(f == TextFormatting.BOLD) {
                style.setBold(true);
            } else if(f == TextFormatting.ITALIC) {
                style.setItalic(true);
            } else if(f == TextFormatting.UNDERLINE) {
                style.setUnderlined(true);
            } else {
                style.setColor(f);
            }
        }
        comp.setStyle(style);
        return comp;
    }
}
