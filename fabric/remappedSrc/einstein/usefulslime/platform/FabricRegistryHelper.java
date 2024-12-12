package einstein.usefulslime.platform;

import einstein.usefulslime.platform.services.RegistryHelper;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static einstein.usefulslime.UsefulSlime.loc;

public class FabricRegistryHelper implements RegistryHelper {

    @Override
    public <T extends Block> Supplier<Block> registerBlock(String name, Supplier<T> block) {
        T t = Registry.register(Registries.BLOCK, loc(name), block.get());
        return () -> t;
    }

    @Override
    public <T extends Item> Supplier<Item> registerItem(String name, Supplier<T> item) {
        T t = Registry.register(Registries.ITEM, loc(name), item.get());
        return () -> t;
    }
}
