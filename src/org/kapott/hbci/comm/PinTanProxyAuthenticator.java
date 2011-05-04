
/*  $Id: PinTanProxyAuthenticator.java,v 1.1 2011/05/04 22:37:50 willuhn Exp $

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

package org.kapott.hbci.comm;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.kapott.hbci.callback.HBCICallback;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.manager.LogFilter;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassportInternal;

public class PinTanProxyAuthenticator 
extends Authenticator 
{
    AbstractPinTanPassport passport;

    public PinTanProxyAuthenticator(HBCIPassportInternal passport)
    {
        this.passport=(AbstractPinTanPassport)passport;
    }

    protected PasswordAuthentication getPasswordAuthentication() 
    {
        HBCIUtils.log("need proxy authentication", HBCIUtils.LOG_DEBUG);
        
        String       user=passport.getProxyUser();
        String       pass=passport.getProxyPass();
        HBCICallback callback=HBCIUtilsInternal.getCallback();
        
        if (user.length()==0) {
            StringBuffer retData=new StringBuffer();
            callback.callback(
                    passport,
                    HBCICallback.NEED_PROXY_USER,
                    HBCIUtilsInternal.getLocMsg("CALLB_PROXY_USERNAME"),
                    HBCICallback.TYPE_TEXT,
                    retData);
            user=retData.toString();
            LogFilter.getInstance().addSecretData(user,"X",LogFilter.FILTER_IDS);
        } else {
            HBCIUtils.log("returning proxyuser from client.passport.PinTan.proxyuser", HBCIUtils.LOG_DEBUG);
        }
        
        if (pass.length()==0) {
            StringBuffer retData=new StringBuffer();
            callback.callback(
                    passport,
                    HBCICallback.NEED_PROXY_PASS,
                    HBCIUtilsInternal.getLocMsg("CALLB_PROXY_PASSWD"),
                    HBCICallback.TYPE_SECRET,
                    retData);
            pass=retData.toString();
            LogFilter.getInstance().addSecretData(pass,"X",LogFilter.FILTER_SECRETS);
        } else {
            HBCIUtils.log("returning proxyuser from client.passport.PinTan.proxypass", HBCIUtils.LOG_DEBUG);
        }
        
        return new PasswordAuthentication(user,pass.toCharArray());
    }

}
