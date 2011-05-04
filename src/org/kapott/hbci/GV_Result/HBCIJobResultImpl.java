
/*  $Id: HBCIJobResultImpl.java,v 1.1 2011/05/04 22:37:48 willuhn Exp $

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

package org.kapott.hbci.GV_Result;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import org.kapott.hbci.GV.HBCIJob;
import org.kapott.hbci.GV.HBCIJobImpl;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.status.HBCIRetVal;
import org.kapott.hbci.status.HBCIStatus;

public class HBCIJobResultImpl
    implements Serializable, HBCIJobResult
{
    private Properties  resultData;
    private HBCIJobImpl parentJob;
    
    public HBCIStatus jobStatus;
    public HBCIStatus globStatus;  

    public HBCIJobResultImpl()
    {
        resultData=new Properties();
        jobStatus=new HBCIStatus();
        globStatus=new HBCIStatus();
    }
    
    public void setParentJob(HBCIJobImpl parentJob)
    {
        this.parentJob=parentJob;
    }
    
    public void storeResult(String key,String value)
    {
        if (value!=null)
            resultData.setProperty(key,value);
    }

    public int getRetNumber()
    {
        return jobStatus.getRetVals().length;
    }

    public HBCIRetVal getRetVal(int idx)
    {
        return jobStatus.getRetVals()[idx];
    }

    public boolean isOK()
    {
        /*
        return jobStatus.getStatusCode()==HBCIStatus.STATUS_OK ||
               (jobStatus.getStatusCode()==HBCIStatus.STATUS_UNKNOWN &&
                globStatus.getStatusCode()==HBCIStatus.STATUS_OK);
        */
        
        // ein job ist dann ok, wenn weder glob- noch job-status einen
        // fehler gemeldet haben. es muss aber wenigstens entweder glob- oder
        // job-status ein explizites OK gemeldet haben
        return globStatus.getStatusCode()!=HBCIStatus.STATUS_ERR &&
               jobStatus.getStatusCode()!=HBCIStatus.STATUS_ERR &&
               (globStatus.getStatusCode()!=HBCIStatus.STATUS_UNKNOWN ||
                jobStatus.getStatusCode()!=HBCIStatus.STATUS_UNKNOWN);
    }

    public String getDialogId()
    {
        return resultData.getProperty("basic.dialogid");
    }

    public String getMsgNum()
    {
        return resultData.getProperty("basic.msgnum");
    }
    
    public String getSegNum()
    {
        return resultData.getProperty("basic.segnum");
    }
    
    public String getJobId()
    {
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date())+"/"+getDialogId()+"/"+getMsgNum()+"/"+getSegNum();
    }

    public Properties getResultData()
    {
        return resultData;
    }
    
    public HBCIStatus getGlobStatus()
    {
        return globStatus;
    }

    public HBCIStatus getJobStatus()
    {
        return jobStatus;
    }
    
    public HBCIPassport getPassport()
    {
        HBCIPassport passport=null;
        HBCIJobImpl  job=(HBCIJobImpl)getParentJob();
        if (job!=null) {
            passport=job.getMainPassport();
        }
        return passport;
    }

    public String toString()
    {
        StringBuffer ret=new StringBuffer();
        Object[] a=resultData.keySet().toArray();

        Arrays.sort(a);
        for (int i=0;i<a.length;i++) {
            String key=(String)(a[i]);
            ret.append(key).append(" = ").append(resultData.getProperty(key)).append(System.getProperty("line.separator"));
        }
        
        return ret.toString().trim();
    }
    
    public HBCIJob getParentJob()
    {
        return this.parentJob;
    }
}