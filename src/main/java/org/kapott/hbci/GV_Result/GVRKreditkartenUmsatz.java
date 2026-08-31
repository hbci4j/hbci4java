/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2026 Franz Bettag
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

package org.kapott.hbci.GV_Result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.kapott.hbci.structures.Value;
import org.kapott.hbci.tools.StringUtil;

/**
 * Ergebnis des Kreditkarten-Umsatzabrufs DKKKU/DIKKU.
 *
 * Die Buchungen werden zugleich als normale {@link GVRKUms.UmsLine}
 * bereitgestellt, damit Anwendungen ihre bestehende Umsatzverarbeitung
 * wiederverwenden koennen.
 */
public class GVRKreditkartenUmsatz extends GVRKUms
{
    private final List<UmsLine> booked = new ArrayList<UmsLine>();

    /**
     * Fuegt einen von HBCI4Java dekodierten DIKKU-Umsatz hinzu.
     * @param data dekodierte Nachrichtendaten.
     * @param header Property-Prefix des Umsatzes.
     */
    public void addTransaction(Properties data, String header)
    {
        UmsLine line = new UmsLine();
        line.bdate = parseDate(data.getProperty(header + ".bookingdate"),"Buchungsdatum");
        line.valuta = parseDate(data.getProperty(header + ".valuedate"),"Valutadatum");
        line.value = parseValue(data.getProperty(header + ".value"),
                                data.getProperty(header + ".currency"),
                                data.getProperty(header + ".creditdebit"),true);
        line.orig_value = parseValue(data.getProperty(header + ".originalvalue"),
                                     data.getProperty(header + ".originalcurrency"),
                                     data.getProperty(header + ".originalcreditdebit"),false);
        line.customerref = StringUtil.normalize(data.getProperty(header + ".reference"));
        line.text = "Kreditkartenzahlung";
        line.isCamt = true;

        String purpose = StringUtil.joinDescription(data.getProperty(header + ".purpose"),
                                                    data.getProperty(header + ".location"));
        line.addUsage(purpose != null ? purpose : "-");
        booked.add(line);
    }

    /**
     * @see org.kapott.hbci.GV_Result.GVRKUms#getFlatData()
     */
    @Override
    public List<UmsLine> getFlatData()
    {
        return new ArrayList<UmsLine>(booked);
    }

    /**
     * @see org.kapott.hbci.GV_Result.GVRKUms#getFlatDataUnbooked()
     */
    @Override
    public List<UmsLine> getFlatDataUnbooked()
    {
        return Collections.emptyList();
    }

    private static Date parseDate(String value, String label)
    {
        String normalized = StringUtil.normalize(value);
        if (normalized == null)
            throw new IllegalArgumentException("DKKKU-Umsatz ohne " + label);

        if (normalized.matches("[0-9]{8}"))
            normalized = normalized.substring(0,4) + "-" +
                         normalized.substring(4,6) + "-" +
                         normalized.substring(6,8);

        try
        {
            LocalDate date = LocalDate.parse(normalized);
            return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        catch (RuntimeException e)
        {
            throw new IllegalArgumentException("Ungueltiges DKKKU-" + label,e);
        }
    }

    private static Value parseValue(String amount, String currency, String creditDebit, boolean required)
    {
        String normalizedAmount = StringUtil.normalize(amount);
        String normalizedCurrency = StringUtil.normalize(currency);
        if (normalizedAmount == null || normalizedCurrency == null)
        {
            if (required)
                throw new IllegalArgumentException("DKKKU-Umsatz ohne Betrag oder Waehrung");
            return null;
        }

        try
        {
            BigDecimal value = new BigDecimal(StringUtil.normalizeDecimal(normalizedAmount));
            if ("D".equalsIgnoreCase(StringUtil.normalize(creditDebit)))
                value = value.abs().negate();
            else if ("C".equalsIgnoreCase(StringUtil.normalize(creditDebit)))
                value = value.abs();
            return new Value(value.toPlainString(),normalizedCurrency);
        }
        catch (NumberFormatException e)
        {
            throw new IllegalArgumentException("Ungueltiger DKKKU-Betrag",e);
        }
    }

}
