package com.webflux.jdbc.controller;

import com.webflux.jdbc.dao.OrdersDao;
import com.webflux.jdbc.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping(value = "order")
@Slf4j
@RequiredArgsConstructor
public class SimpleController {

    private final OrdersDao ordersDao;

    @PostMapping
    public Mono<Order> createOrder(@RequestBody Order order) {
        return Mono.just(order)
                .flatMap(this::persistOrder);
    }

    private Mono<Order> persistOrder(Order order) {
        return Mono.just(order)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(ordersDao::save)
                .doOnError(throwable -> log.error("error persisting to db", throwable));
    }
}
