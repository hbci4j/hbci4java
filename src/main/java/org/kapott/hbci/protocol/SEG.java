
/*  $Id: SEG.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

package org.kapott.hbci.protocol;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.kapott.hbci.exceptions.InvalidSegSeqException;
import org.kapott.hbci.exceptions.NoSuchPathException;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.factory.MultipleDEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleDEsFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class SEG
     extends SyntaxElement
{
    protected String getElementTypeName()
    {
        return "SEG";
    }

    protected MultipleSyntaxElements createNewChildContainer(Node ref, Document syntax)
    {
        MultipleSyntaxElements ret=null;

        if ((ref.getNodeName()).equals("DE"))
            ret=MultipleDEsFactory.getInstance().createMultipleDEs(ref, '+', getPath(), syntax);
        else if ((ref.getNodeName()).equals("DEG"))
            ret=MultipleDEGsFactory.getInstance().createMultipleDEGs(ref, '+', getPath(), syntax);

        return ret;
    }

    public SEG(String type, String name, String path, int idx, Document syntax)
    {
        super(type, name, path, idx, syntax);
    }

    public void init(String type, String name, String path, int idx, Document syntax)
    {
        super.init(type, name, path, idx, syntax);
    }

    public String toString(int zero)
    {
        StringBuffer ret = new StringBuffer(256);
        boolean first=true;

        if (isValid()) {
            int tooMuch=0;
            int saveLen;
            for (ListIterator<MultipleSyntaxElements> i = getChildContainers().listIterator(); i.hasNext(); ) {
                if (!first)
                    ret.append('+');

                saveLen=ret.length();
                MultipleSyntaxElements dataList = i.next();
                if (dataList != null)
                    ret.append(dataList.toString(0));
                
                if (ret.length()==saveLen && !first) {
                    tooMuch++;
                } else {
                    tooMuch=0;
                }
                first=false;
            }

            int retlen = ret.length();
            ret.delete(retlen-tooMuch,retlen);
            ret.append('\'');
        }

        return ret.toString();
    }

    public void setSeq(int idx,boolean allowOverwrite)
    {
        String segcounterPath = "SegHead.seq";
        String targetPath = getPath()+"."+segcounterPath;

        if (!propagateValue(targetPath, 
                            Integer.toString(idx), DONT_TRY_TO_CREATE,allowOverwrite))
            throw new NoSuchPathException(targetPath);
    }

    public int enumerateSegs(int idx,boolean allowOverwrite)
    {
        if (idx == 0 || isValid()) {
            setSeq(idx,allowOverwrite);
            if (idx != 0)
                idx++;
        }

        return idx;
    }

    // Wird in Crypt.isCrypted() benötigt, um anhand des SegCodes des zweiten
    // Segments festzustellen, ob die Nachricht verschlüsselt ist oder nicht.
    // analoges in Sig.hasSig()
    public String getCode(MsgGen gen)
    {
        String codePath = "SegHead.code";
        SyntaxElement code = getElement(getPath() + "." + codePath);
        return code.toString();
    }

    // ---------------------------------------------------------------------------------------------------------------

    protected MultipleSyntaxElements parseNewChildContainer(Node dataref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        MultipleSyntaxElements ret=null;

        if ((dataref.getNodeName()).equals("DEG"))
            ret=MultipleDEGsFactory.getInstance().createMultipleDEGs(dataref, '+', getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
        else if ((dataref.getNodeName()).equals("DE"))
            ret=MultipleDEsFactory.getInstance().createMultipleDEs(dataref, '+', getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids);

        return ret;
    }

    protected char getInDelim()
    {
        return '+';
    }

    public SEG(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen,Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        super(type, name, path, predelim, idx, res, fullResLen, syntax, predefs,valids);
    }

    public void init(String type, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        super.init(type, name, path, predelim, idx, res, fullResLen, syntax, predefs,valids);
    }

    public int checkSegSeq(int value)
    {
        int num=Integer.parseInt(getValueOfDE(getPath()+".SegHead.seq"));
        if (num!=value) {
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreSegSeqErrors",HBCIUtilsInternal.getLocMsg("EXCMSG_INVSEQNUM",
                                                                                                   new Object[] {getPath(),new Integer(value),new Integer(num)})))
                throw new InvalidSegSeqException(getPath(),value,num);
        }
        return value+1;
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
        if (isValid()) {
            p.setProperty(Integer.toString(segref[0]),getPath());
            degref=new int[1];
            degref[0]=1;

            for (Iterator<MultipleSyntaxElements> i=getChildContainers().iterator();i.hasNext();) {
                MultipleSyntaxElements l=i.next();
                if (l!=null) {
                    l.getElementPaths(p,segref,degref,null);
                }
            }
            
            segref[0]++;
        }
    }
    
    public void destroy()
    {
        List<MultipleSyntaxElements> childContainers=getChildContainers();
        for (Iterator<MultipleSyntaxElements> i=childContainers.iterator();i.hasNext();) {
            MultipleSyntaxElements child=i.next();
            if (child instanceof MultipleDEGs) {
                MultipleDEGsFactory.getInstance().unuseObject(child);
            } else {
                MultipleDEsFactory.getInstance().unuseObject(child);
            }
        }
        
        super.destroy();
    }
}
