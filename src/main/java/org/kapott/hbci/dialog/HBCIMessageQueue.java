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

/**
 * Kapselt die Liste der Nachrichten, die innerhalb eines Dialogs an die Bank gesendet werden sollen.
 */
public class HBCIMessageQueue
{
    private List<HBCIMessage> messages = new ArrayList<HBCIMessage>();
    
    /**
     * ct.
     * Erzeugt die Queue und befuellt sie gleich mit der ersten Nachricht.
     */
    public HBCIMessageQueue()
    {
        this.append(new HBCIMessage());
    }
    
    /**
     * Liefert die Kopie der Nachrichten-Liste.
     * Aenderungen an der Liste wirken sich nicht auf die Queue aus. Die Nachrichten darin koennen jedoch geaendert werden.
     * @return die Kopie der Nachrichten-Liste.
     */
    public List<HBCIMessage> getMessages()
    {
        return new ArrayList<HBCIMessage>(this.messages);
    }
    
    /**
     * Liefert die naechste auszufuehrende Nachricht mit Tasks aus der Queue.
     * @return die naechste auszufuehrende Nachricht mit Tasks aus der Queue oder NULL, wenn keine weitere mehr mit Tasks existiert.
     */
    public HBCIMessage poll()
    {
        while (this.messages.size() > 0)
        {
            HBCIMessage m = this.messages.remove(0);
            if (m.getTaskCount() > 0)
                return m;
        }
        return null;
    }
    
    /**
     * Liefert die Anzahl aller Tasks in allen Naxchrichten.
     * @return die Anzahl aller Tasks in allen Naxchrichten.
     */
    public int getTaskCount()
    {
        int count = 0;
        for (HBCIMessage msg:this.messages)
        {
            count += msg.getTaskCount();
        }
        
        return count;
    }
    
    /**
     * Sucht in der ganzen Queue nach einem Task mit dem angegebenen HBCI-Code.
     * @param hbciCode der HBCI-Code.
     * @return der Task oder NULL, wenn er nicht gefunden wurde.
     */
    public HBCIJobImpl findTask(String hbciCode)
    {
        if (hbciCode == null)
            return null;
        
        for (HBCIMessage msg:this.messages)
        {
            HBCIJobImpl task = msg.findTask(hbciCode);
            if (task != null)
                return task;
        }
        
        return null;
    }
    
    /**
     * Liefert die letzte Nachricht.
     * @return die letzte Nachricht.
     */
    public HBCIMessage getLast()
    {
        return this.messages.get(this.messages.size()-1); 
    }
    
    /**
     * Fuegt eine neue Nachricht am Ende der Queue hinzu.
     * @param message die neue Nachricht.
     */
    public void append(HBCIMessage message)
    {
        this.messages.add(message);
    }
    
    /**
     * Fuegt vor der angegebenen Nachricht noch eine neue hinzu und liefert sie zurueck.
     * @param message die Nachricht, vor der noch eine neue eingfuegt werden soll.
     * @return die neue Nachricht.
     */
    public HBCIMessage insertBefore(HBCIMessage message)
    {
        if (message == null)
            throw new IllegalArgumentException("no message given");
        
        final int pos = this.messages.indexOf(message);
        
        if (pos == -1)
            throw new IllegalArgumentException("message unknown to queue");
        
        HBCIMessage m = new HBCIMessage();
        this.messages.add(pos,m);
        return m;
    }
    
    /**
     * Fuegt nach der angegebenen Nachricht noch eine neue hinzu und liefert sie zurueck.
     * @param message die Nachricht, vor der noch eine neue eingfuegt werden soll.
     * @return die neue Nachricht.
     */
    public HBCIMessage insertAfter(HBCIMessage message)
    {
        if (message == null)
            throw new IllegalArgumentException("no message given");
        
        final int pos = this.messages.indexOf(message);
        
        if (pos == -1)
            throw new IllegalArgumentException("message unknown to queue");
        
        HBCIMessage m = new HBCIMessage();
        this.messages.add(pos+1,m);
        return m;
    }
}
