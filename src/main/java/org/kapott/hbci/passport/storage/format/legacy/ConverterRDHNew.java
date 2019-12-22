/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 **********************************************************************/

package org.kapott.hbci.passport.storage.format.legacy;

import java.io.InputStream;

import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.HBCIPassportRDHNew;
import org.kapott.hbci.passport.storage.PassportData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Implementierung des Converter fuer RDHNew.
 */
public class ConverterRDHNew extends AbstractConverterXML
{
    
    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#load(java.io.InputStream)
     */
    @Override
    public PassportData load(InputStream is) throws Exception
    {
        Element root = this.read(is);
        
        final PassportData data = new PassportData();
        data.blz             = this.getElementValue(root,"blz");
        data.country         = this.getElementValue(root,"country");
        data.host            = this.getElementValue(root,"host");
        
        String port = this.getElementValue(root,"port");
        if (port != null)
            data.port = Integer.parseInt(port);

        data.userId          = this.getElementValue(root,"userid");
        data.customerId      = this.getElementValue(root,"customerid");
        data.sysId           = this.getElementValue(root,"sysid");
        
        String sigId = this.getElementValue(root,"sigid");
        if (sigId != null)
            data.sigId = Long.parseLong(sigId);

        data.profileVersion  = this.getElementValue(root,"rdhprofile");
        data.hbciVersion     = this.getElementValue(root,"hbciversion");

        data.bpd             = this.getElementProps(root,"bpd");
        data.upd             = this.getElementProps(root,"upd");
        
        data.instSigKey      = this.getElementKey(root,"inst","S","public");
        data.instEncKey      = this.getElementKey(root,"inst","V","public");
        data.myPublicSigKey  = this.getElementKey(root,"user","S","public");
        data.myPrivateSigKey = this.getElementKey(root,"user","S","private");
        data.myPublicEncKey  = this.getElementKey(root,"user","V","public");
        data.myPrivateEncKey = this.getElementKey(root,"user","V","private");
        
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
        
        this.createElement(doc,root,"userid",data.userId);
        this.createElement(doc,root,"customerid",data.customerId);
        this.createElement(doc,root,"sysid",data.sysId);
        
        if (data.sigId != null)
            this.createElement(doc,root,"sigid",Long.toString(data.sigId));
        
        this.createElement(doc,root,"rdhprofile",data.profileVersion);
        this.createElement(doc,root,"hbciversion",data.hbciVersion);
        
        this.createPropsElement(doc,root,"bpd",data.bpd);
        this.createPropsElement(doc,root,"upd",data.upd);
        
        this.createKeyElement(doc,root,"inst","S","public",data.instSigKey);
        this.createKeyElement(doc,root,"inst","V","public",data.instEncKey);
        this.createKeyElement(doc,root,"user","S","public",data.myPublicSigKey);
        this.createKeyElement(doc,root,"user","S","private",data.myPrivateSigKey);
        this.createKeyElement(doc,root,"user","V","public",data.myPublicEncKey);
        this.createKeyElement(doc,root,"user","V","private",data.myPrivateEncKey);
    }
    
    /**
     * @see org.kapott.hbci.passport.storage.format.legacy.Converter#supports(org.kapott.hbci.passport.HBCIPassport)
     */
    @Override
    public boolean supports(HBCIPassport passport)
    {
        // Wir unterstuetzen nur den RSA-Passport.
        return passport != null && (passport instanceof HBCIPassportRDHNew);
    }

}
