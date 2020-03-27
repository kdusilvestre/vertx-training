package com.kdusilvestre.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class EventBusRec extends AbstractVerticle {

    @Override
    public void start() throws Exception {

        EventBus eb = vertx.eventBus();

        eb.localConsumer("sussexhouse", message -> {
            System.out.println("Received message: " + message.body());
            message.reply(String.valueOf(System.currentTimeMillis()));
        });

        eb.localConsumer("sussexhousedelay", message -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Received message: " + message.body());
            message.reply(String.valueOf(System.currentTimeMillis()));
        });

        System.out.println("Deployed background verticle - running on thread " + Thread.currentThread().getName() );

    }
}
