
/*  $Id: SyntaxCtr.java,v 1.1 2011/05/04 22:37:55 willuhn Exp $

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
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/* @brief class for storing data of type "country"

    contains specification for datatype "ctr";
    this is the representation of a country 
*/
// Speicherung des HBCI-MSG-Formats
public final class SyntaxCtr
     extends SyntaxDE
{
    /** @brief returns the ISO-country-code for a country

        @param x String-representation of the country (e.g. DE or US).
        @return ISO-three-digit-code for this country
    */
    public static String getCode(String x)
    {
        String ret=null;

        if (x.equals("DE")) {
            ret="280";
        } else if (x.equals("AT")) {
            ret="040";
        } else if (x.equals("FR")) {
            ret="250";
        } else if (x.equals("BE")) {
            ret="056";
        } else if (x.equals("BG")) {
            ret="100";
        } else if (x.equals("DK")) {
            ret="208";
        } else if (x.equals("FI")) {
            ret="246";
        } else if (x.equals("GR")) {
            ret="300";
        } else if (x.equals("GB")) {
            ret="826";
        } else if (x.equals("IE")) {
            ret="372";
        } else if (x.equals("IS")) {
            ret="352";
        } else if (x.equals("IT")) {
            ret="380";
        } else if (x.equals("JP")) {
            ret="392";
        } else if (x.equals("CA")) {
            ret="124";
        } else if (x.equals("HR")) {
            ret="191";
        } else if (x.equals("LI")) {
            ret="438";
        } else if (x.equals("LU")) {
            ret="442";
        } else if (x.equals("NL")) {
            ret="528";
        } else if (x.equals("NO")) {
            ret="578";
        } else if (x.equals("PL")) {
            ret="616";
        } else if (x.equals("PT")) {
            ret="620";
        } else if (x.equals("RO")) {
            ret="642";
        } else if (x.equals("RU")) {
            ret="643";
        } else if (x.equals("SE")) {
            ret="752";
        } else if (x.equals("CH")) {
            ret="756";
        } else if (x.equals("SK")) {
            ret="703";
        } else if (x.equals("SI")) {
            ret="705";
        } else if (x.equals("ES")) {
            ret="724";
        } else if (x.equals("CZ")) {
            ret="203";
        } else if (x.equals("TR")) {
            ret="792";
        } else if (x.equals("HU")) {
            ret="348";
        } else if (x.equals("US")) {
            ret="840";
        } else if (x.equals("EU")) {
            ret="978";
        } else {
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXC_DT_UNNKOWN_CTR",x));
        }

        return ret;
    }

    public SyntaxCtr(String x, int minsize, int maxsize)
    {
        super(getCode(x.trim()),3,3);
    }

    public void init(String x, int minsize, int maxsize)
    {
        super.init(getCode(x.trim()),3,3);
    }

    // --------------------------------------------------------------------------------

    public static String getName(String x)
    {
        String ret=null;

        if (x.equals("280")) {
            ret="DE";
        } else if (x.equals("040")) {
            ret="AT";
        } else if (x.equals("250")) {
            ret="FR";
        } else if (x.equals("056")) {
            ret="BE";
        } else if (x.equals("100")) {
            ret="BG";
        } else if (x.equals("208")) {
            ret="DK";
        } else if (x.equals("246")) {
            ret="FI";
        } else if (x.equals("300")) {
            ret="GR";
        } else if (x.equals("826")) {
            ret="GB";
        } else if (x.equals("372")) {
            ret="IE";
        } else if (x.equals("352")) {
            ret="IS";
        } else if (x.equals("380")) {
            ret="IT";
        } else if (x.equals("392")) {
            ret="JP";
        } else if (x.equals("124")) {
            ret="CA";
        } else if (x.equals("191")) {
            ret="HR";
        } else if (x.equals("438")) {
            ret="LI";
        } else if (x.equals("442")) {
            ret="LU";
        } else if (x.equals("528")) {
            ret="NL";
        } else if (x.equals("578")) {
            ret="NO";
        } else if (x.equals("616")) {
            ret="PL";
        } else if (x.equals("620")) {
            ret="PT";
        } else if (x.equals("642")) {
            ret="RO";
        } else if (x.equals("643")) {
            ret="RU";
        } else if (x.equals("752")) {
            ret="SE";
        } else if (x.equals("756")) {
            ret="CH";
        } else if (x.equals("703")) {
            ret="SK";
        } else if (x.equals("705")) {
            ret="SI";
        } else if (x.equals("724")) {
            ret="ES";
        } else if (x.equals("203")) {
            ret="CZ";
        } else if (x.equals("792")) {
            ret="TR";
        } else if (x.equals("348")) {
            ret="HU";
        } else if (x.equals("840")) {
            ret="US";
        } else if (x.equals("978")) {
            ret="EU";
        } else {
            throw new InvalidArgumentException(HBCIUtilsInternal.getLocMsg("EXC_DT_UNNKOWN_CTR",x));
        }

        return ret;
    }
    
    private void initData(StringBuffer res,int minsize,int maxsize)
    {
        int startidx = skipPreDelim(res);
        int endidx = findNextDelim(res, startidx);
        String st = res.substring(startidx, endidx);

        getName(st);
        setContent(st,3,3);
        res.delete(0,endidx);
    }

    public SyntaxCtr(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }
    
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    public String toString()
    {
        String c=getContent();
        return (c==null)?"":getName(c);
    }
}
