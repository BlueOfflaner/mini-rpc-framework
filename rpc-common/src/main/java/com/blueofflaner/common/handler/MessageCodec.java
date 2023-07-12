package com.blueofflaner.common.handler;

import com.blueofflaner.common.message.Message;
import com.blueofflaner.common.serializer.Serializer;
import com.blueofflaner.common.serializer.SerializerMethod;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends MessageToMessageCodec<ByteBuf, Message> {

    Serializer serializer;

    Integer serializerCode;

    public MessageCodec(Integer serializerCode) {
        this.serializerCode = serializerCode;
        this.serializer = SerializerMethod.get(serializerCode);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> list) throws Exception {
        ByteBuf out = ctx.alloc().buffer();

        // 4 字节魔数
        out.writeInt(ProtocolConstants.MAGIC_NUMBER);
        // 1 字节序列化方案
        out.writeByte(serializerCode);
        // 1 字节指令类型
        out.writeByte(msg.getMessageType());
        // 32 字节消息 uuid
        byte[] sequenceId = msg.getSequenceId().getBytes(StandardCharsets.UTF_8);
        out.writeBytes(sequenceId);

        //System.out.println(sequenceId.length);
        // 2 字节填充
        out.writeByte(ProtocolConstants.PADDING);
        out.writeByte(ProtocolConstants.PADDING);
        //log.info("{}", out);

        // 消息内容加密
        byte[] content = serializer.serialize(msg);
        // 4 字节消息长度
        out.writeInt(content.length);
        out.writeBytes(content);
        //log.info("content: {}", content);

        list.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> list) throws Exception {
        int magicNumber = in.readInt();
        if(magicNumber != ProtocolConstants.MAGIC_NUMBER) {
            //TODO 异常处理
            throw new IOException();
        }
        byte serializerCode = in.readByte();
        byte messageType = in.readByte();
        String sequenceId = in.readBytes(ProtocolConstants.ID_LENGTH).toString();
        in.readBytes(2);

        int length = in.readInt();
        byte[] content = new byte[length];
        in.readBytes(content, 0, length);
        //log.info("content: {}", new String(content, StandardCharsets.UTF_8));
        Class<? extends Message> messageClass = Message.getMessageClass(messageType);
        Message message = serializer.deserialize(messageClass, content);

        list.add(message);
        //log.info("decode: {}" , message);
    }
}
