package ua.lyohha.aae.ae2;

import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;

public class TextureManager {
    private static TextureManager instance = new TextureManager();

    public IIcon StorageBus1Back;
    public IIcon StorageBus1Side;
    public IIcon StorageBus1Front;
    public IIcon StorageBus2Side1;
    public IIcon StorageBus2Front1;
    public IIcon StorageBus2Side2;
    public IIcon StorageBus2Front2;
    public IIcon BusSide;
    public IIcon BusColor;

    public static TextureManager getInstance() {
        return instance;
    }

    public void registry(TextureMap textureMap) {
        if(textureMap.getTextureType() == 0) {
            this.StorageBus1Back = textureMap.registerIcon("aae:parts/StorageBus1Back");
            this.StorageBus1Side = textureMap.registerIcon("aae:parts/StorageBus1Side");
            this.StorageBus1Front = textureMap.registerIcon("aae:parts/StorageBus1Front");
            this.StorageBus2Side1 = textureMap.registerIcon("aae:parts/StorageBus2Side1");
            this.StorageBus2Front1 = textureMap.registerIcon("aae:parts/StorageBus2Front1");
            this.StorageBus2Side2 = textureMap.registerIcon("aae:parts/StorageBus2Side2");
            this.StorageBus2Front2 = textureMap.registerIcon("aae:parts/StorageBus2Front2");
            this.BusColor = textureMap.registerIcon("aae:parts/BusColor");
        }
    }
}
