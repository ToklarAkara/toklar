package net.mcreator.toklar.tools;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatSpy {
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent e) {
        ITextComponent component = e.getMessage();
        String msg = component.getUnformattedText();

        if (msg.contains("wall") || msg.contains("lava") || msg.contains("biome") || msg.contains("Chunk [")) {
            System.out.println("[Toklar] === CHAT SPAM DETECTED ===");
            System.out.println("[Toklar] Message: " + msg);
            System.out.println("[Toklar] Raw component class: " + component.getClass().getName());
            System.out.println("[Toklar] Full JSON: " + component.toString());

            ClassLoader loader = component.getClass().getClassLoader();
            System.out.println("[Toklar] Component loaded by: " + loader);

            // If the packet class is accessible, log its loader too
            System.out.println("[Toklar] Event class: " + e.getClass().getName());
            System.out.println("[Toklar] Event loaded by: " + e.getClass().getClassLoader());

            new Exception("[Toklar] Chat spam source trace").printStackTrace();
        }
    }
}