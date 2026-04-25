package de.dasbabypixel.gamestages.neoforge.v1_21_1.entity;

import de.dasbabypixel.gamestages.common.data.server.CompositeStages;
import org.jspecify.annotations.Nullable;

public interface IBlockEntity {
    void reloadOwners();

    @Nullable CompositeStages stages();
}
