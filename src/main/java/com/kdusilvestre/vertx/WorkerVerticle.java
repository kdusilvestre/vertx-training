package com.kdusilvestre.vertx;

import com.google.common.io.Resources;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class WorkerVerticle extends AbstractVerticle {

    private Long hit = new Long(0);

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create());
        router.get("/threadInfo").handler(this::threadInfo);
        router.post("/keepBusy").handler(this::keepBusy);


        vertx.createHttpServer(new HttpServerOptions().
                setUseAlpn(true).
                setSsl(true).
                setPemKeyCertOptions(new PemKeyCertOptions().setKeyPath(Resources.getResource("server-key.pem").getPath()).setCertPath(Resources.getResource("server-cert.pem").getPath())))
                .requestHandler(router)
                .listen(8443);
    }

    // Responds with a JSON Array
    public void threadInfo(RoutingContext routingContext) {
        try {

            routingContext.response().putHeader("content-type", "application/json")
                    .end(Helper.GetThreadInfoJson(new Long(0)).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendError(500, routingContext.response());
        }
    }

    // Hammer it so you can see the thread count going up
    private void keepBusy(RoutingContext routingContext) {
        hit++;
        routingContext.response().setStatusCode(200).end();
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
