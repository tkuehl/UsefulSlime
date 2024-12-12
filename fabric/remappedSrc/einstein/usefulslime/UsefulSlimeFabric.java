package einstein.usefulslime;

import einstein.usefulslime.init.ModBlocks;
import einstein.usefulslime.init.ModCommonConfigs;
import einstein.usefulslime.init.ModItems;
import fuzs.forgeconfigapiport.fabric.api.forge.v4.ForgeConfigRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraftforge.fml.config.ModConfig;

public class UsefulSlimeFabric implements ModInitializer, ClientModInitializer {

    @Override
    public void onInitialize() {
        UsefulSlime.init();

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.SPYGLASS, ModItems.SLIME_SLING.get());
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> {
            entries.add(ModItems.SLIPPERY_SLIME_BLOCK_ITEM.get());
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.addAfter(Items.TURTLE_HELMET, ModItems.SLIME_HELMET.get(), ModItems.SLIME_CHESTPLATE.get(), ModItems.SLIME_LEGGINGS.get(), ModItems.SLIME_BOOTS.get());
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> {
            entries.addBefore(Items.MUSHROOM_STEW, ModItems.JELLO.get());
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(UsefulSlime::onServerStopped);
        ForgeConfigRegistry.INSTANCE.register(UsefulSlime.MOD_ID, ModConfig.Type.COMMON, ModCommonConfigs.SPEC);
    }

    @Override
    public void onInitializeClient() {
        UsefulSlime.clientSetup();
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.SLIPPERY_SLIME_BLOCK.get(), RenderLayer.getTranslucent());
    }
}
