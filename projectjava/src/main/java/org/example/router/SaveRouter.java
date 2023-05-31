package org.example.router;

import org.example.generated.Name;
import org.example.dto.NameDTO;
import org.example.mapper.NameMapper;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaveRouter extends RouteBuilder {
    private final NameMapper mapper;

    public void configure() {
        from("direct:save_to_db")
                .choice()
                .when(body().isInstanceOf(Name.class))
                .log("Message received from Kafka : ${body}")
                .log("    on the topic ${headers[kafka.TOPIC]}")
                .process(exchange -> {
                    Name in = exchange.getIn().getBody(Name.class);
                    org.example.entity.Name name = mapper.mapGenerated(in);

                    exchange.getMessage().setBody(name, org.example.entity.Name.class);
                })
                .log("Saving ${body} to database...")
                .to("jpa:org.example.entity.Name")
                .process(exchange -> {
                    org.example.entity.Name in = exchange.getIn().getBody(org.example.entity.Name.class);
                    NameDTO name = mapper.mapWithoutId(in);

                    exchange.getMessage().setBody(name, NameDTO.class);
                })
                .marshal().json(JsonLibrary.Jackson)
                .log("Saving ${body} to kafka")
                .to("kafka:results?brokers=localhost:9092")
                .setBody(simple("<status>ok</status>"))
                .to("direct:status")
                .to("direct:metrics_router_increment_success_messages")
                .to("direct:metrics_router_stop_timer")
                .otherwise()
                .setBody(simple("<status>error</status><message>XML data isn't instance of Weather</message>"))
                .to("direct:status")
                .to("direct:metrics_router_increment_fail_messages")
                .to("direct:metrics_router_stop_timer");
    }
}
