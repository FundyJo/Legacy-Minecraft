package wily.legacy.minigame.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.client.screen.Panel;
import wily.legacy.client.screen.PanelVListScreen;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.MinigameData;
import wily.legacy.minigame.networking.C2SJoinMinigamePayload;

import java.util.List;

/**
 * Screen for selecting a map for a chosen minigame.
 * Shows available maps with preview info; player can select one or random.
 */
public class MinigameMapSelectionScreen extends PanelVListScreen {

    public static final Component TITLE = Component.translatable("legacy.menu.minigame.select_map");
    private static final Component RANDOM = Component.translatable("legacy.menu.minigame.random_map");
    private static final Component PLAY = Component.translatable("legacy.menu.minigame.play");

    private final Minigame<?> minigame;
    private final List<MinigameData> maps;
    private int selectedMapIndex = -1;
    private int maxPlayers = 8;
    private int rounds = 1;

    public MinigameMapSelectionScreen(Screen parent, Minigame<?> minigame, List<MinigameData> maps) {
        super(parent, s -> Panel.createPanel(s, p -> p.appearance(320, 220)), TITLE);
        this.minigame = minigame;
        this.maps = maps;
    }

    @Override
    protected void init() {
        super.init();
        int buttonX = (width - 300) / 2;
        int startY = panel.getY() + 25;

        for (int i = 0; i < maps.size(); i++) {
            final int idx = i;
            MinigameData mapData = maps.get(i);
            String mapName = mapData.displayName();
            addRenderableWidget(Button.builder(Component.literal(mapName), btn -> {
                selectedMapIndex = idx;
                play(mapData.mapId());
            }).bounds(buttonX, startY + i * 28, 300, 22).build());
        }

        int backY = panel.getY() + panel.height - 35;
        addRenderableWidget(Button.builder(RANDOM, btn -> playRandom())
                .bounds(buttonX, backY - 25, 145, 20).build());
        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, btn -> onClose())
                .bounds(buttonX + 155, backY - 25, 145, 20).build());
    }

    private void play(ResourceLocation mapId) {
        CommonNetwork.sendToServer(new C2SJoinMinigamePayload(minigame.getName(), maxPlayers, rounds));
        onClose();
    }

    private void playRandom() {
        if (maps.isEmpty()) return;
        int idx = (int) (Math.random() * maps.size());
        play(maps.get(idx).mapId());
    }

    @Override
    public void renderDefaultBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderDefaultBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, Component.literal(minigame.getName().toUpperCase()),
                width / 2, panel.getY() + 5, 0xFFD700);
        guiGraphics.drawCenteredString(font, TITLE, width / 2, panel.getY() + 15, 0xFFFFFF);
    }
}
