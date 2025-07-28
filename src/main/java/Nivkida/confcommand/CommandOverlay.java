package Nivkida.confcommand;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Confcommand.MODID, value = Dist.CLIENT)
public class CommandOverlay {

    private static final Minecraft mc = Minecraft.getInstance();
    private static final int panelX = 5;
    private static final int panelY = 35;
    private static final int padding = 6;
    private static final int lineHeight = 12;
    private static final int maxVisibleLines = 10;

    private static List<? extends String> cachedCommands = null;
    private static int scrollOffset = 0;

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!(mc.screen instanceof ChatScreen)) return;

        GuiGraphics gui = event.getGuiGraphics();

        cachedCommands = Confcommand.COMMON.allowedCommands.get();
        if (cachedCommands == null || cachedCommands.isEmpty()) return;

        int total = cachedCommands.size();
        int visibleLines = Math.min(total, maxVisibleLines);

        int maxWidth = 0;
        for (String cmd : cachedCommands) {
            int w = mc.font.width("/" + cmd);
            if (w > maxWidth) maxWidth = w;
        }

        int width = maxWidth + padding * 2;
        int height = visibleLines * lineHeight + padding * 2;

        gui.fill(panelX, panelY, panelX + width, panelY + height, 0x80000000); // фон
        gui.fill(panelX, panelY, panelX + width, panelY + 1, 0xFFFFFFFF);      // верх рамки
        gui.fill(panelX, panelY + height - 1, panelX + width, panelY + height, 0xFFFFFFFF); // низ
        gui.fill(panelX, panelY, panelX + 1, panelY + height, 0xFFFFFFFF);     // левая рамка
        gui.fill(panelX + width - 1, panelY, panelX + width, panelY + height, 0xFFFFFFFF);  // правая

        // Положение мыши
        double mouseX = mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth();
        double mouseY = mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight();

        int hoveredIndex = -1;
        if (mouseX >= panelX && mouseX <= panelX + width &&
                mouseY >= panelY + padding && mouseY <= panelY + height - padding) {
            hoveredIndex = (int)((mouseY - panelY - padding) / lineHeight);
        }

        int x = panelX + padding;
        int y = panelY + padding;

        for (int i = 0; i < visibleLines; i++) {
            int index = i + scrollOffset;
            if (index >= total) break;

            String cmd = "/" + cachedCommands.get(index);

            if (i == hoveredIndex) {
                gui.fill(panelX + 1, y - 1, panelX + width - 1, y + lineHeight - 1, 0x40FFFFFF);
            }

            gui.drawString(mc.font, Component.literal(cmd), x, y, 0xFFFFFF);
            y += lineHeight;
        }
    }

    @SubscribeEvent
    public static void onMouseScroll(ScreenEvent.MouseScrolled event) {
        if (!(mc.screen instanceof ChatScreen)) return;
        if (cachedCommands == null || cachedCommands.size() <= maxVisibleLines) return;

        double delta = event.getScrollDelta();
        int total = cachedCommands.size();
        scrollOffset -= (int) Math.signum(delta);
        scrollOffset = Math.max(0, Math.min(scrollOffset, total - maxVisibleLines));
    }
}
