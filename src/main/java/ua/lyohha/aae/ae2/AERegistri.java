package ua.lyohha.aae.ae2;

import appeng.api.AEApi;
import appeng.api.features.IRegistryContainer;
import ua.lyohha.aae.TilyEntity.AdvancedStorageMEInterfaceTileEntity;
import ua.lyohha.aae.blocks.AdvancedAEBlocks;

public class AERegistri
{
    //Попытки регистрации блока в AE
    public static void Registri()
    {
        IRegistryContainer iRegistryContainer = AEApi.instance().registries();
        iRegistryContainer.movable().blacklistBlock(AdvancedAEBlocks.advancedStorageMEInterface);
        //iRegistryContainer.externalStorage().addExternalStorageInterface(AdvancedStorageMEInterfaceTileEntity.class);
    }

}
