package me.tofaa.itemsystem;

import com.google.gson.*;
import net.kyori.adventure.nbt.BinaryTag;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.NamespaceID;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

public class ItemProcessor implements Iterable<Item>{

    private static ItemProcessor instance;

    public static ItemProcessor init(File itemsFolder) {
        instance = new ItemProcessor(itemsFolder);
        return instance;
    }

    public static ItemProcessor instance() {
        return instance;
    }

    public static Gson GSON = new Gson();

    private final Map<String, Item> items;
    private final Map<Class<? extends ItemComponent>, ItemComponentHandler<?>> handlersByClass;
    private final Map<String, ItemComponentHandler> handlersById;
    private final File itemsFolder;
    private final Tag<String> idTag = Tag.String("id");
    private final Tag<BinaryTag> componentsTag = Tag.NBT("components");

    public ItemProcessor(File itemsFolder) {
        this.itemsFolder = itemsFolder;
        this.items = new HashMap<>();
        this.handlersByClass = new HashMap<>();
        this.handlersById = new HashMap<>();
    }

    public void loadAll() {
        List<File> files = listFiles(".json");
        for (File file : files) {
            loadJson(file);
        }
    }

    public @NotNull ItemStack toItemStack(Item item) {
        if (item == null || item == Item.AIR) return ItemStack.AIR;
        var builder = ItemStack.builder(item.material())
                .amount(item.amount());
        var itemId = item.id().asString();

        Map<String, BinaryTag> componentData = new HashMap<>();

        item.components().forEachOrdered(component -> {
            ItemComponentHandler handler = handlersByClass.get(component.getClass());
            handler.apply(itemId, component, builder);
            componentData.put(handler.componentId(), handler.toNbt(itemId, component));
        });
        builder.set(componentsTag, CompoundBinaryTag.from(componentData));
        builder.set(idTag, itemId);
        return builder.build();
    }

    public @NotNull Item fromItemStack(@NotNull ItemStack stack) {
        if (stack.isAir()) return Item.AIR;
        var material = stack.material();
        var amount = stack.amount();
        var id = requireNonNull(stack.getTag(idTag));
        CompoundBinaryTag components = (CompoundBinaryTag) requireNonNull(stack.getTag(componentsTag));
        ItemComponent[] itemComponents = new ItemComponent[components.size()];
        components.forEach((entry) -> {
            var componentId =entry.getKey();
            CompoundBinaryTag componentData = (CompoundBinaryTag) entry.getValue();
            var handler = handlersById.get(componentId);
            if (handler == null) {
                throw new RuntimeException("No handler found for component id " + componentId);
            }
            itemComponents[handler.priority()] = handler.fromNbt(id, componentData);
        });
        return new ItemImpl(NamespaceID.from(id), material, amount, itemComponents);
    }

    public void loadJson(File file) {
        try {
            FileReader fr = new FileReader(file);
            var json = JsonParser.parseReader(fr); // validate json (throws exception if invalid)
            Collection<Item> items = fromJson(json);
            for (Item item : items) {
                this.items.put(item.id().asString(), item);
            }
            fr.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Item fromJsonObject(JsonObject obj) {
        var id = obj.get("id").getAsString();
        var material = Material.fromNamespaceId(obj.get("material").getAsString());
        var amount = obj.has("amount") ? obj.get("amount").getAsInt() : 1;
        JsonArray components = obj.getAsJsonArray("components");
        ItemComponent[] itemComponents = new ItemComponent[components.size()];
        for (int i=0; i < itemComponents.length; i++) {
            var component = components.get(i).getAsJsonObject();
            var componentId = component.get("id").getAsString();
            var handler = handlersById.get(componentId);
            if (handler == null) {
                throw new RuntimeException("No handler found for component id " + componentId);
            }
            itemComponents[i] = handler.fromJson(id, component);
        }
        return new ItemImpl(NamespaceID.from(id), material, amount, itemComponents);
    }

    public Collection<Item> fromJson(JsonElement json) {
        if (json instanceof JsonObject obj) {
            return List.of(fromJsonObject(obj));
        }
        else if (json instanceof JsonArray array) {
            List<Item> items = new ArrayList<>();
            for (JsonElement element : array) {
                items.addAll(fromJson(element));
            }
            return items;
        }
        else {
            throw new RuntimeException("Invalid json element type: " + json.getClass().getName());
        }
    }

    public <C extends ItemComponent> ItemComponentHandler<C> handler(Class<C> componentType) {
        return (ItemComponentHandler<C>) handlersByClass.get(componentType);
    }

    public Item getItem(NamespaceID id) {
        return items.getOrDefault(id.asString(), Item.AIR);
    }

    public Item getItem(String id) {
        return items.getOrDefault(id, Item.AIR);
    }

    public void registerHandler(ItemComponentHandler<?> handler) {
        handlersByClass.put(handler.componentType(), handler);
        handlersById.put(handler.componentId(), handler);
    }

    public List<File> listFiles(String extension) {
        FilenameFilter filter = (dir, name) -> name.endsWith(extension);
        List<File> files = new ArrayList<>();
        File[]  f= itemsFolder.listFiles(filter);
        if (f == null) {
            return files;
        }
        for (File file : f) {
            if (file.isDirectory()) {
                files.addAll(listFiles(extension));
            }
            else {
                files.add(file);
            }
        }
        return files;
    }

    public File itemsFolder() {
        return itemsFolder;
    }

    @NotNull
    @Override
    public Iterator<Item> iterator() {
        return items.values().iterator();
    }
}
