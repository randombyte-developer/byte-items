package de.randombyte.byteitems

import com.google.inject.Inject
import de.randombyte.byteitems.api.ByteItemsService
import de.randombyte.byteitems.commands.DeleteCommand
import de.randombyte.byteitems.commands.GiveCommand
import de.randombyte.byteitems.commands.ListCommand
import de.randombyte.byteitems.commands.SaveCommand
import de.randombyte.byteitems.config.ConfigAccessor
import de.randombyte.byteitems.config.OldConfigMover
import de.randombyte.kosp.extensions.getPlayer
import de.randombyte.kosp.extensions.sendTo
import de.randombyte.kosp.extensions.toText
import org.apache.commons.lang3.RandomUtils
import org.bstats.sponge.Metrics2
import org.slf4j.Logger
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments.*
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.scheduler.Task
import java.nio.file.Path
import java.util.*
import java.util.concurrent.TimeUnit

@Plugin(id = ByteItems.ID, name = ByteItems.NAME, version = ByteItems.VERSION, authors = [(ByteItems.AUTHOR)])
class ByteItems @Inject constructor(
        @ConfigDir(sharedRoot = false) configPath: Path,
        val logger: Logger,
        val bStats: Metrics2
) {
    internal companion object {
        const val ID = "byte-items"
        const val NAME = "ByteItems"
        const val VERSION = "2.3.1"
        const val AUTHOR = "RandomByte"

        const val ROOT_PERMISSION = "byteItems"

        const val ITEM_ID_ARG = "item"
        const val PLAYER_ARG = "player"
        const val AMOUNT_ARG = "amount"

        private val _INSTANCE = lazy { Sponge.getPluginManager().getPlugin(ID).get().instance.get() as ByteItems }
        val INSTANCE: ByteItems get() = _INSTANCE.value
    }

    val configAccessor = ConfigAccessor(configPath)

    val metricsNoteSent = mutableSetOf<UUID>()

    @Listener
    fun onPreInit(event: GameInitializationEvent) {
        OldConfigMover.moveOldConfigIfNeeded()

        configAccessor.reloadAll()
        registerCommands()
        registerService()

        if (needsMotivationalSpeech()) {
            Task.builder()
                    .delay(RandomUtils.nextLong(80, 130), TimeUnit.SECONDS)
                    .execute { -> Messages.motivationalSpeech.forEach { it.sendTo(Sponge.getServer().console) } }
                    .submit(this)
        }

        logger.info("$NAME loaded: $VERSION")
    }

    @Listener
    fun onReload(event: GameReloadEvent) {
        configAccessor.reloadAll()
        registerCommands()
        logger.info("Reloaded!")
    }

    private fun registerService() {
        // old
        val apiImpl = ByteItemsApiImpl(getConfig = { configAccessor.items.get() })
        Sponge.getServiceManager().setProvider(this, ByteItemsApi::class.java, apiImpl)

        // new
        Sponge.getServiceManager().setProvider(
                this,
                ByteItemsService::class.java,
                ByteItemsServiceImpl(getConfig = { configAccessor.items.get() }))
    }

    private fun registerCommands() {
        Sponge.getCommandManager().run { getOwnedBy(this@ByteItems).forEach { removeMapping(it) } }

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.save")
                        .arguments(string(ITEM_ID_ARG.toText()))
                        .executor(SaveCommand())
                        .build(), "save")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.give")
                        .arguments(
                                playerOrSource(PLAYER_ARG.toText()),
                                ByteItemsIdCommandArgument(ITEM_ID_ARG.toText()),
                                optional(integer(AMOUNT_ARG.toText())))
                        .executor(GiveCommand())
                        .build(), "give")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.list")
                        .executor(ListCommand())
                        .build(), "list")
                .child(CommandSpec.builder()
                        .permission("$ROOT_PERMISSION.delete")
                        .arguments(ByteItemsIdCommandArgument(ITEM_ID_ARG.toText()))
                        .executor(DeleteCommand())
                        .build(), "delete")
                .build(), "byteitems", "bi")
    }

    @Listener
    fun onPlayerJoin(event: ClientConnectionEvent.Join) {
        val uuid = event.targetEntity.uniqueId
        if (needsMotivationalSpeech(event.targetEntity)) {
            Task.builder()
                    .delay(RandomUtils.nextLong(10, 50), TimeUnit.SECONDS)
                    .execute { ->
                        val player = uuid.getPlayer() ?: return@execute
                        metricsNoteSent += uuid
                        Messages.motivationalSpeech.forEach { it.sendTo(player) }
                    }
                    .submit(this)
        }
    }

    private fun needsMotivationalSpeech(player: Player? = null) = configAccessor.general.get().enableMetricsMessages &&
            !Sponge.getMetricsConfigManager().areMetricsEnabled(this) &&
            ((player == null) || player.uniqueId !in metricsNoteSent && player.hasPermission("nucleus.mute.base")) // also passes OPs without Nucleus
}