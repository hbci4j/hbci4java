/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci4java.tools;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.tools.ParameterFinder;
import org.kapott.hbci.tools.ParameterFinder.Query;
import org.kapott.hbci4java.AbstractTest;

/**
 * Tests fuer den Parameter-Finder.
 */
public class TestParameterFinder extends AbstractTest
{
    /**
     * @throws Exception
     */
    @Test
    public void test001() throws Exception
    {
        Properties props = new Properties();
        props.put("Params_1.TAN2StepParams1.ParTAN2Step4.TAN2StepParams2.secfunc", "a");
        props.put("Params_2.TAN2StepParamsFoo.ParTAN2Step1.TAN2StepParams1.secfunc", "b");
        props.put("Params_2.TAN2StepParamsFoo.ParTAN2Step.TAN2StepParams.2secfunc", "c");

        props.put("Params.TAN2StepParams1.ParTAN2Step.TAN2StepParams.2secfunc", "d1");
        props.put("Params.TAN2StepParams1.ParTAN2Step.TAN2StepParams.3secfunc", "d2");
        props.put("Params_1.TAN2StepParams1.ParTAN2Step.TAN2StepParams.foo", "e");
        props.put("Params_1.TAN2StepParams1.ParTAN2Step.secfunc", "f");

        Properties result = ParameterFinder.find(props, "Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams*.*secfunc");
        Assert.assertTrue(result.containsKey("secfunc"));
        Assert.assertTrue(result.containsKey("2secfunc"));
        Assert.assertFalse(result.containsKey("3secfunc"));
        Assert.assertFalse(result.containsKey("foo"));
        
        String v = result.getProperty("secfunc");
        Assert.assertTrue(v != null && (v.equals("a") || v.equals("b")));
        
        v = result.getProperty("2secfunc");
        Assert.assertTrue(v != null && v.equals("c"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void test002() throws Exception
    {
        Properties props = new Properties();
        props.put("Params_1.TAN2StepParams1.ParTAN2Step4.TAN2StepParams2.secfunc", "a");
        props.put("Params_2.TAN2StepParamsFoo.ParTAN2Step1.TAN2StepParams1.secfunc", "b");
        props.put("Params_2.TAN2StepParamsFoo.ParTAN2Step.TAN2StepParams.2secfunc", "c");

        props.put("Params.TAN2StepParams1.ParTAN2Step.TAN2StepParams.2secfunc", "d");
        props.put("Params_1.TAN2StepParams1.ParTAN2Step.TAN2StepParams.foo", "e");
        props.put("Params_1.TAN2StepParams1.ParTAN2Step.secfunc", "f");

        Properties result = ParameterFinder.findAll(props, "Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams*.*secfunc");
        Assert.assertTrue(result.containsValue("a"));
        Assert.assertTrue(result.containsValue("b"));
        Assert.assertTrue(result.containsValue("c"));
        Assert.assertFalse(result.containsValue("d"));
        Assert.assertFalse(result.containsValue("e"));
        Assert.assertFalse(result.containsValue("f"));
    }

    /**
     * @throws Exception
     */
    @Test
    public void test003() throws Exception
    {
        Properties props = new Properties();
        props.put("Params_160.TAN2StepPar1.ParTAN2Step.can1step", "N");
        props.put("Params_2.TAN2StepParamsFoo.ParTAN2Step.TAN2StepParams.can1step", "X");
        props.put("Params_161.TAN2StepPar3.ParTAN2Step.can1step", "J");
        
        Properties result = ParameterFinder.findAll(props, Query.BPD_PINTAN_CAN1STEP);
        Assert.assertTrue(result.containsValue("J"));
        Assert.assertTrue(result.containsValue("N"));
        Assert.assertFalse(result.containsValue("X"));
    }
    
    /**
     * @throws Exception
     */
    @Test
    public void test004() throws Exception
    {
        Properties props = new Properties();
        props.put("Params_160.TAN2StepPar1.ParTAN2Step.orderhashmode", "0");
        props.put("Params_161.TAN2StepPar3.ParTAN2Step.orderhashmode", "1");
        props.put("Params_162.TAN2StepPar6.ParTAN2Step.orderhashmode", "2");

        String s = ParameterFinder.getValue(props,Query.BPD_PINTAN_ORDERHASHMODE.withParameters("6"),null);
        Assert.assertEquals("2",s);
    }
    
    
    /**
     * @throws Exception
     */
    @Test(expected = HBCI_Exception.class)
    public void test005() throws Exception
    {
        Properties props = new Properties();
        ParameterFinder.getValue(props,Query.BPD_PINTAN_ORDERHASHMODE,null);
    }
}
