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

package org.kapott.hbci.passport;

import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.security.Crypt;
import org.kapott.hbci.security.Sig;

public abstract class AbstractDDVPassport 
    extends AbstractHBCIPassport implements HBCIPassportChipcard
{
    protected AbstractDDVPassport(Object init)
    {
        super(init);
    }

    public String getPassportTypeName()
    {
        return "DDV";
    }
    
    public Comm getCommInstance()
    {
        return Comm.getInstance("Standard",this);
    }
    
    public String getProfileMethod()
    {
        return "DDV";
    }
    
    public String getProfileVersion()
    {
        return "1";
    }

    public String getSysStatus()
    {
        return "0";
    }

    public boolean needInstKeys()
    {
        return false;
    }

    public boolean needUserKeys()
    {
        return false;
    }
    
    public boolean needUserSig()
    {
        return false;
    }
    
    public boolean hasInstSigKey()
    {
        return true;
    }
    
    public boolean hasInstEncKey()
    {
        return true;
    }
    
    public boolean hasMySigKey()
    {
        return true;
    }
    
    public boolean hasMyEncKey()
    {
        return true;
    }
    
    public String getCryptKeyType()
    {
        return Crypt.ENC_KEYTYPE_DDV;
    }

    public String getSigFunction()
    {
        return Sig.SECFUNC_HBCI_SIG_DDV;
    }

    public String getSigAlg()
    {
        return Sig.SIGALG_DES;
    }

    public String getSigMode()
    {
        return Sig.SIGMODE_RETAIL_MAC;
    }

    public String getCryptFunction()
    {
        return Crypt.SECFUNC_ENC;
    }

    public String getCryptAlg()
    {
        return Crypt.ENCALG_2K3DES;
    }

    public String getCryptMode()
    {
        return Crypt.ENCMODE_CBC;
    }

    public String getHashAlg()
    {
        return Sig.HASHALG_RIPEMD160;
    }

}
