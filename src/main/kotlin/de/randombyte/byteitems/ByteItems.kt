package de.randombyte.byteitems

import com.google.inject.Inject
import de.randombyte.byteitems.commands.DeleteCommand
import de.randombyte.byteitems.commands.GiveCommand
import de.randombyte.byteitems.commands.ListCommand
import de.randombyte.byteitems.commands.SaveCommand
import de.randombyte.kosp.bstats.BStats
import de.randombyte.kosp.config.ConfigManager
import de.randombyte.kosp.extensions.toText
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GamePreInitializationEvent
import org.spongepowered.api.plugin.Plugin

@Plugin(id = ByteItems.ID, name = ByteItems.NAME, version = ByteItems.VERSION, authors = arrayOf(ByteItems.AUTHOR))
class ByteItems @Inject constructor(
        @DefaultConfig(sharedRoot = true) configurationLoader: ConfigurationLoader<CommentedConfigurationNode>,
        val logger: Logger,
        val bStats: BStats
) {
    internal companion object {
        const val ID = "byte-items"
        const val NAME = "ByteItems"
        const val VERSION = "2.0"
        const val AUTHOR = "RandomByte"

        const val ROOT_PERMISSION = "byteItems"

        const val ID_ARG = "id"
        const val PLAYER_ARG = "player"
    }

    private val configManager = ConfigManager(
            configLoader = configurationLoader,
            clazz = Config::class.java,
            hyphenSeparatedKeys = true
    )

    private lateinit var config: Config

    @Listener
    fun onPreInit(event: GamePreInitializationEvent) {
        loadConfig()
        registerCommands()
        registerService()
        logger.info("$NAME loaded: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        loadConfig()
        registerCommands()
        logger.info("Reloaded!")
    }

    private fun registerService() {
        val apiImpl = ByteItemsApiImpl(getConfig = { config })
        Sponge.getServiceManager().setProvider(this, ByteItemsApi::class.java, apiImpl)
    }

    private fun registerCommands() {
        Sponge.getCommandManager().run { getOwnedBy(this@ByteItems).forEach { removeMapping(it) } }

        val itemChoices = config.items.map { it.key to it.toPair() }.toMap()

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.save")
                        .arguments(string(ID_ARG.toText()))
                        .executor(SaveCommand(saveItemStack = { id, itemStackSnapshot ->
                            if (config.items.containsKey(id)) return@SaveCommand false
                            config = with(config) { copy(items = items + (id to itemStackSnapshot)) }
                            saveConfig()
                            registerCommands()
                            true
                        }))
                        .build(), "save")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.give")
                        .arguments(playerOrSource(PLAYER_ARG.toText()), choices(ID_ARG.toText(), itemChoices))
                        .executor(GiveCommand())
                        .build(), "give")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.list")
                        .executor(ListCommand(items = config.items))
                        .build(), "list")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.delete")
                        .arguments(string(ID_ARG.toText()))
                        .executor(DeleteCommand(
                                deleteItemStack = { id ->
                                    if (!config.items.containsKey(id)) return@DeleteCommand false
                                    config = with(config) { copy(items = items - id) }
                                    saveConfig()
                                    registerCommands()
                                    true
                                }))
                        .build(), "delete")
                .build(), "byteItems", "bi")
    }

    private fun loadConfig() {
        config = configManager.get()
        saveConfig() // regenerate config
    }

    private fun saveConfig() {
        configManager.save(config)
    }
}