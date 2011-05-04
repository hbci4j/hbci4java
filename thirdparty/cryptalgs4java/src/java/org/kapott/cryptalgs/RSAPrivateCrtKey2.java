
/*  $Id: RSAPrivateCrtKey2.java,v 1.1 2011/05/04 22:37:58 willuhn Exp $

    This file is part of CryptAlgs4Java
    Copyright (C) 2001-2010  Stefan Palme

    CryptAlgs4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    CryptAlgs4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.cryptalgs;

import java.math.BigInteger;
import java.security.PrivateKey;

public class RSAPrivateCrtKey2
    implements PrivateKey
{
    private final static long serialVersionUID=1;
    
    private BigInteger p;
    private BigInteger q;
    private BigInteger dP;
    private BigInteger dQ;
    private BigInteger qInv;
    private BigInteger Ap;
    private BigInteger Aq;
    
    public RSAPrivateCrtKey2(BigInteger p,BigInteger q,BigInteger dP,BigInteger dQ,BigInteger qInv)
    {
        this.p=p;
        this.q=q;
        this.dP=dP;
        this.dQ=dQ;
        this.qInv=qInv;
    }
    
    public BigInteger getP()
    {
        return this.p;
    }

    public BigInteger getQ()
    {
        return this.q;
    }

    public BigInteger getdP()
    {
        return this.dP;
    }

    public BigInteger getdQ()
    {
        return this.dQ;
    }

    public BigInteger getQInv()
    {
        return this.qInv;
    }

    public byte[] getEncoded()
    {
        return null;
    }
    
    public String getAlgorithm()
    {
        return "RSA";
    }
    
    public String getFormat()
    {
        return null;
    }
    
    public void setAp(BigInteger ap)
    {
        this.Ap=ap;
    }
    
    public BigInteger getAp()
    {
        return this.Ap;
    }

    public void setAq(BigInteger aq)
    {
        this.Aq=aq;
    }
    
    public BigInteger getAq()
    {
        return this.Aq;
    }
}
