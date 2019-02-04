/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format.legacy;

import java.io.InputStream;

import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportAnonymous;
import org.kapott.hbci.passport.storage.PassportData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementierung des Converter fuer Anonymous-Passports.
 */
public class ConverterAnonymous extends AbstractConverterXML
{
    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#load(java.io.InputStream)
     */
    @Override
    public PassportData load(InputStream is) throws Exception
    {
        Element root = this.read(is);

        final PassportData data = new PassportData();
        data.blz         = this.getElementValue(root,"blz");
        data.country     = this.getElementValue(root,"country");
        data.host        = this.getElementValue(root,"host");
        data.hbciVersion = this.getElementValue(root,"hbciversion");
        data.bpd         = this.getElementProps(root,"bpd");
        data.upd         = this.getElementProps(root,"upd");

        String port = this.getElementValue(root,"port");
        if (port != null)
            data.port = Integer.parseInt(port);
        
        return data;
    }
    
    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.AbstractConverterXML#fill(org.w3c.dom.Document, org.w3c.dom.Element, org.kapott.hbci.passport.storage.PassportData)
     */
    @Override
    protected void fill(Document doc, Element root, PassportData data)
    {
        this.createElement(doc,root,"country",data.country);
        this.createElement(doc,root,"blz",data.blz);
        this.createElement(doc,root,"host",data.host);
        if (data.port != null)
            this.createElement(doc,root,"port",Integer.toString(data.port));
        
        this.createElement(doc,root,"hbciversion",data.hbciVersion);

        this.createPropsElement(doc,root,"bpd",data.bpd);
        this.createPropsElement(doc,root,"upd",data.upd);
    }

    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#supports(org.kapott.hbci.passport.HBCIPassport)
     */
    @Override
    public boolean supports(HBCIPassport passport)
    {
        return passport != null && (passport instanceof HBCIPassportAnonymous);
    }
    
}
