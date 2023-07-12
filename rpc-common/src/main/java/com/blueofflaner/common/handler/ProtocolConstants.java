package com.blueofflaner.common.handler;

public class ProtocolConstants {
    // 魔数
    public static int MAGIC_NUMBER = 114514;

    // uuid 长度
    public static int ID_LENGTH = 32;

    // 占位符
    public static byte PADDING = -128;

    // 最大帧长度
    public static int MAX_FRAME_LENGTH = 1 << 20;
}
