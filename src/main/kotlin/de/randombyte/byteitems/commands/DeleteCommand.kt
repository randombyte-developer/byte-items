package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.kosp.PlayerExecutedCommand
import de.randombyte.kosp.extensions.executeCommand
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player

class DeleteCommand(
        val deleteItemStack: (id: String) -> Boolean,
        val updateCommands: () -> Unit
) : PlayerExecutedCommand() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val id = args.getOne<String>(ByteItems.ID_ARG).get()

        if (!deleteItemStack(id)) throw CommandException("Item '$id' is not available!".toText())

        player.sendMessage("Deleted '$id'!".green())
        updateCommands()
        player.executeCommand("byteItems list")

        return CommandResult.success()
    }
}