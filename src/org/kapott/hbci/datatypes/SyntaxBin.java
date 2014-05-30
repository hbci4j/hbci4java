
/*  $Id: SyntaxBin.java,v 1.1 2011/05/04 22:37:55 willuhn Exp $

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

import java.math.BigInteger;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/* @internal
    @brief SyntaxBin enthält Binärdaten.

    Beim Initialisieren gibt das erste
    Zeichen des übergebenen Strings den Typ der nachfolgenden Daten an:
      - N bedeutet, der nachfolgende String ist eine Integer-Zahl, die binär
        dargestellt werden soll 
      - B bedeutet, der nachfolgende String ist bereits ein Binärstring 
        und soll ohne Änderungen übernommen werden

    @author $Author: willuhn $
*/

// intern wird das byte-array gespeichert
public class SyntaxBin
     extends SyntaxDE
{
    /** @internal @brief Returns a given number in byte notation

        This method transforms a given number into its byte-representation
        in big-endian-format.

        @param x the String representation of the number
        @return a String, where each "character" is one byte of the
                big-endian-byte-representation of the given number
    */
    private static String expandNumber(String x)
    {
        try {
            return new String((new BigInteger(x)).toByteArray(),Comm.ENCODING);
        } catch (Exception ex) {
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_BINNUMERR"),ex);
        }
    }

    /** @internal @brief erzeugt den HBCI-Datentyp BIN 

        Es wird der erzeugte String zurückgegeben. Dazu wird das erste Zeichen
        des uebergebenen Strings ausgewertet. Ist dieses "N", so wird der
        uebergebene String als Integer-Wert interpretiert und in seine
        binaere Byte-Darstellung konvertiert. Bei "B" als erstem Zeichen wird
        der String as-is uebernommen.

        @param x the String to be converted
        @return the binary String representing the given value
        @exception IllegalArgumentException occurs when the first character of
                   the given string is neither "N" nor "B"
    */
    private static String expand(String x)
    {
        char format = x.charAt(0);
        String st = x.substring(1);
        String ret = null;

        switch (format) {
        case 'N':
            ret = expandNumber(st);
            break;
        case 'B':
            ret = st;
            break;
        default:
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg(
                    "EXC_DTBIN_NO_VALID_FORMAT",
                    Character.toString(format)));
        }

        return ret;
    }

    /** @internal @brief creates an object representing the BIN datatype
        @see SyntaxDE
    */
    public SyntaxBin(String x, int minlen, int maxlen)
    {
        super(expand(x),minlen,maxlen);
    }
    
    public void init(String x, int minlen, int maxlen)
    {
        super.init(expand(x),minlen,maxlen);
    }
    
    /** @see SyntaxDE */
    public String toString(int zero)
    {
        String con=getContent();
        String header="@"+Integer.toString(con.length())+"@";
        return header+con;
    }

    // --------------------------------------------------------------------------------

    /** @internal @brief returns the size of the header

        @param st the string representing the complete datatype BIN
        @return the length of the header-field in this string (i.e.
                the number of bytes making the @len@ part of the string
    */
    private int getHeaderLen(String st)
    {
        int idx = 0;
        int delimFound = 0;
        int len=st.length();

        while (idx<len && delimFound!=2) {
            if (st.charAt(idx++) == '@') {
                delimFound++;
            }
        }

        return idx;
    }

    /** @internal @brief gets the "real" value out of an HBCI-BIN-datatype

        this method takes a string representing the value of a BIN-datatype
        and extracts the real value (the data making the element) from it.
        for this to achieve it removes the header containing the length
        of the data

        @param st the content of an HBCI-BIN-datatype-field
        @return the "real" data wrapped into the given string
     */
    private String parse(String st)
    {
        int headerLen = getHeaderLen(st);
        String ret = "";

        if (headerLen != 0) {
            int size = Integer.parseInt(st.substring(1,headerLen-1));
            ret=st.substring(headerLen,headerLen+size);
        }

        return ret;
    }
    
    private void initData(StringBuffer res,int minsize,int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        String temp = parse(st);
        setContent(temp, minsize, maxsize);

        res.delete(0, endidx);
    }

    /** @see SyntaxDE */
    public SyntaxBin(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
}
