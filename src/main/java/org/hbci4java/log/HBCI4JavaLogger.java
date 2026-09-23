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

package org.hbci4java.log;

import org.kapott.hbci.manager.HBCIUtils;

/**
 * Interface für einen Logger für HBCI4Java.
 */
public interface HBCI4JavaLogger
{
  /**
   * Die Log-Level.
   */
  public enum Level
  {
    /**
     * Loglevel für keine Ausgaben
     **/
    NONE(HBCIUtils.LOG_NONE,null),
    
    /**
     * Loglevel für Fehlerausgaben
     **/
    ERROR(HBCIUtils.LOG_ERR,"ERR"),
    
    /**
     * Loglevel für Warnungen
     **/
    WARN(HBCIUtils.LOG_WARN,"WRN"),
    
    /**
     * Loglevel für Informationen
     **/
    INFO(HBCIUtils.LOG_INFO,"INF"),
    
    /**
     * Loglevel für Debug-Ausgaben
     **/
    DEBUG(HBCIUtils.LOG_DEBUG,"DBG"),
    
    /**
     * Loglevel für Debug-Ausgaben für extreme-Debugging
     **/
    DEBUG2(HBCIUtils.LOG_DEBUG2,"DB2"),
    
    /**
     * Loglevel für devel-Debugging - nicht benutzen!
     **/
    INTERN(HBCIUtils.LOG_INTERN,"INT"),
    
    ;
    
    private int level;
    private String name;
    
    /**
     * ct.
     * @param level das Level.
     * @param name der Name des Levels.
     */
    private Level(int level, String name)
    {
      this.level = level;
      this.name = name;
    }
    
    /**
     * Liefert das numerische Level.
     * @return das numerische Level.
     */
    public int getLevel()
    {
      return this.level;
    }
    
    /**
     * Liefert den Namen des Loglevels.
     * @return der Name des Loglevels.
     */
    public String getName()
    {
      return name;
    }
    
    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
      return this.getName();
    }
    
    /**
     * Liefert das passende Loglevel.
     * @param level das Level.
     * @return das Loglevel oder NONE, wenn es nicht existiert. Nie NULL.
     */
    public static Level find(int level)
    {
      for (Level l:values())
      {
        if (l.level == level)
          return l;
      }
      
      return Level.NONE;
    }
    
    /**
     * Liefert true, wenn beim angegebenen Level geloggt werden soll.
     * @param level das Level.
     * @return true, wenn geloggt werden soll.
     */
    public boolean log(int level)
    {
      final Level l = Level.find(level);
      return l != Level.NONE && l.level <= this.level;
    }
  }

  /**
   * Loggt eine Nachricht miit Level DEBUG.
   * @param msg die Message.
   * @param params optionale Parameter.
   */
  public void debug(String msg, Object... params);

  /**
   * Loggt eine Nachricht mit Level INFO.
   * @param msg die Message.
   * @param params optionale Parameter.
   */
  public void info(String msg, Object... params);
  
  /**
   * Loggt eine Nachricht mit Level WARN.
   * @param msg die Message.
   * @param params optionale Parameter.
   */
  public void warn(String msg, Object... params);
  
  /**
   * Loggt eine Nachricht mit Level Error.
   * @param msg die Message.
   * @param t optionale Angabe einer Exception.
   * @param params optionale Parameter.
   */
  public void error(String msg, Throwable t, Object... params);
  
  /**
   * Loggt eine Message mit dem angegebenen Level.
   * @param msg die Message.
   * @param l das Level.
   * @param t optionale Angabe einer Exception.
   * @param params optionale Parameter.
   */
  public void log(String msg, int l, Throwable t, Object... params);
}
