package de.dasbabypixel.gamestages.common;

import de.dasbabypixel.gamestages.common.entity.PlatformPlayerProvider;
import de.dasbabypixel.gamestages.common.network.PlatformPacketCreator;
import de.dasbabypixel.gamestages.common.network.PlatformPacketDistributor;

public class CommonInstances {
    public static PlatformPacketDistributor platformPacketDistributor;
    public static PlatformPacketCreator platformPacketCreator;
    public static PlatformPlayerProvider platformPlayerProvider;
}
