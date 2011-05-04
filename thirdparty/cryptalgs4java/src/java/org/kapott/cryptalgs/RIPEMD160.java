
/*  $Id: RIPEMD160.java,v 1.1 2011/05/04 22:37:59 willuhn Exp $

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

import java.security.DigestException;
import java.security.MessageDigestSpi;

public final class RIPEMD160
     extends MessageDigestSpi
{
    private int   h0, h1, h2, h3, h4;
    private int[] X;
    private int   pos;
    private int   length;

    private final static int[] Ks = {0x00000000,
        0x5a827999,
        0x6ed9eba1,
        0x8f1bbcdc,
        0xa953fd4e};
    private final static int[] K2s = {0x50a28be6,
        0x5c4dd124,
        0x6d703ef3,
        0x7a6d76e9,
        0x00000000};

    private final static byte[] rs = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
        7, 4, 13, 1, 10, 6, 15, 3, 12, 0, 9, 5, 2, 14, 11, 8,
        3, 10, 14, 4, 9, 15, 8, 1, 2, 7, 0, 6, 13, 11, 5, 12,
        1, 9, 11, 10, 0, 8, 12, 4, 13, 3, 7, 15, 14, 5, 6, 2,
        4, 0, 5, 9, 7, 12, 2, 10, 14, 1, 3, 8, 11, 6, 15, 13};
    private final static byte[] r2s = {5, 14, 7, 0, 9, 2, 11, 4, 13, 6, 15, 8, 1, 10, 3, 12,
        6, 11, 3, 7, 0, 13, 5, 10, 14, 15, 8, 12, 4, 9, 1, 2,
        15, 5, 1, 3, 7, 14, 6, 9, 11, 8, 12, 2, 10, 0, 4, 13,
        8, 6, 4, 1, 3, 11, 15, 0, 5, 12, 2, 13, 9, 7, 10, 14,
        12, 15, 10, 4, 1, 5, 8, 7, 6, 2, 13, 14, 0, 3, 9, 11};

    private final static byte[] ss = {11, 14, 15, 12, 5, 8, 7, 9, 11, 13, 14, 15, 6, 7, 9, 8,
        7, 6, 8, 13, 11, 9, 7, 15, 7, 12, 15, 9, 11, 7, 13, 12,
        11, 13, 6, 7, 14, 9, 13, 15, 14, 8, 13, 6, 5, 12, 7, 5,
        11, 12, 14, 15, 14, 15, 9, 8, 9, 14, 5, 6, 8, 6, 5, 12,
        9, 15, 5, 11, 6, 8, 13, 12, 5, 12, 13, 14, 11, 8, 5, 6};
    private final static byte[] s2s = {8, 9, 9, 11, 13, 15, 15, 5, 7, 7, 8, 11, 14, 14, 12, 6,
        9, 13, 15, 7, 12, 8, 9, 11, 7, 7, 12, 7, 6, 15, 13, 11,
        9, 7, 15, 11, 8, 6, 6, 14, 12, 13, 5, 14, 13, 13, 7, 5,
        15, 5, 8, 11, 14, 14, 6, 14, 6, 9, 12, 9, 12, 5, 15, 8,
        8, 5, 12, 9, 12, 5, 14, 6, 8, 13, 6, 5, 15, 13, 11, 11};

    public RIPEMD160()
    {
        this.X = new int[16];
        engineReset();
    }

    @Override
    protected byte[] engineDigest()
    {
        long msgLength = ((long)this.length) << 3;

        // append "1" bit
        engineUpdate((byte)0x80);

        // if length of message (long value) does not fit at end of block
        if (this.pos + 8 > 64)
            // fill rest of block with "0" bits
            while (this.pos != 0) {
                engineUpdate((byte)0x00);
            }

        // fill rest of block up to one long before the end
        // with "0" bits
        while (this.pos + 8 < 64) {
            engineUpdate((byte)0x00);
        }

        for (int i = 0; i < 8; i++) {
            engineUpdate((byte)((msgLength >> (i << 3)) & 0xFF));
        }

        byte[] hashValue = new byte[engineGetDigestLength()];

        for (int i = 0; i < 4; i++) {
            hashValue[i] = (byte)((this.h0 >> (i << 3)) & 0xFF);
            hashValue[4 + i] = (byte)((this.h1 >> (i << 3)) & 0xFF);
            hashValue[8 + i] = (byte)((this.h2 >> (i << 3)) & 0xFF);
            hashValue[12 + i] = (byte)((this.h3 >> (i << 3)) & 0xFF);
            hashValue[16 + i] = (byte)((this.h4 >> (i << 3)) & 0xFF);
        }

        engineReset();

        return hashValue;
    }

    @Override
    protected int engineDigest(byte[] buf, int offset, int len)
        throws DigestException
    {
        if (len < engineGetDigestLength()) {
            throw new java.security.DigestException("buffer length too small to hold hash value");
        }

        byte[] hashValue = engineDigest();

        for (int i = 0; i < engineGetDigestLength(); i++) {
            buf[i + offset] = hashValue[i];
        }

        return engineGetDigestLength();
    }

    @Override
    protected int engineGetDigestLength()
    {
        return 20;
    }

    @Override
    protected void engineReset()
    {
        this.h0 = 0x67452301;
        this.h1 = 0xefcdab89;
        this.h2 = 0x98badcfe;
        this.h3 = 0x10325476;
        this.h4 = 0xc3d2e1f0;

        java.util.Arrays.fill(this.X, 0);

        this.pos = 0;
        this.length = 0;
    }

    @Override
    protected void engineUpdate(byte input)
    {
        this.X[this.pos >> 2] ^= (input & 0xFF) << ((this.pos & 3) << 3);
        this.length++;
        if (++this.pos == 64) {
            hashit();
            java.util.Arrays.fill(this.X, 0);
            this.pos = 0;
        }
    }

    @Override
    protected void engineUpdate(byte[] input, int offset, int len)
    {
        for (int i = offset; i < offset + len; i++) {
            engineUpdate(input[i]);
        }
    }

    private void hashit()
    {
        int A;
        int B;
        int C;
        int D;
        int E;
        int A2;
        int B2;
        int C2;
        int D2;
        int E2;
        int T;

        A2 = A = this.h0;
        B2 = B = this.h1;
        C2 = C = this.h2;
        D2 = D = this.h3;
        E2 = E = this.h4;

        for (byte j = 0; j < 80; j++) {
            T = roll(A + f(j, B, C, D) + this.X[r(j)] + K(j), s(j)) + E;
            A = E;
            E = D;
            D = roll(C, (byte)10);
            C = B;
            B = T;

            T = roll(A2 + f((byte)(79 - j), B2, C2, D2) + this.X[r2(j)] + K2(j), s2(j)) + E2;
            A2 = E2;
            E2 = D2;
            D2 = roll(C2, (byte)10);
            C2 = B2;
            B2 = T;
        }

        T = this.h1 + C + D2;
        this.h1 = this.h2 + D + E2;
        this.h2 = this.h3 + E + A2;
        this.h3 = this.h4 + A + B2;
        this.h4 = this.h0 + B + C2;
        this.h0 = T;
    }

    private int f(byte j, int x, int y, int z)
    {
        int ret;
        
        if (j >= 0 && j <= 15)
            ret=(x ^ y ^ z);
        else if (j >= 16 && j <= 31)
            ret=((x & y) | (~x & z));
        else if (j >= 32 && j <= 47)
            ret=((x | ~y) ^ z);
        else if (j >= 48 && j <= 63)
            ret=((x & z) | (y & ~z));
        else if (j >= 64 && j <= 79)
            ret=(x ^ (y | ~z));
        else
            ret=-1;
        
        return ret;
    }

    private int K(byte j)
    {
        return Ks[j >> 4];
    }

    private int K2(byte j)
    {
        return K2s[j >> 4];
    }

    private byte r(byte j)
    {
        return rs[j];
    }

    private byte r2(byte j)
    {
        return r2s[j];
    }

    private byte s(byte j)
    {
        return ss[j];
    }

    private byte s2(byte j)
    {
        return s2s[j];
    }

    private int roll(int x, byte num)
    {
        return ((x << num) | (x >>> (32 - num)));
    }
}
