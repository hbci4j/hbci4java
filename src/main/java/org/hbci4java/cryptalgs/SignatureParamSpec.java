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

package org.hbci4java.cryptalgs;

import java.security.spec.AlgorithmParameterSpec;

public final class SignatureParamSpec
     implements AlgorithmParameterSpec
{
    private String hashAlg;
    private String provider;

    public SignatureParamSpec(String hashAlg,String provider)
    {
        this.hashAlg = hashAlg;
        this.provider = provider;
    }

    public String getHashAlg()
    {
        return this.hashAlg;
    }

    public String getProvider()
    {
        return this.provider;
    }

    @Override
    public String toString()
    {
        return getProvider()+":"+getHashAlg();
    }
}
