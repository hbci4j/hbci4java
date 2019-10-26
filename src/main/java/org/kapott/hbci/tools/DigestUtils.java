/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
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

package org.kapott.hbci.tools;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;

import org.kapott.cryptalgs.CryptAlgs4JavaProvider;
import org.kapott.hbci.comm.Comm;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Hilfsklasse zum Erzeugen von Hashes.
 */
public class DigestUtils
{
    /**
     * Hash-Algorithmus RIPEMD160.
     */
    public final static String ALG_RIPE_MD160 = "RIPEMD160";
    
    /**
     * Hash-Algorithmus SHA1.
     */
    public final static String ALG_SHA1 = "SHA-1";
    
    /**
     * Die Liste der Algorithmen, bei denen der eigene Provider verwendet werden soll.
     */
    private final static List<String> OWN_PROVIDER = Arrays.asList(ALG_RIPE_MD160);

    /**
     * Hasht die Daten.
     * @param data die zu hashenden Daten.
     * @param alg der zu verwendende Algorithmus.
     * @return der Hash.
     * @throws HBCI_Exception
     */
    public final static String hash(String data, String alg) throws HBCI_Exception
    {
        try
        {
            byte[] hash = hash(data.getBytes(Comm.ENCODING),alg);
            return new String(hash,Comm.ENCODING);
        }
        catch (HBCI_Exception e)
        {
            throw e;
        }
        catch (UnsupportedEncodingException e2)
        {
            throw new HBCI_Exception(e2);
        }
    }
    
    /**
     * Hasht die Daten.
     * @param data die zu hashenden Daten.
     * @param alg der zu verwendende Algorithmus.
     * @return der Hash.
     * @throws HBCI_Exception
     */
    public final static byte[] hash(byte[] data, String alg) throws HBCI_Exception
    {
        try
        {
            final String provider = OWN_PROVIDER.contains(alg) ? CryptAlgs4JavaProvider.NAME : null;
            HBCIUtils.log("using " + alg + "/" + provider + " for generating hash of " + data.length + " bytes", HBCIUtils.LOG_DEBUG);
            MessageDigest digest = MessageDigest.getInstance(alg,provider);
            digest.update(data);
            return digest.digest();
        }
        catch (HBCI_Exception he)
        {
            throw he;
        }
        catch (Exception e)
        {
            throw new HBCI_Exception(e);
        }
    }
}


