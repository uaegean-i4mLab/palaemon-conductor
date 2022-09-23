package gr.aegean.palaemon.conductor.repository;

import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface PameasPersonRepository extends ElasticsearchRepository<PameasPerson, String> {

    Page<PameasPerson> findByPersonalInfoName(String name, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"personalInfo.crew\": \"true\"}}]}}")
    List<PameasPerson> findCrewMembers();


//    @Query("{\"bool\": {\"must\": [{\"match\": {\"networkInfo.braceletId\": \"?0\"}}]}}")
    @Query("{\"bool\": {\"must\": [{\"match\": {\"networkInfo.braceletId\": \"?0\"}}]}}")
    Page<PameasPerson> findByBraceletId(String braceletId, Pageable pageable);


//    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
//    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}
