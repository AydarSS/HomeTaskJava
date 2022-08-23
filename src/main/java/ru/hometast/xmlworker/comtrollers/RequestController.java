package ru.hometast.xmlworker.comtrollers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hometast.xmlworker.models.XmlFileDTO;
import ru.hometast.xmlworker.services.XmlXsdWorker;

/*
 * RequestController - класс для обработки запросов от клиента.
 * Слушает запросы, которые приходят с input form с html-шаблонов с эмулятора клиента :
 * POST /xsdsaveservice/new - для метода savexsd
 * POST /xmlvalidator/checker - для метода validateXML
 * POST /xmlsaveservice/new   - для метода savexml
 * GET  /xmlgetservice/file   - для метода getxml
 * Все методы принимают на вход объект XmlFileDTO, который является надстройкой для Entity-сущностей
 * в методе savexsd подается на вход xmlfileDTO.xmlfilename и xmlfileDTO.xsdfilename. В остальных только xmlfileDTO.xmlfilename
 * */

@RestController
@ResponseBody
public class RequestController {

    Logger logger = LoggerFactory.getLogger(RequestController.class);

    private XmlXsdWorker xmlXsdWorker;

    @Autowired
    public RequestController(XmlXsdWorker xmlXsdWorker) {
        this.xmlXsdWorker = xmlXsdWorker;
    }

    /*save XSD*/
    @PostMapping("/xsdsaveservice/new")
    public ResponseEntity savexsd(@ModelAttribute("xmlfile") XmlFileDTO xmlfile) {
        logger.info("Calling method 'savexsd' with argument = {}",xmlfile);
        xmlXsdWorker.savexsd(xmlfile);
        return new ResponseEntity<String>("Succesful",HttpStatus.OK);
     }

    /*validateXml*/
    @PostMapping("/xmlvalidator/checker")
    public ResponseEntity xmlvalidate (@ModelAttribute("xmlfile") XmlFileDTO xmlfile) {
        logger.info("Calling method 'validatexml' with argument = {}",xmlfile);
        xmlXsdWorker.validatexml(xmlfile);
        return new ResponseEntity<String>("Valid file",HttpStatus.OK);
    }

    /*saveXml*/
    @PostMapping("/xmlsaveservice/new")
    public ResponseEntity savexml (@ModelAttribute("xmlfile") XmlFileDTO xmlfile) {
        logger.info("Calling method 'savexml' with argument = {}",xmlfile);
        xmlXsdWorker.savexml(xmlfile);
        return new ResponseEntity<String>("Succesful",HttpStatus.OK);
    }

    /*getXml*/
    @GetMapping("/xmlgetservice/file")
    public ResponseEntity getxml (@ModelAttribute("xmlfile") XmlFileDTO xmlfile) {
        logger.info("Calling method 'getxml' with argument = {}",xmlfile);
        return  ResponseEntity.ok( xmlXsdWorker.getxml(xmlfile));
    }

}
