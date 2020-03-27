package com.kdusilvestre.vertx;

import com.google.common.io.Resources;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class EventLoopVerticle extends AbstractVerticle {

    private Long hit = new Long(0);
    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/threadInfo").handler(this::threadInfo);
        router.post("/keepBusy").handler(this::keepBusy);
        router.post("/block").handler(this::block);
        router.post("/eventBus").handler(this::backNForthEventBus);
        router.post("/eventBusDelay").handler(this::backNForthEventBusDelay);
        router.post("/eventloopIOMono").handler(this::asynchIOMono);
        router.post("/eventloopIOExecBlocking").handler(this::asynchIOExecBlocking);

        vertx.createHttpServer(new HttpServerOptions().
                setUseAlpn(true).
                setSsl(true).
                setPemKeyCertOptions(new PemKeyCertOptions().setKeyPath(Resources.getResource("server-key.pem").getPath()).setCertPath(Resources.getResource("server-cert.pem").getPath())))
                .requestHandler(router)
                .listen(8443);
    }

    private void block(RoutingContext routingContext) {
        try {
            Thread.sleep(5500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        routingContext.response().setStatusCode(200).end();
    }

    private void keepBusy(RoutingContext routingContext) {
        hit++;
        routingContext.response().setStatusCode(200).end();
    }

    private void threadInfo(RoutingContext routingContext) {
        try {
            routingContext.response().putHeader("content-type", "application/json")
                    .end(Helper.GetThreadInfoJson(hit).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(500, routingContext.response());
        }
    }

    private void backNForthEventBus(RoutingContext routingContext) {
        EventBus eb = vertx.eventBus();

        // get the timestmap and send it
        long l = System.currentTimeMillis();

        eb.request("sussexhouse", "ping!", reply -> {
            if (reply.succeeded()) {
                long lPlus = Long.valueOf(reply.result().body().toString());
                routingContext.response().setStatusCode(200).end(String.valueOf(lPlus - l));
            } else {
                System.out.println("Something went wrong with the eventbus " + reply);
                sendError(500, routingContext.response());
            }
        });
    }

    private void backNForthEventBusDelay(RoutingContext routingContext) {
        EventBus eb = vertx.eventBus();

        // get the timestmap and send it
        long l = System.currentTimeMillis();

        eb.request("sussexhousedelay", "ping!", reply -> {
            if (reply.succeeded()) {
                long lPlus = Long.valueOf(reply.result().body().toString());
                routingContext.response().setStatusCode(200).end(String.valueOf(lPlus - l));
            } else {
                System.out.println("Something went wrong with the eventbus " + reply);
                sendError(500, routingContext.response());
            }
        });
    }

    // Async using a Reactive stream Scheduler thread
    private void asynchIOMono(RoutingContext routingContext) {
       callAPIViaApigeeArgh()
               .subscribeOn(Schedulers.elastic())
                       .subscribe( consumer -> {
                            System.out.println("Received " + consumer);
                           routingContext.response().setStatusCode(200).end();
                        }, errorConsumer -> {
                           System.out.println("Something went wrong - " + errorConsumer);
                           sendError(500, routingContext.response());
                       });
    }

    // Async using the Vert.x worker thread pool
    private void asynchIOExecBlocking(RoutingContext routingContext) {
       vertx.executeBlocking( v -> {
           // Let's pretend we're talking to an API via the lovely Apigee rails
           try {
               Thread.sleep(3500);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
           v.complete("Response body");
       }, result -> {
           routingContext.response().setStatusCode(200).end();
       });
    }

    private Mono<String> callAPIViaApigeeArgh() {

        return Mono.create( consumer -> {
            // Let's pretend we're talking to an API via the lovely Apigee rails
            try {
                Thread.sleep(3500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            consumer.success("Response body");
        });
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
