
/*  $Id: ThreadSyncer.java,v 1.1 2011/05/04 22:37:46 willuhn Exp $

    This file is part of HBCI4Java
    Copyright (C) 2001-2008  Stefan Palme

    HBCI4Java is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    HBCI4Java is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package org.kapott.hbci.manager;

import java.util.Hashtable;

public class ThreadSyncer
{
    private String    name;
    private boolean   waiting;
    private boolean   notified;
    private boolean   timeouted;
    private Hashtable<String, Object> data;
    
    public ThreadSyncer(String name)
    {
        this.name=name;
        this.waiting=false;
        this.notified=false;
        this.timeouted=false;
        this.data=new Hashtable<String, Object>();
    }
    
    public synchronized void startWaiting(long seconds, String errMsg)
    {
        try {
            if (!notified) {
                HBCIUtils.log(name+".startWaiting: !notified, waiting now",HBCIUtils.LOG_DEBUG);
                // wenn das notify() nicht schon vor dem wait() kam, dann 
                // wirklich warten
                
                waiting=true;
                wait(seconds*1000);
                waiting=false;
                
                if (!notified) {
                    HBCIUtils.log(name+".startWaiting: end of wait: !notified (timeouted)",HBCIUtils.LOG_DEBUG);
                    // wenn das wait() wegen timeouted terminierte
                    timeouted=true;
                    throw new RuntimeException(name+": "+errMsg);
                }
                HBCIUtils.log(name+".startWaiting: end of wait: notified, normal end of wait",HBCIUtils.LOG_DEBUG);
                
                // damit ist alles wieder im ausgangszustand
                notified=false;
            } else {
                HBCIUtils.log(name+".startWaiting: notified (notified before wait())",HBCIUtils.LOG_DEBUG);
                notified=false;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public synchronized void stopWaiting()
    {
        HBCIUtils.log(name+".stopWaiting",HBCIUtils.LOG_DEBUG);
        notified=true;
        if (waiting) {
            HBCIUtils.log(name+".stopWaiting: someone waits, so notify()",HBCIUtils.LOG_DEBUG);
            notify();
        } else {
            if (timeouted) {
                HBCIUtils.log(name+".stopWaiting: trying to awake a timeouted wait() - aborting",HBCIUtils.LOG_DEBUG);
                timeouted=false;
                throw new RuntimeException(name+": can not awake a timeouted wait()");
            }
            
            HBCIUtils.log(name+".stopWaiting: no one waits, so we do nothing",HBCIUtils.LOG_DEBUG);
        }
    }
    
    public void setData(String key,Object obj)
    {
        if (obj!=null) {
            data.put(key,obj);
        } else {
            data.remove(key);
        }
    }
    
    public void clearData()
    {
        data.clear();
    }
    
    public Object getData(String key)
    {
        return data.get(key);
    }
}
