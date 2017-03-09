
/*  $Id: AbstractHBCICallback.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

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

package org.kapott.hbci.callback;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.kapott.hbci.passport.HBCIPassport;

/** Diese Klasse dient als Basisklasse für allen Callback-Klassen. Eine Anwendung sollte
    zur Erstellung einer eigenen Callback-Klasse diese oder eine der schon bereitgestellten
    "fertigen" Klassen ({@link org.kapott.hbci.callback.HBCICallbackConsole},
    {@link org.kapott.hbci.callback.HBCICallbackSwing}) erweitern */
public abstract class AbstractHBCICallback
    implements HBCICallback
{
    /** Erzeugt einen Log-Eintrag. Diese Methode wird von den mitgelieferten
     * Callback-Klassen für die Erzeugung von Log-Einträgen verwendet. Um 
     * ein eigenes Format für die Log-Eintrage zu definieren, kann diese
     * Methode mit einer eigenen Implementierung überschrieben werden.<br/>
     * Die Parameter entsprechen denen der 
     * {@link HBCICallback#log(String, int, Date, StackTraceElement) log}-Methode
     * @return ein Log-Eintrag
     */
    protected String createDefaultLogLine(String msg, int level, Date date, StackTraceElement trace)
    {
        String[] levels={"NON","ERR","WRN","INF","DBG","DB2","INT"};
        StringBuffer ret=new StringBuffer(128);
        ret.append("<").append(levels[level]).append("> ");
        
        SimpleDateFormat df=new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
        ret.append("[").append(df.format(date)).append("] ");
        
        Thread thread=Thread.currentThread();
        ret.append("[").append(thread.getThreadGroup().getName());
        ret.append("/").append(thread.getName()).append("] ");
        
        String classname=trace.getClassName();
        String hbciname="org.kapott.hbci.";
        if (classname!=null && classname.startsWith(hbciname))
            ret.append(classname.substring((hbciname).length())).append(": ");
        
        if (msg==null)
            msg="";
        StringBuffer escapedString=new StringBuffer();
        int len=msg.length();
        
        for (int i=0;i<len;i++) {
            char ch=msg.charAt(i);
            int  x=ch;
            
            if ((x<26 && x!=9 && x!=10 && x!=13) || ch=='\\') {
                String temp=Integer.toString(x,16);
                if (temp.length()!=2)
                    temp="0"+temp;
                escapedString.append("\\").append(temp);
            }
            else escapedString.append(ch);
        }
        ret.append(escapedString);
        return ret.toString();
    }

    public synchronized final void status(HBCIPassport passport,int statusTag,Object o)
    {
        status(passport,statusTag,new Object[] {o});
    }
    
    /** Standard-Verhalten - gibt für alle Callbacks <code>false</code> (= asynchrone
     * Callback-Behandlung) zurück.*/
    public boolean useThreadedCallback(HBCIPassport passport,int reason,String msg,
                                       int datatype,StringBuffer retData)
    {
        return false;
    }
}
