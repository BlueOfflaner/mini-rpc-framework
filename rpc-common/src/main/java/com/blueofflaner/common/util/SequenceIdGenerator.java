package com.blueofflaner.common.util;

import java.util.UUID;

public abstract class SequenceIdGenerator {

    public static String getId() {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        return id;
    }
}
