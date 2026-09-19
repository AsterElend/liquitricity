package aster.liquitricity.registry;

import aster.liquitricity.Liquitricity;
import aster.liquitricity.LiquitricityRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

public class LiquitricityTab {
    public static final ItemGroup LIQUITRICITY_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            Liquitricity.id("liquitricity_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("liquitricity.group.name"))
                    .icon(() -> new ItemStack(LiquitricityRegistry.CRACKLE_BUCKET))
                    .entries(((displayContext, entries) -> {
                        for (Item item: LiquitricityRegistry.ADD_TO_CREATIVE_TAB){
                            entries.add(item);
                        }
                    })
                    ).build()
    );
    public static void register(){};
}
