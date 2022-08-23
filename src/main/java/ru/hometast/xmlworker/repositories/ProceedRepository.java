package ru.hometast.xmlworker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hometast.xmlworker.entities.ProceedEntity;
import ru.hometast.xmlworker.entities.XmlXsdRelationEntity;

@Repository
public interface ProceedRepository extends JpaRepository<ProceedEntity, Long> {
      boolean existsByFilename (String filename);

}
