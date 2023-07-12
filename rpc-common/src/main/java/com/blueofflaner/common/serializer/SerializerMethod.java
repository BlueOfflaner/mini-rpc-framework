package com.blueofflaner.common.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

//TODO 重写序列化反序列化方法
public enum SerializerMethod implements Serializer {
    //JDK 序列化会出bug
    JDK {
        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return (T) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("反序列化失败", e);
            }
        }

        @Override
        public <T> byte[] serialize(T object) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(object);
                return bos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException("序列化失败", e);
            }
        }
    },
    JSON {
        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.readValue(bytes, clazz);
            } catch (IOException e) {
                throw new RuntimeException("反序列化失败", e);
            }
        }

        @Override
        public <T> byte[] serialize(T object) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                return mapper.writeValueAsBytes(object);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化失败", e);
            }
        }
    };

    private static Map<Integer, Serializer> map = new HashMap<>();
    static {
        for(SerializerMethod method : SerializerMethod.values()) {
            map.put(method.ordinal(), method);
        }
    }

    public static Serializer get(int key) {
        return map.get(key);
    }
}
