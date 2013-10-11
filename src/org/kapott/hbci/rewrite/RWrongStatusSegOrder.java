
/*  $Id: RWrongStatusSegOrder.java,v 1.1 2011/05/04 22:37:57 willuhn Exp $

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

package org.kapott.hbci.rewrite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;

// dieser Rewriter muss *VOR* "WrongSequenceNumbers" ausgeführt werden,
// weil hierbei u.U. die Segment-Sequenz-Nummern durcheinandergebracht werden
public class RWrongStatusSegOrder 
    extends Rewrite
{
    // Liste mit segmentInfo-Properties aus der Nachricht erzeugen
    private List<Properties> createSegmentListFromMessage(String msg)
    {
        List<Properties> segmentList=new ArrayList<Properties>();
        
        boolean quoteNext=false;
        int     startPosi=0;
        
        for (int i=0;i<msg.length();i++) {
            char ch=msg.charAt(i);
            
            if (!quoteNext && ch=='@') {
                // skip binary values
                int    idx=msg.indexOf("@",i+1);
                String len_st=msg.substring(i+1,idx);
                i+=Integer.parseInt(len_st)+1+len_st.length();
            } else if (!quoteNext && ch=='\'') {
                // segment-ende gefunden
                Properties segmentInfo=new Properties();
                segmentInfo.setProperty("code", msg.substring(startPosi, msg.indexOf(":",startPosi)));
                segmentInfo.setProperty("start",Integer.toString(startPosi));
                segmentInfo.setProperty("length",Integer.toString(i-startPosi+1));
                
                segmentList.add(segmentInfo);
                startPosi=i+1;
            }
            quoteNext=!quoteNext && ch=='?';
        }
        
        return segmentList;
    }
    
    public String incomingClearText(String st,MsgGen gen)
    {
        List<Properties> segmentList=createSegmentListFromMessage(st);
        
        List<Properties> headerList=new ArrayList<Properties>();
        List<Properties> HIRMGList=new ArrayList<Properties>();
        List<Properties> HIRMSList=new ArrayList<Properties>();
        List<Properties> dataList=new ArrayList<Properties>();
        
        boolean inHeader=true;
        boolean inGlob=false;
        boolean inSeg=false;
        boolean inData=false;
        boolean errorOccured=false;
        
        // alle segmente aus der nachricht durchlaufen und der richtigen liste
        // zuordnen (header, globstatus, segstatus, rest)
        for (Iterator<Properties> i=segmentList.iterator();i.hasNext();) {
            Properties segmentInfo= i.next();
            String     segmentCode=segmentInfo.getProperty("code");
            
            if (segmentCode.equals("HNHBK") || segmentCode.equals("HNSHK")) {
                // HNHBK und HNSHK gehören in den header-bereich
                headerList.add(segmentInfo);
                
                if (!inHeader) {
                    HBCIUtils.log("RWrongStatusSegOrder: found segment "+segmentCode+" at invalid position",HBCIUtils.LOG_WARN);
                    errorOccured=true;
                }
                
            } else if (segmentCode.equals("HIRMG")) {
                // anschliessend muss ein HIRMG folgen
                HIRMGList.add(segmentInfo);
                
                if (inHeader) {
                    inHeader=false;
                    inGlob=true;
                }
                if (!inGlob) {
                    HBCIUtils.log("RWrongStatusSegOrder: found segment "+segmentCode+" at invalid position",HBCIUtils.LOG_WARN);
                    errorOccured=true;
                }
                
            } else if (segmentCode.equals("HIRMS")) {
                // nach HIRMG folgen 0-n HIRMS
                HIRMSList.add(segmentInfo);
                
                if (inGlob) {
                    inGlob=false;
                    inSeg=true;
                }
                if (!inSeg) {
                    HBCIUtils.log("RWrongStatusSegOrder: found segment "+segmentCode+" at invalid position",HBCIUtils.LOG_WARN);
                    errorOccured=true;
                }
                
            } else {
                // nach den status-segmenten folgen die datensegmente
                dataList.add(segmentInfo);
                
                if (inGlob || inSeg) {
                    inGlob=false;
                    inSeg=false;
                    inData=true;
                }
                if (!inData) {
                    HBCIUtils.log("RWrongStatusSegOrder: found segment "+segmentCode+" at invalid position",HBCIUtils.LOG_WARN);
                    errorOccured=true;
                }
            }
        }
        
        StringBuffer new_msg=new StringBuffer();
        if (errorOccured) {
            // nachricht mit den richtig sortierten segmenten wieder 
            // zusammensetzen
            int counter=1;
            
            // alle segmente aus dem header 
            new_msg.append(getDataForSegmentList(st,headerList,counter));
            counter+=headerList.size();
            
            // HIRMG-segment
            new_msg.append(getDataForSegmentList(st,HIRMGList,counter));
            counter+=HIRMGList.size();
            
            // HIRMS-segmente
            new_msg.append(getDataForSegmentList(st,HIRMSList,counter));
            counter+=HIRMSList.size();
            
            // restliche daten-segmente
            new_msg.append(getDataForSegmentList(st,dataList,counter));
            
            HBCIUtils.log("RWrongStatusSegOrder: new message after reordering: "+new_msg,HBCIUtils.LOG_DEBUG2);
        } else {
            // kein fehler aufgetreten, also originale nachricht unverändert zurückgeben
            new_msg.append(st);
        }
        
        return new_msg.toString();
    }
    
    private String getDataForSegmentList(String origMsg,List<Properties> list,int counter)
    {
        StringBuffer data=new StringBuffer();
        
        for (Iterator<Properties> i=list.iterator();i.hasNext();) {
            Properties segmentInfo= i.next();
            int        start=Integer.parseInt(segmentInfo.getProperty("start"));
            int        len=Integer.parseInt(segmentInfo.getProperty("length"));
            
            // segment aus originalnachricht extrahieren
            StringBuffer segmentData=new StringBuffer(origMsg.substring(start,start+len));
            
            // TODO: hier noch die segnum korrigieren (-->counter)
            // korrektur wird nun doch nicht hier vorgenommen, sondern statt
            // muss einfach der Rewriter "WrongSequenceNumbers" nach diesem
            // Rewriter angeordnet werden
            
            // segmentdaten hinten anhängen
            data.append(segmentData.toString());
            
            counter++;
         }
        
        return data.toString();
    }
}
