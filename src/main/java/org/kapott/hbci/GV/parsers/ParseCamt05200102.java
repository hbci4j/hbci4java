/**********************************************************************
 *
 * Copyright (c) 2018 Olaf Willuhn
 * All rights reserved.
 * 
 * This software is copyrighted work licensed under the terms of the
 * Jameica License.  Please consult the file "LICENSE" for details. 
 *
 **********************************************************************/

package org.kapott.hbci.GV.parsers;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.JAXB;

import org.kapott.hbci.GV.SepaUtil;
import org.kapott.hbci.GV_Result.GVRKUms;
import org.kapott.hbci.GV_Result.GVRKUms.BTag;
import org.kapott.hbci.GV_Result.GVRKUms.UmsLine;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.AccountIdentification4Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.AccountReport11;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.ActiveOrHistoricCurrencyAndAmount;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.BalanceType12Code;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.BankToCustomerAccountReportV02;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.BankTransactionCodeStructure4;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.BranchAndFinancialInstitutionIdentification4;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.CashAccount16;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.CashAccount20;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.CashBalance3;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.CreditDebitCode;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.DateAndDateTimeChoice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.Document;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.EntryDetails1;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.EntryTransaction2;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.FinancialInstitutionIdentification7;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.PartyIdentification32;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.Purpose2Choice;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.ReportEntry2;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.TransactionAgents2;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.TransactionParty2;
import org.kapott.hbci.sepa.jaxb.camt_052_001_02.TransactionReferences2;
import org.kapott.hbci.structures.Konto;
import org.kapott.hbci.structures.Saldo;
import org.kapott.hbci.structures.Value;

/**
 * Parser zum Lesen von Umsaetzen im CAMT.052 Format in Version 001.02.
 */
public class ParseCamt05200102 implements ISEPAParser<GVRKUms>
{
    /**
     * @see org.kapott.hbci.GV.parsers.ISEPAParser#parse(java.io.InputStream, java.lang.Object)
     */
    @Override
    public void parse(InputStream xml, GVRKUms result)
    {
        
        Document doc = JAXB.unmarshal(xml, Document.class);
        BankToCustomerAccountReportV02 container = doc.getBkToCstmrAcctRpt();

        // Dokument leer
        if (container == null)
        {
            HBCIUtils.log("camt document empty",HBCIUtils.LOG_WARN);
            return;
        }

        // Enthaelt per Definition genau einen Report von einem Buchungstag
        List<AccountReport11> reports = container.getRpt();
        if (reports == null || reports.size() == 0)
        {
            HBCIUtils.log("camt document empty",HBCIUtils.LOG_WARN);
            return;
        }
        
        // Per Definition enthaelt die Datei beim CAMT-Abruf zwar genau einen Buchungstag.
        // Da wir aber eine passende Datenstruktur haben, lesen wir mehr ein, falls
        // mehr vorhanden sind. Dann koennen wird den Parser spaeter auch nutzen,
        // um CAMT-Dateien aus anderen Quellen zu lesen.
        for (AccountReport11 report:reports)
        {
            ////////////////////////////////////////////////////////////////////
            // Kopf des Buchungstages
            BTag tag = this.createDay(report);
            result.getDataPerDay().add(tag);
            //
            ////////////////////////////////////////////////////////////////////

            ////////////////////////////////////////////////////////////////////
            // Die einzelnen Buchungen
            BigDecimal saldo = tag.start != null && tag.start.value != null ? tag.start.value.getBigDecimalValue() : BigDecimal.ZERO;
            
            for (ReportEntry2 entry:report.getNtry())
            {
                UmsLine line = this.createLine(entry,saldo);
                if (line != null)
                {
                    tag.lines.add(line);
                    
                    // Saldo fortschreiben
                    saldo = line.saldo.value.getBigDecimalValue();
                }
            }
            //
            ////////////////////////////////////////////////////////////////////
        }
    }

    /**
     * Erzeugt eine einzelne Umsatzbuchung.
     * @param entry der Entry aus der CAMT-Datei.
     * @param der aktuelle Saldo vor dieser Buchung.
     * @return die Umsatzbuchung.
     */
    private UmsLine createLine(ReportEntry2 entry, BigDecimal currSaldo)
    {
        UmsLine line = new UmsLine();
        line.isSepa = true;
        line.isCamt = true;
        line.other = new Konto();
        
        List<EntryDetails1> details = entry.getNtryDtls();
        if (details.size() == 0)
            return null;
        
        // Das Schema sieht zwar mehrere Detail-Elemente vor, ich wuesste
        // aber ohnehin nicht, wie man das sinnvoll mappen koennte 
        EntryDetails1 detail = details.get(0);
        
        List<EntryTransaction2> txList = detail.getTxDtls();
        if (txList.size() == 0)
            return null;
        
        // Checken, ob es Soll- oder Habenbuchung ist
        boolean haben = entry.getCdtDbtInd() != null && entry.getCdtDbtInd() == CreditDebitCode.CRDT;
        
        // ditto
        EntryTransaction2 tx = txList.get(0);
        
        ////////////////////////////////////////////////////////////////////////
        // Buchungs-ID
        TransactionReferences2 ref = tx.getRefs();
        line.id = ref.getPrtry() != null ? ref.getPrtry().getRef() : null;
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Gegenkonto: IBAN + Name
        TransactionParty2 other = tx.getRltdPties();
        if (other != null)
        {
            CashAccount16 acc = haben ? other.getDbtrAcct() : other.getCdtrAcct();
            AccountIdentification4Choice id = acc != null ? other.getDbtrAcct().getId() : null;
            line.other.iban = id != null ? id.getIBAN() : null;
            
            PartyIdentification32 name = haben ? other.getDbtr() : other.getCdtr();
            line.other.name = name != null ? name.getNm() : null;
        }
        //
        ////////////////////////////////////////////////////////////////////////
            
        ////////////////////////////////////////////////////////////////////////
        // Gegenkonto: BIC
        TransactionAgents2 banks = tx.getRltdAgts();
        if (banks != null)
        {
            BranchAndFinancialInstitutionIdentification4 bank = haben ? banks.getDbtrAgt() : banks.getCdtrAgt();
            FinancialInstitutionIdentification7 bic = bank != null ? bank.getFinInstnId() : null;
            line.other.bic = bank != null ? bic.getBIC() : null;
        }
        //
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Verwendungszweck
        List<String> usages = tx.getRmtInf() != null ? tx.getRmtInf().getUstrd() : null;
        if (usages != null && usages.size() > 0)
            line.usage.addAll(usages);
        //
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Betrag
        ActiveOrHistoricCurrencyAndAmount amt = entry.getAmt();
        BigDecimal bd = amt.getValue() != null ? amt.getValue() : BigDecimal.ZERO;
        line.value = new Value(haben ? bd : BigDecimal.ZERO.subtract(bd)); // Negativ-Betrag bei Soll-Buchung
        line.value.setCurr(amt.getCcy());
        //
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Storno-Kennzeichen
        // Laut Spezifikation kehrt sich bei Stornobuchungen im Gegensatz zu MT940
        // nicht das Vorzeichen um. Der Betrag bleibt also gleich
        line.isStorno = entry.isRvslInd() != null ? entry.isRvslInd().booleanValue() : false;
        //
        ////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////////////////////////////
        // Buchungs- und Valuta-Datum
        DateAndDateTimeChoice bdate = entry.getBookgDt();
        line.bdate = bdate != null ? SepaUtil.toDate(bdate.getDt()) : null;
        
        DateAndDateTimeChoice vdate = entry.getValDt();
        line.valuta = vdate != null ? SepaUtil.toDate(vdate.getDt()) : null;
        
        // Wenn einer von beiden Werten fehlt, uebernehmen wir dort den jeweils anderen
        if (line.bdate == null) line.bdate = line.valuta;
        if (line.valuta == null) line.valuta = line.bdate;
        //
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Saldo
        line.saldo = new Saldo();
        line.saldo.value = new Value(currSaldo.add(line.value.getBigDecimalValue()));
        line.saldo.timestamp = line.bdate;
        //
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Art und Kundenreferenz
        line.text = entry.getAddtlNtryInf();
        line.customerref = entry.getAcctSvcrRef();
        //
        ////////////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////////////
        // Primanota, GV-Code und GV-Code-Ergaenzung
        // Ich weiss nicht, ob das bei allen Banken so codiert ist.
        // Bei der Sparkasse ist es jedenfalls so.
        BankTransactionCodeStructure4 b = tx.getBkTxCd();
        String code = (b != null && b.getPrtry() != null) ? b.getPrtry().getCd() : null;
        if (code != null && code.contains("+"))
        {
            String[] parts = code.split("\\+");
            if (parts.length == 4)
            {
                line.gvcode    = parts[1];
                line.primanota = parts[2];
                line.addkey    = parts[3];
            }
        }
        //
        ////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////
        // Purpose-Code
        Purpose2Choice purp = tx.getPurp();
        line.purposecode = purp != null ? purp.getCd() : null;
        //
        ////////////////////////////////////////////////////////////////////////
        
        return line;
    }
    
    
    /**
     * Erzeugt einen neuen Buchungstag.
     * @param report der Report.
     * @return der erzeugte Buchungstag.
     */
    private BTag createDay(AccountReport11 report)
    {
        BTag tag = new BTag();
        tag.start = new Saldo();
        tag.end = new Saldo();
        tag.starttype = 'F';
        tag.endtype = 'F';

        ////////////////////////////////////////////////////////////////
        // Start- un End-Saldo ermitteln
        for (CashBalance3 bal:report.getBal())
        {
            BalanceType12Code code = bal.getTp().getCdOrPrtry().getCd();
            
            // Schluss-Saldo vom Vortag
            if (code == BalanceType12Code.PRCD)
            {
                tag.start.value = new Value(bal.getAmt().getValue());
                tag.start.value.setCurr(bal.getAmt().getCcy());
                tag.start.timestamp = SepaUtil.toDate(bal.getDt().getDt());
            }
            
            // End-Saldo
            else if (code == BalanceType12Code.CLBD)
            {
                tag.end.value = new Value(bal.getAmt().getValue());
                tag.end.value.setCurr(bal.getAmt().getCcy());
            }
        }
        
        // Bei CAMT gibt es keinen Start-Saldo des aktuellen Tages
        // sondern einen Schluss-Saldo des Vortages. Da per Definition
        // immer genau ein Tag in der Datei ist, koennen wir das
        // End-Datum auch als Start-Datum nehmen
        tag.end.timestamp = tag.start.timestamp;
        //
        ////////////////////////////////////////////////////////////////
        
        ////////////////////////////////////////////////////////////////
        // Das eigene Konto ermitteln
        CashAccount20 acc = report.getAcct();
        tag.my = new Konto();
        tag.my.iban = acc.getId().getIBAN();
        tag.my.curr = acc.getCcy();
        tag.my.bic  = acc.getSvcr().getFinInstnId().getBIC();
        ////////////////////////////////////////////////////////////////
        
        return tag;
    }
}


