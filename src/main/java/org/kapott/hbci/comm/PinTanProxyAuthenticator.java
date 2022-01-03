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
                    HBCICallback.ResponseType.TEXT,
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
                    HBCICallback.ResponseType.SECRET,
                    retData);
            pass=retData.toString();
            LogFilter.getInstance().addSecretData(pass,"X",LogFilter.FILTER_SECRETS);
        } else {
            HBCIUtils.log("returning proxyuser from client.passport.PinTan.proxypass", HBCIUtils.LOG_DEBUG);
        }
        
        return new PasswordAuthentication(user,pass.toCharArray());
    }

}
