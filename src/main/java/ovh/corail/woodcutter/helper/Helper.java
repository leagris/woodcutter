package ovh.corail.woodcutter.helper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import ovh.corail.woodcutter.config.ConfigWoodcutter;
import ovh.corail.woodcutter.config.CustomConfig;
import ovh.corail.woodcutter.recipe.WoodcuttingRecipe;
import ovh.corail.woodcutter.registry.ModRecipeTypes;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.StreamSupport;

public class Helper {
    public static void registerSharedConfig() {
        ModLoadingContext ctx = ModLoadingContext.get();
        ctx.getActiveContainer().addConfig(new CustomConfig(ConfigWoodcutter.GENERAL_SPEC, ctx.getActiveContainer()));
    }

    public static List<WoodcuttingRecipe> getSortedMatchingRecipes(Level level, Container inventory) {
        return level.getRecipeManager().byType(ModRecipeTypes.WOODCUTTING)
                .values().stream()
                .filter(recipe -> recipe.matches(inventory, level))
                .sorted(RECIPE_COMPARATOR.apply(level))
                .toList();
    }

    public static final Function<Level, Comparator<Recipe<Container>>> RECIPE_COMPARATOR = level -> Comparator.<Recipe<Container>, String>comparing(recipe -> {
        String[] name = getRegistryPath(recipe.getResultItem(level.registryAccess()).getItem()).split("_");
        return name[name.length - 1];
    }).thenComparing(recipe -> getRegistryName(recipe.getResultItem(level.registryAccess()).getItem()));

    public static ResourceLocation getRegistryRL(ItemStack stack) {
        return getRegistryRL(stack.getItem());
    }

    public static ResourceLocation getRegistryRL(Item item) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
    }

    public static ResourceLocation getRegistryRL(Block block) {
        return Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
    }

    public static String getRegistryNamespace(Item item) {
        return Optional.ofNullable(ForgeRegistries.ITEMS.getKey(item)).map(ResourceLocation::getNamespace).map(String::toString).orElse("");
    }

    public static String getRegistryPath(Item item) {
        return Optional.ofNullable(ForgeRegistries.ITEMS.getKey(item)).map(ResourceLocation::getPath).map(String::toString).orElse("");
    }

    public static String getRegistryName(Item item) {
        return Optional.ofNullable(ForgeRegistries.ITEMS.getKey(item)).map(ResourceLocation::toString).orElse("");
    }

    public static void fillItemSet(Set<Item> items, TagKey<Item> tagKey) {
        //noinspection deprecation
        BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).forEach(holder -> items.add(holder.value()));
    }

    public static Iterable<Holder<Item>> getItems(TagKey<Item> tagKey) {
        //noinspection deprecation
        return BuiltInRegistries.ITEM.getTagOrEmpty(tagKey);
    }

    public static boolean isInTag(Item item, TagKey<Item> tagKey) {
        // TODO re-evaluate
        return StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).spliterator(), false).anyMatch(holder -> holder.value() == item);
    }

    @SuppressWarnings("ConstantConditions")
    public static <T> T unsafeNullCast() {
        return null;
    }
}
