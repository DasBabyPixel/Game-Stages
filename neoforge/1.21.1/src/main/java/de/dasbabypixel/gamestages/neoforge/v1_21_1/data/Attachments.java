package de.dasbabypixel.gamestages.neoforge.v1_21_1.data;

import de.dasbabypixel.gamestages.common.BuildConstants;
import de.dasbabypixel.gamestages.common.data.GameStageReference;
import de.dasbabypixel.gamestages.common.v1_21_1.network.util.GameStagePayload;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class Attachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BuildConstants.MOD_ID);
    public static final Supplier<AttachmentType<List<GameStageReference>>> ATTACHMENT_REFERENCES = ATTACHMENT_TYPES.register("references", () -> AttachmentType
            .<List<GameStageReference>>builder(() -> List.of())
            .serialize(GameStagePayload.CODEC_REFERENCE_LIST)
            .copyOnDeath()
            .build());
}
