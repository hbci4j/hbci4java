
/*  $Id: MSG.java,v 1.1 2011/05/04 22:38:03 willuhn Exp $

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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;

import org.kapott.hbci.exceptions.NoSuchPathException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.kapott.hbci.protocol.factory.MultipleSEGsFactory;
import org.kapott.hbci.protocol.factory.MultipleSFsFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public final class MSG
    extends SyntaxElement
{
    public final static boolean CHECK_SEQ=true;
    public final static boolean DONT_CHECK_SEQ=false;
    public final static boolean CHECK_VALIDS=true;
    public final static boolean DONT_CHECK_VALIDS=false;
    
    protected MultipleSyntaxElements createNewChildContainer(Node ref, Document syntax)
    {
        MultipleSyntaxElements ret=null;
        
        if ((ref.getNodeName()).equals("SEG"))
            ret=MultipleSEGsFactory.getInstance().createMultipleSEGs(ref, getPath(), syntax);
        else if ((ref.getNodeName()).equals("SF"))
            ret=MultipleSFsFactory.getInstance().createMultipleSFs(ref, getPath(), syntax);
        
        return ret;
    }
    
    protected String getElementTypeName()
    {
        return "MSG";
    }

    /** in 'clientValues' wird eine hashtable uebergeben, die als schluessel den
        pfadnames und als wert den wert eines zu setzenden elementes enthaelt. mit
        der methode werden vom nutzer einzugebenede daten (wie kontonummern, namen
        usw.) in die generierte nachricht eingebaut */
    private void propagateUserData(String name, Hashtable<String,String> clientValues)
    {
        String dottedName = name+".";
        Enumeration<String> e     = clientValues.keys();
        while (e.hasMoreElements()) {
            String key =   e.nextElement();
            String value = clientValues.get(key);

            if (key.startsWith(dottedName) && value.length()!=0) {
                if (!propagateValue(key,value,TRY_TO_CREATE,DONT_ALLOW_OVERWRITE)) {
                    HBCIUtils.log("could not insert the following user-defined data into message: "+key+"="+value,HBCIUtils.LOG_WARN);
                }
            }
        }
    }

    /** setzen des feldes "nachrichtengroesse" im nachrichtenkopf einer nachricht */
    private void setMsgSizeValue(MsgGen gen, int value,boolean allowOverwrite)
    {
        String absPath = getPath() + ".MsgHead.msgsize";
        SyntaxElement msgsizeElem = getElement(absPath);

        if (msgsizeElem == null)
            throw new NoSuchPathException(absPath);
        
        int    size = ((DE)msgsizeElem).getMinSize();
        char[] zeros = new char[size];
        Arrays.fill(zeros, '0');
        DecimalFormat df = new DecimalFormat(String.valueOf(zeros));
        if (!propagateValue(absPath, df.format(value),DONT_TRY_TO_CREATE,allowOverwrite))
            throw new NoSuchPathException(absPath);
    }

    private void initMsgSize(MsgGen gen)
    {
        setMsgSizeValue(gen,0,DONT_ALLOW_OVERWRITE);
    }

    public void autoSetMsgSize(MsgGen gen)
    {
        setMsgSizeValue(gen, toString(0).length(),ALLOW_OVERWRITE);
    }
    
    /** @brief erstellen eines neuen nachrichten-syntaxelements */
    public MSG(String type, MsgGen gen, Hashtable<String,String> clientValues)
    {
        super(type,type,null,0,gen.getSyntax());
        initData(type,gen,clientValues);
    }
    
    public void init(String type,MsgGen gen,Hashtable<String,String> clientValues)
    {
        super.init(type,type,null,0,gen.getSyntax());
        initData(type,gen,clientValues);
    }
    
    private void initData(String type,MsgGen gen,Hashtable<String,String> clientValues)
    {
        propagateUserData(getName(), clientValues);

        enumerateSegs(0,DONT_ALLOW_OVERWRITE);
        initMsgSize(gen);
        validate();
        enumerateSegs(1,ALLOW_OVERWRITE);
        autoSetMsgSize(gen);
    }

    public String toString(int zero)
    {
        StringBuffer ret = new StringBuffer(1024);

        if (isValid())
            for (Iterator<MultipleSyntaxElements> i = getChildContainers().listIterator(); i.hasNext(); ) {
                MultipleSyntaxElements list = i.next();

                if (list != null)
                    ret.append(list.toString(0));
            }

        return ret.toString();
    }

    // -------------------------------------------------------------------------------------------
    
    private void initData(String type,String res,int fullResLen,MsgGen gen,boolean checkSeq,boolean checkValids)
    {
        if (checkSeq)
            checkSegSeq(1);
    }

    public MSG(String type,String res,int fullResLen,MsgGen gen,boolean checkSeq,boolean checkValids)
    {
        super(type,type,null,(char)0,0,new StringBuffer(res),fullResLen,
                gen.getSyntax(),
                new Hashtable<String, String>(),
                checkValids?new Hashtable<String, String>():null);
        initData(type,res,fullResLen,gen,checkSeq,checkValids);
    }
    
    public void init(String type,String res,int fullResLen,MsgGen gen,boolean checkSeq,boolean checkValids)
    {
        super.init(type,type,null,(char)0,0,new StringBuffer(res),fullResLen,
                gen.getSyntax(),new Hashtable<String, String>(),
                checkValids?new Hashtable<String, String>():null);
        initData(type,res,fullResLen,gen,checkSeq,checkValids);
    }

    protected char getInDelim()
    {
        return '\'';
    }

    protected MultipleSyntaxElements parseNewChildContainer(Node segref, char predelim0, char predelim1, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        MultipleSyntaxElements ret=null;

        if ((segref.getNodeName()).equals("SEG"))
            ret=MultipleSEGsFactory.getInstance().createMultipleSEGs(segref, getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
        else if ((segref.getNodeName()).equals("SF"))
            ret=MultipleSFsFactory.getInstance().createMultipleSFs(segref, getPath(), predelim0, predelim1, res, fullResLen, syntax, predefs,valids);
        
        return ret;
    }

    public String getValueOfDE(String path)
    {
        String ret = null;

        for (ListIterator<MultipleSyntaxElements> i = getChildContainers().listIterator(); i.hasNext(); ) {
            MultipleSyntaxElements l = i.next();

            String temp = l.getValueOfDE(path);
            if (temp != null) {
                ret = temp;
                break;
            }
        }

        if (ret == null)
            throw new NoSuchPathException(path);

        return ret;
    }

    // -------------------------------------------------------------------------------------------

    public Properties getData()
    {
        Hashtable<String,String>  hash=new Hashtable<String, String>();
        Properties p=new Properties();
        int        nameskip=getName().length()+1;
        
        extractValues(hash);
        
        for (Enumeration<String> e = hash.keys(); e.hasMoreElements(); ) {
            String key = e.nextElement();
            p.setProperty(key.substring(nameskip), hash.get(key));
        }
        
        return p;
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
        segref=new int[1];
        segref[0]=1;

        for (Iterator<MultipleSyntaxElements> i=getChildContainers().iterator();i.hasNext();) {
            MultipleSyntaxElements l=i.next();
            if (l!=null) {
                l.getElementPaths(p,segref,null,null);
            }
        }
    }
    
    public void destroy()
    {
        List<MultipleSyntaxElements> childContainers=getChildContainers();
        if (childContainers != null)
        {
          for (Iterator<MultipleSyntaxElements> i=childContainers.iterator();i.hasNext();) {
            MultipleSyntaxElements child=i.next();
            if (child instanceof MultipleSFs) {
                MultipleSFsFactory.getInstance().unuseObject(child);
            } else {
                MultipleSEGsFactory.getInstance().unuseObject(child);
            }
          }
        }
        
        super.destroy();
    }
}
