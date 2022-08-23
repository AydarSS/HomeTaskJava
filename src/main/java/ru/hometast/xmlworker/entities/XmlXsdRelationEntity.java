package ru.hometast.xmlworker.entities;

import javax.persistence.*;

@Entity
public class XmlXsdRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name = "xmlfile_name")
    String xmlfilename;

    @Column(name = "xsdfile_name")
    String xsdlfilename;

    public XmlXsdRelationEntity() {
    }

    public XmlXsdRelationEntity(String xmlfilename, String xsdlfilename) {
        this.xmlfilename = xmlfilename;
        this.xsdlfilename = xsdlfilename;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getXmlfilename() {
        return xmlfilename;
    }

    public void setXmlfilename(String xmlfilename) {
        this.xmlfilename = xmlfilename;
    }

    public String getXsdlfilename() {
        return xsdlfilename;
    }

    public void setXsdlfilename(String xsdlfilename) {
        this.xsdlfilename = xsdlfilename;
    }
}
