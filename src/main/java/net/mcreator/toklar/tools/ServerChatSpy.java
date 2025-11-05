package net.mcreator.toklar.tools;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;

public class ServerChatSpy {
    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ServerChatSpy());
    }

    @SubscribeEvent
    public void onServerChat(ServerChatEvent e) {
        String msg = e.getMessage();
        if (msg.contains("wall") || msg.contains("lava") || msg.contains("biome") || msg.contains("Chunk [")) {
            System.out.println("[Toklar] === SERVER CHAT SPAM DETECTED ===");
            System.out.println("[Toklar] Message: " + msg);
            new Exception("[Toklar] Server chat spam source trace").printStackTrace();
        }
    }
}