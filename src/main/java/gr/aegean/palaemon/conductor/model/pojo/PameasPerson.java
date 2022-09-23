package gr.aegean.palaemon.conductor.model.pojo;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;


@Document(indexName = "pameas-person-#{T(java.time.format.DateTimeFormatter).ofPattern(\"yyyy.MM.dd\").format(T(java.time.LocalDate).now())}" )
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PameasPerson implements Serializable {

    @Id
    private String id;

    @Field(type = FieldType.Nested, includeInParent = true)
    Personalinfo personalInfo;

    @Field(type = FieldType.Nested, includeInParent = true)
    NetworkInfo networkInfo;

    @Field(type = FieldType.Nested, includeInParent = true)
    LocationInfo locationInfo;
}
