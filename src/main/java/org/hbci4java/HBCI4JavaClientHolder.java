/**********************************************************************
 *
 * Copyright (c) 2026 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.hbci4java;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Liefert statischen Zugriff auf den aktuellen HBCI-Client.
 */
public abstract class HBCI4JavaClientHolder
{
  /**
   * Der Holder pro Thread.
   */
  public final static HBCI4JavaClientHolder BY_THREAD = new ThreadHolder();
  
  /**
   * Der Holder pro Threadgroup.
   */
  public final static HBCI4JavaClientHolder BY_THREADGROUP = new ThreadGroupHolder();
  
  /**
   * Liefert die Instanz für diese ThreadGroup.
   * @return die Instanz oder NULL, wenn keine existiert.
   */
  public abstract HBCI4JavaClient get();

  /**
   * Speichert die Instanz für diese ThreadGroup.
   * @param value die Instanz.
   */
  public abstract void set(HBCI4JavaClient value);

  /**
   * Entfernt die Instanz aus dieser ThreadGroup.
   */
  public abstract void remove();
  
  /**
   * Liefert einen Identifier.
   * @return der Identifier.
   */
  public abstract String getId();
  
  /**
   * Implementiert den Holder auf Thread-Basis.
   */
  public static class ThreadHolder extends HBCI4JavaClientHolder
  {
    private final static ThreadLocal<HBCI4JavaClient> THREADLOCAL = new ThreadLocal();
    
    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#get()
     */
    @Override
    public HBCI4JavaClient get()
    {
      return THREADLOCAL.get();
    }

    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#set(org.hbci4java.HBCI4JavaClient)
     */
    @Override
    public void set(HBCI4JavaClient value)
    {
      THREADLOCAL.set(value);
    }

    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#remove()
     */
    @Override
    public void remove()
    {
      THREADLOCAL.remove();
    }
    
    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#getId()
     */
    @Override
    public String getId()
    {
      return "by-thread";
    }
  }
  
  /**
   * Alternative zur ThreadLocal - jedoch pro Thread-Group.
   * Bewahrt die Abwärtskompatibilität zu HBCI4Java bis einschliesslich 4.1.13.
   * Denn dort wurde die Instanz pro ThreadGroup gespeichert.
   */
  public static class ThreadGroupHolder extends HBCI4JavaClientHolder
  {
    private final Map<ThreadGroup, HBCI4JavaClient> map = new ConcurrentHashMap<>();

    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#get()
     */
    @Override
    public HBCI4JavaClient get()
    {
      return map.get(this.getThreadGroup());
    }

    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#set(org.hbci4java.HBCI4JavaClient)
     */
    @Override
    public void set(HBCI4JavaClient value)
    {
      map.put(this.getThreadGroup(), value);
    }

    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#remove()
     */
    @Override
    public void remove()
    {
      map.remove(this.getThreadGroup());
    }
    
    /**
     * Liefert die aktuelle ThreadGroup.
     * @return die aktuelle ThreadGroup.
     */
    private ThreadGroup getThreadGroup()
    {
      return Thread.currentThread().getThreadGroup();
    }
    
    /**
     * @see org.hbci4java.HBCI4JavaClientHolder#getId()
     */
    @Override
    public String getId()
    {
      return "by-threadgroup";
    }
  }
}
