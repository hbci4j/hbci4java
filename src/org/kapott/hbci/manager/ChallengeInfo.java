
/*  $Id: ChallengeInfo.java,v 1.2 2011/05/13 15:28:35 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.manager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//TODO: doku fehlt
public class ChallengeInfo 
{
    private static ChallengeInfo _instance=null;

    private Hashtable challengedata;

    static public ChallengeInfo getInstance()
    {
        if (_instance==null) {
            _instance=new ChallengeInfo();
        }
        return _instance;
    }

    private ChallengeInfo()
    {
        HBCIUtils.log("initializing challenge info engine",HBCIUtils.LOG_DEBUG);

        // classloader ermitteln, mit dem die challengedata.xml geladen werden soll
        ClassLoader cl=this.getClass().getClassLoader();

        // versuchen, die challengeinfo.xml mit diesem classloader zu laden
        String xmlpath=HBCIUtils.getParam("kernel.kernel.challengedatapath");
        InputStream dataStream=null;
        if (xmlpath==null) {
            xmlpath="";
        }

        String filename=xmlpath+"challengedata.xml";
        dataStream=cl.getResourceAsStream(filename);
        if (dataStream==null)
            throw new InvalidUserDataException("*** can not load challenge information from "+filename);

        // mit den so gefundenen xml-daten ein xml-dokument bauen
        Document doc=null;
        try {
            DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();

            dbf.setIgnoringComments(true);
            dbf.setValidating(true);

            DocumentBuilder db=dbf.newDocumentBuilder();
            doc=db.parse(dataStream);
            dataStream.close();
        } catch (Exception e) {
            throw new HBCI_Exception("*** can not load challengedata from file "+filename,e);
        }

        // dieses xml-dokument parsen und daraus ein entsprechendes properties
        // objekt mit den challenge-informationen machen
        this.challengedata=new Hashtable();

        NodeList jobs=doc.getElementsByTagName("job");
        int      jobs_len=jobs.getLength();
        for (int j=0;j<jobs_len;j++) {
            Element    job=(Element)jobs.item(j);
            String     segcode=job.getAttribute("code");

            Hashtable h_specs=new Hashtable();
            challengedata.put(segcode,h_specs);

            NodeList specs=job.getElementsByTagName("challengeinfo");
            int      specs_len=specs.getLength();
            for (int s=0;s<specs_len;s++) {
                Element spec=(Element)specs.item(s);
                
                // willuhn 2011-05-13 In dem Attribut koennen mehrere HHD-Versionen angegeben werden
                String [] specnames=spec.getAttribute("spec").split(",");

                Hashtable h_info=new Hashtable();
                for (String names:specnames)
                  h_specs.put(names,h_info);

                h_info.put("klass", ((Element)spec.getElementsByTagName("klass").item(0)).getFirstChild().getNodeValue());

                NodeList params=spec.getElementsByTagName("param");
                int      params_len=params.getLength();
                
                // jetzt alle params durchgehen und merken 
                List     params_l=new ArrayList();
                for (int p=0;p<params_len;p++) {
                    Element param=(Element)params.item(p);
                    params_l.add(param.getFirstChild().getNodeValue());
                }
                h_info.put("param_paths", params_l.toArray(new String[params_len]));

                // das gleiche für value
                NodeList values=spec.getElementsByTagName("value");
                if (values.getLength()!=0) {
                    h_info.put("value_path", values.item(0).getFirstChild().getNodeValue());
                }

                // und für curr
                NodeList currs=spec.getElementsByTagName("curr");
                if (currs.getLength()!=0) {
                    h_info.put("curr_path", currs.item(0).getFirstChild().getNodeValue());
                }
            }
        }

        HBCIUtils.log("challenge information loaded",HBCIUtils.LOG_DEBUG);
    }

    public Hashtable getInfoBySegCode(String segcode,String spec)
    {
        Hashtable result=null;
        Hashtable specs_table=(Hashtable)(this.challengedata.get(segcode));
        if (specs_table!=null) {
            result=(Hashtable)specs_table.get(spec);
        }
        return result;
    }

    public String getKlassBySegCode(String segcode,String spec)
    {
        String    klass=null;
        Hashtable info=getInfoBySegCode(segcode,spec);
        if (info!=null) {
            klass=(String)info.get("klass");
        }
        return klass;
    }

    public String[] getParamPathsBySegCode(String segcode,String spec)
    {
        String[]  parampaths=new String[0];
        Hashtable info=getInfoBySegCode(segcode,spec);
        if (info!=null) {
            String[] paths=(String[])info.get("param_paths");
            if (paths!=null) {
                parampaths=paths;
            }
        }
        return parampaths;
    }

    public String getValuePathBySegCode(String segcode,String spec)
    {
        String    valuepath=null;
        Hashtable info=getInfoBySegCode(segcode,spec);
        if (info!=null) {
            valuepath=(String)info.get("value_path");
        }
        return valuepath;
    }

    public String getCurrPathBySegCode(String segcode,String spec)
    {
        String    currpath=null;
        Hashtable info=getInfoBySegCode(segcode,spec);
        if (info!=null) {
            currpath=(String)info.get("curr_path");
        }
        return currpath;
    }
}
