package gr.aegean.palaemon.conductor.config;

import gr.aegean.palaemon.conductor.utils.EnvUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import javax.net.ssl.SSLContext;

@Configuration
@EnableElasticsearchRepositories(basePackages = "gr.uaegean.palaemondbproxy.repository")
@ComponentScan(basePackages = { "gr.uaegean.palaemondbproxy.service" })
public class ESConfig {


//    @Value("${elasticsearch.url}")
    public String elasticsearchUrl = EnvUtils.getEnvVar("DFB_URI","dfb.palaemon.itml.gr");
//    @Value("${elasticsearch.port}")
    public String elasticsearchPort = EnvUtils.getEnvVar("DFB_PORT","443");
//    @Value("${elasticsearch.username}")
    public String elasticsearchUsername=EnvUtils.getEnvVar("ES_USER","esuser");
//    @Value("${elasticsearch.password}")
    public String elasticsearchPassword=EnvUtils.getEnvVar("ES_PASS","kyroCMA2081!");

    private SSLConfig sslConfig;

    public ESConfig() throws Exception {
        sslConfig = new SSLConfig();
    }

    @Bean
    public RestHighLevelClient client() {
//        ClientConfiguration clientConfiguration
//                = ClientConfiguration.builder()
//                .connectedTo("localhost:9200")
//                .build();
//
//        return RestClients.create(clientConfiguration).rest();
        SSLContext sslContext = null;
        try {
            sslContext = this.sslConfig.getSSLContext();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final ClientConfiguration config = ClientConfiguration.builder()
                .connectedTo(elasticsearchUrl + ":" + elasticsearchPort)
                .usingSsl(sslContext)
                .withBasicAuth(elasticsearchUsername, elasticsearchPassword)
                .build();
        return RestClients.create(config).rest();
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
            return new ElasticsearchRestTemplate(client());
    }
}
