/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci4java.swift;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.swift.Swift;

/**
 * Testet das Parsen von kaputten MT940-Strings, die etwa so aussehen:
 * 
 * Test 1: Korrekter Aufbau
 * 
 * Test 2: CRLF-
 * :60M:C140106EUR1,00
 * -:61:1401060106CR1,00N062NONREF
 *
 * Das "-" am Beginn der zweiten Zeile ist falsch. Einige Banken
 * senden sowas aber. Irgendwie muessen wir das tolerieren.
 * 
 * Test 3: CRLF-CRLF (Bank: Kreissparkasse Grafschaft Bentheim zu Nordhorn)
 * :60M:C140106EUR1,00
 * -
 * :61:1401060106CR1,00N062NONREF
 * 
 * Das "-" in der zweiten Zeile ist ebenfalls falsch
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
    
    /**
     * Ungueltiger Aufbau.
     * Muss aber trotzdem korrekt geparst werden.
     * @throws Exception
     */
    @Test
    public void test003() throws Exception
    {
        String st = "\r\n:60M:C140106EUR1,00\r\n-\r\n:61:1401060106CR5,00N062NONREF";
        String value = Swift.getTagValue(st,"60M",0);
        Assert.assertEquals("C140106EUR1,00",value);
    }
    
    /**
     * Fehlendes "-" auf der letzten Zeile.
     * Muss aber trotzdem korrekt geparst werden.
     * @throws Exception
     */
    @Test
    public void test004() throws Exception
    {
        String st = "\r\n:62F:C150626EUR91,32\r\n";
        String value = Swift.getTagValue(st,"62F",0);
        Assert.assertEquals("C150626EUR91,32",value);
    }

    /**
     * Noch ein Zeilenumbruch nach der letzten Zeile.
     * @throws Exception
     */
    @Test
    public void test005() throws Exception
    {
        String st = "\r\n:62F:C150626EUR91,32\r\n-\r\n";
        String value = Swift.getTagValue(st,"62F",0);
        Assert.assertEquals("C150626EUR91,32",value);
    }

    /**
     * Kein "-", dafuer aber zwei Leerzeilen am Ende.
     * @throws Exception
     */
    @Test
    public void test006() throws Exception
    {
        String st = "\r\n:62F:C150626EUR91,32\r\n\r\n";
        String value = Swift.getTagValue(st,"62F",0);
        Assert.assertEquals("C150626EUR91,32",value);
    }

    /**
     * Linux-Zeilenumbruch am Ende.
     * @throws Exception
     */
    @Test
    public void test007() throws Exception
    {
        String st = "\r\n:62F:C150626EUR91,32\n";
        String value = Swift.getTagValue(st,"62F",0);
        Assert.assertEquals("C150626EUR91,32",value);
    }
}
