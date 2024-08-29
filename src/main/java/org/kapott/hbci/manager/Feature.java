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

package org.kapott.hbci.manager;

/**
 * Kapselt verschiedene Feature-Flags.
 */
public enum Feature
{
    /**
     * Feature, mit dem festgelegt werden kann, ob die personalisierte Dialog-Initialisierung ohne HKTAN gesendet werden soll, wenn als TAN-Verfahren 999 verwendet wird. 
     */
    PINTAN_INIT_SKIPONESTEPSCA(true),
    
    /**
     * Feature, mit dem festgelegt werden kann, ob HBCI4Java versuchen soll, das TAN-Verfahren automatisch zu ermitteln, wenn es noch keine per 3920 erhalten hat.
     * Leider geht das bei einigen Banken (wie Deutsche Bank) nicht, da die keine personalisierte Dialog-Initialisierung mit TAN-Verfahren 999 erlauben.
     */
    PINTAN_INIT_AUTOMETHOD(true),
    
    /**
     * Feature, ob der Segment-Code nur bei TAN-Prozess 1 und 4 mitgesendet wird. In den anderen Faellen ist es teilweise optional.
     * Der FinTS-Server der Baader Bank prueft das aber ziemlich streng. Und damit wir uns nicht die Kompatibilitaet mit anderen Banken
     * kaputt machen, habe ich es als optionales Feature umgesetzt - denn alle anderen Banken tolerieren das bisherige Verhalten.
     * Siehe https://homebanking-hilfe.de/forum/topic.php?p=157481#real157481
     */
    PINTAN_SEGCODE_STRICT(false),
    
    /**
     * Feature, mit dem festgelegt werden kann, ob HBCI4Java die automatischen Refresh-Abfragen bei PushTAN Decoupled durchführen soll.
     */
    PINTAN_DECOUPLED_REFRESH(true),
    
    /**
     * Feature, mit dem festgelegt werden kann, ob HBCI4Java bei der Synchronisierung das HKSPA ausfuehren soll, um die SEPA-Informationen der Konten abzurufen.
     * Konfigurierbar, weil der bei der Commerzbank nicht funktioniert - dort liefert die Bank eine fuer HBCI4Java nicht lesbare Antwort - und ich weiss leider
     * noch nicht, warum genau das nicht gelesen werden kann. Daher mache ich es erstmal abschaltbar.
     * Siehe https://homebanking-hilfe.de/forum/topic.php?p=153155#real153155
     */
    SYNC_SEPAINFO(true),
    
    /**
     * Feature, mit dem festgelegt werden kann, ob bei der Dialoginitialisierung die Registierung des Instituts mit der des Users getauscht werden soll.
     * Laut https://github.com/hbci4j/hbci4java/pull/67 kann man damit auch bei der Postbank eine personalisierte Liste der TAN-Verfahren erhalten
     */
    INIT_FLIP_USER_INST(true),
    
    ;
    
    private boolean enabled = false;
    
    /**
     * ct.
     * @param enabled Legt fest, ob das Feature per Default aktiviert sein soll.
     */
    private Feature(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    /**
     * Liefert true, wenn das Feature per Default aktiv sein soll.
     * @return true, wenn das Feature per Default aktiv sein soll.
     */
    public boolean getDefault()
    {
        return this.enabled;
    }
    
    /**
     * Liefert den aktuellen Zustand des Features.
     * @return true, wenn das Feature aktiv ist.
     */
    public boolean isEnabled()
    {
        return Boolean.parseBoolean(HBCIUtils.getParam("feature." + this,Boolean.toString(this.getDefault())));
    }
    
    /**
     * Setzt den Status des Features zur Laufzeit.
     * @param b true, wenn das Feature aktiv sein soll.
     */
    public void setEnabled(boolean b)
    {
        HBCIUtils.setParam("feature." + this,Boolean.toString(b)); // Hier nicht "NULL" bei false, weil das den Default-Zustand wieder herstellen koennte. Und der kann true sein
    }

    /**
     * Setzt den Status des Features zur Laufzeit auf die Werksvorgabe zurueck.
     */
    public void reset()
    {
        HBCIUtils.setParam("feature." + this,null);
    }
}
