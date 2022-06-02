package gr.aegean.palaemon.conductor.config;

import gr.aegean.palaemon.conductor.utils.EnvUtils;
import gr.aegean.palaemon.conductor.utils.KafkaJsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    //TODO read these from .env
    private final Properties producerProperties = new Properties();

    private final Properties consumerProperties = new Properties();


    public KafkaConfig() {
        String kafkaURI = EnvUtils.getEnvVar("KAFKA_URI_WITH_PORT", "dfb.palaemon.itml.gr:30093");
        String trustStoreLocation = EnvUtils.getEnvVar("KAFKA_TRUST_STORE_LOCATION", "/home/ni/code/java/palaemon-db-proxy/truststore.jks");
        String trustStorePass = EnvUtils.getEnvVar("KAFKA_TRUST_STORE_PASSWORD", "teststore");
        String keyStoreLocation = EnvUtils.getEnvVar("KAFKA_KEYSTORE_LOCATION", "/home/ni/code/java/palaemon-db-proxy/keystore.jks");
        String keyStorePass = EnvUtils.getEnvVar("KAFKA_KEY_STORE_PASSWORD", "teststore");
        this.producerProperties.put("bootstrap.servers", kafkaURI);
        this.producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.producerProperties.put("security.protocol", "SSL");
        this.producerProperties.put("ssl.truststore.location", trustStoreLocation);
        this.producerProperties.put("ssl.truststore.password", trustStorePass);
        this.producerProperties.put("ssl.keystore.location", keyStoreLocation);
        this.producerProperties.put("ssl.keystore.password", keyStorePass);

        this.consumerProperties.put("bootstrap.servers", kafkaURI);
        this.consumerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.consumerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.consumerProperties.put("security.protocol", "SSL");
        this.consumerProperties.put("ssl.truststore.location", trustStoreLocation);
        this.consumerProperties.put("ssl.truststore.password", trustStorePass);
        this.consumerProperties.put("ssl.keystore.location", keyStoreLocation);
        this.consumerProperties.put("ssl.keystore.password", keyStorePass);
        this.consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        this.consumerProperties.put("group.id", "uaeg-consumer-group");

    }

    @Bean
    public KafkaProducer producer() {
        KafkaProducer<String, String> myProducer = new KafkaProducer<String, String>(this.producerProperties, new StringSerializer(),
                new KafkaJsonSerializer());
        return myProducer;
    }





}
