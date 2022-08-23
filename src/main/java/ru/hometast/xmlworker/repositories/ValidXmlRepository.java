package ru.hometast.xmlworker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hometast.xmlworker.entities.ValidXmlEntity;

@Repository
public interface ValidXmlRepository extends JpaRepository<ValidXmlEntity, Long> {
   boolean existsByFilename(String filename);
}
