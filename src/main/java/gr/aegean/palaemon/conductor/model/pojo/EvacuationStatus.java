package gr.aegean.palaemon.conductor.model.pojo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.io.Serializable;

import static org.springframework.data.elasticsearch.annotations.FieldType.Text;

@Document(indexName = "pameas-evacuation-status-#{T(java.time.format.DateTimeFormatter).ofPattern(\"yyyy.MM.dd\").format(T(java.time.LocalDate).now())}")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EvacuationStatus implements Serializable {

    @Id
    private String id;
    @Field(type = Text)
    private String status;


}
