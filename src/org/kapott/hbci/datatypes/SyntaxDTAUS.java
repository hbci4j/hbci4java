
/*  $Id: SyntaxDTAUS.java,v 1.2 2012/03/06 23:18:26 willuhn Exp $

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

package org.kapott.hbci.datatypes;

import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.manager.HBCIUtilsInternal;

// Speicherung im HBCI-MSG-Format
public class SyntaxDTAUS
     extends SyntaxAN
{
    public static String check(String st)
    {
        // [willuhn 2012-03-06, BUG 1129] siehe http://de.wikipedia.org/wiki/DIN_66003
        // DIN-66003 (der DTAUS-Zeichensatz) ist eigentlich ASCII, jedoch sind ein paar
        // unuebliche Sonderzeichen gegen deutsche Umlaute ersetzt worden.
        // Da HBCI4Java den Datenstrom jedoch als Latin1 liest, werden die Umlaure
        // faelschlicherweise als die Sonderzeichen aus der ASCII-Tabelle interpretiert.
        // In Wirklichkeit sind es jedoch die deutschen Umlaute. Wir biegen das
        // hier wieder gerade.
        st=st.replace("[","Ä");  // 0x5B
        st=st.replace("\\","Ö"); // 0x5C
        st=st.replace("]","Ü");  // 0x5D
        st=st.replace("{","ä");  // 0x7B
        st=st.replace("|","ö");  // 0x7C
        st=st.replace("}","ü");  // 0x7D
        st=st.replace("~","ß");  // 0x7E
      
        st=st.toUpperCase();
        st=st.replace('\344','\133').replace('\304','\133');
        st=st.replace('\366','\134').replace('\326','\134');
        st=st.replace('\374','\135').replace('\334','\135');
        st=st.replace('\337','\176');
        
        int len=st.length();
        for (int i=0;i<len;i++) {
            char ch=st.charAt(i);

            if (!((ch>='A' && ch<='Z') ||
                  (ch>='0' && ch<='9') ||
                  (ch==' ') || (ch=='.') || (ch==',') ||
                  (ch=='&') || (ch=='-') || (ch=='+') ||
                  (ch=='*') || (ch=='%') || (ch=='/') ||
                  (ch=='$') || 
                  (ch==0x5B) || (ch==0x5C) || (ch==0x5D) || (ch==0x7E))) {              // Ä Ö Ü ß
                
                String msg=HBCIUtilsInternal.getLocMsg("EXC_DTAUS_INV_CHAR",Character.toString(ch));
                if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreWrongDataSyntaxErrors",msg)) {
                    // [willuhn 2012-03-06, BUG 1129] Exception als fatal markieren
                    InvalidArgumentException e = new InvalidArgumentException(msg);
                    e.setFatal(true);
                    throw e;
                }
                
                st=st.replace(ch,' ');
            }
        }

        return st;
    }

    public SyntaxDTAUS(String x, int minlen, int maxlen)
    {
        super(check(x.trim()),minlen,maxlen);
    }

    public void init(String x, int minlen, int maxlen)
    {
        super.init(check(x.trim()),minlen,maxlen);
    }

    protected SyntaxDTAUS()
    {
        super();
    }
    
    protected void init()
    {
        super.init();
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        setContent(check(unquote(st)),minsize,maxsize);
        res.delete(0,endidx);
    }
    
    public SyntaxDTAUS(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public String toString()
    {
        String ret=super.toString();
        ret=ret.replace('\133','\304');
        ret=ret.replace('\134','\326');
        ret=ret.replace('\135','\334');
        ret=ret.replace('\176','\337');
        return ret;
    }
}
