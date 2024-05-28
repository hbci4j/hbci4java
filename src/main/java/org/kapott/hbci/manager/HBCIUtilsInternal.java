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

package org.kapott.hbci.manager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.tools.ParameterFinder;

public class HBCIUtilsInternal
{

    public static Properties blzs;
    public static Map<String,BankInfo> banks = null;
    public static Hashtable<ThreadGroup, HBCICallback>  callbacks;  // threadgroup->callbackObject
    public static Hashtable<ThreadGroup, ResourceBundle>  locMsgs;    // threadgroup->resourceBundle
    public static Hashtable<ThreadGroup, Locale>  locales;    // threadgroup->Locale
    
    public static String bigDecimal2String(BigDecimal value)
    {
        DecimalFormat format=new DecimalFormat("0.##");
        DecimalFormatSymbols symbols=format.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        format.setDecimalFormatSymbols(symbols);
        format.setDecimalSeparatorAlwaysShown(false);
        return format.format(value);
    }

    /**
     * Liefert die Zeile aus der blz.properties mit der angegebenen BLZ.
     * @param blz die BLZ.
     * @return die Zeile aus der blz.properties
     * @deprecated Bitte {@link HBCIUtils#getBankInfo(String)} verwenden.
     */
    public static String getBLZData(String blz)
    {
        return blz!=null?blzs.getProperty(blz,"|||||"):"|||||";
    }

    /**
     * Liefert den n-ten Datensatz (beginnend bei 1) aus der Zeile.
     * @param st die Zeile.
     * @param idx der Index, beginnend bei 1.
     * @return der Wert oder Leerstring.
     * @deprecated Bitte {@link HBCIUtils#getBankInfo(String)} verwenden.
     */
    public static String getNthToken(String st,int idx)
    {
        String[] parts=st.split("\\|");
        String   ret=null;
        
        if ((idx-1)<parts.length) {
            ret=parts[idx-1];
        } else {
            ret="";
        }
        
        return ret;
    }
    
    /**
     * Liefert das Pruefziffern-Verfahren fuer diese Bank.
     * @param blz die BLZ.
     * @return das Pruefziffern-Verfahren fuer diese Bank.
     */
    public static String getAlgForBLZ(String blz)
    {
        BankInfo info = banks.get(blz);
        if (info == null)
            return "";
        return info.getChecksumMethod() != null ? info.getChecksumMethod() : "";
    }

    public static HBCICallback getCallback()
    {
        ThreadGroup group=Thread.currentThread().getThreadGroup();
        return callbacks.get(group);
    }
    
    public static String getLocMsg(String key)
    {
        ThreadGroup group=Thread.currentThread().getThreadGroup();
        try
        {
            return locMsgs.get(group).getString(key);
        }
        catch (MissingResourceException re)
        {
            // tolerieren wir
            HBCIUtils.log(re,HBCIUtils.LOG_ERR);
            return key;
        }
    }

    public static String getLocMsg(String key,Object o)
    {
        return HBCIUtilsInternal.getLocMsg(key,new Object[] {o});
    }

    public static String getLocMsg(String key,Object[] o)
    {
        return MessageFormat.format(getLocMsg(key),o);
    }

    public static boolean ignoreError(HBCIPassport passport,String paramName,String msg)
    {
        boolean ret=false;
        String  paramValue=HBCIUtils.getParam(paramName,"no");
        
        if (paramValue.equals("yes")) {
            HBCIUtils.log(msg,HBCIUtils.LOG_ERR);
            HBCIUtils.log("ignoring error because param "+paramName+"=yes",HBCIUtils.LOG_ERR);
            ret=true;
        } else if (paramValue.equals("callback")) {
            StringBuffer sb=new StringBuffer();
            getCallback().callback(passport,
                                   HBCICallback.HAVE_ERROR,
                                   msg,
                                   HBCICallback.TYPE_BOOLEAN,
                                   sb);
            if (sb.length()==0) {
                HBCIUtils.log(msg,HBCIUtils.LOG_ERR);
                HBCIUtils.log("ignoring error because param "+paramName+"=callback",HBCIUtils.LOG_ERR);
                ret=true;
            }
        }
        
        return ret;
    }

    public static long string2Long(String st,long factor)
    {
        BigDecimal result = new BigDecimal(st);
        result = result.multiply(new BigDecimal(factor));
        return result.longValue();
    }

    public static String withCounter(String st, int idx)
    {
        return st + ((idx!=0)?"_"+Integer.toString(idx+1):"");
    }
    
    public static String[] getNextRelativePathElem(String currentPath,String targetPath)
    {
        String[] ret=null;
        
        if (targetPath.startsWith(currentPath+".")) {
            ret=new String[2];
            
            String subPath=targetPath.substring(currentPath.length()+1);
            int    dotPosi=subPath.indexOf('.');
            if (dotPosi==-1) {
                dotPosi=subPath.length();
            }
            String nextPath=subPath.substring(0,dotPosi);
            
            String nextName=nextPath;
            int    underscorePosi=nextPath.lastIndexOf('_');
            if (underscorePosi!=-1) {
                nextName=nextPath.substring(0,underscorePosi);
            }
            
            ret[0]=nextName;
            ret[1]=nextPath;
        }
        
        return ret;
    }

    public static int getPosiOfNextDelimiter(String st, int posi)
    {
		int     len=st.length();
		boolean quoting=false;
		while (posi<len) {
			char ch=st.charAt(posi);
			
			if (!quoting) {
				if (ch=='?') {
					quoting=true;
				} else if (ch=='@') {
					int    endpos=st.indexOf('@',posi+1);
					String binlen_st=st.substring(posi+1,endpos);
					int    binlen=Integer.parseInt(binlen_st);
					posi+=binlen_st.length()+1+binlen;
				} else if (ch=='\'' || ch=='+' || ch==':') {
					// Ende gefunden
					break;
				}
			} else {
				quoting=false;
			}
			
			posi++;
		}
		
		return posi;
    }
    
    public static String ba2string(byte[] ba)
    {
        StringBuffer ret=new StringBuffer();
        
        for (int i=0;i<ba.length;i++) {
            int x=ba[i];
            if (x<0) {
                x+=256;
            }
            String st=Integer.toString(x,16);
            if (st.length()==1) {
                st="0"+st;
            }
            ret.append(st+" ");
        }
        
        return ret.toString();
        
    }
    
    public static String stripLeadingZeroes(String st)
    {
    	String ret=null;
    	
    	if (st!=null) {
    		int start=0;
    		int l=st.length();
    		while (start<l && st.charAt(start)=='0') {
    			start++;
    		}
    		ret=st.substring(start);
    	}
    	
    	return ret;
    }

    /**
     * Hilfs-methode um eine numerische property anhand einer Query zu finden.
     * @param properties Die Properties.
     * @param query Die Query, um die property zu finden.
     * @param useMinimum Ob im Falle von mehreren Werten das Minimum oder Maximum returned werden soll.
     * @return Die property als integer, oder null, falls nicht gefunden.
     */
    public static Integer getIntegerProperty(Properties properties, ParameterFinder.Query query, boolean useMinimum) {
        List<Integer> values = ParameterFinder.findAll(properties, query).values()
                .stream()
                .map(value -> {
                    try {
                        return Integer.parseInt(value.toString());
                    } catch (NumberFormatException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (values.isEmpty()) {
            return null;
        }
        if (useMinimum) {
            return values.stream().min(Integer::compareTo).orElse(null);
        } else {
            return values.stream().max(Integer::compareTo).orElse(null);
        }
    }
    
}
