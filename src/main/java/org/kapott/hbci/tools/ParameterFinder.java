/**********************************************************************
 *
 * This file is part of HBCI4Java.
 * Copyright (c) 2019 Olaf Willuhn
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

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Properties;

import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.HBCIUtils;

/**
 * Parser zum bequemen Zugriff auf BPD/UPD-Parameter.
 */
public class ParameterFinder
{
    /**
     * Liste bekannter Queries.
     */
    public static class Query
    {
        /**
         * Informationen zur Verfuegbarkeit von Einschritt-Verfahren in den BPD.
         */
        public final static Query BPD_PINTAN_CAN1STEP = new Query("Params_*.TAN2StepPar*.ParTAN2Step*.can1step",false);

        /**
         * Information ueber den Order-Hashmode. Ist ein Query, welches einen Parameter benoetigt.
         */
        public final static Query BPD_PINTAN_ORDERHASHMODE = new Query("Params_*.TAN2StepPar{0}.ParTAN2Step*.orderhashmode",true);

        /**
         * Die 3 verschiedenen Parameter für status refresh requests beim Decoupled Verfahren:
         *  1. Minimale Zeit vor dem ersten refresh (Sekunden).
         *  2. Minimale Zeit vor weiteren refreshes (Sekunden).
         *  3. Maximale Anzahl von refreshes.
         */
        public final static Query BPD_DECOUPLED_TIME_BEFORE_FIRST_STATUS_REQUEST = new Query("Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams_*.decoupled_time_before_first_status_request",false);
        public final static Query BPD_DECOUPLED_TIME_BEFORE_NEXT_STATUS_REQUEST = new Query("Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams_*.decoupled_time_before_next_status_request",false);
        public final static Query BPD_DECOUPLED_MAX_STATUS_REQUESTS = new Query("Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams_*.decoupled_max_status_requests",false);

        private String query = null;
        private boolean paramsSet = false;
        
        /**
         * ct.
         * @param q das Query.
         * @param needParams true, wenn das Query Parameter benoetigt.
         */
        private Query(String q, boolean needParams)
        {
            this.query = q;
            this.paramsSet = !needParams;
        }
        
        /**
         * Liefert eine neue Instanz des Querys mit gesetzten Parametern.
         * @param parameters die Parameter.
         * @return das neue Query.
         */
        public final Query withParameters(Object... parameters)
        {
            return new Query(MessageFormat.format(this.query,parameters),false);
        }
        
        /**
         * Liefert das Query.
         * @return das Query.
         */
        public String getQuery()
        {
            if (!this.paramsSet)
                throw new HBCI_Exception("Parameters not set in query: " + this.query);
            
            return this.query;
        }
    }

    /**
     * Sucht in props nach allen Schluesseln im genannten Pfad und liefert sie zurueck.
     * @param props die Properties, in denen gesucht werden soll.
     * @param query das Query.
     * @return Liefert die gefundenen Properties. Niemals NULL sondern hoechstens leere Properties.
     * Als Schluessel wird jeweils nicht der gesamte Pfad verwendet sondern nur der Teil hinter dem letzten Punkt.
     */
    public static Properties find(Properties props, Query query)
    {
        return find(props,query != null ? query.getQuery() : null);
    }

    /**
     * Sucht in props nach allen Schluesseln im genannten Pfad und liefert sie zurueck.
     * @param props die Properties, in denen gesucht werden soll.
     * @param path der Pfad. Es koennen Wildcards verwendet werden.
     * Etwa so: Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams*.*secfunc")
     * @return Liefert die gefundenen Properties. Niemals NULL sondern hoechstens leere Properties.
     * Als Schluessel wird jeweils nicht der gesamte Pfad verwendet sondern nur der Teil hinter dem letzten Punkt.
     */
    public static Properties find(Properties props, String path)
    {
        // Kein Pfad angegeben. Also treffen alle.
        if (path == null || path.length() == 0)
            return props != null ? props : new Properties();

        // Die neue Map fuer die naechste Runde
        Properties next = new Properties();

        String[] keys = path.split("\\.");
        String key = keys[0];

        boolean endsWith = key.startsWith("*");
        boolean startsWith = key.endsWith("*");
        key = key.replace("*", "");

        Enumeration e = props.keys();
        while (e.hasMoreElements())
        {
            String name = (String) e.nextElement();

            String[] names = name.split("\\.");

            if (startsWith && !endsWith && !names[0].startsWith(key)) // Beginnt mit?
                continue;
            else if (!startsWith && endsWith && !names[0].endsWith(key)) // Endet mit?
                continue;
            else if (startsWith && endsWith && !names[0].contains(key)) // Enthaelt?
                continue;
            else if (!startsWith && !endsWith && !names[0].equals(key)) // Ist gleich?
                continue;

            // Wenn wir einen Wert haben, uebernehmen wir ihn in die naechste Runde.
            // Wir schneiden den geprueften Teil ab
            String newName = name.substring(name.indexOf(".") + 1);
            next.put(newName, props.getProperty(name));
        }

        // Wir sind hinten angekommen
        if (!path.contains("."))
            return next;

        // naechste Runde
        return find(next, path.substring(path.indexOf(".") + 1));
    }

    /**
     * Sucht in props nach allen Schluesseln im genannten Pfad und liefert sie zurueck.
     * Als Schluessel bleibt hierbei jedoch der gesamte Pfad erhalten. Das ist sinnvoll,
     * wenn man ueber grosse Bereiche sucht und die Namen des letzen Elements im Baum gleich lauten koennen.
     * @param props die Properties, in denen gesucht werden soll.
     * @param query das Query.
     * @return Liefert die gefundenen Properties. Niemals NULL sondern hoechstens leere Properties.
     */
    public static Properties findAll(Properties props, Query query)
    {
        return findAll(props,query != null ? query.getQuery() : null);
    }
    
    /**
     * Sucht in props nach allen Schluesseln im genannten Pfad und liefert sie zurueck.
     * Als Schluessel bleibt hierbei jedoch der gesamte Pfad erhalten. Das ist sinnvoll,
     * wenn man ueber grosse Bereiche sucht und die Namen des letzen Elements im Baum gleich lauten koennen.
     * @param props die Properties, in denen gesucht werden soll.
     * @param path der Pfad. Es koennen Wildcards verwendet werden.
     * Etwa so: Params_*.TAN2StepPar*.ParTAN2Step*.TAN2StepParams*.*secfunc")
     * @return Liefert die gefundenen Properties. Niemals NULL sondern hoechstens leere Properties.
     */
    public static Properties findAll(Properties props, String path)
    {
        // Kein Pfad angegeben. Also treffen alle.
        if (path == null || path.length() == 0)
            return props != null ? props : new Properties();

        // Die Restmenge, nachdem wir durch sind
        Properties rest = new Properties();
        rest.putAll(props);

        for (int i = 0; i < 100; ++i)
        {
            String[] keys = path.split("\\.");
            if (keys.length < i + 1)
                break; // Ende

            String key = keys[i];

            boolean endsWith = key.startsWith("*");
            boolean startsWith = key.endsWith("*");
            key = key.replace("*", "");

            Enumeration e = props.keys();
            while (e.hasMoreElements())
            {
                String name = (String) e.nextElement();

                String[] names = name.split("\\.");
                if (names.length < i + 1)
                {
                    rest.remove(name);
                    continue;
                }

                boolean b1 = (startsWith && !endsWith && names[i].startsWith(key)); // Beginnt mit?
                boolean b2 = (!startsWith && endsWith && names[i].endsWith(key)); // Endet mit?
                boolean b3 = (startsWith && endsWith && names[i].contains(key)); // Enthaelt?
                boolean b4 = (!startsWith && !endsWith && names[i].equals(key)); // Ist gleich?

                if (!b1 && !b2 && !b3 && !b4)
                {
                    rest.remove(name);
                }
            }
        }

        return rest;
    }
    
    /**
     * Liefert einen einzelnen Wert.
     * Die Funktion loggt eine Warnung, wenn der gefundene Wert nicht eindeutig ist.
     * @param props die Properties.
     * @param query das Query.
     * @param defaultValue der Default-Wert, falls kein Wert gefunden wurde.
     * @return der gefundene Wert oder der Default-Wert.
     */
    public static String getValue(Properties props, Query query, String defaultValue)
    {
        return getValue(props,query != null ? query.getQuery() : null,defaultValue);
    }
    
    /**
     * Liefert einen einzelnen Wert.
     * Die Funktion loggt einen Hinweis, wenn der gefundene Wert nicht eindeutig ist.
     * @param props die Properties.
     * @param path der Pfad.
     * @param defaultValue der Default-Wert, falls kein Wert gefunden wurde.
     * @return der gefundene Wert oder der Default-Wert.
     */
    public static String getValue(Properties props, String path, String defaultValue)
    {
        Properties result = findAll(props,path);
        if (result == null || result.size() == 0)
            return defaultValue;
        
        if (result.size() > 1)
            HBCIUtils.log("query " + path + " mode ambiguous, found multiple values: " + result,HBCIUtils.LOG_INFO);
        
        // Liefert den ersten Treffer
        String s = (String) result.values().iterator().next();
        return s != null ? s : defaultValue;
    }

}
