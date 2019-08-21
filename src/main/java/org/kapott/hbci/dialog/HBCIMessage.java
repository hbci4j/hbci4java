/**********************************************************************
 *
 * Copyright (c) 2019 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.dialog;

import java.util.ArrayList;
import java.util.List;

import org.kapott.hbci.GV.HBCIJobImpl;

/**
 * Kapselt die fachlichen Jobs in einr HBCI-Nachricht an die Bank.
 */
public class HBCIMessage
{
    private List<HBCIJobImpl> tasks = new ArrayList<HBCIJobImpl>();
    
    /**
     * Liefert die Kopie der Task-Liste.
     * Aenderungen an der Liste wirken sich nicht auf die Nachricht aus. Die Tasks darin koennen jedoch geaendert werden.
     * @return die Kopie der Task-Liste.
     */
    public List<HBCIJobImpl> getTasks()
    {
        return new ArrayList<HBCIJobImpl>(this.tasks);
    }
    
    /**
     * Liefert die Anzahl aller Tasks in der Naxchricht.
     * @return die Anzahl aller Tasks in der Naxchricht.
     */
    public int getTaskCount()
    {
        return this.tasks.size();
    }
    
    /**
     * Sucht in der Nachricht nach einem Task mit dem angegebenen HBCI-Code.
     * @param hbciCode der HBCI-Code.
     * @return der Task oder NULL, wenn er nicht gefunden wurde.
     */
    public HBCIJobImpl findTask(String hbciCode)
    {
        if (hbciCode == null)
            return null;
        
        for (HBCIJobImpl task:this.tasks)
        {
            if (hbciCode.equals(task.getHBCICode()))
              return task;
        }
        return null;
    }

    /**
     * Fuegt einen neuen Job zur Nachricht hinzu.
     * @param task der neue Job.
     */
    public void append(HBCIJobImpl task)
    {
        this.tasks.add(task);
    }
}


