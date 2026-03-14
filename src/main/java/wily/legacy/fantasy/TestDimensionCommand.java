package wily.legacy.fantasy;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Test-Command zum Laden und Teleportieren in die Cavern-Welt.
 *
 * Verwendung: /test
 */
public class TestDimensionCommand {

    private static final ResourceLocation CAVERN_ID = ResourceLocation.fromNamespaceAndPath("legacy", "cavern");
    private static final String CAVERN_PATH = "C:\\Users\\timos\\Documents\\testing\\cavern_largeplus";

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        commandDispatcher.register(Commands.literal("test")
                .requires(source -> source.hasPermission(2)) // Operator-Level
                .executes(TestDimensionCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        // Prüfen ob der Command von einem Spieler ausgeführt wurde
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Dieser Command kann nur von einem Spieler ausgeführt werden!"));
            return 0;
        }

        MinecraftServer server = source.getServer();

        // Prüfen ob die Welt bereits geladen ist
        if (DimensionLoader.isDimensionLoaded(CAVERN_ID)) {
            // Welt ist bereits geladen - direkt teleportieren
            teleportToCavern(player, source);
        } else {
            // Welt muss erst geladen werden
            source.sendSuccess(() -> Component.literal("§eWelt wird geladen, bitte warten..."), false);

            DimensionLoader.loadExternalWorld(server, CAVERN_ID, CAVERN_PATH, (srv, holder) -> {
                // Diese Callback wird ausgeführt, sobald die Welt geladen ist
                ServerLevel cavernWorld = holder.world.asWorld();

                if (cavernWorld != null) {
                    // Welteinstellungen
                    cavernWorld.setDayTime(6000); // Mittag für bessere Sicht
                    cavernWorld.setWeatherParameters(0, 0, false, false);

                    // Spieler teleportieren
                    teleportPlayerToWorld(player, cavernWorld, source);
                } else {
                    source.sendFailure(Component.literal("§cFehler beim Laden der Welt!"));
                }
            });
        }

        return 1;
    }

    /**
     * Teleportiert den Spieler zur bereits geladenen Cavern-Welt.
     */
    private static void teleportToCavern(ServerPlayer player, CommandSourceStack source) {
        ServerLevel cavernWorld = DimensionLoader.getLoadedWorld(CAVERN_ID);

        if (cavernWorld != null) {
            teleportPlayerToWorld(player, cavernWorld, source);
        } else {
            source.sendFailure(Component.literal("§cWelt konnte nicht gefunden werden!"));
        }
    }

    /**
     * Teleportiert einen Spieler zu einer Welt.
     */
    private static void teleportPlayerToWorld(ServerPlayer player, ServerLevel targetWorld, CommandSourceStack source) {
        // Spawn-Position der Zielwelt finden (0, 64, 0 als Fallback)
        BlockPos spawnPos = new BlockPos(0, 64, 0);

        // Sichere Y-Koordinate finden (nicht in der Luft oder im Boden)
        BlockPos safePos = findSafeSpawnPosition(targetWorld, spawnPos);

        // Teleportation durchführen
        Vec3 targetPos = new Vec3(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);

        // Teleportiere den Spieler zur Zielwelt
        player.teleportTo(targetWorld, targetPos.x, targetPos.y, targetPos.z,
            java.util.Set.of(), player.getYRot(), player.getXRot(), true);

        // Erfolgsmeldung
        source.sendSuccess(() -> Component.literal(
            "§aTeleportiert zu Cavern-Welt! §7(" +
            safePos.getX() + ", " + safePos.getY() + ", " + safePos.getZ() + ")"
        ), false);

        player.sendSystemMessage(Component.literal("§6Willkommen in der Cavern-Welt!"));
    }

    /**
     * Findet eine sichere Spawn-Position (auf festem Boden).
     */
    private static BlockPos findSafeSpawnPosition(ServerLevel world, BlockPos startPos) {
        // Versuche bis zu 10 Blöcke nach oben/unten zu suchen
        for (int yOffset = 0; yOffset <= 10; yOffset++) {
            BlockPos checkPos = startPos.offset(0, yOffset, 0);
            if (isSafeSpawnPosition(world, checkPos)) {
                return checkPos;
            }

            checkPos = startPos.offset(0, -yOffset, 0);
            if (isSafeSpawnPosition(world, checkPos)) {
                return checkPos;
            }
        }

        // Fallback: Verwende Original-Position
        return startPos;
    }

    /**
     * Prüft ob eine Position sicher zum Spawnen ist.
     */
    private static boolean isSafeSpawnPosition(ServerLevel world, BlockPos pos) {
        // Boden muss solide sein
        if (!world.getBlockState(pos.below()).isSolidRender()) {
            return false;
        }

        // Position und darüber müssen Luft sein
        return world.getBlockState(pos).isAir() &&
               world.getBlockState(pos.above()).isAir();
    }
}
