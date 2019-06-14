package me.guichaguri.tickratechanger;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class TickrateConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final ForgeConfigSpec ConfigSpec = BUILDER.build();


    public static void loadConfig(ForgeConfigSpec speccy, Path path){
        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync().autosave().writingMode(WritingMode.REPLACE).build();
        configData.load();
        ConfigSpec.setConfig(configData);
    }
}
class General {
    public ForgeConfigSpec.ConfigValue<Double> defaultTickrate;
    public General(ForgeConfigSpec.Builder builder) {
        builder.push("General");
        defaultTickrate = builder
                .comment("Change the default tickrate")
                .defineInRange("DefaultTickrate", 20, 0.1, 1000);
        builder.pop();
    }
}