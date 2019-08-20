package me.guichaguri.tickratechanger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.nio.file.Path;

public class TickrateConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec ConfigSpec = BUILDER.build();
    public static ModConfig ClientConfig;


    public static void loadConfig(ForgeConfigSpec speccy, Path path){
        final CommentedFileConfig configData = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();

        configData.load();
        ConfigSpec.setConfig(configData);
    }

    public static void setValueAndSave(final ModConfig modConfig, final String path, final Object newValue){
        modConfig.getConfigData().set(path, newValue);
        modConfig.save();
    }

    public static void setModConfig(ModConfig modConfig){
        ClientConfig=modConfig;
    }
}
class General {
    public ForgeConfigSpec.DoubleValue defaultTickrate;
    public ForgeConfigSpec.DoubleValue MAXtickrate;
    public ForgeConfigSpec.DoubleValue MINtickrate;
    public ForgeConfigSpec.BooleanValue ChangeSound;
    public ForgeConfigSpec.BooleanValue ShowKeyBindings;
    public ForgeConfigSpec.BooleanValue ShowMessage;

    public General(ForgeConfigSpec.Builder builder) {
        defaultTickrate = builder
                .comment("Default tickrate. The game will always initialize with this value.")
                .defineInRange("DefaultTickrate", 20.0, TickrateChanger.MIN_TICKRATE, TickrateChanger.MAX_TICKRATE);

        MAXtickrate= builder
                .comment("Maximum tickrate from servers. Prevents really high tickrate values.")
                .defineInRange("MAXtickrate",1000.0, 0, 5000);

        MINtickrate= builder
                .comment("Minimum tickrate from servers. Prevents really high tickrate values.")
                .defineInRange("MINtickrate",0.1, 0, 5000);

        ChangeSound= builder
                .comment("Whether it will change the sound speed")
                .define("ChangeSound",true);

        ShowKeyBindings= builder
                .comment("Whether it will have special keys for setting the tickrate")
                .define("Keybind", false);

        ShowMessage= builder
                .comment("Whether it will show log messages in the console and the game")
                .define("ShowMessages", false);
    }
}