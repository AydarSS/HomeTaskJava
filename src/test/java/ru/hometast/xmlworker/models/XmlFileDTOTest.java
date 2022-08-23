package ru.hometast.xmlworker.models;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ru.hometast.xmlworker.entities.NotValidXmlEntity;
import ru.hometast.xmlworker.entities.ProceedEntity;
import ru.hometast.xmlworker.entities.ValidXmlEntity;
import ru.hometast.xmlworker.entities.XmlXsdRelationEntity;

import static org.junit.jupiter.api.Assertions.*;

class XmlFileDTOTest {

    @Test
    void convertToXmlXsdEntity() {
    XmlFileDTO xmlFileDTO = new XmlFileDTO();
    xmlFileDTO.setXmlFileName("user.xml");
    xmlFileDTO.setXsdFileName("user.xsd");
    XmlXsdRelationEntity xmlXsdRelationEntity = xmlFileDTO.convertToXmlXsdEntity();
        Assert.assertEquals("user.xsd",xmlXsdRelationEntity.getXsdlfilename());
        Assert.assertEquals("user.xml",xmlXsdRelationEntity.getXmlfilename());

    }

    @Test
    void convertToXmlValidEntity() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        xmlFileDTO.setXmlFileName("user.xml");
        xmlFileDTO.setXsdFileName("user.xsd");
        ValidXmlEntity validXmlEntity = xmlFileDTO.convertToXmlValidEntity();
        Assert.assertEquals("user.xml",validXmlEntity.getFilename());
    }

    @Test
    void convertToXmlNotValidEntity() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        xmlFileDTO.setXmlFileName("user.xml");
        NotValidXmlEntity notValidXmlEntity = xmlFileDTO.convertToXmlNotValidEntity();
        Assert.assertEquals("user.xml",notValidXmlEntity.getFilename());
    }

    @Test
    void convertToProceedEntity() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        xmlFileDTO.setXmlFileName("user.xml");
        ProceedEntity proceedEntity = xmlFileDTO.convertToProceedEntity();
        Assert.assertEquals("user.xml",proceedEntity.getFilename());

    }
}