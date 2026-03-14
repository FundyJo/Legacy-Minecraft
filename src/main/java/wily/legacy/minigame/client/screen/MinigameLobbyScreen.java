package wily.legacy.minigame.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.client.screen.Panel;
import wily.legacy.client.screen.PanelVListScreen;
import wily.legacy.minigame.client.MinigameClientState;
import wily.legacy.minigame.networking.C2SMapVotePayload;

/**
 * In-game lobby screen shown during map voting phase.
 * Shows map options with current vote counts and lets the player vote.
 */
public class MinigameLobbyScreen extends PanelVListScreen {

    public static final Component TITLE = Component.translatable("legacy.menu.minigame.lobby");
    private static final Component VOTE_TITLE = Component.translatable("legacy.menu.minigame.vote_for_map");
    private static final Component YOUR_VOTE = Component.translatable("legacy.menu.minigame.your_vote");

    private final Runnable voteUpdateListener;
    private Button[] voteButtons = new Button[0];

    public MinigameLobbyScreen(Screen parent) {
        super(parent, s -> Panel.createPanel(s, p -> p.appearance(280, 200)), TITLE);
        this.voteUpdateListener = this::refreshVotes;
        MinigameClientState.ON_VOTE_UPDATE.add(voteUpdateListener);
    }

    @Override
    protected void init() {
        super.init();
        rebuildVoteButtons();
        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, btn -> onClose())
                .bounds((width - 260) / 2, panel.getY() + panel.height - 30, 260, 20).build());
    }

    private void rebuildVoteButtons() {
        ResourceLocation[] maps = MinigameClientState.getVoteMapOptions();
        String[] names = MinigameClientState.getVoteMapNames();
        int[] counts = MinigameClientState.getVoteMapCounts();
        voteButtons = new Button[maps.length];
        int buttonX = (width - 260) / 2;
        int startY = panel.getY() + 35;
        for (int i = 0; i < maps.length; i++) {
            final ResourceLocation mapId = maps[i];
            final int idx = i;
            String label = (names != null && idx < names.length ? names[idx] : mapId.getPath()) +
                    (counts != null && idx < counts.length ? " (" + counts[idx] + ")" : "");
            boolean isMyVote = mapId.equals(MinigameClientState.getMyVote());
            voteButtons[i] = Button.builder(Component.literal(label + (isMyVote ? " ✓" : "")), btn -> {
                        CommonNetwork.sendToServer(new C2SMapVotePayload(mapId));
                        MinigameClientState.setMyVote(mapId);
                        rebuildVoteButtons();
                    })
                    .bounds(buttonX, startY + i * 28, 260, 22).build();
            addRenderableWidget(voteButtons[i]);
        }
    }

    private void refreshVotes() {
        if (Minecraft.getInstance().screen == this) {
            clearWidgets();
            init();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        MinigameClientState.ON_VOTE_UPDATE.remove(voteUpdateListener);
    }

    @Override
    public void renderDefaultBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderDefaultBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, VOTE_TITLE, width / 2, panel.getY() + 10, 0xFFFFFF);

        int countdownTicks = MinigameClientState.getCountdownTicks();
        if (countdownTicks > 0) {
            String timerText = String.valueOf(countdownTicks / 20);
            guiGraphics.drawCenteredString(font, Component.literal(timerText), width / 2, panel.getY() + 22, 0xFFD700);
        }

        if (MinigameClientState.getMyVote() != null) {
            guiGraphics.drawCenteredString(font,
                    Component.translatable("legacy.menu.minigame.your_vote",
                            MinigameClientState.getMyVote().getPath()),
                    width / 2, panel.getY() + panel.height - 50, 0x55FF55);
        }
    }
}
