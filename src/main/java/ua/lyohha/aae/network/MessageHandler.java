package ua.lyohha.aae.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ua.lyohha.aae.inventory.ContainerAdvancedPatternTerminal;

public class MessageHandler implements IMessageHandler<Message, IMessage> {
    @Override
    public IMessage onMessage(Message message, MessageContext ctx) {

        switch (message.getMessageType())
        {
            case CLEAR_PATTERN:
                ((ContainerAdvancedPatternTerminal)ctx.getServerHandler().playerEntity.openContainer).getTileEntity().clearPattern();
                break;
            case CREATE_PATTERN:
                ((ContainerAdvancedPatternTerminal)ctx.getServerHandler().playerEntity.openContainer).getTileEntity().createPattern();
                break;
        }

        return null;
    }
}
