/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.swift;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.swift.Swift;

/**
 * Testet das Parsen von kaputten MT940-Strings, die etwa so aussehen:
 * 
 * :60M:C140106EUR1,00
 * -:61:1401060106CR1,00N062NONREF
 * 
 * Das "-" am Beginn der zweiten Zeile ist falsch. Einige Banken
 * senden sowas aber. Irgendwie muessen wir das tolerieren.
 * 
 */
public class TestBrokenMT940
{
    /**
     * Korrekter Aufbau.
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        String st = "\r\n:60M:C140106EUR1,00\r\n:61:1401060106CR5,00N062NONREF";
        String value = Swift.getTagValue(st,"60M",0);
        Assert.assertEquals("C140106EUR1,00",value);
    }
    
    
    /**
     * Ungueltiger Aufbau.
     * Muss aber trotzdem korrekt geparst werden.
     * @throws Exception
     */
    @Test
    public void test002() throws Exception
    {
        String st = "\r\n:60M:C140106EUR1,00\r\n-:61:1401060106CR5,00N062NONREF";
        String value = Swift.getTagValue(st,"60M",0);
        Assert.assertEquals("C140106EUR1,00",value);
    }
    
}
