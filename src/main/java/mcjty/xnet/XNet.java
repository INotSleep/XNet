package mcjty.xnet;


import mcjty.lib.base.ModBase;
import mcjty.xnet.apiimpl.XNetApi;
import mcjty.xnet.compat.TOPSupport;
import mcjty.xnet.compat.WAILASupport;
import mcjty.xnet.setup.Config;
import mcjty.xnet.setup.ModSetup;
import mcjty.xnet.setup.Registration;
import net.minecraft.item.Item;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(XNet.MODID)
public class XNet implements ModBase {

    public static final String MODID = "xnet";
    public static final String MODNAME = "XNet";

    public static ModSetup setup = new ModSetup();

    public static XNet instance;

    public static XNetApi xNetApi = new XNetApi();

    public XNet() {
        instance = this;

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        Registration.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener((FMLCommonSetupEvent event) -> setup.init(event));
    }

//    @Mod.EventHandler
//    public void serverLoad(FMLServerStartingEvent event) {
//        event.registerServerCommand(new CommandDump());
////        event.registerServerCommand(new CommandGen());
//        event.registerServerCommand(new CommandRebuild());
//        event.registerServerCommand(new CommandCheck());
//    }

    @Override
    public String getModId() {
        return MODID;
    }

    // @todo 1.14
//    @Mod.EventHandler
//    public void imcCallback(FMLInterModComms.IMCEvent event) {
//        for (FMLInterModComms.IMCMessage message : event.getMessages()) {
//            if (message.key.equalsIgnoreCase("getXNet")) {
//                Optional<Function<IXNet, Void>> value = message.getFunctionValue(IXNet.class, Void.class);
//                if (value.isPresent()) {
//                    value.get().apply(xNetApi);
//                } else {
//                    setup.getLogger().warn("Some mod didn't return a valid result with getXNet!");
//                }
//            }
//        }
//    }

    @Override
    public void handleTopExtras() {
        TOPSupport.registerTopExtras();
    }

    @Override
    public void handleWailaExtras() {
        WAILASupport.registerWailaExtras();
    }

}
