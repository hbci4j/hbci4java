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

import java.io.Serializable;
import java.security.Key;

/**
 * Die Klasse gibt es aus Gründen der Abwärtskompatibilität doppelt, da Instanzen
 * dieses Objekts serialisiert werden, wenn ein Passport gespeichert wird.
 * Wir könnten sonst keine Passports mehr laden, die vor der Package-Umbenennung erstellt wurden.
 */
@Deprecated
public final class HBCIKey implements Serializable
{
    private static final long serialVersionUID =1L;
    
    /** Ländercode des Schlüsselbesitzers */
    public String country;
    /** Bankleitzahl des Schlüsselbesitzers */
    public String blz;
    /** Nutzerkennung des Schlüsselbesitzers. Wenn der Schlüssel
        einem "richtigen" Nutzer gehört, so wird hier seine HBCI-Userkennung eingestellt;
        gehört der Schlüssel der Bank, so steht hier eine bankinterne
        ID (u.U. die Bankleitzahl o.ä.) */
    public String userid;
    /** Schlüsselnummer */
    public String num;
    /** Schlüsselversion */
    public String version;
    /** kryptographische Schlüsseldaten (kann <code>null</code> sein)*/
    public Key key;

    /** Neues <code>HBCIKey</code>-Objekt erzeugen */
    public HBCIKey()
    {
        // empty constructor
    }

    public HBCIKey(String country, String blz, String userid, String num, String version, Key key)
    {
        this.country = country;
        this.blz = blz;
        this.userid = userid;
        this.num = num;
        this.version = version;
        this.key = key;
    }
    
    @Override
    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        
        ret.append("country="+this.country);
        ret.append(", blz="+this.blz);
        ret.append(", userid="+this.userid);
        ret.append(", num="+this.num);
        ret.append(", version="+this.version);
        ret.append(", key="+this.key);
        
        return ret.toString();
    }
    
    /**
     * Migriert den Schlüssel in das neue Format.
     * @return der Schlüssel im neuen Format.
     */
    public org.hbci4java.hbci.manager.HBCIKey migrate()
    {
      final org.hbci4java.hbci.manager.HBCIKey result = new org.hbci4java.hbci.manager.HBCIKey();
      result.blz = this.blz;
      result.country = this.country;
      result.key = this.key;
      result.num = this.num;
      result.userid = this.userid;
      result.version = this.version;
      
      return result;
    }
}
