package de.randombyte.byteitems

import com.google.inject.Inject
import de.randombyte.byteitems.commands.DeleteCommand
import de.randombyte.byteitems.commands.GiveCommand
import de.randombyte.byteitems.commands.ListCommand
import de.randombyte.byteitems.commands.SaveCommand
import de.randombyte.kosp.bstats.BStats
import de.randombyte.kosp.extensions.toText
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.plugin.Plugin
import java.nio.file.Files
import java.nio.file.Path

@Plugin(id = ByteItems.ID, name = ByteItems.NAME, version = ByteItems.VERSION, authors = arrayOf(ByteItems.AUTHOR))
class ByteItems @Inject constructor(
        @ConfigDir(sharedRoot = false) configPath: Path,
        val logger: Logger,
        val bStats: BStats
) {
    internal companion object {
        const val ID = "byte-items"
        const val NAME = "ByteItems"
        const val VERSION = "0.1"
        const val AUTHOR = "RandomByte"

        const val ROOT_PERMISSION = "byteItems"

        const val ID_ARG = "id"
        const val PLAYER_ARG = "player"
    }

    init {
        if (Files.notExists(configPath)) Files.createDirectory(configPath)
    }

    private val savedItemsConfigLoader = HoconConfigurationLoader.builder().setPath(configPath.resolve("saved-items.conf")).build()
    private val itemManager = ItemManager(savedItemsConfigLoader)

    @Listener
    fun onInit(event: GameInitializationEvent) {
        registerCommands()
        logger.info("$NAME loaded: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        registerCommands()
        logger.info("Reloaded!")
    }

    private fun registerCommands() {
        Sponge.getCommandManager().run { getOwnedBy(this@ByteItems).forEach { removeMapping(it) } }

        val idChoices = itemManager.getAll().keys.map { it to it }.toMap()

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.save")
                        .arguments(string(ID_ARG.toText()))
                        .executor(SaveCommand(
                                saveItemStack = this::safelyAddItemStack,
                                updateCommands = this::registerCommands))
                        .build(), "save")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.give")
                        .arguments(playerOrSource(PLAYER_ARG.toText()), choices(ID_ARG.toText(), idChoices))
                        .executor(GiveCommand(plugin = this, getItemStack = itemManager::get))
                        .build(), "give")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.list")
                        .executor(ListCommand(getAllItemStacks = itemManager::getAll))
                        .build(), "list")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.delete")
                        .arguments(string(ID_ARG.toText()))
                        .executor(DeleteCommand(
                                deleteItemStack = this::safelyDeleteItemStack,
                                updateCommands = this::registerCommands))
                        .build(), "delete")
                .build(), "byteItems", "bi")
    }

    private fun safelyAddItemStack(id: String, itemStackSnapshot: ItemStackSnapshot): Boolean = if (itemManager.has(id)) false else {
        itemManager.put(id, itemStackSnapshot)
        true
    }

    private fun safelyDeleteItemStack(id: String): Boolean = if (!itemManager.has(id)) false else {
        itemManager.remove(id)
        true
    }
}