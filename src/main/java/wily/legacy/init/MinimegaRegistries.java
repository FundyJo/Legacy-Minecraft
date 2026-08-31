package wily.legacy.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.RegisterListing;

public final class MinimegaRegistries {
    private static final RegisterListing<Block> BLOCK_REGISTER = FactoryAPIPlatform.createRegister("minimega", BuiltInRegistries.BLOCK);
    private static final RegisterListing<SoundEvent> SOUND_EVENT_REGISTER = FactoryAPIPlatform.createRegister("minimega", BuiltInRegistries.SOUND_EVENT);

    public static final RegisterListing.Holder<Block> ABSOLUTE_SPEED_BOOST = BLOCK_REGISTER.add("absolute_speed_boost", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));
    public static final RegisterListing.Holder<Block> BEACON_BEAM = BLOCK_REGISTER.add("beacon_beam", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));
    public static final RegisterListing.Holder<Block> BOOSTER_VISUALIZER = BLOCK_REGISTER.add("booster_visualizer", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));
    public static final RegisterListing.Holder<Block> DIAMOND_RING_BLOCK = BLOCK_REGISTER.add("diamond_ring_block", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));
    public static final RegisterListing.Holder<Block> EMERALD_RING_BLOCK = BLOCK_REGISTER.add("emerald_ring_block", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));
    public static final RegisterListing.Holder<Block> GOLD_RING_BLOCK = BLOCK_REGISTER.add("gold_ring_block", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));
    public static final RegisterListing.Holder<Block> QBOOSTER_VISUALIZER = BLOCK_REGISTER.add("qbooster_visualizer", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));
    public static final RegisterListing.Holder<Block> THERMAL_VISUALIZER = BLOCK_REGISTER.add("thermal_visualizer", id -> new Block(FactoryAPIPlatform.setupBlockProperties(BlockBehaviour.Properties.copy(Blocks.BARRIER), id)));

    public static final RegisterListing.Holder<SoundEvent> BATTLE_ROUND_START = SOUND_EVENT_REGISTER.add("battle.round_start", () -> SoundEvent.createVariableRangeEvent(FactoryAPI.createLocation("minimega", "battle/round_start")));
    public static final RegisterListing.Holder<SoundEvent> BATTLE_THEME = SOUND_EVENT_REGISTER.add("battle.theme", () -> SoundEvent.createVariableRangeEvent(FactoryAPI.createLocation("minimega", "battle/theme")));
    public static final RegisterListing.Holder<SoundEvent> GLIDE_BOOST = SOUND_EVENT_REGISTER.add("glide.boost", () -> SoundEvent.createVariableRangeEvent(FactoryAPI.createLocation("minimega", "glide/boost")));
    public static final RegisterListing.Holder<SoundEvent> GLIDE_THEME = SOUND_EVENT_REGISTER.add("glide.theme", () -> SoundEvent.createVariableRangeEvent(FactoryAPI.createLocation("minimega", "glide/theme")));
    public static final RegisterListing.Holder<SoundEvent> TIMER_COUNTDOWN = SOUND_EVENT_REGISTER.add("timer.countdown", () -> SoundEvent.createVariableRangeEvent(FactoryAPI.createLocation("minimega", "timer/countdown")));
    public static final RegisterListing.Holder<SoundEvent> SHOWDOWN_START = SOUND_EVENT_REGISTER.add("showdown.start", () -> SoundEvent.createVariableRangeEvent(FactoryAPI.createLocation("minimega", "showdown/start")));

    private MinimegaRegistries() {
    }

    public static void register() {
        BLOCK_REGISTER.register();
        SOUND_EVENT_REGISTER.register();
    }
}
