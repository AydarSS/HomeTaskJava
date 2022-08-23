package ru.hometast.xmlworker.services;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import ru.hometast.xmlworker.entities.XmlXsdRelationEntity;
import ru.hometast.xmlworker.exceptions.ApiRequestException;
import ru.hometast.xmlworker.exceptions.ExceptionText;
import ru.hometast.xmlworker.models.Status;
import ru.hometast.xmlworker.models.XmlFileDTO;
import ru.hometast.xmlworker.repositories.NotValidXmlRepository;
import ru.hometast.xmlworker.repositories.ProceedRepository;
import ru.hometast.xmlworker.repositories.ValidXmlRepository;
import ru.hometast.xmlworker.repositories.XmlXsdRelationRepository;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
* Класс сервис XmlXsdWorker обрабатывает одноименные запросы от контроллера
* */
@Service
public class XmlXsdWorker {

    Logger logger = LoggerFactory.getLogger(XmlXsdWorker.class);

    public FileExistsChecker getFileExistsChecker() {
        return fileExistsChecker;
    }

    private FileExistsChecker fileExistsChecker;
    private XmlXsdRelationRepository xmlXsdRelationRepository;
    private NotValidXmlRepository notValidXmlRepository;
    private ProceedRepository proceedRepository;
    private ValidXmlRepository validXmlRepository;

    /*переменная где хранятся файлы*/
    @Value("${filesrepository.path}")
    String filepath;

    @Autowired
    public XmlXsdWorker(FileExistsChecker fileExistsChecker, XmlXsdRelationRepository xmlXsdRelationRepository, NotValidXmlRepository notValidXmlRepository, ProceedRepository proceedRepository, ValidXmlRepository validXmlRepository) {
        this.fileExistsChecker = fileExistsChecker;
        this.xmlXsdRelationRepository = xmlXsdRelationRepository;
        this.notValidXmlRepository = notValidXmlRepository;
        this.proceedRepository = proceedRepository;
        this.validXmlRepository = validXmlRepository;
    }


    public void savexsd (XmlFileDTO xmlFileDTO)  {
        /*если расширение файлов не .xml и .xsd выбрасываем исключение, который формирует ответ*/
        if (!xmlFileDTO.getXmlFileName().endsWith(".xml") || !(xmlFileDTO.getXsdFileName().endsWith("xsd"))) {
            throw new ApiRequestException( ExceptionText.EMPTYORWRONG.getTextexception(),ExceptionText.EMPTYORWRONG.getCode());
        }
        /*если записи уже существуют в таблице связи xml и xsd (table: xml_xsd_relation_entity) - бросаем исключение*/
        if (xmlXsdRelationRepository.existsByXmlfilename(xmlFileDTO.getXmlFileName())) {
            throw new ApiRequestException( ExceptionText.RECORDEXISTS.getTextexception(),ExceptionText.RECORDEXISTS.getCode());
        };
        /*с помощью объекта-помощника класса FileExistsChecker проверяем, что файлы есть в директории /resource/files. Если файлов нет - бросаем исключение,
        если файлы есть - сохраняем в таблицу связи xml и xsd (table: xml_xsd_relation_entity)*/
        if(getFileExistsChecker().fileexistcheck(xmlFileDTO.getXmlFileName()) && getFileExistsChecker().fileexistcheck(xmlFileDTO.getXsdFileName())) {
           xmlXsdRelationRepository.save(xmlFileDTO.convertToXmlXsdEntity());
           logger.info("xmlXsdRelationRepository saved object = {}", xmlFileDTO);
        }
        else {
            throw new ApiRequestException(ExceptionText.FILESNOTEXISTS.getTextexception(),ExceptionText.FILESNOTEXISTS.getCode());
        }
    }

    public void validatexml (XmlFileDTO xmlFileDTO) {
        /*если расширение не .xml - бросаем исключение*/
        if (!xmlFileDTO.getXmlFileName().endsWith(".xml")) {
            throw new ApiRequestException( ExceptionText.EMPTYORWRONG.getTextexception(),ExceptionText.EMPTYORWRONG.getCode());
        }
        /*здесь наоборот, проверяем, что записм существуют в (table: xml_xsd_relation_entity), если нет - исключение*/
        if (!xmlXsdRelationRepository.existsByXmlfilename(xmlFileDTO.getXmlFileName())) {
            throw new ApiRequestException(ExceptionText.RECORDNOTEXISTS.getTextexception(),ExceptionText.RECORDNOTEXISTS.getCode());
        };
        /*ищем запись в таблице xml_xsd_relation_entity, чтобы сопоставить xml и xsd файлы */
            XmlXsdRelationEntity xmlXsdRelationEntity = xmlXsdRelationRepository.findByXmlfilename(xmlFileDTO.getXmlFileName());
            File schemaFile = new File(filepath + xmlXsdRelationEntity.getXsdlfilename());
            /*пробуем валмдировать*/
            Source xmlFileVar = new StreamSource(new File(filepath + xmlXsdRelationEntity.getXmlfilename()));
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                Schema schema = schemaFactory.newSchema(schemaFile);
                Validator validator = schema.newValidator();
                validator.validate(xmlFileVar);
                /*если валидный файл - сохраняем в таблицу валидных файлов valid_xml_entity*/
                validXmlRepository.save(xmlFileDTO.convertToXmlValidEntity());
                logger.info("Valid file. ValidXmlRepository saved object = {}", xmlFileDTO);
            } catch (SAXException e) {
                /*если свалились при валидации, значит файл невалидный - сохраняем в таблицу невалидных файлов not_valid_xml_entity*/
                notValidXmlRepository.save(xmlFileDTO.convertToXmlNotValidEntity());
                logger.info("Not valid file. NotValidXmlRepository saved object = {}", xmlFileDTO);
                throw new ApiRequestException(ExceptionText.FILENOTVALID.getTextexception(),ExceptionText.FILENOTVALID.getCode());
            } catch (IOException e) {
                /*это если что-то пошло не так и получили IOException, обработаем его, чтобы хоть что-то ответить клиенту */
                throw new ApiRequestException(ExceptionText.SOMETHINGWRONG.getTextexception(),ExceptionText.SOMETHINGWRONG.getCode());
            }

    }

    public void savexml (XmlFileDTO xmlFileDTO) {
        /*если расширение не .xml - бросаем исключение*/
        if (!xmlFileDTO.getXmlFileName().endsWith(".xml")) {
            throw new ApiRequestException( ExceptionText.EMPTYORWRONG.getTextexception(),ExceptionText.EMPTYORWRONG.getCode());
        }
        /*если уже существует в таблице proceed_entity, значит уже сохранен - бросаем исключение*/
        if (proceedRepository.existsByFilename((xmlFileDTO.getXmlFileName()))) {
            throw new ApiRequestException(ExceptionText.FILEISALREADYSAVED.getTextexception(),ExceptionText.FILEISALREADYSAVED.getCode());
        }
        /*если существует в таблице valid_xml_entity, значит валидный файл - сохраняем в proceed_entity*/
       if (validXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())) {
          proceedRepository.save(xmlFileDTO.convertToProceedEntity());
           logger.info("ProceedRepository saved object = {}", xmlFileDTO);
       } else {
           /*если записи в valid_xml_entity нет, значит файл невалидный - бросаем исключение для формирования ответа*/
           throw new ApiRequestException(ExceptionText.RECORDNOTSAVED.getTextexception(),ExceptionText.RECORDNOTSAVED.getCode());
       }
    }

    /*в этом методе пытаемся получить на выходе объект XmlFileDTO, чтобы вернуть клиенту */
    public XmlFileDTO getxml (XmlFileDTO xmlFileDTO) {
        /*если расширение не .xml - бросаем исключение*/
        if (!xmlFileDTO.getXmlFileName().endsWith(".xml")) {
            throw new ApiRequestException( ExceptionText.EMPTYORWRONG.getTextexception(),ExceptionText.EMPTYORWRONG.getCode());
        }
        XmlFileDTO xmlFileresultDTO = null;
        /*если есть запись в proceed_entity, значит формируем выходной объект xmlFileresultDTO со статусом "валидный"*/
        if(proceedRepository.existsByFilename(xmlFileDTO.getXmlFileName())) {
            xmlFileresultDTO = xmlFileForResponseBuilder(xmlFileDTO, Status.VALID);
            logger.info("Getting valid file = {}", xmlFileresultDTO);
        }
        /*если есть запись в valid_xml_entity, значит формируем выходной объект xmlFileresultDTO со статусом "несохраненный"*/
        else if (validXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())) {
            xmlFileresultDTO = xmlFileForResponseBuilder(xmlFileDTO, Status.NOTSAVED);
            logger.info("Getting not saved file = {}", xmlFileresultDTO);
        }
        /*если есть запись в not_valid_xml_entity, значит формируем выходной объект xmlFileresultDTO со статусом "невалидный"*/
        else if (notValidXmlRepository.existsByFilename(xmlFileDTO.getXmlFileName())) {
            xmlFileresultDTO = xmlFileForResponseBuilder(xmlFileDTO, Status.NOTVALID);
            logger.info("Getting not valid file = {}", xmlFileresultDTO);
        }
        else {
           /*бросаем исключение для формирования ответа клиенту, что файл неизвестный*/
            throw new ApiRequestException(ExceptionText.UNKNOWNFILE.getTextexception(),ExceptionText.UNKNOWNFILE.getCode());
        }
        return xmlFileresultDTO;
    }

    /*чтение из файла*/
    private byte[] readfromFile (String xmlFilename) {
        byte[] bytes = null;
        try {
            String contents = null;
            Path path = Paths.get(filepath+xmlFilename);
            contents = Files.readString(path, StandardCharsets.ISO_8859_1);
            bytes= Base64.encodeBase64(contents.getBytes());
        } catch (IOException e) {
            logger.error("IO Exception in readfromFile is {}.",xmlFilename);
            throw new ApiRequestException(ExceptionText.SOMETHINGWRONG.getTextexception(),ExceptionText.SOMETHINGWRONG.getCode());
        }
        return bytes;
    }

    /*это класс помощник для формирования выходного объекта для метода getxml*/
    private XmlFileDTO xmlFileForResponseBuilder (XmlFileDTO inputXmlFileDTO, Status status){
        XmlFileDTO outputXmlFileDTO = new XmlFileDTO( inputXmlFileDTO.getXmlFileName(),
                                             status,
                                             new String(readfromFile(inputXmlFileDTO.getXmlFileName()), StandardCharsets.UTF_8),
                                             xmlXsdRelationRepository.findByXmlfilename(inputXmlFileDTO.getXmlFileName()).getXsdlfilename()
                                            );
        logger.info("Generated output file for getXml method");
        return outputXmlFileDTO;
    }
}
