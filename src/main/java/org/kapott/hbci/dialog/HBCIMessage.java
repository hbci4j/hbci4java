/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
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

package org.kapott.hbci.dialog;

import java.util.ArrayList;
import java.util.List;

import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.manager.HBCIUtils;

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
     * Fügt einen Job vor dem angegebenen Task hinzu.
     * @param task der Task.
     * @param toInsert der vorher einzufuegende Task.
     */
    public void prepend(HBCIJobImpl task, HBCIJobImpl toInsert)
    {
      final int pos = this.tasks.indexOf(task);
      if (pos < 0)
      {
        HBCIUtils.log("task " + task.getJobName() + " not found, cannot prepend " + toInsert.getJobName(),HBCIUtils.LOG_WARN);
        return;
      }
      
      this.tasks.add(pos,toInsert);
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


