package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.kosp.PlayerExecutedCommand
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.toText
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStackSnapshot

internal class SaveCommand(val saveItemStack: (id: String, ItemStackSnapshot) -> Boolean) : PlayerExecutedCommand() {
    override fun executedByPlayer(player: Player, args: CommandContext): CommandResult {
        val id = args.getOne<String>(ByteItems.ID_ARG).get()

        val itemStack = player.getItemInHand(HandTypes.MAIN_HAND)
                .orElseThrow { CommandException("Hold the ItemStack in your main hand while saving!".toText()) }

        if (!saveItemStack(id, itemStack.createSnapshot())) throw CommandException("ID '$id' is already in use!".toText())

        player.sendMessage("Saved ItemStack '$id'!".green())

        return CommandResult.success()
    }
}