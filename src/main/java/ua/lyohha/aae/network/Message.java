package ua.lyohha.aae.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class Message implements IMessage {

    private MessageType type;
    public int priority;

    public Message()
    {
        type = MessageType.NONE;
    }

    public Message(MessageType type)
    {
        this.type = type;
    }
    public Message(MessageType type, int priority)
    {
        this.type = type;
        this.priority = priority;
    }

    public MessageType getMessageType()
    {
        return type;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        this.type = MessageType.values()[buf.readInt()];
        if(this.type == MessageType.SET_PRIORITY)
            this.priority = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.type.ordinal());
        if(this.type == MessageType.SET_PRIORITY)
            buf.writeInt(this.priority);
    }

    public enum MessageType
    {
        NONE,
        CLEAR_PATTERN,
        CREATE_PATTERN,
        SET_PRIORITY
    }
}

