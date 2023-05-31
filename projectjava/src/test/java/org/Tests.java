package org;

import org.example.entity.Name;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


@CamelSpringBootTest
@EnableAutoConfiguration
@SpringBootTest(properties = {"kafka-requests-path=direct:requests"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
public class Tests {

    @Autowired
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:jpa:org.example.entity.Name")
    public MockEndpoint saveToDb;

    @EndpointInject("mock:kafka:results")
    public MockEndpoint kafkaResults;

    @EndpointInject("mock:kafka:status_topic")
    public MockEndpoint kafkaStatusTopic;

    @Test
    public void canSaveToData() throws InterruptedException {
        Name build = new Name();
        build.setName("building");
        saveToDb.expectedBodiesReceived(build);

        producerTemplate.sendBody("direct:requests", "<build><name>building</name>");

        MockEndpoint.assertIsSatisfied(saveToDb);
    }

    @Test
    public void canGetResult() throws InterruptedException {
        kafkaResults.expectedBodiesReceived("{\"name\":building}");

        producerTemplate.sendBody("direct:requests", "<name><name>building</name>");

        MockEndpoint.assertIsSatisfied(kafkaResults);
    }

    @Test
    public void canSendStatus() throws InterruptedException {
        kafkaStatusTopic.expectedBodiesReceived("<status>ok</status>");

        producerTemplate.sendBody("direct:requests", "<name><name>building</name>");

        kafkaStatusTopic.assertIsSatisfied(5000);
    }

    @Test
    public void canSendError() throws InterruptedException {
        kafkaStatusTopic.expectedBodiesReceived("<status>error</status><message>Unmarshaling failed</message>");

        producerTemplate.sendBody("direct:requests", "<not_name><name>building</name>");

        kafkaStatusTopic.assertIsSatisfied(5000);
    }
}
