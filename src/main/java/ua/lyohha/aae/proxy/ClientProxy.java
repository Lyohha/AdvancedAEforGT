package ua.lyohha.aae.proxy;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import ua.lyohha.aae.AdvancedAE;
import ua.lyohha.aae.ae2.TextureManager;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    public ClientProxy() {
        super();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent.Pre textureStitchEvent) {
        TextureMap map = textureStitchEvent.map;
        TextureManager.getInstance().registry(map);
        AdvancedAE.logger.info("registerTextures");
    }
}
