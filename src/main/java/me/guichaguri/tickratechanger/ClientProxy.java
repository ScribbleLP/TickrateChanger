package me.guichaguri.tickratechanger;

import me.guichaguri.tickratechanger.api.TickrateAPI;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.glfw.GLFW;

public class ClientProxy extends CommonProxy{
    public static KeyBinding KEY_5 = new KeyBinding(TickrateChanger.MODID+ ".keybinding.5", GLFW.GLFW_KEY_UNKNOWN, "Tickratechanger");
    public static KeyBinding KEY_10 = new KeyBinding(TickrateChanger.MODID+ ".keybinding.10", GLFW.GLFW_KEY_UNKNOWN, "Tickratechanger");
    public static KeyBinding KEY_15 = new KeyBinding(TickrateChanger.MODID+ ".keybinding.15", GLFW.GLFW_KEY_UNKNOWN, "Tickratechanger");
    public static KeyBinding KEY_20 = new KeyBinding(TickrateChanger.MODID+ ".keybinding.20", GLFW.GLFW_KEY_UNKNOWN, "Tickratechanger");
    public static KeyBinding KEY_40 = new KeyBinding(TickrateChanger.MODID+ ".keybinding.40", GLFW.GLFW_KEY_UNKNOWN, "Tickratechanger");
    public static KeyBinding KEY_60 = new KeyBinding(TickrateChanger.MODID+ ".keybinding.60", GLFW.GLFW_KEY_UNKNOWN, "Tickratechanger");
    public static KeyBinding KEY_100 = new KeyBinding(TickrateChanger.MODID+ ".keybinding.100", GLFW.GLFW_KEY_UNKNOWN, "Tickratechanger");

    public void preInit(){
        MinecraftForge.EVENT_BUS.register(new Keybinds());
        super.preInit();
    }

    public void postInit(){
        TickrateAPI.changeTickrate(TickrateChanger.DEFAULT_TICKRATE);
        super.postInit();
    }
}
