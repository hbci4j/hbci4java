/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2001-2008 Stefan Palme
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

