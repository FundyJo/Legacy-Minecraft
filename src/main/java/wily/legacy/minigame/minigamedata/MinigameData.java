package wily.legacy.minigame.minigamedata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import wily.legacy.minigame.Minigame;
import wily.legacy.minigame.config.MinigameConfigCodecs;
import wily.legacy.minigame.config.MinigameSpecificConfig;

import java.util.List;

public record MinigameData(List<Identifier> selectedMaps,
                           Minigame minigame,
                           int maxPlayers,
                           MinigameSpecificConfig config,
                           boolean online,
                           boolean isPublic) {
    public static final Codec<MinigameData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.listOf().fieldOf("selectedMaps").forGetter(MinigameData::selectedMaps),
            Codec.INT.xmap(Minigame::fromId, Minigame::getId).fieldOf("minigame").forGetter(MinigameData::minigame),
            Codec.intRange(1, 32).fieldOf("maxPlayers").forGetter(MinigameData::maxPlayers),
            MinigameConfigCodecs.CODEC.fieldOf("config").forGetter(MinigameData::config),
            Codec.BOOL.fieldOf("online").forGetter(MinigameData::online),
            Codec.BOOL.fieldOf("public").forGetter(MinigameData::isPublic)
    ).apply(instance, MinigameData::new));

    public static final StreamCodec<ByteBuf, MinigameData> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
}
