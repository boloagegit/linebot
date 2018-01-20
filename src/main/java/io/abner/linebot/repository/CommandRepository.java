package io.abner.linebot.repository;

import io.abner.linebot.dto.CommandDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommandRepository extends MongoRepository<CommandDTO, Long> {

    CommandDTO findTopByName(String name);

    List<CommandDTO> findAllByNameNot(String name);

    void deleteByName(String name);

    void deleteByNameNot(String name);
}
