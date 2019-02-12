package de.randombyte.byteitems.api;

import java.util.Optional;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.annotation.NonnullByDefault;

@NonnullByDefault
public interface ByteItemsService {
  /**
   * Returns the saved {@link ItemStackSnapshot}, or an empty {@link Optional} if there is no such
   * entry with the given id.
   *
   * @param id The ID of the saved item, for example "byte-items:my-item" or just "my-item"
   * @return The saved {@link ItemStackSnapshot} or an empty {@link Optional} if none was found
   */
  Optional<ItemStackSnapshot> get(String id);

  /**
   * Returns the prefix which ByteItems uses to identify saved {@link ItemStackSnapshot}.
   *
   * @return The ByteItems prefix, usually it is "byte-items"
   */
  String getPrefix();
}