package de.randombyte.byteitems

import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.PatternMatchingCommandElement
import org.spongepowered.api.text.Text

/**
 * Matches against the saved items, and returns the ID.
 */
class ByteItemsIdCommandArgument(key: Text) : PatternMatchingCommandElement(key) {
    override fun getChoices(source: CommandSource) = ByteItems.INSTANCE.configAccessor.items.get().items.keys.toMutableList()

    override fun getValue(choice: String): String {
        return if (ByteItems.INSTANCE.configAccessor.items.get().items.containsKey(choice)) choice else {
            throw IllegalArgumentException(Messages.itemNotAvailable(choice))
        }
    }
}