/*
* Класс ClientEmulationController - эмулятор работы клиента, методы возвращают названия template -шаблонов
* */



package ru.hometast.xmlworker.comtrollers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.hometast.xmlworker.models.XmlFileDTO;

@Controller
@RequestMapping("/file")
public class ClientEmulationController {


    /*Save Xsd*/
    @GetMapping("/xsdsaveservice")
    public String xsdsaveservice (Model model) {
        model.addAttribute("xmlfile", new XmlFileDTO());
        return "xsdsaveservice";
    }

    /*Validate Xml*/
    @GetMapping("/xmlvalidator")
    public String xmlvalidator(Model model) {
        model.addAttribute("xmlfile", new XmlFileDTO());
        return "xmlvalidator";
    }

    /*Save Xml*/
    @GetMapping("/xmlsaveservice")
    public String xmlsaveservice(Model model) {
        model.addAttribute("xmlfile", new XmlFileDTO());
        return "xmlsaveservice";
    }

    /*Get Xml*/
   @GetMapping("/xmlgetservice")
    public String xmlgetservice (Model model) {
        model.addAttribute("xmlfile", new XmlFileDTO());
        return "xmlgetservice";
    }

}

