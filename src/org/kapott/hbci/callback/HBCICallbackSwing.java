  
/*  $Id: HBCICallbackSwing.java,v 1.1 2011/05/04 22:37:52 willuhn Exp $

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

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.kapott.hbci.exceptions.AbortedException;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.exceptions.InvalidUserDataException;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIUtilsInternal;
import org.kapott.hbci.passport.AbstractPinTanPassport;
import org.kapott.hbci.passport.HBCIPassport;
import org.kapott.hbci.passport.INILetter;

/** Default-Implementation einer Callback-Klasse für Anwendungen mit GUI.
    Diese Klasse überschreibt die <code>callback()</code>-Methode. Benötigte Nutzereingaben 
    werden hier nicht mehr über STDIN abgefragt, sondern es wird ein neues Top-Level-Window
    erzeugt, welches die entsprechende Meldung sowie ein Feld zur Eingabe
    der Antwort enthält. Kernel-Meldungen und erwartete Nutzeraktionen werden ebenfalls
    durch ein neues Top-Level-Window realisiert. Die Methoden <code>log()</code> und
    <code>status()</code> werden nicht überschrieben, so dass diese weiterhin das Verhalten
    der {@link org.kapott.hbci.callback.HBCICallbackConsole}-Klasse zeigen. */
public class HBCICallbackSwing
    extends HBCICallbackConsole
{
    public static final boolean ACTION_BLOCKING=true;
    public static final boolean ACTION_NOT_BLOCKING=false;
    public static final boolean DIALOG_MODAL=true;
    public static final boolean DIALOG_NOT_MODAL=false;

    private final class SyncObject
    {
        private boolean stopCalled=false;
        
        public synchronized void startWaiting()
        {
            if (!stopCalled) {
                try {
                    wait();
                } catch (Exception e) {
                    throw new HBCI_Exception("*** error in sync object",e);
                }
            }
        }

        public synchronized void stopWaiting()
        {
            stopCalled=true;
            notify();
        }
    }
    
    protected Hashtable<HBCIPassport, Hashtable<String, Object>> passports;
    
    public HBCICallbackSwing()
    {
        super();
        passports=new Hashtable<HBCIPassport, Hashtable<String, Object>>();
    }
    
    public void callback(final HBCIPassport passport,int reason,String msg,int datatype,StringBuffer retData)
    {
        if (msg==null)
            msg="";
            
        Hashtable<String, Object> currentData= passports.get(passport);
        if (currentData==null) {
            currentData=new Hashtable<String, Object>();
            currentData.put("passport",passport);
            currentData.put("dataRequested",Boolean.FALSE);
            currentData.put("proxyRequested",Boolean.FALSE);
            currentData.put("msgcounter",new Integer(0));
            passports.put(passport,currentData);
        }
        currentData.put("reason",new Integer(reason));
        currentData.put("msg",msg);
        
        if (retData!=null)
            currentData.put("retData",retData);
        
        try {
            switch (reason) {
                case NEED_PASSPHRASE_LOAD:
                case NEED_PASSPHRASE_SAVE:
                    needSecret(currentData,"passphrase");
                    break;
                case NEED_SOFTPIN:
                    needSecret(currentData,"softpin");
                    break;
                case NEED_PT_PIN:
                    needSecret(currentData,"ptpin");
                    break;
                case NEED_PT_TAN:
                    needSecret(currentData,"pttan");
                    break;
                    
                case NEED_COUNTRY:
                    if (!((Boolean)currentData.get("dataRequested")).booleanValue())
                        needRDHData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("data_country"));
                    break;
                case NEED_BLZ:
                    if (!((Boolean)currentData.get("dataRequested")).booleanValue())
                        needRDHData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("data_blz"));
                    break;
                case NEED_HOST:
                    if (!((Boolean)currentData.get("dataRequested")).booleanValue())
                        needRDHData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("data_host"));
                    break;
                case NEED_PORT:
                    if (!((Boolean)currentData.get("dataRequested")).booleanValue())
                        needRDHData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("data_port"));
                    break;
                case NEED_FILTER:
                    if (!((Boolean)currentData.get("dataRequested")).booleanValue())
                        needRDHData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("data_filter"));
                    break;
                case NEED_USERID:
                    if (!((Boolean)currentData.get("dataRequested")).booleanValue())
                        needRDHData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("data_userid"));
                    break;
                case NEED_CUSTOMERID:
                    if (!((Boolean)currentData.get("dataRequested")).booleanValue())
                        needRDHData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("data_customerid"));
                    break;
                    
                case NEED_CHIPCARD:
                    needAction(currentData,ACTION_NOT_BLOCKING,"chipcard");
                    break;
                case NEED_HARDPIN:
                    needAction(currentData,ACTION_NOT_BLOCKING,"hardpin");
                    break;
                case NEED_REMOVE_CHIPCARD:
                    needAction(currentData,ACTION_BLOCKING,"remove");
                    break;
                    
                case HAVE_CHIPCARD:
                    removeActionWindow(currentData,"chipcard");
                    break;
                case HAVE_HARDPIN:
                    removeActionWindow(currentData,"hardpin");
                    break;
                    
                case NEED_NEW_INST_KEYS_ACK:
                    ackInstKeys(currentData,"ackinstkeys");
                    break;
                case HAVE_NEW_MY_KEYS:
                    haveNewMyKeys(currentData,"ackmykeys");
                    break;
                    
                case HAVE_INST_MSG:
                    showInstMessage(currentData,"instmsg",retData!=null);
                    break;
                case NEED_CONNECTION:
                case CLOSE_CONNECTION:
                    showConnectionMessage(currentData,"connmsg");
                    break;
                    
                case HAVE_CRC_ERROR:
                    correctAccountData(currentData,"crcerror");
                    break;
                case HAVE_IBAN_ERROR:
                    correctIBANData(currentData,"ibanerror");
                    break;
                case HAVE_ERROR:
                    handleError(currentData,"error");
                    break;
                    
                case NEED_SIZENTRY_SELECT:
                    needSIZEntrySelect(currentData,"sizentryselect");
                    break;
                    
                case NEED_PT_SECMECH:
                    needPTSecMech(currentData,"pt_method");
                    break;
                    
                case NEED_PROXY_USER:
                    if (!((Boolean)currentData.get("proxyRequested")).booleanValue())
                        needProxyData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("proxy_user"));
                    break;
                case NEED_PROXY_PASS:
                    if (!((Boolean)currentData.get("proxyRequested")).booleanValue())
                        needProxyData(currentData);
                    retData.replace(0,retData.length(),(String)currentData.get("proxy_pass"));
                    break;
                case NEED_INFOPOINT_ACK:
                    ackInfoPoint(currentData,"accinfopoint");
                    break;

                default:
                    throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_UNKNOWN",Integer.toString(reason)));
            }
        } catch (Exception e) {
            throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_ERR"),e);
        }
    }
    
    private void needSecret(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();
        
        final int[] aborted=new int[1];
        aborted[0]=0;
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI",winname);
            
            Box framebox=Box.createHorizontalBox();
            win.add(framebox);
            
            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));
            
            mainbox.add(Box.createVerticalStrut(8));
            
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);
            
            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(10));

            final JPasswordField input=new JPasswordField(10);
            mainbox.add(input);
            mainbox.add(Box.createVerticalStrut(8));
            
            JPasswordField tempinput=null;
            if (((Integer)currentData.get("reason")).intValue()==NEED_PASSPHRASE_SAVE) {
                tempinput=new JPasswordField(10);
                mainbox.add(tempinput);
                mainbox.add(Box.createVerticalStrut(8));
            }
            final JPasswordField input2=tempinput;

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));
            
            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            ((JComponent)win).getRootPane().setDefaultButton(ok);
            
            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());
            
            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                String passphrase=String.valueOf(input.getPassword());
                if (input2!=null) {
                    String passphrase2=String.valueOf(input2.getPassword());
                    if (!passphrase.equals(passphrase2)) {
                        aborted[0]=2;
                    }
                }
                if (aborted[0]==0) {
                    StringBuffer retData=(StringBuffer)currentData.get("retData");
                    retData.replace(0,retData.length(),passphrase);
                }
                removeWin(currentData,winname);
                sync.stopWaiting();
            }});
            
            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname);
                aborted[0]=1;
                sync.stopWaiting();
            }});
            
            input.requestFocus();
            drawWin(currentData,winname);
        }});
        
        sync.startWaiting();
        if (aborted[0]==1)
            throw new AbortedException(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_ABORT"));
        else if (aborted[0]==2)
            throw new InvalidUserDataException(HBCIUtilsInternal.getLocMsg("EXCMSG_PWDONTMATCH"));
    }
    
    private void correctAccountData(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();

        final boolean[] aborted=new boolean[1];
        aborted[0]=false;

        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI",winname);

            Box framebox=Box.createHorizontalBox();
            win.add(framebox);

            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));

            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);

            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(10));

            JPanel table=new JPanel(new GridBagLayout());
            mainbox.add(table);
            
            GridBagConstraints constr=new GridBagConstraints();
            constr.anchor=GridBagConstraints.NORTHWEST;
            constr.insets=new Insets(2,2,2,2);
            
            final StringBuffer retData=(StringBuffer)currentData.get("retData");
            int idx=retData.indexOf("|");
            
            constr.gridx=0; constr.gridy=0;
            l=new JLabel(HBCIUtilsInternal.getLocMsg("BLZ"));
            table.add(l,constr);
            constr.gridx++;
            final JTextField blz=new JTextField(retData.substring(0,idx),10);
            table.add(blz,constr);

            constr.gridx=0; constr.gridy++;
            l=new JLabel(HBCIUtilsInternal.getLocMsg("ACCNUMBER"));
            table.add(l,constr);
            constr.gridx++;
            final JTextField number=new JTextField(retData.substring(idx+1),10);
            table.add(number,constr);
            
            mainbox.add(Box.createVerticalStrut(8));
            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));

            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            ((JComponent)win).getRootPane().setDefaultButton(ok);

            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());

            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                retData.replace(0,retData.length(),blz.getText()+
                                                   "|"+
                                                   number.getText());
                removeWin(currentData,winname);
                sync.stopWaiting();
            }});

            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname);
                aborted[0]=true;
                sync.stopWaiting();
            }});

            blz.requestFocus();
            drawWin(currentData,winname);
        }});

        sync.startWaiting();
        if (aborted[0])
            throw new AbortedException(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_ABORT")); 
    }

    private void correctIBANData(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();

        final boolean[] aborted=new boolean[1];
        aborted[0]=false;

        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI",winname);

            Box framebox=Box.createHorizontalBox();
            win.add(framebox);

            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));

            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);

            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(10));

            JPanel table=new JPanel(new GridBagLayout());
            mainbox.add(table);
            
            GridBagConstraints constr=new GridBagConstraints();
            constr.anchor=GridBagConstraints.NORTHWEST;
            constr.insets=new Insets(2,2,2,2);
            
            final StringBuffer retData=(StringBuffer)currentData.get("retData");
            
            constr.gridx=0; constr.gridy=0;
            l=new JLabel(HBCIUtilsInternal.getLocMsg("IBAN"));
            table.add(l,constr);
            constr.gridx++;
            final JTextField iban=new JTextField(retData.toString(),10);
            table.add(iban,constr);

            mainbox.add(Box.createVerticalStrut(8));
            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));

            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            ((JComponent)win).getRootPane().setDefaultButton(ok);

            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());

            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                retData.replace(0,retData.length(),iban.getText());
                removeWin(currentData,winname);
                sync.stopWaiting();
            }});

            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname);
                aborted[0]=true;
                sync.stopWaiting();
            }});

            iban.requestFocus();
            drawWin(currentData,winname);
        }});

        sync.startWaiting();
        if (aborted[0])
            throw new AbortedException(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_ABORT")); 
    }

    private void needRDHData(final Hashtable<String, Object> currentData)
    {
        final SyncObject sync=new SyncObject();
        
        final boolean[] aborted=new boolean[1];
        aborted[0]=false;
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI","rdhdata");
            
            Box framebox=Box.createHorizontalBox();
            win.add(framebox);
            
            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));
            
            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);

            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel(HBCIUtilsInternal.getLocMsg("CALLB_NEEDRDHDATA")));
            box2.add(Box.createHorizontalGlue());
            
            mainbox.add(Box.createVerticalStrut(10));
            
            JPanel table=new JPanel(new GridBagLayout());
            mainbox.add(table);
            mainbox.add(Box.createVerticalStrut(8));
            
            GridBagConstraints constr=new GridBagConstraints();
            constr.anchor=GridBagConstraints.NORTHWEST;
            constr.fill=GridBagConstraints.HORIZONTAL;
            constr.insets=new Insets(4,0,4,8);
            
            HBCIPassport passport=(HBCIPassport)currentData.get("passport");
            
            constr.gridx=0;constr.gridy=0;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("COUNTRY")),constr);
            final JTextField input_country=new JTextField(passport.getCountry(),3);
            constr.gridx++;
            table.add(input_country,constr);

            constr.gridx=0;constr.gridy++;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("BLZ")),constr);
            final JTextField input_blz=new JTextField(passport.getBLZ(),25);
            constr.gridx++;
            table.add(input_blz,constr);

            constr.gridx=0;constr.gridy++;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("HOST")),constr);
            final JTextField input_host=new JTextField(passport.getHost(),25);
            constr.gridx++;
            table.add(input_host,constr);

            constr.gridx=0;constr.gridy++;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("PORT")),constr);
            final JTextField input_port=new JTextField(passport.getPort().toString(),25);
            constr.gridx++;
            table.add(input_port,constr);

            constr.gridx=0;constr.gridy++;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("FILTER")),constr);
            final JTextField input_filter=new JTextField(passport.getFilterType(),25);
            constr.gridx++;
            table.add(input_filter,constr);

            constr.gridx=0;constr.gridy++;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("USERID")),constr);
            final JTextField input_userid=new JTextField(passport.getUserId(),25);
            constr.gridx++;
            table.add(input_userid,constr);
            
            constr.gridx=0;constr.gridy++;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("CUSTOMERID")),constr);
            final JTextField input_customerid=new JTextField(passport.getCustomerId(),25);
            constr.gridx++;constr.weightx=1;constr.weighty=1;
            table.add(input_customerid,constr);
            
            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));
            
            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            ((JComponent)win).getRootPane().setDefaultButton(ok);
            
            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());
            
            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                currentData.put("data_country",input_country.getText());
                currentData.put("data_blz",input_blz.getText());
                currentData.put("data_host",input_host.getText());
                currentData.put("data_port",input_port.getText());
                currentData.put("data_filter",input_filter.getText());
                currentData.put("data_userid",input_userid.getText());
                currentData.put("data_customerid",input_customerid.getText());
                currentData.put("dataRequested",Boolean.TRUE);
                removeWin(currentData,"rdhdata");
                sync.stopWaiting();
            }});
            
            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,"rdhdata");
                aborted[0]=true;
                sync.stopWaiting();
            }});
            
            input_country.requestFocus();
            drawWin(currentData,"rdhdata");
        }});
        
        sync.startWaiting();
        if (aborted[0])
            throw new AbortedException(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_ABORT"));
    }
    
    private void needProxyData(final Hashtable<String, Object> currentData)
    {
        final SyncObject sync=new SyncObject();
        
        final boolean[] aborted=new boolean[1];
        aborted[0]=false;
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI","proxydata");
            
            Box framebox=Box.createHorizontalBox();
            win.add(framebox);
            
            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));
            
            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);

            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel(HBCIUtilsInternal.getLocMsg("CALLB_NEED_PROXYDATA")));
            box2.add(Box.createHorizontalGlue());
            
            mainbox.add(Box.createVerticalStrut(10));
            
            JPanel table=new JPanel(new GridBagLayout());
            mainbox.add(table);
            mainbox.add(Box.createVerticalStrut(8));
            
            GridBagConstraints constr=new GridBagConstraints();
            constr.anchor=GridBagConstraints.NORTHWEST;
            constr.fill=GridBagConstraints.HORIZONTAL;
            constr.insets=new Insets(4,0,4,8);
            
            AbstractPinTanPassport passport=(AbstractPinTanPassport)currentData.get("passport");
            
            constr.gridx=0;constr.gridy=0;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("CALLB_PROXY_USERNAME")),constr);
            final JTextField input_user=new JTextField(passport.getProxyUser(),3);
            constr.gridx++;
            table.add(input_user,constr);

            constr.gridx=0;constr.gridy++;
            table.add(new JLabel(HBCIUtilsInternal.getLocMsg("CALLB_PROXY_PASSWD")),constr);
            final JPasswordField input_pass=new JPasswordField(passport.getProxyPass(),25);
            constr.gridx++;
            table.add(input_pass,constr);

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));
            
            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            ((JComponent)win).getRootPane().setDefaultButton(ok);
            
            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());
            
            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                currentData.put("proxy_user",input_user.getText());
                currentData.put("proxy_pass",new String(input_pass.getPassword()));
                removeWin(currentData,"proxydata");
                sync.stopWaiting();
            }});
            
            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,"proxydata");
                aborted[0]=true;
                sync.stopWaiting();
            }});
            
            input_user.requestFocus();
            drawWin(currentData,"proxydata");
        }});
        
        sync.startWaiting();
        if (aborted[0])
            throw new AbortedException(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_ABORT"));
    }
    
    private void needAction(final Hashtable<String, Object> currentData,final boolean blocking,final String winname)
    {
        final SyncObject sync=new SyncObject();
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            Container tempWin=createWin(currentData,"HBCI",winname);
            
            Box framebox=Box.createHorizontalBox();
            tempWin.add(framebox);
            
            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));
            
            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);
            
            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());
            
            mainbox.add(Box.createVerticalStrut(10));
            
            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));
            
            box2.add(Box.createHorizontalGlue());
            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CLOSE"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());
            
            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname);
                sync.stopWaiting();
            }});
            
            if (!blocking) {
                sync.stopWaiting();
            }
            drawWin(currentData,winname);
        }});
        
        sync.startWaiting();
    }
    
    private void removeActionWindow(Hashtable<String, Object> currentData,String winname)
    {
        removeWin(currentData,winname);
    }
    
    private void ackInstKeys(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            try {
                final Container win=createWin(currentData,"HBCI",winname);
                
                Box framebox=Box.createHorizontalBox();
                win.add(framebox);
                
                framebox.add(Box.createHorizontalStrut(8));
                Box mainbox=Box.createVerticalBox();
                framebox.add(mainbox);
                framebox.add(Box.createHorizontalStrut(8));
                
                mainbox.add(Box.createVerticalStrut(8));
                Box box2=Box.createHorizontalBox();
                mainbox.add(box2);
                
                JLabel l=new JLabel("Passport: "+
                                    (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
                l.setFont(new Font("Arial",Font.PLAIN,10));
                box2.add(Box.createHorizontalGlue());
                box2.add(l);
                box2.add(Box.createHorizontalGlue());
    
                mainbox.add(Box.createVerticalStrut(8));
    
                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                box2.add(new JLabel(HBCIUtilsInternal.getLocMsg("CALLB_NEW_INST_KEYS")));
                box2.add(Box.createHorizontalGlue());
                
                mainbox.add(Box.createVerticalStrut(10));
                
                JPanel table=new JPanel(new GridBagLayout());
                mainbox.add(table);
                mainbox.add(Box.createVerticalStrut(8));
                
                GridBagConstraints constr=new GridBagConstraints();
                constr.anchor=GridBagConstraints.NORTHWEST;
                constr.fill=GridBagConstraints.HORIZONTAL;
                constr.insets=new Insets(4,0,4,8);
                
                HBCIPassport passport=(HBCIPassport)currentData.get("passport");
                INILetter iniletter=new INILetter(passport,INILetter.TYPE_INST);
                
                String exp_st=HBCIUtils.data2hex(iniletter.getKeyExponentDisplay());
                String mod_st=HBCIUtils.data2hex(iniletter.getKeyModulusDisplay());
                String hash_st=HBCIUtils.data2hex(iniletter.getKeyHashDisplay());
                Font font=new Font("Monospaced",Font.PLAIN,10);
    
                constr.gridx=0;constr.gridy=0;constr.gridheight=8;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("EXPONENT")),constr);
                constr.gridx++;constr.gridheight=1;
                for (int i=0;i<=exp_st.length()/3/16;i++) {
                    if (i==0)
                        constr.insets=new Insets(8,0,1,8);
                    else
                        constr.insets=new Insets(1,0,1,8);
                    
                    l=new JLabel(exp_st.substring(i*16*3, Math.min((i+1)*16*3-1, exp_st.length())));
                    l.setFont(font);
                    l.setForeground(Color.BLUE);
                    table.add(l,constr);
                    constr.gridy++;
                }
                
                constr.gridx=0;constr.gridheight=8;
                constr.insets=new Insets(4,0,4,8);
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("MODULUS")),constr);
                constr.gridx++;constr.gridheight=1;
                for (int i=0;i<=mod_st.length()/3/16;i++) {
                    if (i==0)
                        constr.insets=new Insets(8,0,1,8);
                    else
                        constr.insets=new Insets(1,0,1,8);
                    
                    l=new JLabel(mod_st.substring(i*16*3, Math.min((i+1)*16*3-1, mod_st.length())));
                    l.setFont(font);
                    l.setForeground(Color.BLUE);
                    table.add(l,constr);
                    constr.gridy++;
                }
                
                constr.gridx=0;constr.gridheight=2;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("HASH")),constr);
                constr.insets=new Insets(4,0,4,8);
                constr.gridx++;constr.gridheight=1;
                for (int i=0;i<=hash_st.length()/3/10;i++) {
                    if (i==0)
                        constr.insets=new Insets(8,0,1,8);
                    else
                        constr.insets=new Insets(1,0,1,8);
                    
                    l=new JLabel(hash_st.substring(i*10*3, Math.min((i+1)*10*3-1, hash_st.length())));
                    l.setFont(font);
                    l.setForeground(Color.MAGENTA);
                    table.add(l,constr);
                    constr.gridy++;
                }
                
                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                mainbox.add(Box.createVerticalStrut(4));
                
                box2.add(Box.createHorizontalGlue());
                JButton ok=new JButton("OK");
                box2.add(ok);
                ((JComponent)win).getRootPane().setDefaultButton(ok);
                
                JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
                box2.add(cancel);
                box2.add(Box.createHorizontalGlue());
                
                final StringBuffer retData=(StringBuffer)currentData.get("retData");
                ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                    retData.replace(0,retData.length(),"");
                    removeWin(currentData,winname);
                    sync.stopWaiting();
                }});
                
                cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                    retData.replace(0,retData.length(),"ERROR");
                    removeWin(currentData,winname);
                    sync.stopWaiting();
                }});
                
                ok.requestFocus();
                drawWin(currentData,winname);
            } catch (Exception e) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_ERR"),e);
            }
        }});
        
        sync.startWaiting();
    }
    
    private void handleError(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();

        SwingUtilities.invokeLater(new Runnable() { public void run() {
            try {
                final Container win=createWin(currentData,"HBCI",winname);

                Box framebox=Box.createHorizontalBox();
                win.add(framebox);

                framebox.add(Box.createHorizontalStrut(8));
                Box mainbox=Box.createVerticalBox();
                framebox.add(mainbox);
                framebox.add(Box.createHorizontalStrut(8));

                mainbox.add(Box.createVerticalStrut(8));
                Box box2=Box.createHorizontalBox();
                mainbox.add(box2);

                JLabel l=new JLabel("Passport: "+
                                    (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
                l.setFont(new Font("Arial",Font.PLAIN,10));
                box2.add(Box.createHorizontalGlue());
                box2.add(l);
                box2.add(Box.createHorizontalGlue());
    
                mainbox.add(Box.createVerticalStrut(8));
                
                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                box2.add(new JLabel(HBCIUtilsInternal.getLocMsg("CALLB_ERROR_OCCURED")));
                box2.add(Box.createHorizontalGlue());

                mainbox.add(Box.createVerticalStrut(6));

                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                box2.add(new JLabel((String)currentData.get("msg")));
                box2.add(Box.createHorizontalGlue());

                mainbox.add(Box.createVerticalStrut(10));

                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                mainbox.add(Box.createVerticalStrut(4));

                box2.add(Box.createHorizontalGlue());
                JButton ok=new JButton(HBCIUtilsInternal.getLocMsg("IGNORE"));
                box2.add(ok);
                ((JComponent)win).getRootPane().setDefaultButton(ok);

                JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("ABORT"));
                box2.add(cancel);
                box2.add(Box.createHorizontalGlue());
                
                final StringBuffer retData=(StringBuffer)currentData.get("retData");
                ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                    retData.replace(0,retData.length(),"");
                    removeWin(currentData,winname);
                    sync.stopWaiting();
                }});

                cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                    retData.replace(0,retData.length(),"ERROR");
                    removeWin(currentData,winname);
                    sync.stopWaiting();
                }});

                ok.requestFocus();
                drawWin(currentData,winname);
            } catch (Exception e) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_ERR"),e);
            }
        }});

        sync.startWaiting();
    }

    private void haveNewMyKeys(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            try {
                final Container win=createWin(currentData,"HBCI",winname);
                
                Box framebox=Box.createHorizontalBox();
                win.add(framebox);
                
                framebox.add(Box.createHorizontalStrut(8));
                Box mainbox=Box.createVerticalBox();
                framebox.add(mainbox);
                framebox.add(Box.createHorizontalStrut(8));
                
                mainbox.add(Box.createVerticalStrut(8));
                Box box2=Box.createHorizontalBox();
                mainbox.add(box2);
                
                JLabel l=new JLabel("Passport: "+
                                    (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
                l.setFont(new Font("Arial",Font.PLAIN,10));
                box2.add(Box.createHorizontalGlue());
                box2.add(l);
                box2.add(Box.createHorizontalGlue());

                mainbox.add(Box.createVerticalStrut(8));

                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                box2.add(new JLabel(HBCIUtilsInternal.getLocMsg("CALLB_NEW_USER_KEYS")));
                box2.add(Box.createHorizontalGlue());
                
                mainbox.add(Box.createVerticalStrut(10));
                
                JPanel table=new JPanel(new GridBagLayout());
                mainbox.add(table);
                mainbox.add(Box.createVerticalStrut(8));
                
                GridBagConstraints constr=new GridBagConstraints();
                constr.anchor=GridBagConstraints.NORTHWEST;
                constr.fill=GridBagConstraints.HORIZONTAL;
                constr.insets=new Insets(4,0,4,8);
                
                HBCIPassport passport=(HBCIPassport)currentData.get("passport");
                INILetter iniletter=new INILetter(passport,INILetter.TYPE_USER);
                
                String exp_st=HBCIUtils.data2hex(iniletter.getKeyExponentDisplay());
                String mod_st=HBCIUtils.data2hex(iniletter.getKeyModulusDisplay());
                String hash_st=HBCIUtils.data2hex(iniletter.getKeyHashDisplay());
                Font font=new Font("Monospaced",Font.PLAIN,10);

                Date date=new Date();                
                constr.gridx=0;constr.gridy=0;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("DATE")),constr);
                constr.gridx++;
                table.add(new JLabel(HBCIUtils.date2StringLocal(date)),constr);
                
                constr.gridx=0;constr.gridy++;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("TIME")),constr);
                constr.gridx++;
                table.add(new JLabel(HBCIUtils.time2StringLocal(date)),constr);
                
                constr.gridx=0;constr.gridy++;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("BLZ")),constr);
                constr.gridx++;
                table.add(new JLabel(passport.getBLZ()),constr);
                
                constr.gridx=0;constr.gridy++;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("USERID")),constr);
                constr.gridx++;
                table.add(new JLabel(passport.getUserId()),constr);
                
                constr.gridx=0;constr.gridy++;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("KEYNUM")),constr);
                constr.gridx++;
                table.add(new JLabel(passport.getMyPublicSigKey().num),constr);
                
                constr.gridx=0;constr.gridy++;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("KEYVERSION")),constr);
                constr.gridx++;
                table.add(new JLabel(passport.getMyPublicSigKey().version),constr);
                
                constr.gridx=0;constr.gridy++;constr.gridheight=8;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("EXPONENT")),constr);
                constr.gridx++;constr.gridheight=1;
                for (int i=0;i<=exp_st.length()/3/16;i++) {
                    if (i==0)
                        constr.insets=new Insets(8,0,1,8);
                    else
                        constr.insets=new Insets(1,0,1,8);
                    
                    l=new JLabel(exp_st.substring(i*16*3, Math.min((i+1)*16*3-1, exp_st.length())));
                    l.setFont(font);
                    l.setForeground(Color.BLUE);
                    table.add(l,constr);
                    constr.gridy++;
                }
                
                constr.gridx=0;constr.gridheight=8;
                constr.insets=new Insets(4,0,4,8);
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("MODULUS")),constr);
                constr.gridx++;constr.gridheight=1;
                for (int i=0;i<=mod_st.length()/3/16;i++) {
                    if (i==0)
                        constr.insets=new Insets(8,0,1,8);
                    else
                        constr.insets=new Insets(1,0,1,8);
                    
                    l=new JLabel(mod_st.substring(i*16*3, Math.min((i+1)*16*3-1, mod_st.length())));
                    l.setFont(font);
                    l.setForeground(Color.BLUE);
                    table.add(l,constr);
                    constr.gridy++;
                }
                
                constr.gridx=0;constr.gridheight=2;
                table.add(new JLabel(HBCIUtilsInternal.getLocMsg("HASH")),constr);
                constr.insets=new Insets(4,0,4,8);
                constr.gridx++;constr.gridheight=1;
                for (int i=0;i<=hash_st.length()/3/10;i++) {
                    if (i==0)
                        constr.insets=new Insets(8,0,1,8);
                    else
                        constr.insets=new Insets(1,0,1,8);
                    
                    l=new JLabel(hash_st.substring(i*10*3, Math.min((i+1)*10*3-1, hash_st.length())));
                    l.setFont(font);
                    l.setForeground(Color.MAGENTA);
                    table.add(l,constr);
                    constr.gridy++;
                }
                
                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                mainbox.add(Box.createVerticalStrut(4));
                
                box2.add(Box.createHorizontalGlue());
                JButton ok=new JButton("OK");
                box2.add(ok);
                box2.add(Box.createHorizontalGlue());
                ((JComponent)win).getRootPane().setDefaultButton(ok);
                
                ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                    removeWin(currentData,winname);
                    sync.stopWaiting();
                }});
                
                ok.requestFocus();
                sync.stopWaiting();
                drawWin(currentData,winname);
            } catch (Exception e) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_ERR"),e);
            }
        }});
        
        sync.startWaiting();
    }
    
    protected void showInstMessage(final Hashtable<String, Object> currentData,final String winname)
    {
        showInstMessage(currentData,winname,true);
    }
    
    protected void showInstMessage(final Hashtable<String, Object> currentData,final String winname,final boolean blocking)
    {
        final SyncObject sync=new SyncObject();
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final int       msgcounter=((Integer)currentData.get("msgcounter")).intValue();
            final String    winname2=winname+"_"+(msgcounter);
            final Container win=createWin(currentData,"HBCI",winname2);
            currentData.put("msgcounter",new Integer(msgcounter+1));
            
            Box framebox=Box.createHorizontalBox();
            win.add(framebox);
            
            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));
            
            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);
            
            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            JLabel label=new JLabel(HBCIUtilsInternal.getLocMsg("GUI_HAVEINSTMSG"));
            label.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(label);
            box2.add(Box.createHorizontalGlue());
            
            mainbox.add(Box.createVerticalStrut(4));
            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());
            
            mainbox.add(Box.createVerticalStrut(8));
            
            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));
            
            box2.add(Box.createHorizontalGlue());
            JButton cancel=new JButton("OK");
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());
            ((JComponent)win).getRootPane().setDefaultButton(cancel);
            
            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname2);
                sync.stopWaiting();
            }});
            
            if (!blocking) {
                sync.stopWaiting();
            }
            
            drawWin(currentData,winname2);
        }});
        
        sync.startWaiting();
    }

    protected void showConnectionMessage(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI",winname);
            
            Box framebox=Box.createHorizontalBox();
            win.add(framebox);
            
            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));
            
            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);
            
            JLabel l=new JLabel("Passport: "+
                    (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());
            
            mainbox.add(Box.createVerticalStrut(8));
            
            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));
            
            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            box2.add(Box.createHorizontalGlue());
            ((JComponent)win).getRootPane().setDefaultButton(ok);
            
            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname);
                sync.stopWaiting();
            }});
            
            drawWin(currentData,winname);
        }});
        
        sync.startWaiting();
    }

    private void needSIZEntrySelect(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();

        final boolean[] aborted=new boolean[1];
        aborted[0]=false;

        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI",winname);

            Box framebox=Box.createHorizontalBox();
            win.add(framebox);

            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));

            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);

            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(10));

            final StringBuffer retData=(StringBuffer)currentData.get("retData");
            
            String[] tableCols={"ID",HBCIUtilsInternal.getLocMsg("BLZ"),HBCIUtilsInternal.getLocMsg("USERID")};
            ArrayList<String[]> data=new ArrayList<String[]>();
            StringTokenizer tok=new StringTokenizer(retData.toString(),"|");
            while (tok.hasMoreTokens()) {
                String entry=tok.nextToken();
                StringTokenizer tok2=new StringTokenizer(entry,";");
                data.add(new String[] {tok2.nextToken(),tok2.nextToken(),tok2.nextToken()});
            }
            String[][] tableData= data.toArray(new String[data.size()][]);
            final JTable table=new JTable(tableData,tableCols);
            table.setCellSelectionEnabled(false);
            table.setColumnSelectionAllowed(false);
            table.setRowSelectionAllowed(true);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            mainbox.add(table);
            
            mainbox.add(Box.createVerticalStrut(10));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));

            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            ((JComponent)win).getRootPane().setDefaultButton(ok);

            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());

            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                retData.replace(0,retData.length(),table.getModel().getValueAt(table.getSelectedRow(),0).toString());
                removeWin(currentData,winname);
                sync.stopWaiting();
            }});

            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname);
                aborted[0]=true;
                sync.stopWaiting();
            }});

            drawWin(currentData,winname);
        }});

        sync.startWaiting();
        if (aborted[0])
            throw new AbortedException(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_ABORT")); 
    }

    private void needPTSecMech(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();

        final boolean[] aborted=new boolean[1];
        aborted[0]=false;

        SwingUtilities.invokeLater(new Runnable() { public void run() {
            final Container win=createWin(currentData,"HBCI",winname);

            Box framebox=Box.createHorizontalBox();
            win.add(framebox);

            framebox.add(Box.createHorizontalStrut(8));
            Box mainbox=Box.createVerticalBox();
            framebox.add(mainbox);
            framebox.add(Box.createHorizontalStrut(8));

            mainbox.add(Box.createVerticalStrut(8));
            Box box2=Box.createHorizontalBox();
            mainbox.add(box2);

            JLabel l=new JLabel("Passport: "+
                                (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
            l.setFont(new Font("Arial",Font.PLAIN,10));
            box2.add(Box.createHorizontalGlue());
            box2.add(l);
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(8));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            box2.add(new JLabel((String)currentData.get("msg")));
            box2.add(Box.createHorizontalGlue());

            mainbox.add(Box.createVerticalStrut(10));

            final StringBuffer retData=(StringBuffer)currentData.get("retData");
            
            String[]  tableCols={"ID","Name"};
            ArrayList<String[]> data=new ArrayList<String[]>();
            String[]  entries=retData.toString().split("\\|");
            int       len=entries.length;
            for (int i=0;i<len;i++) {
                String   entry=entries[i];
                String[] values=entry.split(":");
                data.add(new String[] {values[0], values[1]});
            }
            String[][]   tableData= data.toArray(new String[data.size()][]);
            final JTable table=new JTable(tableData,tableCols);
            table.setCellSelectionEnabled(false);
            table.setColumnSelectionAllowed(false);
            table.setRowSelectionAllowed(true);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            mainbox.add(table);
            
            mainbox.add(Box.createVerticalStrut(10));

            box2=Box.createHorizontalBox();
            mainbox.add(box2);
            mainbox.add(Box.createVerticalStrut(4));

            box2.add(Box.createHorizontalGlue());
            JButton ok=new JButton("OK");
            box2.add(ok);
            ((JComponent)win).getRootPane().setDefaultButton(ok);

            JButton cancel=new JButton(HBCIUtilsInternal.getLocMsg("CANCEL"));
            box2.add(cancel);
            box2.add(Box.createHorizontalGlue());

            ok.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                retData.replace(0,retData.length(),table.getModel().getValueAt(table.getSelectedRow(),0).toString());
                removeWin(currentData,winname);
                sync.stopWaiting();
            }});

            cancel.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                removeWin(currentData,winname);
                aborted[0]=true;
                sync.stopWaiting();
            }});

            drawWin(currentData,winname);
        }});

        sync.startWaiting();
        if (aborted[0])
            throw new AbortedException(HBCIUtilsInternal.getLocMsg("EXCMSG_USR_ABORT")); 
    }

    private void ackInfoPoint(final Hashtable<String, Object> currentData,final String winname)
    {
        final SyncObject sync=new SyncObject();
        
        SwingUtilities.invokeLater(new Runnable() { public void run() {
            try {
                final Container win=createWin(currentData,"HBCI",winname);
                
                Box framebox=Box.createHorizontalBox();
                win.add(framebox);
                
                framebox.add(Box.createHorizontalStrut(8));
                Box mainbox=Box.createVerticalBox();
                framebox.add(mainbox);
                framebox.add(Box.createHorizontalStrut(8));
                
                mainbox.add(Box.createVerticalStrut(8));
                Box box2=Box.createHorizontalBox();
                mainbox.add(box2);
                
                JLabel l=new JLabel("Passport: "+
                                    (String)((HBCIPassport)currentData.get("passport")).getClientData("init"));
                l.setFont(new Font("Arial",Font.PLAIN,10));
                box2.add(Box.createHorizontalGlue());
                box2.add(l);
                box2.add(Box.createHorizontalGlue());
    
                mainbox.add(Box.createVerticalStrut(8));
    
                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                box2.add(new JLabel(HBCIUtilsInternal.getLocMsg("GUI_ACKINFOPOINT")));
                box2.add(Box.createHorizontalGlue());
                
                mainbox.add(Box.createVerticalStrut(10));
                
                box2=Box.createHorizontalBox();
                mainbox.add(box2);
                mainbox.add(Box.createVerticalStrut(4));
                
                box2.add(Box.createHorizontalGlue());
                JButton yes=new JButton(HBCIUtilsInternal.getLocMsg("YES"));
                box2.add(yes);
                ((JComponent)win).getRootPane().setDefaultButton(yes);
                
                JButton no=new JButton(HBCIUtilsInternal.getLocMsg("NO"));
                box2.add(no);
                box2.add(Box.createHorizontalGlue());
                
                final StringBuffer retData=(StringBuffer)currentData.get("retData");
                yes.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                    retData.replace(0,retData.length(),"");
                    removeWin(currentData,winname);
                    sync.stopWaiting();
                }});
                
                no.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) {
                    retData.replace(0,retData.length(),"ERROR");
                    removeWin(currentData,winname);
                    sync.stopWaiting();
                }});
                
                yes.requestFocus();
                drawWin(currentData,winname);
            } catch (Exception e) {
                throw new HBCI_Exception(HBCIUtilsInternal.getLocMsg("EXCMSG_CALLB_ERR"),e);
            }
        }});
        
        sync.startWaiting();
    }
    
    protected Container createWin(Hashtable<String, Object> currentData,String title,String winname)
    {
        JDialog swingDialog=new JDialog((JFrame)(null),title,DIALOG_MODAL);
        
        swingDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        swingDialog.setResizable(false);
        currentData.put("win_"+winname,swingDialog);
        
        return swingDialog.getContentPane();
    }
    
    protected void removeWin(Hashtable<String, Object> currentData,String winname)
    {
        JDialog swingDialog=(JDialog)currentData.get("win_"+winname);
        swingDialog.dispose();
    }
    
    protected void drawWin(Hashtable<String, Object> currentData,String winname)
    {
        JDialog swingDialog=(JDialog)currentData.get("win_"+winname);
        swingDialog.pack();
        swingDialog.setVisible(true);
    }
}
