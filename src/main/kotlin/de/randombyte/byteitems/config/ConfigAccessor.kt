package de.randombyte.byteitems.config

import de.randombyte.kosp.config.ConfigAccessor
import java.nio.file.Path

class ConfigAccessor(configPath: Path) : ConfigAccessor(configPath) {

    companion object {
        // because it is used somewhere else
        const val ITEMS_CONFIG_FILE_NAME = "items.conf"
    }

    val general = getConfigHolder<GeneralConfig>("general.conf")
    val items = getConfigHolder<ItemsConfig>(ITEMS_CONFIG_FILE_NAME)

    override val holders = listOf(general, items)
}