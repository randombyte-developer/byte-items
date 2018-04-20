package de.randombyte.byteitems.commands

import de.randombyte.byteitems.ByteItems
import de.randombyte.kosp.extensions.give
import de.randombyte.kosp.extensions.green
import de.randombyte.kosp.extensions.orNull
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStackSnapshot

class GiveCommand : CommandExecutor {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val targetPlayer = args.getOne<Player>(ByteItems.PLAYER_ARG).get()
        val (id, itemStackSnapshot) = args.getOne<Pair<String, ItemStackSnapshot>>(ByteItems.ID_ARG).get()
        val amount = args.getOne<Int>(ByteItems.AMOUNT_ARG).orNull()

        val itemStack = itemStackSnapshot.createStack()

        if (amount != null) {
            itemStack.quantity = amount.coerceAtLeast(1)
        }

        targetPlayer.give(itemStack)
        targetPlayer.sendMessage("Given '$id' to ${targetPlayer.name}!".green())

        return CommandResult.success()
    }
}