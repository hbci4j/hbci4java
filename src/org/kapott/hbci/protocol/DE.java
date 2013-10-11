
/*  $Id: DE.java,v 1.1 2011/05/04 22:38:02 willuhn Exp $

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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.datatypes.SyntaxDE;
import org.kapott.hbci.datatypes.factory.SyntaxDEFactory;
import org.kapott.hbci.exceptions.NoValidValueException;
import org.kapott.hbci.exceptions.NoValueGivenException;
import org.kapott.hbci.exceptions.OverwriteException;
import org.kapott.hbci.exceptions.ParseErrorException;
import org.kapott.hbci.exceptions.PredelimErrorException;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DE
    extends SyntaxElement
{
    private SyntaxDE value;
    private int minsize;
    private int maxsize;
    private List<String> valids;

    protected MultipleSyntaxElements createNewChildContainer(Node dedef, Document syntax)
    {
        return null;
    }

    protected String getElementTypeName()
    {
        return "DE";
    }

    /** setzen des wertes des de */
    public boolean propagateValue(String destPath,String valueString,boolean tryToCreate,boolean allowOverwrite)
    {
        boolean ret = false;

        // wenn dieses de gemeint ist
        if (destPath.equals(getPath())) {
            if (this.value!=null) { // es gibt schon einen Wert
                if (!allowOverwrite) { // überschreiben ist nicht erlaubt
                    // fehler
                    if (!HBCIUtilsInternal.ignoreError(null,"client.errors.allowOverwrites",
                            "*** trying to overwrite "+getPath()+"="+value.toString()+" with "+valueString))
                        throw new OverwriteException(getPath(),value.toString(),valueString);
                }
                
                // ansonsten den alten Wert löschen
                SyntaxDEFactory.getInstance().unuseObject(value,getType());
            }

            setValue(valueString);
            ret = true;
        }

        return ret;
    }

    public String getValueOfDE(String path)
    {
        String ret = null;

        if (path.equals(getPath()))
            ret=value.toString();

        return ret;
    }

    public String getValueOfDE(String path, int zero)
    {
        String ret = null;

        if (path.equals(getPath()))
            ret = value.toString(0);

        return ret;
    }

    private void initData(Node dedef, String name, String path, int idx, Document syntax)
    {
        this.value=null;
        this.valids=new ArrayList<String>();

        String st;

        minsize = 1;
        st = ((Element)dedef).getAttribute("minsize");
        if (st.length() != 0)
            minsize = Integer.parseInt(st);

        maxsize = 0;
        st = ((Element)dedef).getAttribute("maxsize");
        if (st.length() != 0)
            maxsize = Integer.parseInt(st);
    }
    
    public DE(Node dedef, String name, String path, int idx, Document syntax)
    {
        super(((Element)dedef).getAttribute("type"),name,path,idx,null);
        initData(dedef,name,path,idx,syntax);
    }

    public void init(Node dedef, String name, String path, int idx, Document syntax)
    {
        super.init(((Element)dedef).getAttribute("type"),name,path,idx,null);
        initData(dedef,name,path,idx,syntax);
    }

    /** validierung eines DE: validate ist ok, wenn DE einen wert enthaelt und
        der wert in der liste der gueltigen werte auftaucht */
    public void validate()
    {
        if (value == null) {
            throw new NoValueGivenException(getPath());
        }

        int validssize=valids.size();
        if (validssize!=0) {
            boolean ok=false;
            String valString=(value!=null)?value.toString():"";

            for (int i=0;i<validssize;i++) {
                if (valids.get(i).equals(valString))  {
                    ok=true;
                    break;
                }
            }

            if (!ok) {
                if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreValidValueErrors","*** invalid value for "+getPath()+": "+valString))
                    throw new NoValidValueException(getPath(),valString);
            }
        }

        setValid(true);
    }
    
    public void setValids(List<String> valids)
    {
        this.valids=valids;
    }

    public String toString()
    {
        // return (value != null) ? value.toString() : "";
        return isValid()?value.toString():"";
    }

    public int getMinSize()
    {
        return minsize;
    }

    public void setValue(String st)
    {
        this.value=SyntaxDEFactory.getInstance().createSyntaxDE(getType(),getPath(),st,minsize,maxsize);
    }
    
    public SyntaxDE getValue()
    {
        return value;
    }

    // ---------------------------------------------------------------------------------------------------------------

    protected MultipleSyntaxElements parseNewChildContainer(Node deref, char predelim0, char predelim1, StringBuffer res, int fullResLen,Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        return null;
    }

    protected char getInDelim()
    {
        return (char)0;
    }

    /** anlegen eines de beim parsen funktioniert analog zum
        anlegen eines de bei der message-synthese */
    private void parseValue(StringBuffer res,Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        String temp=res.toString();
        int len=temp.length();
        char preDelim=getPreDelim();
        
        if (preDelim!=(char)0 && temp.charAt(0)!=preDelim) {
            if (len==0) {
                throw new ParseErrorException(HBCIUtilsInternal.getLocMsg("EXCMSG_ENDOFSTRG",getPath()));
            } 

            // HBCIUtils.log("error string: "+res.toString(),HBCIUtils.LOG_ERR);
            // HBCIUtils.log("current: "+getPath()+":"+type+"("+minsize+","+maxsize+")="+value,HBCIUtils.LOG_ERR);
            // HBCIUtils.log("predelimiter mismatch (required:"+getPreDelim()+" found:"+temp.charAt(0)+")",HBCIUtils.LOG_ERR);
            throw new PredelimErrorException(getPath(),Character.toString(preDelim),Character.toString(temp.charAt(0)));
        }

        this.value=SyntaxDEFactory.getInstance().createSyntaxDE(getType(),getPath(),res,minsize,maxsize);

        String valueString=value.toString(0);
        String predefined = predefs.get(getPath());
        if (predefined!=null) {
            if (!valueString.equals(predefined)) {
                throw new ParseErrorException(HBCIUtilsInternal.getLocMsg("EXCMSG_PREDEFERR",
                                                                  new Object[] {getPath(),predefined,value}));
            }
        }

        boolean atLeastOne=false;
        boolean ok=false;
        if (valids!=null) {
            String header=getPath()+".value";
            for (Enumeration<String> e=valids.keys();e.hasMoreElements();) {
                String key= e.nextElement();
                
                if (key.startsWith(header) && 
                        key.indexOf(".",header.length())==-1) {
                    
                    atLeastOne=true;
                    String validValue= valids.get(key);
                    if (valueString.equals(validValue)) {
                        ok=true;
                        break;
                    }
                }
            }
        }
        
        if (atLeastOne && !ok) {
            if (!HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreValidValueErrors","*** invalid value for "+getPath()+": "+valueString))
                throw new NoValidValueException(getPath(),valueString);
        }
    }

    private void initData(Node dedef, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String,String> predefs,Hashtable<String,String> valids)
    {
        setValid(false);

        value = null;
        this.valids=new ArrayList<String>();

        String st;

        minsize = 1;
        st = ((Element)dedef).getAttribute("minsize");
        if (st.length() != 0)
            minsize = Integer.parseInt(st);

        maxsize = 0;
        st = ((Element)dedef).getAttribute("maxsize");
        if (st.length() != 0)
            maxsize = Integer.parseInt(st);

        try {
            parseValue(res,predefs,valids);
            setValid(true);
        } catch (RuntimeException e) {
            SyntaxDEFactory.getInstance().unuseObject(value,getType());
            throw e;
        }
    }
    
    public DE(Node dedef, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        super(((Element)dedef).getAttribute("type"),name,path,predelim,idx,res,fullResLen,null,predefs,valids);
        initData(dedef,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    public void init(Node dedef, String name, String path, char predelim, int idx, StringBuffer res, int fullResLen, Document syntax, Hashtable<String, String> predefs,Hashtable<String, String> valids)
    {
        super.init(((Element)dedef).getAttribute("type"),name,path,predelim,idx,res,fullResLen,null,predefs,valids);
        initData(dedef,name,path,predelim,idx,res,fullResLen,syntax,predefs,valids);
    }

    public void extractValues(Hashtable<String,String> values)
    {
        if (isValid())
            values.put(getPath(),value.toString());
    }

    public String toString(int zero)
    {
        return isValid()?value.toString(0):"";
    }

    public void getElementPaths(Properties p,int[] segref,int[] degref,int[] deref)
    {
        if (deref==null) {
            p.setProperty(Integer.toString(segref[0])+
                          ":"+Integer.toString(degref[0]),getPath());
            degref[0]++;
        } else {
            p.setProperty(Integer.toString(segref[0])+
                          ":"+
                          Integer.toString(degref[0])+
                          ","+
                          Integer.toString(deref[0]),
                          getPath());
            deref[0]++;
        }
    }

    public void destroy()
    {
        SyntaxDEFactory.getInstance().unuseObject(value,getType());
        value=null;
        valids.clear();
        valids=null;
        
        super.destroy();
    }
}
