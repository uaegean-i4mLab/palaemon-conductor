package gr.aegean.palaemon.conductor.config;

import gr.aegean.palaemon.conductor.service.utils.EnvUtils;
import gr.aegean.palaemon.conductor.service.utils.KafkaJsonSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    //TODO read these from .env
    private final Properties properties = new Properties();


    public KafkaConfig() {
        String kafkaURI = EnvUtils.getEnvVar("KAFKA_URI_WITH_PORT", "dfb.palaemon.itml.gr:30093");
        String trustStoreLocation = EnvUtils.getEnvVar("KAFKA_TRUST_STORE_LOCATION", "/home/ni/code/java/palaemon-db-proxy/truststore.jks");
        String trustStorePass = EnvUtils.getEnvVar("KAFKA_TRUST_STORE_PASSWORD", "teststore");
        String keyStoreLocation = EnvUtils.getEnvVar("KAFKA_KEYSTORE_LOCATION", "/home/ni/code/java/palaemon-db-proxy/keystore.jks");
        String keyStorePass = EnvUtils.getEnvVar("KAFKA_KEY_STORE_PASSWORD", "teststore");
        this.properties.put("bootstrap.servers", kafkaURI);
        this.properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        this.properties.put("security.protocol", "SSL");
        this.properties.put("ssl.truststore.location", trustStoreLocation);
        this.properties.put("ssl.truststore.password", trustStorePass);
        this.properties.put("ssl.keystore.location", keyStoreLocation);
        this.properties.put("ssl.keystore.password", keyStorePass);
    }

    @Bean
    public KafkaProducer producer() {
        KafkaProducer<String, String> myProducer = new KafkaProducer<String, String>(this.properties, new StringSerializer(),
                new KafkaJsonSerializer());
        return myProducer;
    }


}
