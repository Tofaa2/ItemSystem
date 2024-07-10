package me.tofaa.itemsystem;

import org.jetbrains.annotations.NotNull;

/**
 * This class represents a bit of data an {@link Item} can store.
 * <p>
 *     Each Item can have only one type of ItemComponent attached to it.
 *     This is useful for storing additional data about an item.
 *     For example, an ItemComponent could be used to store the custom durability of an item, or attributes.
 * </p>
 */
public interface ItemComponent extends Comparable<ItemComponent> {

    default int handlerPriority() {
        return ItemProcessor.instance().handler(this.getClass()).priority();
    }

    @Override
    default int compareTo(@NotNull ItemComponent o) {
        return Integer.compare(handlerPriority(), o.handlerPriority());
    }
}
