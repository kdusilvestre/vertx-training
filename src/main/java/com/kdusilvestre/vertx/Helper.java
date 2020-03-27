package com.kdusilvestre.vertx;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.vertx.core.Vertx;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Helper {
    public static JsonObject GetThreadInfoJson(Long hit) {
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        List<String> threadNameList = threadSet.stream().map(Thread::getName).collect(Collectors.toList());;
        System.out.println(threadNameList);
        List<String> eventLoopThreadsList = filterThreads(threadNameList, "loop");
        List<String> workerThreadsList = filterThreads(threadNameList, "worker");


        JsonObject resp = new JsonObject();
        resp.addProperty("instanceCount", Vertx.currentContext().getInstanceCount());
        resp.addProperty("hits", hit);
        resp.addProperty("currThread", Thread.currentThread().getName());
        resp.add("eventLoopThreads", getJsonArray(eventLoopThreadsList));
        resp.add("workerThreads", getJsonArray(workerThreadsList));
        resp.add("allthreads", getJsonArray(threadNameList));

        return resp;
    }


    private static List<String> filterThreads(List<String> threadNameList, String filter) {
        return threadNameList
                .stream()
                .filter(s -> s.contains(filter))
                .collect(Collectors.toList());
    }

    private static JsonArray getJsonArray(List<String> list) {
        return (JsonArray) new Gson().toJsonTree(list,
                new TypeToken<List<String>>() {
                }.getType());
    }
}
