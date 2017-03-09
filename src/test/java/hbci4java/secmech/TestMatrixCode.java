/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package hbci4java.secmech;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.manager.HHDVersion;
import org.kapott.hbci.manager.MatrixCode;

import hbci4java.AbstractTest;

/**
 * Testet den Parser fuer die Matrix-Codes.
 */
public class TestMatrixCode extends AbstractTest
{
    /**
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        byte[] data = this.getBytes("secmech/TestMatrixCode-001.txt");
        MatrixCode code = new MatrixCode(data);
        Assert.assertEquals("Mime-Type falsch","image/png",code.getMimetype());
        Assert.assertEquals("Bild-Groesse falsch",4556,code.getImage().length);
    }

    /**
     * @throws Exception
     */
    @Test
    public void test002() throws Exception
    {
        byte[] data = this.getBytes("secmech/TestMatrixCode-002.txt");
        MatrixCode code = new MatrixCode(data);
        Assert.assertEquals("Mime-Type falsch","image/png",code.getMimetype());
        Assert.assertEquals("Bild-Groesse falsch",4980,code.getImage().length);
    }

    /**
     * @throws Exception
     */
    @Test(expected=Exception.class)
    public void test003() throws Exception
    {
        new MatrixCode((byte[]) null);
    }

    /**
     * @throws Exception
     */
    @Test(expected=Exception.class)
    public void test004() throws Exception
    {
        new MatrixCode("zu kurz");
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void test005() throws Exception
    {
        // Testet die Erkennung des Matrix-Code-Verfahrens aus dem Secmech.
        Properties props = new Properties();
        props.put("id","MS1.0.0");
        props.put("segversion","5");
        
        HHDVersion version = HHDVersion.find(props);
        Assert.assertEquals("Matrix-Code-Verfahren nicht erkannt",HHDVersion.MS_1,version);
    }

}


