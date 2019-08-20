package me.guichaguri.tickratechanger;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Keybinds {
    private long lastKeyInputTime = 0;

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event){

        float tickrate;

        if (ClientProxy.KEY_5.isKeyDown()) {
            tickrate = 5;
        } else if (ClientProxy.KEY_10.isKeyDown()) {
            tickrate = 10;
        } else if (ClientProxy.KEY_15.isKeyDown()) {
            tickrate = 15;
        } else if (ClientProxy.KEY_20.isKeyDown()) {
            tickrate = 20;
        } else if (ClientProxy.KEY_40.isKeyDown()) {
            tickrate = 40;
        } else if (ClientProxy.KEY_60.isKeyDown()) {
            tickrate = 60;
        } else if (ClientProxy.KEY_100.isKeyDown()) {
            tickrate = 100;
        } else {
            return;
        }

        // Cooldown. 0.1 real life second to prevent spam
        if (lastKeyInputTime > System.currentTimeMillis() - 150) return;
        lastKeyInputTime = System.currentTimeMillis();

        TickrateChanger.NETWORK.sendToServer(new TickrateMessageHandler.TickrateMessage(tickrate));
    }
}
