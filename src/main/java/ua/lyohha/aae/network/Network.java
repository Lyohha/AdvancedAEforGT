package ua.lyohha.aae.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class Network {

    private static SimpleNetworkWrapper network;

    public static SimpleNetworkWrapper getNetworkWrapper()
    {
        return network;
    }

    public static void preInit()
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("AdvancedAR");
        network.registerMessage(MessageHandler.class, Message.class, 0, Side.SERVER);
    }
}
