package de.dasbabypixel.gamestages.common;

import de.dasbabypixel.gamestages.common.data.PlatformPlayerStagesProvider;
import de.dasbabypixel.gamestages.common.integration.kubejs.KJSCommonProvider;
import de.dasbabypixel.gamestages.common.network.PlatformPacketCreator;
import de.dasbabypixel.gamestages.common.network.PlatformPacketDistributor;

public class CommonInstances {
    public static KJSCommonProvider kjsCommonProvider;
    public static PlatformPacketDistributor platformPacketDistributor;
    public static PlatformPacketCreator platformPacketCreator;
    public static PlatformPlayerStagesProvider platformPlayerStagesProvider;
}
