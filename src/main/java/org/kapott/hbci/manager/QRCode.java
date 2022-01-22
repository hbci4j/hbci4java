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

package org.kapott.hbci.manager;

import org.kapott.hbci.comm.Comm;

/**
 * Klasse zum Parsen von QR-Codes.
 */
public class QRCode
{
    private String mimetype = null;
    private String text = null;
    private byte[] image = null;
    
    /**
     * Versucht die Daten als QR-Code zu parsen.
     * @param hhd der HHDuc.
     * @param msg die Nachricht.
     * @return der QR-Code oder NULL.
     */
    public static QRCode tryParse(String hhd, String msg)
    {
        try
        {
            return new QRCode(hhd,msg);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     * ct.
     * @param hhd die Rohdaten aus dem HHDuc als String.
     * @param msg Die Sparkassen verwenden QR-Code in HHD 1.3 und uebertragen dort (wie beim Flickercode auch) die
     * maschinenlesbaren Daten direkt in der Text-Nachricht per Base64-Codierung in den Tags CHLGUC und CHLGTEXT.
     * Wir brauchen daher auch den eigentlich dem User anzuzeigenden Text, um den Code bei Bedarf dort zu extrahieren.
     * @throws Exception wenn die Daten nicht als Bild geparst werden konnten.
     */
    public QRCode(String hhd, String msg) throws Exception
    {
        byte[] data = hhd != null && hhd.length() > 0 ? hhd.getBytes(Comm.ENCODING) : null;

        // Ich weiss nicht, ob es ueberhaupt Banken gibt, die den QR-Code in dieser
        // Form senden - also identisch zu Photo-TAN. Es ist aber anzunehmen, dass das bei
        // HHD 1.4 so ist. Die Sparkassen embedden den QR-Code jedenfalls als Base64-codiertes PNG direkt in den Text.
        // Scherz am Rand: Die Sparkassen senden in "data" tatsaechlich: [0x6e,0x75,0x6c,0x6c] -> "null" ;)             
        if (data != null && data.length > 100) // unter 100 Bytes kann es nichts sinnvolles sein.
        {
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
            
            this.text = msg;
            return;
        }

        // Ggf. vorhandene Whitespaces entfernen
        if (msg != null)
            msg = msg.trim();

        if (msg == null || msg.length() == 0)
            throw new Exception("invalid QR code");

        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Embedded Grafik extrahieren

        {
            String code = msg;
            
            code = code.replaceAll("[\\n\\t\\r ]",""); // Alle Leerzeichen und Whitespaces entfernen

            // Positionen von Start- und End-Tag ermitteln
            int t1Start = code.indexOf("CHLGUC");
            int t2Start = code.indexOf("CHLGTEXT");
            if (t1Start == -1 || t2Start == -1 || t2Start <= t1Start)
                throw new Exception("invalid QR code");

            // Erstmal den 2. Token abschneiden
            code = code.substring(0,t2Start);
            
            // Dann alles abschneiden bis zum Beginn von "CHLGUC"
            code = code.substring(t1Start);

            // Wir haben eigentlich nicht nur "CHLGUC", sondern "CHLGUCXXXX"
            // Wobei die 4 Zahlen die Laenge des Codes angeben. Wir schneiden einfach alles ab.
            code = code.substring(10);
            
            this.image = HBCIUtils.decodeBase64(code);
            
            // Convenience-Funktion: Wenn der Byte-Strom mit "0x89PNG" beginnt, setzen wir den Mimetyp manuell
            if (this.image.length > 4 &&
                (this.image[0] & 0xFF) == 0x89 && // PNG-Version
                (this.image[1] & 0xFF) == 0x50 && // "P"
                (this.image[2] & 0xFF) == 0x4E && // "N"
                (this.image[3] & 0xFF) == 0x47) // G"
            {
                this.mimetype = "image/png";
            }
        }
        //
        ///////////////////////////////////////////////////////////////////////////////////////////////
        
        ///////////////////////////////////////////////////////////////////////////////////////////////
        // Den anzuzeigenden Text extrahieren

        int t1Start = msg.indexOf("CHLGTEXT");
        
        // Wir haben eigentlich nicht nur "CHLGTEXT", sondern "CHLGTEXTXXXX"
        // Wobei die 4 Zahlen die Laenge des Textes angeben. Wir schneiden einfach alles bis dahin ab.
        this.text = msg.substring(t1Start+12);
        //
        ///////////////////////////////////////////////////////////////////////////////////////////////

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
    public byte[] getImage()
    {
        return image;
    }

    /**
     * Liefert den Mimetype des Bildes.
     * @return mimetype Kann durchaus NULL sein.
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * Liefert den fuer den User bestimmten Text. Falls die Bank den QR-Code dort per CHLGUC/CHLGTEXT embedded hat,
     * dann wird hier der bereinigte Text zurueckgeliefert.
     * @return der ggf. bereinigte Text.
     */
    public String getMessage()
    {
        return this.text;
    }
}


