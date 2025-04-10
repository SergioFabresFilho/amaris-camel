package com.amaris.test.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import java.util.Map;

public class TodoLogger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        onException(Exception.class)
                .handled(true)
                .process(exchange -> {
                    var exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    exchange.getMessage().setBody("Error calling API: " + exception.getMessage());
                })
                .log("${body}");

        from("timer:apiCall?period=5000")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("https://jsonplaceholder.typicode.com/todos/1")
                .unmarshal().json()
                .process(exchange -> {
                    var title = (String) exchange.getIn().getBody(Map.class).get("title");
                    exchange.getIn().setBody("Uppercase Title: " + title.toUpperCase());
                })
                .log("${body}");
    }
}
