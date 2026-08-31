package wily.legacy.minigame.network.payload;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.Util;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.network.CommonNetwork;

public record C2S2CMinimegaProtocolVersionPayload(int version) implements CommonNetwork.Payload {
    private static final int[] PREVIOUS_VERSIONS = new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 100, 101, 102, 103, 104, 105, 106, 107, 108, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 401110, 401111, 401120, 401130, 500000, 500001, 600000, 600010, 601000, 601001, 601010, 601020, 602000, 602001, 602002, 602003, 603000, 60301000, 60301100, 60400000, 60500000, 60501000, 60502000, 60503000, 60503100};
    public static final int[] CURRENT_WORKING = new int[0];
    public static final int VERSION = 60503200;
    public static final CommonNetwork.Identifier<C2S2CMinimegaProtocolVersionPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createLocation("minimega", "protocol_version"), C2S2CMinimegaProtocolVersionPayload::new);
    private static final Int2ObjectMap<String> MAP = Util.make(() -> {
        Int2ObjectArrayMap<String> map = new Int2ObjectArrayMap<>();
        map.put(9, "0.0.1-alpha.8.13");
        map.put(10, "0.0.1-alpha.8.14");
        map.put(11, "0.0.1-alpha.9");
        map.put(12, "0.0.1-alpha.9.1");
        map.put(13, "0.0.1-alpha.9.2");
        map.put(14, "0.0.1-alpha.9.21");
        map.put(15, "0.0.1-alpha.9.22");
        map.put(16, "0.0.1-alpha.9.23");
        map.put(17, "0.0.1-alpha.9.24");
        map.put(18, "0.0.1-alpha.10");
        map.put(19, "0.0.1-alpha.10.1");
        map.put(20, "0.0.1-alpha.10.2");
        map.put(21, "0.0.1-alpha.10.21");
        map.put(22, "0.0.1-alpha.10.22");
        map.put(23, "0.0.1-alpha.10.23");
        map.put(24, "0.0.1-alpha.11");
        map.put(25, "0.0.1-alpha.11.1");
        map.put(26, "0.0.1-alpha.12");
        map.put(27, "0.0.1-alpha.12.1");
        map.put(28, "0.0.1-alpha.13");
        map.put(29, "0.0.1-alpha.13.1");
        map.put(30, "0.0.1-alpha.13.2");
        map.put(31, "0.0.1-alpha.13.3");
        map.put(32, "0.0.1-alpha.13.4");
        map.put(33, "0.0.1-alpha.14");
        map.put(34, "0.0.1-alpha.14.1");
        map.put(35, "0.0.1-alpha.14.2");
        map.put(36, "0.0.1-alpha.14.21");
        map.put(37, "0.0.1-alpha.14.22");
        map.put(38, "0.0.1-alpha.14.23");
        map.put(39, "0.0.1-alpha.14.24");
        map.put(40, "0.0.1-alpha.15");
        map.put(41, "0.0.1-alpha.15.1");
        map.put(42, "0.0.1-alpha.15.2");
        map.put(43, "0.0.1-alpha.15.3");
        map.put(44, "0.0.1-alpha.15.4");
        map.put(45, "0.0.1-alpha.15.5");
        map.put(46, "0.0.1-alpha.15.6");
        map.put(47, "0.0.1-alpha.15.7");
        map.put(48, "0.0.1-alpha.15.8");
        map.put(49, "0.0.1-alpha.16");
        map.put(50, "0.0.1-alpha.17");
        map.put(51, "0.0.1-alpha.17.1");
        map.put(52, "0.0.1-alpha.17.2");
        map.put(53, "0.0.1-alpha.17.3");
        map.put(54, "0.0.1-alpha.17.4");
        map.put(55, "0.0.1-alpha.17.5");
        map.put(56, "0.0.1-alpha.17.6");
        map.put(57, "0.0.1-alpha.17.7");
        map.put(100, "0.0.1-alpha.18");
        map.put(101, "0.0.1-alpha.18.1");
        map.put(102, "0.0.1-alpha.18.2");
        map.put(103, "0.0.1-alpha.18.3");
        map.put(104, "0.0.1-alpha.18.4");
        map.put(105, "0.0.1-alpha.18.5");
        map.put(106, "0.0.1-alpha.18.6");
        map.put(107, "0.0.1-alpha.18.7");
        map.put(108, "0.0.1-alpha.18.8");
        map.put(200, "0.0.1-alpha.19");
        map.put(201, "0.0.1-alpha.19.1");
        map.put(202, "0.0.1-alpha.19.2");
        map.put(203, "0.0.1-alpha.19.3");
        map.put(204, "0.0.1-alpha.19.4");
        map.put(205, "0.0.1-alpha.19.5");
        map.put(206, "0.0.1-alpha.19.6");
        map.put(207, "0.0.1-alpha.19.7");
        map.put(208, "4.1.60");
        map.put(209, "4.1.61");
        map.put(210, "4.1.70");
        map.put(211, "4.1.80");
        map.put(212, "4.1.90");
        map.put(213, "4.1.100");
        map.put(214, "4.1.101");
        map.put(215, "4.1.102");
        map.put(216, "4.1.103");
        map.put(401110, "4.1.110");
        map.put(401111, "4.1.111");
        map.put(401120, "4.1.120");
        map.put(401130, "4.1.130-special");
        map.put(500000, "5.0.0");
        map.put(500001, "5.0.1");
        map.put(500010, "5.0.10");
        map.put(600000, "6.0.0");
        map.put(600010, "6.0.10");
        map.put(601000, "6.1.0");
        map.put(601001, "6.1.1");
        map.put(601010, "6.1.10");
        map.put(601020, "6.1.20");
        map.put(602000, "6.2.0");
        map.put(602001, "6.2.1");
        map.put(602002, "6.2.2");
        map.put(602003, "6.2.3");
        map.put(603000, "6.3.0");
        map.put(60301000, "6.3.10");
        map.put(60301100, "6.3.11");
        map.put(60400000, "6.4.0");
        map.put(60500000, "6.5.0");
        map.put(60501000, "6.5.10");
        map.put(60502000, "6.5.20");
        map.put(60503000, "6.5.30");
        map.put(60503100, "6.5.31");
        map.put(60503200, "6.5.32");
        return map;
    });

    public C2S2CMinimegaProtocolVersionPayload(CommonNetwork.PlayBuf buf) {
        this(buf.get().readInt());
    }

    public static String getFriendlyVersion() {
        return getFriendlyVersion(VERSION);
    }

    public static String getFriendlyVersion(int version) {
        if (!MAP.containsKey(version)) {
            return version > VERSION ? "a future version" : "unknown";
        }
        return MAP.get(version);
    }

    public int[] previousVersions() {
        return PREVIOUS_VERSIONS;
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeInt(version);
    }

    @Override
    public void apply(Context context) {
        MinimegaNetworkHandlers.blocked(this, context);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }
}
