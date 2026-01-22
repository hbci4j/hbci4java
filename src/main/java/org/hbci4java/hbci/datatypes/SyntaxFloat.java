/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

package org.hbci4java.hbci.datatypes;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.hbci4java.hbci.exceptions.HBCI_Exception;
import org.hbci4java.hbci.exceptions.InvalidUserDataException;
import org.hbci4java.hbci.manager.HBCIUtils;
import org.hbci4java.hbci.manager.HBCIUtilsInternal;

/* a class for representing the HBCI-datatype "float" */
// interne Speicherung im HBCI-MSG-Format
public class SyntaxFloat
    extends SyntaxDE
{
    public SyntaxFloat(String x, int minsize, int maxsize)
    {
        super(double2string(x),minsize,maxsize);
    }

    @Override
    public void init(String x, int minsize, int maxsize)
    {
        super.init(double2string(x),minsize,maxsize);
    }

    /** @brief converts the given number into hbci-float-format

        @param st a double number in string format (with "," or "." as
               decimal delimiter)
        @return a valid hbci-representation of this number (i.e. with ","
                as decimal delimiter and with no trailing zeroes after
                the "0")
     */
    private static String double2string(String st)
    {
        DecimalFormat hbciFormat=new DecimalFormat("0.##");
        DecimalFormatSymbols symbols=hbciFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator(',');
        hbciFormat.setDecimalFormatSymbols(symbols);
        hbciFormat.setDecimalSeparatorAlwaysShown(true);
        
        return hbciFormat.format(HBCIUtils.string2BigDecimal(st));
    }

    // --------------------------------------------------------------------------------

    private void initData(StringBuffer res, int minsize, int maxsize)
    {
        String st=null;
        
        try {
            int startidx=skipPreDelim(res);
            int endidx=findNextDelim(res,startidx);
            st=res.substring(startidx,endidx);

            if (st.length()!=0) {
                DecimalFormat hbciFormat=new DecimalFormat("0.##");
                DecimalFormatSymbols symbols=hbciFormat.getDecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                hbciFormat.setDecimalFormatSymbols(symbols);
                hbciFormat.setDecimalSeparatorAlwaysShown(true);
                
                hbciFormat.parse(st).doubleValue();
            }

            setContent(st,minsize,maxsize);
            res.delete(0,endidx);
        } catch (Exception ex) {
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_FLOATERR",st),ex); 
        }
    }

    public SyntaxFloat(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    @Override
    public void init(StringBuffer res, int minsize, int maxsize)
    {
        initData(res,minsize,maxsize);
    }

    @Override
    public String toString()
    {
        try {
            String ret="";
            String c=getContent();
            
            if (c!=null) {
                DecimalFormat hbciFormat=new DecimalFormat("0.##");
                DecimalFormatSymbols symbols=hbciFormat.getDecimalFormatSymbols();
                symbols.setDecimalSeparator(',');
                hbciFormat.setDecimalFormatSymbols(symbols);
                hbciFormat.setDecimalSeparatorAlwaysShown(true);
                hbciFormat.setParseBigDecimal(true);
                
                ret=HBCIUtils.bigDecimal2String((BigDecimal) hbciFormat.parse(c));
            }
            
            return ret;
        } catch (Exception e) {
            throw new HBCI_Exception(e);
        }
    }
}
