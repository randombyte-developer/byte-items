package de.randombyte.byteitems.config

import de.randombyte.byteitems.ByteItems
import org.spongepowered.api.Sponge
import java.nio.file.Files

object OldConfigMover {
    fun moveOldConfigIfNeeded() {
        val oldFile = Sponge.getConfigManager().getSharedConfig(ByteItems.INSTANCE).configPath
        val newFile = Sponge.getConfigManager().getPluginConfig(ByteItems.INSTANCE).directory.resolve(ConfigAccessor.ITEMS_CONFIG_FILE_NAME)

        if (Files.exists(oldFile) && Files.notExists(newFile)) {
            Files.move(oldFile, newFile)
            ByteItems.INSTANCE.logger.info("Moved '${oldFile.toAbsolutePath()}' to '${newFile.toAbsolutePath()}'!")
        }
    }
}