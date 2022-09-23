package gr.aegean.palaemon.conductor.service.impl;

import gr.aegean.palaemon.conductor.model.pojo.EvacuationStatus;
import gr.aegean.palaemon.conductor.model.pojo.PameasPerson;
import gr.aegean.palaemon.conductor.repository.EvacuationStatusRepository;
import gr.aegean.palaemon.conductor.repository.PameasPersonRepository;
import gr.aegean.palaemon.conductor.service.ElasticService;
import gr.aegean.palaemon.conductor.utils.CryptoUtils;
import gr.aegean.palaemon.conductor.utils.PameasPersonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
@Slf4j
public class ElasticServiceImpl implements ElasticService {

    @Autowired
    ElasticsearchOperations elasticsearchTemplate;

    @Autowired
    PameasPersonRepository personRepository;

    @Autowired
    EvacuationStatusRepository evacuationStatusRepository;

    @Autowired
    CryptoUtils cryptoUtils;


    // personalIdentifier in plain text,
    @Override
    public Optional<PameasPerson> getPersonByPersonalIdentifierDecrypted(String personalIdentifier) {
        String date = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());

        Query searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("personalInfo.personalId",personalIdentifier).minimumShouldMatch("100%"))
                .build();
        List<SearchHit<PameasPerson>> matchingPersons =
                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person-" + date))
                        .stream().filter(result -> {
                            try {
                                return cryptoUtils.decryptBase64Message(result.getContent().getPersonalInfo().getPersonalId()).equals(personalIdentifier);
                            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                                     IllegalBlockSizeException | BadPaddingException e) {
                                log.error(e.getMessage());
                                return false;
                            }
                        }).collect(Collectors.toList());
        if (matchingPersons.size() > 0) {
            return Optional.of(matchingPersons.get(0).getContent());
        }
        return Optional.empty();
    }

    @Override
    public Optional<PameasPerson> getPersonBySurname(String surname) {
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("personalInfo.surname", surname).minimumShouldMatch("100%"))
                .build();
        SearchHits<PameasPerson> matchingPersons =
                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person"));
        if (matchingPersons.getTotalHits() > 0) {
            return Optional.of(matchingPersons.getSearchHit(0).getContent());
        }
        return Optional.empty();
    }

    @Override
    public Optional<PameasPerson> getPersonByHashedMacAddress(String hashedMacAddress) {
        String date = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());
        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("networkInfo.deviceInfoList.hashedMacAddress", hashedMacAddress).minimumShouldMatch("100%"))
                .build();
        SearchHits<PameasPerson> matchingPersons =
                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person-" + date));
        if (matchingPersons.getTotalHits() > 0) {
            return Optional.of(matchingPersons.getSearchHit(0).getContent());
        }
        return Optional.empty();
    }


    @Override
    public Optional<PameasPerson> getPersonByBraceletId(String braceletId) {
//        String date = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());
////        Query searchQuery = new NativeSearchQueryBuilder()
////                .withQuery(matchQuery("networkInfo.braceletId", braceletId).minimumShouldMatch("100%"))
////                .withQuery(matchQuery("personalInfo.mobilityIssues", "walking_disability").minimumShouldMatch("100%"))
////                .build();
//        log.info("will search for a passenger with bracelet id {} on subject {}", braceletId, "pameas-person-" + date);
////        SearchHits<PameasPerson> matchingPersons =
////                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person-2022.09.23"));
//        Query searchQuery = new NativeSearchQueryBuilder()
//                .build();
//        List<SearchHit<PameasPerson>> matchingPersons =
//                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person-" + date)).stream()
//                        .collect(Collectors.toList());
//        //        if (matchingPersons.getTotalHits() > 0) {
////            return Optional.of(matchingPersons.getSearchHit(0).getContent());
////        }
//        return Optional.empty();
        String date = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("networkInfo.braceletId",braceletId).minimumShouldMatch("100%"))
                .build();
        List<SearchHit<PameasPerson>> matchingPersons =
                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person-" + date))
                        .stream().collect(Collectors.toList());

        if(matchingPersons.size() >0){
            return  Optional.of(matchingPersons.get(0).getContent());
        }

        return Optional.empty();
    }


    // personalIdentifier in plain text,
    @Override
    public List<PameasPerson> getAllPersonsDecrypted() {
        String date = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());

        Query searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(matchQuery("personalInfo.personalId",personalIdentifier).minimumShouldMatch("100%"))
                .build();
        return
                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person-" + date))
                        .stream().map(SearchHit::getContent).map(pameasPerson -> {
                            try {
                                pameasPerson.getPersonalInfo().setName(cryptoUtils.decryptBase64Message(pameasPerson.getPersonalInfo().getName()));
                                pameasPerson.getPersonalInfo().setSurname(cryptoUtils.decryptBase64Message(pameasPerson.getPersonalInfo().getSurname()));
                                pameasPerson.getPersonalInfo().setPersonalId(cryptoUtils.decryptBase64Message(pameasPerson.getPersonalInfo().getPersonalId()));
                                pameasPerson.getPersonalInfo().getTicketInfo().forEach(ticketInfo -> {
                                    try {
                                        ticketInfo.setSurname(cryptoUtils.decryptBase64Message(ticketInfo.getSurname()));
                                        ticketInfo.setName(cryptoUtils.decryptBase64Message(ticketInfo.getName()));
                                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                                             IllegalBlockSizeException | BadPaddingException e) {
                                        log.error(e.getMessage());
                                    }
                                });

                            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                                     IllegalBlockSizeException | BadPaddingException e) {
                                log.error(e.getMessage());
                            }
                            return pameasPerson;
                        }).collect(Collectors.toList());


    }


    @Override
    public List<PameasPerson> getAllPassengersDecrypted() {
        String date = DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());

        Query searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("personalInfo.role", "passenger").minimumShouldMatch("100%"))
                .build();
        return
                this.elasticsearchTemplate.search(searchQuery, PameasPerson.class, IndexCoordinates.of("pameas-person-" + date))
                        .stream().map(SearchHit::getContent).map(pameasPerson -> {
                            try {
                                pameasPerson.getPersonalInfo().setName(cryptoUtils.decryptBase64Message(pameasPerson.getPersonalInfo().getName()));
                                pameasPerson.getPersonalInfo().setSurname(cryptoUtils.decryptBase64Message(pameasPerson.getPersonalInfo().getSurname()));
                                pameasPerson.getPersonalInfo().setPersonalId(cryptoUtils.decryptBase64Message(pameasPerson.getPersonalInfo().getPersonalId()));
                                pameasPerson.getPersonalInfo().getTicketInfo().forEach(ticketInfo -> {
                                    try {
                                        ticketInfo.setSurname(cryptoUtils.decryptBase64Message(ticketInfo.getSurname()));
                                        ticketInfo.setName(cryptoUtils.decryptBase64Message(ticketInfo.getName()));
                                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                                             IllegalBlockSizeException | BadPaddingException e) {
                                        log.error(e.getMessage());
                                    }
                                });

                            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                                     IllegalBlockSizeException | BadPaddingException e) {
                                log.error(e.getMessage());
                            }
                            return pameasPerson;
                        }).collect(Collectors.toList());


    }


    @Override
    public void updatePerson(String personIdentifier, PameasPerson person) {
        Optional<PameasPerson> matchingPerson = this.getPersonByPersonalIdentifierDecrypted(personIdentifier);
        if (matchingPerson.isPresent()) {
            PameasPerson fetchedPerson = matchingPerson.get();
            PameasPersonUtils.updatePerson(fetchedPerson, person);
            this.personRepository.save(fetchedPerson);
        }
    }

    @Override
    public void save(PameasPerson person) {
        this.personRepository.save(person);
    }

    @Override
    public Optional<EvacuationStatus> getEvacuationStatus() {
        return evacuationStatusRepository.findStatus().stream().findFirst();
    }

    @Override
    public void saveEvacuationStatus(EvacuationStatus evacuationStatus) {
        try {
            Optional<EvacuationStatus> existingStatus = evacuationStatusRepository.findStatus().stream().findFirst();
            if (existingStatus.isPresent()) {
                existingStatus.get().setStatus(evacuationStatus.getStatus());
                evacuationStatusRepository.save(existingStatus.get());
            } else {
                evacuationStatusRepository.save(evacuationStatus);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            evacuationStatusRepository.save(evacuationStatus);
        }

    }
}



