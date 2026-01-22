/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) Olaf Willuhn
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

package org.hbci4java.hbci.manager;

import org.hbci4java.hbci.comm.Comm;

/**
 * Klasse zum Parsen von Matrix-Codes.
 */
public class MatrixCode
{
    private String mimetype = null;
    private byte[] image = null;
    
    /**
     * Versucht den Text als Matrix-Code zu parsen.
     * @param data die zu parsenden Daten.
     * @return der Matrix-Code, wenn er lesbar war, sonst NULL.
     */
    public static MatrixCode tryParse(String data)
    {
        try
        {
            return new MatrixCode(data);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     * ct.
     * @param data die Rohdaten aus dem HHDuc als String.
     * @throws Exception wenn die Daten nicht als Bild geparst werden konnten.
     */
    public MatrixCode(String data) throws Exception
    {
        this(data != null ? data.getBytes(Comm.ENCODING) : null);
    }
    
    /**
     * ct.
     * @param data die Rohdaten aus dem HHDuc als Byte-Array.
     * @throws Exception wenn die Daten nicht als Bild geparst werden konnten.
     */
    public MatrixCode(byte[] data) throws Exception
    {
        if (data == null || data.length < 100)
            throw new Exception("invalid matrix code");

        int offset = 0;
        // Mime-Type
        {
            byte[] b = new byte[2];
            System.arraycopy(data,offset,b,0,2);
            
            int len = Integer.parseInt(this.decode(b));
            b = new byte[len];
            offset += 2;
            
            System.arraycopy(data,offset,b,0,len);
            this.mimetype = new String(b,Comm.ENCODING);
            offset += len;
        }
        
        // Bild-Daten
        {
            // Die Groesse brauchen wir hier nicht ermitteln,
            // weil das Bild ja genau bis zum Ende des Byte-Array geht
            offset +=2;
            int len = data.length -  offset;
            byte[] b = new byte[len];
            
            System.arraycopy(data,offset,b,0,len);
            this.image = b;
        }
    }
    
    /**
     * Decodiert die Bytes als String.
     * @param bytes die Bytes.
     * @return der String.
     */
    private String decode(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<bytes.length;++i)
        {
            sb.append(Integer.toString(bytes[i],10));
        }
        return sb.toString();
    }
    
    /**
     * Liefert die Rohdaten des Bildes.
     * @return image die Rohdaten des Bildes.
     */
    public byte[] getImage() {
        return image;
    }
    
    /**
     * Liefert den Mimetype des Bildes.
     * @return mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

}


