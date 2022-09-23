package gr.aegean.palaemon.conductor.config;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ElasticIndex {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public String getIndexDate() {
        return formatter.format(LocalDate.now());
    }
}

//java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd").format(java.time.LocalDate.now())
//T(java.time.LocalDate).now().toString()