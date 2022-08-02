package gr.aegean.palaemon.conductor.repository;

import gr.aegean.palaemon.conductor.model.pojo.EvacuationStatus;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface EvacuationStatusRepository extends ElasticsearchRepository<EvacuationStatus, String> {



    @Query("{\"bool\": {\"must\": [{\"match_all\": {}}]}}")
    List<EvacuationStatus> findStatus();


//    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
//    Page<Article> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}
