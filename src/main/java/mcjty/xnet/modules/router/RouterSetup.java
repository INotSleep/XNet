package mcjty.xnet.modules.router;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.xnet.modules.router.blocks.TileEntityRouter;
import mcjty.xnet.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.xnet.setup.Registration.*;

public class RouterSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<BaseBlock> ROUTER = BLOCKS.register("router", TileEntityRouter::createBlock);
    public static final RegistryObject<Item> ROUTER_ITEM = ITEMS.register("router", () -> new BlockItem(ROUTER.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_ROUTER = TILES.register("router", () -> TileEntityType.Builder.create(TileEntityRouter::new, ROUTER.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_ROUTER = CONTAINERS.register("router", GenericContainer::createContainerType);
}
