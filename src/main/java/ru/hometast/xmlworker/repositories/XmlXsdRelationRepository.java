package ru.hometast.xmlworker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.hometast.xmlworker.entities.XmlXsdRelationEntity;

@Repository
public interface XmlXsdRelationRepository extends JpaRepository<XmlXsdRelationEntity, Long> {

    XmlXsdRelationEntity findByXmlfilename (String xmlfilename);
    boolean existsByXmlfilename (String xmlfilename);
}
