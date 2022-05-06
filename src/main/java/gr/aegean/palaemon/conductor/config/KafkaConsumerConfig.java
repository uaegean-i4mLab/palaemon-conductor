package gr.aegean.palaemon.conductor.config;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import gr.aegean.palaemon.conductor.service.utils.EnvUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private String kafkaURI = EnvUtils.getEnvVar("KAFKA_URI_WITH_PORT", "dfb.palaemon.itml.gr:30093");
    private String trustStoreLocation = EnvUtils.getEnvVar("KAFKA_TRUST_STORE_LOCATION", "/home/ni/code/java/palaemon-db-proxy/truststore.jks");
    private String trustStorePass = EnvUtils.getEnvVar("KAFKA_TRUST_STORE_PASSWORD", "teststore");
    private String keyStoreLocation = EnvUtils.getEnvVar("KAFKA_KEYSTORE_LOCATION", "/home/ni/code/java/palaemon-db-proxy/keystore.jks");
    private String keyStorePass = EnvUtils.getEnvVar("KAFKA_KEY_STORE_PASSWORD", "teststore");

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> properties = new HashMap<>();

        properties.put("bootstrap.servers", kafkaURI);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("security.protocol", "SSL");
        properties.put("ssl.truststore.location", trustStoreLocation);
        properties.put("ssl.truststore.password", trustStorePass);
        properties.put("ssl.keystore.location", keyStoreLocation);
        properties.put("ssl.keystore.password", keyStorePass);

        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "uaeg-consumer-group");


        return new DefaultKafkaConsumerFactory<>(properties);
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

}
