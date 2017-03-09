
/*  $Id: HBCICallbackSwingInternal.java,v 1.1 2011/05/04 22:37:51 willuhn Exp $

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

package org.kapott.hbci.callback;

import java.awt.Container;
import java.util.Hashtable;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

/** Callback für Anwendungen mit GUI; arbeitet mit <code>JInternalFrame</code>s
    anstatt mit Top-Level-Windows. */
public class HBCICallbackSwingInternal
    extends HBCICallbackSwing
{
    public static final boolean DIALOG_RESIZABLE=true;
    public static final boolean DIALOG_NOT_RESIZABLE=false;
    public static final boolean DIALOG_MAXIMIZABLE=true;
    public static final boolean DIALOG_NOT_MAXIMIZABLE=false;
    public static final boolean DIALOG_CLOSABLE=true;
    public static final boolean DIALOG_NOT_CLOSABLE=false;
    public static final boolean DIALOG_ICONIFIABLE=true;
    public static final boolean DIALOG_NOT_ICONIFIABLE=false;

    private JDesktopPane desk;
    
    public HBCICallbackSwingInternal(JDesktopPane desk)
    {
        this.desk=desk;
    }
    
    protected Container createWin(Hashtable currentData,String title,String winname)
    {
        JInternalFrame win=new JInternalFrame(title,DIALOG_NOT_RESIZABLE,DIALOG_NOT_CLOSABLE,DIALOG_NOT_MAXIMIZABLE,DIALOG_NOT_ICONIFIABLE);
        desk.add(win);
        currentData.put("win_"+winname,win);
        
        return win.getContentPane();
    }
    
    protected void removeWin(Hashtable currentData,String winname)
    {
        JInternalFrame win=(JInternalFrame)currentData.get("win_"+winname);
        win.dispose();
        desk.repaint();
    }
    
    protected void drawWin(Hashtable currentData,String winname)
    {
        JInternalFrame win=(JInternalFrame)currentData.get("win_"+winname);

        win.pack();
        win.setVisible(true);
        desk.repaint();
    }
}

