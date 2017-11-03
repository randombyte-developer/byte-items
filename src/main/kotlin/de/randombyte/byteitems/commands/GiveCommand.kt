package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.kosp.extensions.give
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.item.inventory.ItemStackSnapshot

class GiveCommand(
        val plugin: ByteItems,
        val getItemStack: (id: String) -> ItemStackSnapshot?
) : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val targetPlayer = args.getOne<Player>(ByteItems.PLAYER_ARG).get()
        val id = args.getOne<String>(ByteItems.ID_ARG).get()

        val itemStackSnapshot = getItemStack(id) ?: throw CommandException("Item '$id' is not available!".toText())

        targetPlayer.give(itemStackSnapshot.createStack(), Cause.source(plugin).build())
        targetPlayer.sendMessage("Given '$id' to ${targetPlayer.name}!".green())

        return CommandResult.success()
    }
}