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

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.kapott.hbci.callback.HBCICallbackConsole;
import org.kapott.hbci.manager.HBCIKernelImpl;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.MsgGen;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** TODO: doku fehlt (analog zu ShowLowlevelGVRs */
public class ShowLowlevelGVRestrictions 
    extends AbstractShowLowlevelData
{
    public static void main(String[] args) throws Exception
    {
        HBCIUtils.init(null,new HBCICallbackConsole());

        String hbciversion;
        if (args.length >= 1) {
            hbciversion = args[0];
        } else {
            System.out.print("hbciversion: ");
            System.out.flush();
            hbciversion = new BufferedReader(new InputStreamReader(System.in))
                    .readLine();
        }

        HBCIKernelImpl kernel = new HBCIKernelImpl(null, hbciversion);
        MsgGen         msggen = kernel.getMsgGen();
        Document       syntax = msggen.getSyntax();

        Element  paramlist = syntax.getElementById("Params");
        NodeList paramnodes = paramlist.getChildNodes();
        int      len = paramnodes.getLength();

        for (int i = 0; i < len; i++) {
            Node paramrefnode = paramnodes.item(i);

            if (paramrefnode.getNodeType() == Node.ELEMENT_NODE) {
                String paramname = ((Element) paramrefnode).getAttribute("type");
                showData(paramname, syntax, 1);
            }
        }
    }

}
