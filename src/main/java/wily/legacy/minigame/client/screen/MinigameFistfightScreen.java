package wily.legacy.minigame.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import wily.legacy.minigame.client.MinigameClientState;
import wily.legacy.minigame.networking.S2CLeaderboardPayload;

/**
 * Fistfight minigame HUD overlay.
 * Shows kill leaderboard and round info.
 */
public class MinigameFistfightScreen extends Screen {

    public MinigameFistfightScreen() {
        super(Component.translatable("legacy.minigame.fistfight"));
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
        int hudX = width - 170;
        int hudY = 10;

        guiGraphics.fill(hudX - 2, hudY - 2, hudX + 165, hudY + 60, 0x80000000);

        guiGraphics.drawCenteredString(mc.font,
                Component.translatable("legacy.minigame.fistfight.kills"),
                hudX + 80, hudY, 0xFFD700);

        S2CLeaderboardPayload.LeaderboardEntry[] entries = MinigameClientState.getLeaderboard();
        for (int i = 0; i < Math.min(5, entries.length); i++) {
            S2CLeaderboardPayload.LeaderboardEntry entry = entries[i];
            String text = "#" + entry.rank() + " " + entry.playerName() + ": " + entry.score();
            boolean isMe = mc.player != null && entry.playerId().equals(mc.player.getUUID());
            int color = isMe ? 0xFFFF00 : 0xFFFFFF;
            guiGraphics.drawString(mc.font, text, hudX, hudY + 12 + i * 10, color);
        }

        int countdown = MinigameClientState.getCountdownTicks();
        if (countdown > 0) {
            guiGraphics.drawCenteredString(mc.font,
                    Component.literal(String.valueOf(countdown / 20)),
                    width / 2, height / 2 - 30, 0xFF4444);
        }
    }
}
