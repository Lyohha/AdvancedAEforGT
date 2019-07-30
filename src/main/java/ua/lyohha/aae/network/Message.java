package ua.lyohha.aae.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class Message implements IMessage {

    private MessageType type;

    public Message()
    {
        type = MessageType.NONE;
    }

    public Message(MessageType type)
    {
        this.type = type;
    }

    public MessageType getMessageType()
    {
        return type;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        type = MessageType.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.ordinal());
    }

    public enum MessageType
    {
        NONE,
        CLEAR_PATTERN,
        CREATE_PATTERN
    }
}

