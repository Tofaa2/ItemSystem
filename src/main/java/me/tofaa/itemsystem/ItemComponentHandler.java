package me.tofaa.itemsystem;

import com.google.gson.JsonElement;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This interface is responsible for handling a specific type of {@link ItemComponent}.
 * Each implementation of this interface MUST be registered and also provide ways of reading and writing the component from json and nbt.
 * It should also optionally write data to the ItemStack if needed.
 * These handlers are ordered by priority when applying to the ItemStack. So keep that in mind when adding lore or other data to the ItemStack.
 * @param <C> The type of {@link ItemComponent} this handler is responsible for.
 */
public interface ItemComponentHandler<C extends ItemComponent> extends Comparable<ItemComponentHandler<?>>  {

    @NotNull Class<C> componentType();
    @NotNull String componentId();

    @NotNull C fromJson(String itemId, JsonElement json);

    @NotNull C fromNbt(String itemId, CompoundBinaryTag nbt);

    CompoundBinaryTag toNbt(String itemId, @NotNull C component);

    void apply(String itemId, @NotNull C component, ItemStack.Builder builder);

    int priority();

    @Override
    default int compareTo(@NotNull ItemComponentHandler<?> o) {
        return Integer.compare(priority(), o.priority());
    }
}
