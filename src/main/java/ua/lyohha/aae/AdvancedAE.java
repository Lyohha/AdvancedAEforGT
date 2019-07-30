package ua.lyohha.aae;


import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import ua.lyohha.aae.TilyEntity.TileEntity;
import ua.lyohha.aae.ae2.AERegistri;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;
import ua.lyohha.aae.crafting.CraftRegistri;
import ua.lyohha.aae.gui.GuiHandler;
import ua.lyohha.aae.items.AdvancedAEItems;
import ua.lyohha.aae.network.Network;
import ua.lyohha.aae.proxy.CommonProxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = AdvancedAE.modid, version = AdvancedAE.version, name = AdvancedAE.modname, dependencies = "after:waila;required-after:appliedenergistics2")
public class AdvancedAE {
    //Mod Info
    public static final String modid = "aae";
    public static final String modname = "Advanced AE for GT";
    private static final int build = 1;
    public static final String version = "0.1.5." + build;

    //not used now
    //public static final Logger logger = LogManager.getLogger("Advanced AE for GT");

    @Mod.Instance(AdvancedAE.modid)
    public static AdvancedAE instance;

    @SidedProxy(clientSide = "ua.lyohha.aae.proxy.ClientProxy", serverSide = "ua.lyohha.aae.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preLoad(FMLPreInitializationEvent event) {
        AdvancedAEBlocks.preLoad();
        GuiHandler.RegisterGuiHandler();
        TileEntity.RegisterTileEntity();
        AERegistri.Registri();
        CraftRegistri.Registri();
        AdvancedAEItems.RegisterItems();

    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        Network.preInit();
    }


}
