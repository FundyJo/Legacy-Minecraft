package wily.legacy.minigame.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.network.CommonNetwork;
import wily.legacy.client.screen.Panel;
import wily.legacy.client.screen.PanelVListScreen;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.MinigameData;
import wily.legacy.minigame.grf.GrfMap;
import wily.legacy.minigame.networking.C2SJoinMinigamePayload;

import java.util.List;

/**
 * Screen for selecting which minigame to play: Glide, Tumble, or Fistfight.
 * Shows each minigame with a description and player count selector.
 */
public class MinigameSelectScreen extends PanelVListScreen {

    public static final Component TITLE = Component.translatable("legacy.menu.minigame.select");
    public static final Component GLIDE_NAME = Component.translatable("legacy.minigame.glide");
    public static final Component TUMBLE_NAME = Component.translatable("legacy.minigame.tumble");
    public static final Component FISTFIGHT_NAME = Component.translatable("legacy.minigame.fistfight");

    private int maxPlayers = 8;
    private int rounds = 1;

    public MinigameSelectScreen(Screen parent) {
        super(parent, s -> Panel.createPanel(s, p -> p.appearance(300, 200)), TITLE);
    }

    @Override
    protected void init() {
        super.init();
        int buttonX = (width - 280) / 2;
        int startY = panel.getY() + 30;

        addRenderableWidget(createMinigameButton(buttonX, startY, Minigame.GLIDE, GLIDE_NAME));
        addRenderableWidget(createMinigameButton(buttonX, startY + 35, Minigame.TUMBLE, TUMBLE_NAME));
        addRenderableWidget(createMinigameButton(buttonX, startY + 70, Minigame.FISTFIGHT, FISTFIGHT_NAME));

        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, btn -> onClose())
                .bounds(buttonX, startY + 120, 280, 20).build());
    }

    private Button createMinigameButton(int x, int y, Minigame<?> minigame, Component name) {
        return Button.builder(name, btn -> openMapSelection(minigame))
                .bounds(x, y, 280, 25).build();
    }

    private void openMapSelection(Minigame<?> minigame) {
        List<wily.legacy.minigame.MinigameData> mapsData = GrfMap.discoverMaps(minigame.getName()).stream()
                .map(mapId -> new MinigameData(mapId, mapId.getPath().replace("_", " "), maxPlayers, 2, rounds, List.of()))
                .toList();
        Minecraft.getInstance().setScreen(new MinigameMapSelectionScreen(this, minigame, mapsData));
    }

    @Override
    public void renderDefaultBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderDefaultBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, TITLE, width / 2, panel.getY() + 10, 0xFFFFFF);
    }

    public static boolean canOpen() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.getCurrentServer() != null;
    }
}
