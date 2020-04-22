package com.vd.canary.data.common.kafka.consumer.impl;

import java.io.IOException;

public interface Function {

    public void performES(String msg) throws IOException;

}
