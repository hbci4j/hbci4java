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

package org.hbci4java;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kapott.hbci.tools.StringUtil;

/**
 * Eine HBCI-Session innerhalb der neuen API.
 */
public class HBCI4JavaConfig
{
  private Map<String,String> props = new HashMap<>();
  
  /**
   * ct.
   */
  public HBCI4JavaConfig()
  {
    this((Map)null);
  }

  /**
   * ct.
   * @param props die Konfigurationsdaten.
   */
  public HBCI4JavaConfig(Properties props)
  {
    if (props != null)
    {
      for (String name:props.stringPropertyNames())
      {
        this.props.put(name, props.getProperty(name));
      }
    }
    
    this.initConfig();
  }

  /**
   * ct.
   * @param props die Konfigurationsdaten.
   */
  public HBCI4JavaConfig(Map<String,String> props)
  {
    if (props != null)
      this.props.putAll(props);
    
    this.initConfig();
  }
  
  /**
   * Liefert den Wert des Parameters als String.
   * @param name der Name des Parameters.
   * @return der Wert.
   */
  public String getString(String name)
  {
    return this.getString(name,null);
  }

  /**
   * Liefert den Wert des Parameters als String.
   * @param name der Name des Parameters.
   * @param def der Default-Wert.
   * @return der Wert.
   */
  public String getString(String name, String def)
  {
    return this.props.getOrDefault(name,def);
  }
  
  /**
   * Liefert den Wert des Parameters als Integer.
   * @param name der Name des Parameters.
   * @return der Wert.
   */
  public Integer getInteger(String name)
  {
    return this.getInteger(name,null);
  }
  
  /**
   * Liefert den Wert des Parameters als Integer.
   * @param name der Name des Parameters.
   * @param def der Default-Wert.
   * @return der Wert.
   */
  public Integer getInteger(String name, Integer def)
  {
    final String s = this.getString(name,def != null ? def.toString() : null);
    if (!StringUtil.hasText(s))
      return null;
    
    try
    {
      return Integer.parseInt(s);
    }
    catch (Exception e)
    {
      return def;
    }
  }
  
  /**
   * Liefert die Konfiguration als Properties.
   * @return die Konfiguration als Properties.
   */
  public Properties getProperties()
  {
    final Properties result = new Properties();
    result.putAll(this.props);
    return result;
  }
  
  /**
   * Leert die Konfiguration.
   */
  public void clear()
  {
    this.props.clear();
  }
  
  /**
   * Speichert einen Wert in der Config.
   * @param name der Name.
   * @param value der Wert.
   */
  public void setString(String name, String value)
  {
    this.props.put(name,value);
  }
  
  /**
   * Definiert für einige Parameter Default-Werte.
   */
  private void initConfig()
  {
    if (this.getString("kernel.rewriter") == null)
      this.setString("kernel.rewriter","InvalidSegment,WrongStatusSegOrder,WrongSequenceNumbers,MissingMsgRef,HBCIVersion,SigIdLeadingZero,InvalidSuppHBCIVersion,SecTypeTAN,KUmsDelimiters,KUmsEmptyBDateSets");
  }

  /**
   * Erzeugt eine Default-Konfiguration, wenn keine angegeben ist.
   * @return die Default-Konfiguration.
   */
  public static HBCI4JavaConfig createDefault()
  {
    return new HBCI4JavaConfig();
  }
}
