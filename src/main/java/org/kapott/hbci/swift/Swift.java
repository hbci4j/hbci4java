
/*  $Id: Swift.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

package org.kapott.hbci.swift;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Swift
{
    /* With this, a block always ends with \r\n- */
    public static String getOneBlock(StringBuffer stream)
    {
        String ret=null;
        
        int endpos=stream.indexOf("\r\n:20:", 1);
        if (endpos==-1) {
            endpos=stream.length();
        }
        if (endpos>0) {
            ret=stream.substring(0,endpos);
        }
        
        return ret; 
    }

    public static String getTagValue(String st,String tag,int counter)
    {
        String  ret=null;
        Pattern patternNLTag=Pattern.compile("\\r\\n(-|-\\r\\n)?:\\d{2}[A-Z]?:"); // Zu dem "(-)?" siehe TestBrokenMT940.java
        
        int endpos=0;
        while (true) {
            ret=null;

            // find start-posi of the requested tag
            int startpos=st.indexOf("\r\n:"+tag+":",endpos);
            if (startpos!=-1) {
                // skip \r\n:XY: of start tag
                startpos+=3+tag.length()+1;
                
                // tag found - find start of next tag
                Matcher matcher=patternNLTag.matcher(st);
                if (matcher.find(startpos))
                {
                    endpos=matcher.start();
                    ret=st.substring(startpos, endpos);
                }
                else
                {
                    ret = st.substring(startpos);
                    
                    // Kein weiteres Tag gefunden. Alle "\n", "\r" und "-" am Ende abschneiden
                    ret = ret.replaceAll("[\\r\\n-]{0,10}","");
                } 
            }

            if ((counter--)==0 || startpos==-1) {
                // we found the "counter"th tag, or there is no more such tag
                break;
            }
        }

        return ret;
    }

    
    /* Removes the \r\n sequences which have no meaning */
    public static String packMulti(String st)
    {
        return st.replaceAll("\r\n","");
    }

    
    /* Gets a value from the "multi-tag". Codes look like ?20 - a value goes until
     * the next value-code or until end of data */
    public static String getMultiTagValue(String st,String tag)
    {
        String ret=null;
        int    pos=st.indexOf("?"+tag);

        if (pos!=-1) {
            // first possible endpos
            int searchpos=pos+3;
            int endpos=-1;
            
            while (true) {
                // search for start of next value-code
                endpos=st.indexOf("?", searchpos);
                
                // check if this is REALLY a value-code
                if (endpos!=-1 && endpos+2<st.length()) {
                    /* "?" is far enough before end to make it possible  
                     * to be followed by two digits */
                    
                    if (st.charAt(endpos+1)>='0' && st.charAt(endpos+1)<='9' &&
                            st.charAt(endpos+2)>='0' && st.charAt(endpos+2)<='9')
                    {
                        /* the "?" must be followed by two digits, a single "?"
                         *  does NOT mark the end of value */
                        break;
                    }
                } else {
                    /* the "?" is near the end of the string, so we break out
                     * here und use the complete string as value */
                    endpos=-1;
                    break;
                }
                
                // start search for the next "?" after the current, wrong one
                searchpos = endpos+1;
            }
            
            if (endpos==-1)
                endpos=st.length();

            ret=st.substring(pos+3,endpos);
        }

        return ret;
    }
    
    
    public static String decodeUmlauts(String st)
    {
        String ret=st.replace('\133','\304');
               ret=ret.replace('\134','\326');
               ret=ret.replace('\135','\334');
               ret=ret.replace('\176','\337');
        return ret;
    }
}
