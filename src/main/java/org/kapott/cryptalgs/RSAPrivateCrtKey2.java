/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2010 Stefan Palme
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
