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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

// TODO: doku fehlt
public class LogFilter 
{
    public static final int FILTER_NONE=0;
    public static final int FILTER_SECRETS=1;
    public static final int FILTER_IDS=2;
    public static final int FILTER_MOST=3;

	private static LogFilter _instance;
	
	private Map<Integer,List<String[]>> secretDataByLevel;
	
	public static synchronized LogFilter getInstance()
	{
		if (_instance==null) {
			_instance=new LogFilter();
		}
		return _instance;
	}
	
	private LogFilter()
	{
		this.secretDataByLevel = new Hashtable<Integer, List<String[]>>();
	}
	
	public synchronized void clearSecretData()
	{
		this.secretDataByLevel.clear();
	}
	
	public synchronized void addSecretData(String secret, String replacement, int level)
	{
	    if (secret!=null && secret.length()!=0) {
	        // liste der secrets im gewählten level holen bzw. erzeugen
	        List<String[]> secretData= secretDataByLevel.get(Integer.valueOf(level));
	        if (secretData==null) {
	            secretData=new ArrayList<String[]>();
	            secretDataByLevel.put(Integer.valueOf(level),secretData);
	        }

	        // duplikats-check für "secret"
	        boolean found=false;
	        for (Iterator<String[]> i=secretData.iterator();i.hasNext();) {
	            String[] entry= i.next();
	            if (entry[0].equals(secret)) {
	                found=true;
	                break;
	            }
	        }

	        if (!found) {
	            // secret noch nicht in filterliste

	            if (replacement==null || replacement.length()<2) {
	                // wenn der replacement-string kein vollständiger String ist,
	                // diesen mit einem Filler auf die länge des secrets bringen 
	                char filler;

	                if (replacement!=null && replacement.length()==1) {
	                    // der replacement-string besteht aus genau einem zeichen,
	                    // also dieses zeichen als filler verwenden
	                    filler=replacement.charAt(0);
	                } else {
	                    // ansonsten default-filler "X" verwenden
	                    filler='X';
	                }

	                char[] ca=new char[secret.length()];
	                Arrays.fill(ca,filler);
	                replacement=new String(ca);
	            }

	            secretData.add(new String[] {secret,replacement});
	        }
	    }
	}
	
	public synchronized String filterLine(String line, int filterLevel)
	{
		String ret=null;
		if (line!=null) {
			StringBuffer line2=new StringBuffer(line);

			for (int level=filterLevel; level>0; level--) {
				List<String[]> secretData= secretDataByLevel.get(Integer.valueOf(level));
				if (secretData==null) {
					continue;
				}
				
				for (Iterator<String[]> i = secretData.iterator(); i.hasNext();) {
					String[] entry = i.next();

					String secret=entry[0];
					String replacement=entry[1];

					if (secret.isEmpty()) {
						continue;
					}

					int posi=0;
					while ((posi=line2.indexOf(secret,posi))!=-1) {
						line2.replace(posi,posi+secret.length(),replacement);
						posi+=replacement.length();
					}
				}
			}
		
			ret=line2.toString();
		}
		return ret;
	}
}
