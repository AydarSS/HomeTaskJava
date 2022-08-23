package ru.hometast.xmlworker.services;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.hometast.xmlworker.entities.XmlXsdRelationEntity;
import ru.hometast.xmlworker.exceptions.ApiRequestException;
import ru.hometast.xmlworker.models.XmlFileDTO;
import ru.hometast.xmlworker.repositories.NotValidXmlRepository;
import ru.hometast.xmlworker.repositories.ProceedRepository;
import ru.hometast.xmlworker.repositories.ValidXmlRepository;
import ru.hometast.xmlworker.repositories.XmlXsdRelationRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
class XmlXsdWorkerTest {

    @Autowired
    private XmlXsdWorker xmlXsdWorker;

    @MockBean
    private XmlXsdRelationRepository xmlXsdRelationRepository;

    @MockBean
    private ValidXmlRepository validXmlRepository;

    @MockBean
    private ProceedRepository proceedRepository;

    @MockBean
    private NotValidXmlRepository notValidXmlRepository;


    @Test
    void savexsd() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        xmlFileDTO.setXmlFileName("user.xml");
        xmlFileDTO.setXsdFileName("user.xsd");
        Mockito.when(xmlXsdRelationRepository.existsByXmlfilename(xmlFileDTO.getXmlFileName())).thenReturn(false);
        xmlXsdWorker.savexsd(xmlFileDTO);
        Assert.assertTrue(xmlXsdWorker.getFileExistsChecker().fileexistcheck(xmlFileDTO.getXmlFileName()));
        Assert.assertTrue(xmlXsdWorker.getFileExistsChecker().fileexistcheck(xmlFileDTO.getXsdFileName()));

        Mockito.verify(xmlXsdRelationRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(xmlFileDTO.convertToXmlXsdEntity()));
    }

    @Test()
    void savexsdFailFilenotExists() {

        ApiRequestException thrown = assertThrows(
                ApiRequestException.class,
                () -> {
                    XmlFileDTO xmlFileDTO = new XmlFileDTO();
                    xmlFileDTO.setXmlFileName("bla.xml");
                    xmlFileDTO.setXsdFileName("bla.xsd");
                    xmlXsdWorker.savexsd(xmlFileDTO);
                },
                "Files are not exists in directory"
        );
        Assertions.assertEquals("Files are not exists in directory", thrown.getMessage());
    }

    @Test()
    void savexsdFailFileWrongName() {

        ApiRequestException thrown = assertThrows(
                ApiRequestException.class,
                () -> {
                    XmlFileDTO xmlFileDTO = new XmlFileDTO();
                    xmlFileDTO.setXmlFileName("bla.txt");
                    xmlFileDTO.setXsdFileName("bla.txt");
                    xmlXsdWorker.savexsd(xmlFileDTO);
                },
                "File name is empty or not end with .xsd for xsd file and .xml for xml file"
        );
        Assertions.assertEquals("File name is empty or not end with .xsd for xsd file and .xml for xml file", thrown.getMessage());
    }

    @Test()
    void savexsdFailRecordIsAlreadyExists() {

        ApiRequestException thrown = assertThrows(
                ApiRequestException.class,
                () -> {
                    XmlFileDTO xmlFileDTO = new XmlFileDTO();
                    xmlFileDTO.setXmlFileName("user.xml");
                    xmlFileDTO.setXsdFileName("user.xsd");
                    Mockito.when(xmlXsdRelationRepository.existsByXmlfilename(xmlFileDTO.getXmlFileName())).thenReturn(true);
                    xmlXsdWorker.savexsd(xmlFileDTO);

                },
                "Record is already exists"
        );
        Assertions.assertEquals("Record is already exists", thrown.getMessage());
    }


    @Test
    void validatexml() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        xmlFileDTO.setXmlFileName("user.xml");
        Mockito.when(xmlXsdRelationRepository.existsByXmlfilename(xmlFileDTO.getXmlFileName())).thenReturn(true);
        Mockito.doReturn(new XmlXsdRelationEntity("user.xml", "user.xsd")).when(xmlXsdRelationRepository).findByXmlfilename(ArgumentMatchers.anyString());
        xmlXsdWorker.validatexml(xmlFileDTO);
        Mockito.verify(validXmlRepository, Mockito.times(1)).save(ArgumentMatchers.refEq(xmlFileDTO.convertToXmlValidEntity()));
    }

    @Test
    void validatexmlNotValidFile() {

        ApiRequestException thrown = assertThrows(
                ApiRequestException.class,
                () -> {
                    XmlFileDTO xmlFileDTO = new XmlFileDTO();
                    xmlFileDTO.setXmlFileName("1.xml");
                    Mockito.when(xmlXsdRelationRepository.existsByXmlfilename(xmlFileDTO.getXmlFileName())).thenReturn(true);
                    Mockito.doReturn(new XmlXsdRelationEntity("1.xml", "1.xsd")).when(xmlXsdRelationRepository).findByXmlfilename(ArgumentMatchers.anyString());
                    xmlXsdWorker.validatexml(xmlFileDTO);

                },
                "File not valid"
        );
        Assertions.assertEquals("File not valid", thrown.getMessage());
    }


    @Test
    void savexml() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        xmlFileDTO.setXmlFileName("user.xml");
        Mockito.when(proceedRepository.existsByFilename((xmlFileDTO.getXmlFileName()))).thenReturn(false);
        Mockito.when(validXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(true);
        xmlXsdWorker.savexml(xmlFileDTO);
        Mockito.verify(proceedRepository, Mockito.times(1)).save(ArgumentMatchers.any());
    }

    @Test
    void savexmlFailed() {


        ApiRequestException thrown = assertThrows(
                ApiRequestException.class,
                () -> {
                    XmlFileDTO xmlFileDTO = new XmlFileDTO();
                    xmlFileDTO.setXmlFileName("user.xml");
                    Mockito.when(proceedRepository.existsByFilename((xmlFileDTO.getXmlFileName()))).thenReturn(true);
                    xmlXsdWorker.savexml(xmlFileDTO);

                },
                "File is already saved"
        );
        Assertions.assertEquals("File is already saved", thrown.getMessage());

    }

    @Test
    void getxmlValidFile() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        XmlFileDTO xmlFileDTOresult;

        xmlFileDTO.setXmlFileName("user.xml");
        Mockito.when(proceedRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(true);
        Mockito.when(xmlXsdRelationRepository.findByXmlfilename(xmlFileDTO.getXmlFileName())).thenReturn(new XmlXsdRelationEntity("user.xml", "user.xsd"));

        xmlFileDTOresult = xmlXsdWorker.getxml(xmlFileDTO);
        Assert.assertNotNull(xmlFileDTOresult);
        Assert.assertEquals("VALID", xmlFileDTOresult.getStatus().toString());
    }

    @Test
    void getxmlNotSavedFile() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        XmlFileDTO xmlFileDTOresult;

        xmlFileDTO.setXmlFileName("user.xml");
        Mockito.when(proceedRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(false);
        Mockito.when(validXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(true);
        Mockito.when(xmlXsdRelationRepository.findByXmlfilename(xmlFileDTO.getXmlFileName())).thenReturn(new XmlXsdRelationEntity("user.xml", "user.xsd"));

        xmlFileDTOresult = xmlXsdWorker.getxml(xmlFileDTO);
        Assert.assertNotNull(xmlFileDTOresult);
        Assert.assertEquals("NOTSAVED", xmlFileDTOresult.getStatus().toString());
    }

    @Test
    void getxmlNotValidFile() {
        XmlFileDTO xmlFileDTO = new XmlFileDTO();
        XmlFileDTO xmlFileDTOresult;

        xmlFileDTO.setXmlFileName("user.xml");
        Mockito.when(proceedRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(false);
        Mockito.when(validXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(false);
        Mockito.when(notValidXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(true);
        Mockito.when(xmlXsdRelationRepository.findByXmlfilename(xmlFileDTO.getXmlFileName())).thenReturn(new XmlXsdRelationEntity("user.xml", "user.xsd"));

        xmlFileDTOresult = xmlXsdWorker.getxml(xmlFileDTO);
        Assert.assertNotNull(xmlFileDTOresult);
        Assert.assertEquals("NOTVALID", xmlFileDTOresult.getStatus().toString());
    }

    @Test
    void getxmlFailed() {
        ApiRequestException thrown = assertThrows(
                ApiRequestException.class,
                () -> {
                    XmlFileDTO xmlFileDTO = new XmlFileDTO();

                    xmlFileDTO.setXmlFileName("user.xml");
                    Mockito.when(proceedRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(false);
                    Mockito.when(validXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(false);
                    Mockito.when(notValidXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())).thenReturn(false);
                    xmlXsdWorker.getxml(xmlFileDTO);

                },
                "Unknown File"
        );
        Assertions.assertEquals("Unknown File", thrown.getMessage());

    }

}