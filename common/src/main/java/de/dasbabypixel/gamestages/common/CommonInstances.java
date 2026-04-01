package de.dasbabypixel.gamestages.common;

import de.dasbabypixel.gamestages.common.entity.PlatformPlayerProvider;
import de.dasbabypixel.gamestages.common.network.PlatformPacketCreator;
import de.dasbabypixel.gamestages.common.network.PlatformPacketDistributor;
import org.jspecify.annotations.NonNull;

@SuppressWarnings("NotNullFieldNotInitialized")
public class CommonInstances {
    public static @NonNull PlatformPacketDistributor platformPacketDistributor;
    public static @NonNull PlatformPacketCreator platformPacketCreator;
    public static @NonNull PlatformPlayerProvider platformPlayerProvider;
}
