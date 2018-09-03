/**********************************************************************
 *
 * Copyright (c) 2018 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.GV.generators;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.kapott.hbci.GV_Result.GVRKUms.BTag;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.AccountReport22;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.ActiveOrHistoricCurrencyAndAmount;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.BalanceType10Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.BalanceType13;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.BankToCustomerAccountReportV07;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.BankTransactionCodeStructure4;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.BranchAndFinancialInstitutionIdentification5;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.CashAccount24;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.CashAccount36;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.CashBalance8;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.CreditDebitCode;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.DateAndDateTime2Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.Document;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.EntryDetails8;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.EntryStatus1Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.EntryTransaction9;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.FinancialInstitutionIdentification8;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.GroupHeader73;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.ObjectFactory;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.Party35Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.PartyIdentification125;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.ProprietaryBankTransactionCodeStructure1;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.ProprietaryReference1;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.Purpose2Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.RemittanceInformation15;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.ReportEntry9;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.TransactionAgents4;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.TransactionParties4;
import org.kapott.hbci.sepa.jaxb.camt_052_001_07.TransactionReferences3;
import org.kapott.hbci.structures.Saldo;

/**
 * Generator fuer CAMT-Dateien im Format CAMT052.001.07
 */
public class GenKUmsAllCamt05200107 extends AbstractSEPAGenerator<List<BTag>>
{
    /**
     * @see org.kapott.hbci.GV.generators.ISEPAGenerator#generate(java.lang.Object, java.io.OutputStream, boolean)
     */
    @Override
    public void generate(List<BTag> source, OutputStream os, boolean validate) throws Exception
    {
        final long now = System.currentTimeMillis();
        
        final Document doc = new Document();
        final BankToCustomerAccountReportV07 container = new BankToCustomerAccountReportV07();
        doc.setBkToCstmrAcctRpt(container);
        
        ///////////////////////////////////////////////////////////////////////////
        // Wir haben zwar keine Informationen fuer den Header. Er ist aber Pflicht.
        GroupHeader73 grphdr = new GroupHeader73();
        grphdr.setMsgId(Long.toString(now));
        grphdr.setCreDtTm(this.createCalendar(now));
        container.setGrpHdr(grphdr);
        //
        ///////////////////////////////////////////////////////////////////////////
        
        for (BTag tag:source)
        {
            AccountReport22 report = this.createDay(tag);
            
            // ID ist Pflicht. Wir tragen einfach immer die Typ-Bezeichnung ein.
            report.setId("camt05200107");
            report.setCreDtTm(this.createCalendar(now));
            
            container.getRpt().add(report);

            final List<ReportEntry9> entries = report.getNtry();
            for (UmsLine line:tag.lines)
            {
                entries.add(this.createLine(tag,line));
            }
        }
        
        ObjectFactory of = new ObjectFactory();
        this.marshal(of.createDocument(doc), os, validate);
    }
    
    /**
     * Erzeugt eine einzelne CAMT-Umsatzbuchung.
     * @param tag der Buchungstag.
     * @param line eine Umsatzbuchung aus HBCI4Java.
     * @return die CAMT-Umsatzbuchung.
     * @throws Exception
     */
    private ReportEntry9 createLine(BTag tag, UmsLine line) throws Exception
    {
        ReportEntry9 entry = new ReportEntry9();
        
        EntryStatus1Choice status = new EntryStatus1Choice();
        status.setCd("BOOK");
        entry.setSts(status);
        
        EntryDetails8 detail = new EntryDetails8();
        entry.getNtryDtls().add(detail);
        
        EntryTransaction9 tx = new EntryTransaction9();
        detail.getTxDtls().add(tx);
        
        // Checken, ob es Soll- oder Habenbuchung ist
        boolean haben = line.value != null && line.value.getBigDecimalValue().compareTo(BigDecimal.ZERO) > 0;
        
        entry.setCdtDbtInd(haben ? CreditDebitCode.CRDT : CreditDebitCode.DBIT);
        
        ////////////////////////////////////////////////////////////////////////
        // Buchungs-ID
        {
            TransactionReferences3 ref = new TransactionReferences3();
            tx.setRefs(ref);
            
            ProprietaryReference1 prt = new ProprietaryReference1();
            prt.setTp("UNKNOWN"); // Feld ist Pficht, wir haben aber nichts zum Reinschreiben
            prt.setRef(line.id);
            ref.getPrtry().add(prt);
        }
        //
        ////////////////////////////////////////////////////////////////////////

        TransactionParties4 other = new TransactionParties4();
        tx.setRltdPties(other);

        TransactionAgents4 banks = new TransactionAgents4();
        tx.setRltdAgts(banks);

        ////////////////////////////////////////////////////////////////////////
        // Eigenes Konto: IBAN + Name
        if (tag != null && tag.my != null)
        {
            // IBAN
            CashAccount24 acc = new CashAccount24();
            if (haben)
                other.setCdtrAcct(acc);
            else
                other.setDbtrAcct(acc);
            
            AccountIdentification4Choice id = new AccountIdentification4Choice();
            acc.setId(id);
            id.setIBAN(tag.my.iban);
            
            // Name
            Party35Choice party = new Party35Choice();
            PartyIdentification125 pi = new PartyIdentification125();
            pi.setNm(tag.my.name);
            party.setPty(pi);
            
            if (haben)
                other.setCdtr(party);
            else
                other.setDbtr(party);

            // BIC
            BranchAndFinancialInstitutionIdentification5 bank = new BranchAndFinancialInstitutionIdentification5();
            if (haben)
                banks.setCdtrAgt(bank);
            else
                banks.setDbtrAgt(bank);
            
            FinancialInstitutionIdentification8 bic = new FinancialInstitutionIdentification8();
            bank.setFinInstnId(bic);
            bic.setBICFI(tag.my.bic);
        }
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Gegenkonto: IBAN + Name + BIC
        if (line.other != null)
        {
            CashAccount24 acc = new CashAccount24();
            if (haben)
                other.setDbtrAcct(acc);
            else
                other.setCdtrAcct(acc);
            
            // IBAN
            AccountIdentification4Choice id = new AccountIdentification4Choice();
            acc.setId(id);
            id.setIBAN(line.other.iban);

            // Name
            Party35Choice party = new Party35Choice();
            PartyIdentification125 pi = new PartyIdentification125();
            pi.setNm(line.other.name);
            party.setPty(pi);
            
            if (haben)
                other.setDbtr(party);
            else
                other.setCdtr(party);

            // BIC
            BranchAndFinancialInstitutionIdentification5 bank = new BranchAndFinancialInstitutionIdentification5();
            if (haben)
                banks.setDbtrAgt(bank);
            else
                banks.setCdtrAgt(bank);
            
            FinancialInstitutionIdentification8 bic = new FinancialInstitutionIdentification8();
            bank.setFinInstnId(bic);
            bic.setBICFI(line.other.bic);
        }
        //
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Verwendungszweck
        if (line.usage != null && line.usage.size() > 0)
        {
            RemittanceInformation15 usages = new RemittanceInformation15();
            usages.getUstrd().addAll(line.usage);
            tx.setRmtInf(usages);
        }
        //
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Betrag
        if (line.value != null)
        {
            ActiveOrHistoricCurrencyAndAmount amt = new ActiveOrHistoricCurrencyAndAmount();
            entry.setAmt(amt);
            
            BigDecimal val = line.value.getBigDecimalValue();
            amt.setValue(val.abs()); // Hier gibt es keine negativen Werte. Wir haben stattdessen DEB/CRED-Merkmale
            amt.setCcy(line.value.getCurr());
        }
        //
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Storno-Kennzeichen
        if (line.isStorno)
            entry.setRvslInd(Boolean.TRUE);
        //
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Buchungs- und Valuta-Datum
        Date bdate  = line.bdate;
        Date valuta = line.valuta;
        
        if (bdate == null) bdate = valuta;
        if (valuta == null) valuta = bdate;
        
        if (bdate != null)
        {
            DateAndDateTime2Choice d = new DateAndDateTime2Choice();
            d.setDt(this.createCalendar(bdate.getTime()));
            entry.setBookgDt(d);
        }
        
        if (valuta != null)
        {
            DateAndDateTime2Choice d = new DateAndDateTime2Choice();
            d.setDt(this.createCalendar(valuta.getTime()));
            entry.setValDt(d);
        }
        //
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Art und Kundenreferenz
        entry.setAddtlNtryInf(line.text);
        entry.setAcctSvcrRef(line.customerref);
        //
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Primanota, GV-Code und GV-Code-Ergaenzung
        StringBuilder sb = new StringBuilder();
        if (line.gvcode != null && line.gvcode.length() > 0)
        {
            sb.append(line.gvcode);
            sb.append("+");
        }
        if (line.primanota != null && line.primanota.length() > 0)
        {
            sb.append(line.primanota);
            sb.append("+");
        }
        if (line.addkey != null && line.addkey.length() > 0)
        {
            sb.append(line.addkey);
        }
        
        String s = sb.toString();
        if (s.length() > 0)
        {
            BankTransactionCodeStructure4 b = new BankTransactionCodeStructure4();
            tx.setBkTxCd(b);
            
            ProprietaryBankTransactionCodeStructure1 pb = new ProprietaryBankTransactionCodeStructure1();
            pb.setCd(s);
            b.setPrtry(pb);
        }
        //
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Purpose-Code
        if (line.purposecode != null)
        {
            Purpose2Choice c = new Purpose2Choice();
            c.setCd(line.purposecode);
            tx.setPurp(c);
        }
        //
        ////////////////////////////////////////////////////////////////////////

        return entry;
    }
    
    /**
     * Erzeugt den Header des Buchungstages.
     * @param tag der Tag.
     * @return der Header des Buchungstages.
     * @throws Exception
     */
    private AccountReport22 createDay(BTag tag) throws Exception
    {
        AccountReport22 report = new AccountReport22();
        
        if (tag != null)
        {
            report.getBal().add(this.createSaldo(tag.start,true));
            report.getBal().add(this.createSaldo(tag.end,false));
        }

        if (tag != null && tag.my != null)
        {
            CashAccount36 acc = new CashAccount36();
            report.setAcct(acc);
            
            AccountIdentification4Choice id = new AccountIdentification4Choice();
            id.setIBAN(tag.my.iban);
            acc.setId(id);
            
            acc.setCcy(tag.my.curr);
            
            BranchAndFinancialInstitutionIdentification5 svc = new BranchAndFinancialInstitutionIdentification5();
            acc.setSvcr(svc);
            
            FinancialInstitutionIdentification8 inst = new FinancialInstitutionIdentification8();
            svc.setFinInstnId(inst);
            
            inst.setBICFI(tag.my.bic);
        }
        
        return report;
    }
    
    /**
     * Erzeugt ein Saldo-Objekt.
     * @param saldo das HBCI4Java-Saldo-Objekt.
     * @param start true, wenn es ein Startsaldo ist.
     * @return das CAMT-Saldo-Objekt.
     * @throws Exception
     */
    private CashBalance8 createSaldo(Saldo saldo, boolean start) throws Exception
    {
        CashBalance8 bal = new CashBalance8();
        
        BalanceType13 bt = new BalanceType13();
        bt.setCdOrPrtry(new BalanceType10Choice());
        bt.getCdOrPrtry().setCd(start ? "PRCD" : "CLBD");
        bal.setTp(bt);
        
        ActiveOrHistoricCurrencyAndAmount amt = new ActiveOrHistoricCurrencyAndAmount();
        bal.setAmt(amt);
        
        if (saldo != null && saldo.value != null)
        {
            amt.setCcy(saldo.value.getCurr());
            amt.setValue(saldo.value.getBigDecimalValue());
            
            // Checken, ob es Soll- oder Habenbuchung ist
            boolean haben = saldo.value.getBigDecimalValue().compareTo(BigDecimal.ZERO) > 0;
            bal.setCdtDbtInd(haben ? CreditDebitCode.CRDT : CreditDebitCode.DBIT);
        }
        
        long ts = saldo != null && saldo.timestamp != null ? saldo.timestamp.getTime() : 0;
        
        // Startsaldo ist in CAMT der Endsaldo vom Vortag. Daher muessen wir noch einen Tag abziehen
        if (start && ts > 0)
            ts -= 24 * 60 * 60 * 1000L;

        DateAndDateTime2Choice date = new DateAndDateTime2Choice();
        date.setDt(this.createCalendar(ts));
        bal.setDt(date);
        
        return bal;
    }
    
    /**
     * Erzeugt ein Calendar-Objekt.
     * @param timestamp der Zeitstempel.
     * @return das Calendar-Objekt.
     * @throws Exception
     */
    private XMLGregorianCalendar createCalendar(Long timestamp) throws Exception
    {
        DatatypeFactory df = DatatypeFactory.newInstance();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timestamp != null ? timestamp.longValue() : System.currentTimeMillis());
        XMLGregorianCalendar xml = df.newXMLGregorianCalendar(cal);
        
        // Explizit die Zeitzone streichen - die landet sonst mit im XML
        xml.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        return xml;
    }
}


