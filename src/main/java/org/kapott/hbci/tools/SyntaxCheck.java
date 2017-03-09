
/*  $Id: SyntaxCheck.java,v 1.1 2011/05/04 22:37:45 willuhn Exp $

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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.MSG;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public final class SyntaxCheck 
{
    private static String getArg(String[] args,int idx,String st)
        throws IOException
    {
        String ret=null;
        
        if (args!=null && idx<args.length) {
            ret=args[idx];
        } else {
            System.out.print(st+": ");
            System.out.flush();
            ret=new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        
        return ret;
    }
    
    public static void main(String[] args)
        throws IOException
    {
        String ifilename=getArg(args,0,"Dateiname der Datei mit der HBCI-Nachricht");
        String version=getArg(args,1,"HBCI-Version");
        String msgName=getArg(args,2,"Name der Nachricht");
        String checkSeq_st=getArg(args,3,"Sequenznummern validieren (0/1)");
        boolean checkSeq=checkSeq_st.equals("1");
        String checkValids_st=getArg(args,4,"Auf gültige Werte testen (0/1)");
        boolean checkValids=checkValids_st.equals("1");
        
        Properties props=new Properties();
        props.setProperty("log.loglevel.default", "6");
        HBCIUtils.init(props,new HBCICallbackConsole());
        
        FileInputStream fi=new FileInputStream(ifilename);
        byte[]          buffer=new byte[1024];
        int             len;
        StringBuffer    st=new StringBuffer();
        
        while ((len=fi.read(buffer))>0) {
            st.append(new String(buffer,0,len,Comm.ENCODING));
        }
        fi.close();
        
        MsgGen gen=new MsgGen(SyntaxCheck.class.getClassLoader().getResourceAsStream("hbci-"+version+".xml"));
        
        if (msgName!=null && msgName.length()!=0) {
            MSG msg=new MSG(msgName,st.toString(),st.length(),gen,checkSeq,checkValids);
            String st2=msg.toString(0);
            
            if (st2.equals(st.toString())) {
                System.out.println("ok");
            } else {
                System.out.println("detected, but different in- and output");
                System.out.println(st2);
            }
            
            Properties p=msg.getData();
            ArrayList al=new ArrayList();
            for (Enumeration e=p.propertyNames();e.hasMoreElements();) {
                al.add(e.nextElement());
            }
            String[] sa=(String[])al.toArray(new String[al.size()]);
            Arrays.sort(sa);
            
            for (int i=0;i<sa.length;i++) {
                String value=p.getProperty(sa[i]);
                System.out.println(sa[i]+" = "+value+" ("+value.length()+" Bytes)");
            }
        } else {
            NodeList list=gen.getSyntax().getElementsByTagName("MSGdef");
            int      size=list.getLength();
            MSG      msg;
            
            for (int i=0;i<size;i++) {
                msgName=((Element)list.item(i)).getAttribute("id");
                System.out.println("checking for '"+msgName+"'");
                try {
                    msg=new MSG(msgName,st.toString(),st.length(),gen,checkSeq,checkValids);
                } catch (Exception e) {
                    msg=null;
                }
                if (msg!=null) {
                    String st2=msg.toString(0);
                    
                    if (st2.equals(st.toString())) {
                        System.out.println("ok");
                    } else {
                        System.out.println("detected, but different in- and output");
                        System.out.println(st2);
                    }
                }
            }
        }
    }
}
