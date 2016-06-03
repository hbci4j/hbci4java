/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.manager;

import org.kapott.hbci.comm.Comm;

/**
 * Klasse zum Parsen von Matrix-Codes.
 */
public class MatrixCode
{
    private String mimetype = null;
    private byte[] image = null;
    
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


