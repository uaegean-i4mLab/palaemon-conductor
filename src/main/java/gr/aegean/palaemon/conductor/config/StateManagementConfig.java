package gr.aegean.palaemon.conductor.config;

import gr.aegean.palaemon.conductor.model.TO.EvacuationStatusTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StateManagementConfig {


    @Bean
    public EvacuationStatusTO getEvacuationStatus(){
        return  new EvacuationStatusTO();
    }
}
