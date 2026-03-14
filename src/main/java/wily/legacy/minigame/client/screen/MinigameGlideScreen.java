package wily.legacy.minigame.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import wily.legacy.minigame.client.MinigameClientState;
import wily.legacy.minigame.networking.S2CLeaderboardPayload;

/**
 * Glide minigame HUD overlay.
 * Shows checkpoint progress and current time.
 * Rendered as an overlay on the game view (not a full screen).
 */
public class MinigameGlideScreen extends Screen {

    public MinigameGlideScreen() {
        super(Component.translatable("legacy.minigame.glide"));
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!MinigameClientState.isInMinigame()) {
            onClose();
            return;
        }
        renderHUD(guiGraphics);
    }

    private void renderHUD(GuiGraphics guiGraphics) {
        Minecraft mc = Minecraft.getInstance();
        int checkpoint = MinigameClientState.getGlideCheckpoint();
        int total = MinigameClientState.getGlideTotalCheckpoints();
        long timeMs = MinigameClientState.getGlideCheckpointTimeMs();

        int hudX = 10;
        int hudY = 10;

        guiGraphics.fill(hudX - 2, hudY - 2, hudX + 160, hudY + 40, 0x80000000);

        guiGraphics.drawString(mc.font,
                Component.translatable("legacy.minigame.glide.checkpoint", checkpoint, total),
                hudX, hudY, 0xFFFFFF);

        if (timeMs > 0) {
            long seconds = timeMs / 1000;
            long ms = timeMs % 1000;
            guiGraphics.drawString(mc.font,
                    Component.literal(String.format("%d.%03d s", seconds, ms)),
                    hudX, hudY + 12, 0xFFD700);
        }

        renderLeaderboard(guiGraphics, mc, hudX, hudY + 25);
    }

    private void renderLeaderboard(GuiGraphics guiGraphics, Minecraft mc, int x, int y) {
        S2CLeaderboardPayload.LeaderboardEntry[] entries = MinigameClientState.getLeaderboard();
        for (int i = 0; i < Math.min(3, entries.length); i++) {
            S2CLeaderboardPayload.LeaderboardEntry entry = entries[i];
            String text = "#" + entry.rank() + " " + entry.playerName() + " - CP " + entry.score();
            int color = entry.playerId().equals(mc.player != null ? mc.player.getUUID() : null) ? 0xFFFF00 : 0xFFFFFF;
            guiGraphics.drawString(mc.font, text, x, y + i * 10, color);
        }
    }
}
