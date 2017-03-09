
/*  $Id: SyntaxDE.java,v 1.1 2011/05/04 22:37:56 willuhn Exp $

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

/** @internal
    @brief Collection of HBCI datatypes.

    This package contains a collection of classes, where each class represents
    one of the datatypes defined in HBCI syntax descriptions (e.g. AN for
    alphanumeric data, Bin for binary data etc.).
    These classes are used during the message generation process either when
    building a new message to be sent or when parsing a received message.
    These classes are never instantiated directly, but they are loaded and instantiated
    dynamically (for a certain syntax element, the name of the class to be used
    for storing its value is determined by the value of the attribute @c type in
    the syntax description for this syntax element). The classname to be loaded is
    built from the @c type attribute by prepending @c org.kapott.hbci.datatypes.Syntax */
package org.kapott.hbci.datatypes; 

import org.kapott.hbci.exceptions.InvalidArgumentException;
import org.kapott.hbci.manager.HBCIUtilsInternal;

/* @internal
    @brief Abstract base class for datatypes.

    This is an abstract base class for all classes that will represent a certain HBCI-datatype.

    @author $Author: willuhn $
*/
public abstract class SyntaxDE
{
    private String content;  /**< @internal @brief contains the value of the DE in human readable format */
    
    private void initData(String x,int minsize,int maxsize)
    {
        content=null;
        setContent(x,minsize,maxsize);
    }

    /** @internal 
        @brief Creates a new instance of a datatype.

        In this constructor the data element will be initialized with the given value @p x.
        During initializing process the size constraints for this element will be verified.
        If verification fails, an IllegalArgumentException will be thrown.

        @param x The string representation of the init value.
        @param minsize The minimum size (string length) of the init value.
        @param maxsize The maximum size (string length) of the init value. If this is zero, no
                       checking for @p maxsize will be done.
    */
    public SyntaxDE(String x, int minsize, int maxsize)
    {
        initData(x,minsize,maxsize);
    }
    
    public void init(String x, int minsize, int maxsize)
    {
        initData(x,minsize,maxsize);
    }
    
    public void init(StringBuffer x, int minsize, int maxsize)
    {
        initData(x.toString(),minsize,maxsize);
    }
    
    private void initData()
    {
        content=null;
    }

    /** @internal @overload */
    protected SyntaxDE()
    {
        initData();
    }
    
    protected void init()
    {
        initData();
    }

    /** @internal
        @brief Sets the value for a data element.

        @param st The string representation of the init value.
        @param minsize The minimum size (string length) of the init value.
        @param maxsize The maximum size (string length) of the init value. If this is zero, no
                       checking for @p maxsize will be done.
        @exception IllegalArgumentException when the length of @p x is not in the range
                                            @p minsize ... @p maxsize
    */
    protected final void setContent(String st,int minsize,int maxsize)
    {
        /* die stringlaenge muss zwischen minsize und maxsize liegen,
           im falle 'maxsize=0' kann sie beliebig gross sein */
        int len=st.length();
        if (len<minsize || (maxsize!=0 && len>maxsize)) {
            String msg=HBCIUtilsInternal.getLocMsg("EXC_DT_INV_STRINGLEN",new Object[] {
                                           st,Integer.toString(len),Integer.toString(minsize),Integer.toString(maxsize)});
            if (len==0 || !HBCIUtilsInternal.ignoreError(null,"client.errors.ignoreWrongDataLengthErrors",msg))
                throw new InvalidArgumentException(msg);
        }

        content=st;
    }

    /** @internal
        @brief Returns the value of the data element in the form needed in HBCI messages

        @param zero Just a dummy argument to overload the toString method.
        @return A String containing the HBCI representation of this data element
    */
    public String toString(int zero)
    {
        return (content!=null)?content:"";
    }

    /** @internal @brief Returns the current @c content. 

        @return den aktuellen Inhalt des SyntaxDE in der intern gespeicherten
                Form (human-readable)
     */
    protected String getContent()
    {
        return content;
    }

    // -----------------------------------------------------------------------------------

    /** @internal
        @brief Returns the index, where the next value in the HBCI message @c res
               starts.

        This method is needed when parsing HBCI messages. It checks the first
        character of String @p res. If this character is one of the
        HBCI-specific data-element-delimiters, 1 will be returned, else 0
        (i.e. the return value is the index into the String @p res where we
        can start fetching the next syntax token)

        @param res A part of an HBCI message to be parsed.
        @return Index into @p res where next token starts.
    */
    protected static int skipPreDelim(StringBuffer res)
    {
        int ret = 0;

        if (res.length() != 0) {
            char ch = res.charAt(0);
 
            if (ch == '\'' || ch == '+' || ch == ':') {
                ret++;
            }
        }

        return ret;
    }

    /** @internal
        @brief Returns the index of the next delimiter token.

        This method is needed when parsing an HBCI message. It scans
        @p res starting at position @p startidx for the next occurence
        of an HBCI-specific data-element-delimiter and returns its
        position.

        @param res Part of an HBCI-message to be parsed.
        @param startidx Index position where to start looking for the next delimiter.
        @return The position of the next delimiter character in @p res. If there is no
                next delimiter character, the return value will be the next character
                after the end of the String @p res (i.e. will equal @c res.length())
    */
    public static int findNextDelim(StringBuffer res, int startidx)
    {
        int ret = startidx;
        boolean quoted = false;
        boolean quit = false;
        boolean inBinLen = false;
        int binLenStart = 0;
        int len=res.length();

        while (ret<len && !quit) {
            char ch=res.charAt(ret++);

            if (!quoted) {
                if (ch == '?') {
                    quoted = true;
                } else if (ch == '@') {
                    if (!inBinLen) {
                        inBinLen = true;
                        binLenStart = ret;
                    } else {
                        int binLenEnd=ret-1;
                        String st=res.substring(binLenStart, binLenEnd);
                        int binLen = Integer.parseInt(st);

                        ret+=binLen;
                        inBinLen=false;
                    }
                } else if (ch == '\'' || ch == '+' || ch == ':') {
                    ret--;
                    quit=true;
                }
            } else {
                quoted = false;
            }
        }

        return ret;
    }

    /** @internal @brief Returns the human-readable value of this data element. 

        @return Die human-reable Repraesentation dieses Elementes
    */
    public String toString()
    {
        return (content==null)?"":content;
    }
    
    public void destroy()
    {
        content=null;
    }
}
