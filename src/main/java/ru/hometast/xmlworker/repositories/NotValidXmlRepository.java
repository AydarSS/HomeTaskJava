package ru.hometast.xmlworker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hometast.xmlworker.entities.NotValidXmlEntity;

@Repository
public interface NotValidXmlRepository extends JpaRepository<NotValidXmlEntity, Long> {
    boolean existsByFilename (String filename);
}
