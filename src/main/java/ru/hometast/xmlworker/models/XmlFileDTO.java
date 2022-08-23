package ru.hometast.xmlworker.models;

import lombok.*;
import ru.hometast.xmlworker.entities.NotValidXmlEntity;
import ru.hometast.xmlworker.entities.ProceedEntity;
import ru.hometast.xmlworker.entities.ValidXmlEntity;
import ru.hometast.xmlworker.entities.XmlXsdRelationEntity;


/*Класс надстройка над таблицами
* not_valid_xml_entity
* procced_entity
* valid_xml_entity
* xml_xsd_relation_entity
* Осуществляет конвертацию из объектов *entity в объект этого класса
* */
@ToString
@EqualsAndHashCode
@Getter
@Setter
public class XmlFileDTO {
    private String xmlFileName;
    private Status status;
    private String content;
    private String xsdFileName;

    public XmlFileDTO() {
    }

    public XmlFileDTO(String xmlFileName, Status status, String content, String xsdFileName) {
        this.xmlFileName = xmlFileName;
        this.status = status;
        this.content = content;
        this.xsdFileName = xsdFileName;
    }

    public XmlXsdRelationEntity convertToXmlXsdEntity() {
        XmlXsdRelationEntity xmlXsdRelationEntity = new XmlXsdRelationEntity();
        xmlXsdRelationEntity.setXmlfilename(this.xmlFileName);
        xmlXsdRelationEntity.setXsdlfilename(this.xsdFileName);
        return xmlXsdRelationEntity;
    }

    public ValidXmlEntity convertToXmlValidEntity() {
        ValidXmlEntity validXmlEntity = new ValidXmlEntity();
        validXmlEntity.setFilename(this.xmlFileName);
        return validXmlEntity;
    }

    public NotValidXmlEntity convertToXmlNotValidEntity() {
        NotValidXmlEntity notValidXmlEntity = new NotValidXmlEntity();
        notValidXmlEntity.setFilename(this.xmlFileName);
        return notValidXmlEntity;
    }

    public ProceedEntity convertToProceedEntity() {
        ProceedEntity proceedEntity = new ProceedEntity();
        proceedEntity.setFilename(this.xmlFileName);
        return proceedEntity;
    }


}

