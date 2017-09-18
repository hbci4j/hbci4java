/**********************************************************************
 * $Source:
 * /cvsroot/hibiscus/hbci4java/src/org/kapott/hbci/tools/UpdateBLZProperties.java,v
 * $ $Revision: 1.1 $ $Date: 2012/04/16 22:24:40 $ $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services All rights reserved
 *
 **********************************************************************/

package org.kapott.hbci.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Util-Klasse, welche die FinTS-Bankenliste im CSV-Format (kann von
 * http://www.hbci-zka.de/institute/institut_hersteller.htm bezogen werden)
 * parst, un die aktualisierten Daten in die blz.properties von HBCI4Java
 * uebernimmt.
 */
public class UpdateBLZProperties
{
	private final static Charset	CHARSET_INPUT	= StandardCharsets.ISO_8859_1;
	private final static Charset	CHARSET_OUTPUT	= StandardCharsets.UTF_8;

	/**
	 * @param args
	 *            1. Pfad/Dateiname zu "fints_institute.csv". 2. Pfad/Dateiname
	 *            zu "blz.properties". 3. Pfad/Dateiname zur neuen
	 *            "blz.properties". 4. optional: Pfad/Dateiname zur BLZ-Datei
	 *            der Bundesbank. Falls angegeben, werden die eventuell
	 *            vorhandene BIC-Updates übernommen
	 * @throws Exception
	 */
	public static void main ( String[] args ) throws Exception
	{
		if (args == null || args.length < 3 || args.length > 4)
		{
			System.err.println("benoetigte Parameter: 1) fints_institute.csv, 2) blz.properties, 3) zu schreibende blz.properties");
			System.err.println("optionaler Parameter: 4) BLZ_20160606.txt (BLZ-Update der Bundesbank)");
			System.exit(1);
		}

		BufferedReader f1 = null;
		BufferedReader f2 = null;
		BufferedWriter f3 = null;
		BufferedReader f4 = null;
		String line = null;
		try
		{
			Map<String, String> lookup = new HashMap<String, String>();
			Map<String, String> versions = new HashMap<String, String>();
			Map<String, BICLine> bicLokup = new HashMap<String, BICLine>();

			//////////////////////////////////////////////////////////////////////////
			// fints_institute.csv lesen
			f1 = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]), CHARSET_INPUT));
			int count = 0;
			while ( ( line = f1.readLine() ) != null)
			{
				if (++count <= 1)
				{
					continue; // erste Zeile ueberspringen
				}
				if (line.trim().length() == 0)
				{
					continue; // leere Zeile
				}

				List<String> values = Arrays.asList(line.split(";"));

				if (values.size() <= 20)
				{
					continue; // Bank hat keine PIN/TAN-URL
				}

				String blz = values.get(1).trim();
				if (blz.length() == 0)
				{
					continue; // Die Zeile enthaelt keine BLZ
				}
				if (lookup.get(blz) != null)
				{
					continue; // die Zeile haben wir schon
				}

				String url = values.get(23).trim();
				if (url.length() == 0)
				{
					continue; // keine URL gefunden
				}

				lookup.put(blz, url);
				versions.put(blz, values.get(24).trim());
			}
			//
			//////////////////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////////////////
			// BLZ-Datei der Bundesbank einlesen, wenn vorhanden
			if (args.length == 4)
			{
				File f = new File(args[3]);
				if (f.exists() && f.isFile() && f.canRead())
				{
					f4 = new BufferedReader(new InputStreamReader(new FileInputStream(f), CHARSET_INPUT));
					while ( ( line = f4.readLine() ) != null)
					{
						BICLine current = new BICLine(line);
						if (current.blz == null || current.bic == null)
						{
							continue;
						}

						bicLokup.put(current.blz, current);
					}
				}
			}
			//
			//////////////////////////////////////////////////////////////////////////

			//////////////////////////////////////////////////////////////////////////
			// blz.properties lesen und abgleichen
			f2 = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), CHARSET_OUTPUT));
			f3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[2]), CHARSET_OUTPUT));
			while ( ( line = f2.readLine() ) != null)
			{
				String[] values = line.split("=");
				Line current = new Line(values[0], values[1]);

				String url = lookup.get(current.blz);
				String version = versions.get(current.blz);

				// Checken, ob sich die BIC geaendert hat
				if (f4 != null)
				{
					BICLine bic = bicLokup.get(current.blz);
					if (bic != null)
					{
						current.updateBic(bic.bic);
					}
				}

				// URL uebernehmen
				current.updateUrl(url);

				// Version uebernehmen
				current.updateVersion(version);

				// Neue Zeile schreiben
				f3.write(current.toString());
				f3.newLine();
			}
			//
			//////////////////////////////////////////////////////////////////////////
		}
		catch (Exception e)
		{
		  System.out.println("Fehler in Zeile: " + line);
		  throw e;
		}
		finally
		{
			if (f1 != null)
			{
				f1.close();
			}
			if (f2 != null)
			{
				f2.close();
			}
			if (f3 != null)
			{
				f3.close();
			}
			if (f4 != null)
			{
				f4.close();
			}
		}
	}

	/**
	 * Implementiert eine einzelne Zeile aus der BLZ-Datei der Bundesbank.
	 */
	private static class BICLine
	{
		private String	blz	= null;
		private String	bic	= null;

		/**
		 * ct.
		 *
		 * @param line
		 *            die Zeile aus der BLZ-Datei.
		 */
		private BICLine ( String line )
		{
			if (line == null || line.length() < 150)
			{
				return;
			}

			blz = line.substring(0, 8).trim();
			bic = line.substring(139, 150).trim();

			if (blz.length() == 0)
			{
				blz = null;
			}
			if (bic.length() == 0)
			{
				bic = null;
			}
		}
	}

	/**
	 * Implementiert eine einzelne Zeile der blz.properties
	 */
	private static class Line
	{
		private String		blz		= null;
		private String[]	values	= new String[9];

		/**
		 * ct.
		 *
		 * @param blz
		 *            die BLZ.
		 * @param line
		 *            die Zeile aus der blz.properties
		 */
		private Line ( String blz, String line )
		{
			this.blz = blz;
			String[] s = line.split("\\|");
			System.arraycopy(s, 0, this.values, 0, s.length);
		}

		/**
		 * Speichert die neue URL, wenn vorher keine da war oder eine andere.
		 *
		 * @param url
		 *            die neue URL.
		 */
		private void updateUrl ( String url )
		{
			// Keine neue URL
			if (url == null || url.length() == 0)
			{
				return;
			}

			String current = this.values[5];
			current = current != null ? current.trim() : "";
			if (!current.equals(url))
			{
				System.out.println(blz + ": URL \"" + current + "\" -> \"" + url + "\"");
				this.values[5] = url;
			}
		}

		/**
		 * Speichert die neue BIC, wenn vorher keine da war oder eine andere.
		 *
		 * @param bic
		 *            die neue BIC.
		 */
		private void updateBic ( String bic )
		{
			// Keine neue BIC
			if (bic == null || bic.length() == 0)
			{
				return;
			}

			String current = this.values[2];
			current = current != null ? current.trim() : "";
			if (!current.equals(bic))
			{
				// Wenn sich die BIC nur in den letzten 3 Stellen unterscheiden,
				// koennen wir nicht mit
				// Sicherheit sagen, ob unsere Aenderung korrekt waere. In dem
				// Fall kann es sein,
				// dass eine der BIC auf "XXX" endet und keine konkrete Filiale
				// meint. Oder - und das
				// ist noch fataler: Es gibt bei einigen Banken zur selben BLZ
				// UNTERSCHIEDLICHE
				// BIC. Beispiel Deutsche Bank Kiel und Rendsburg. Beide die BLZ
				// "21070020", jedoch
				// unterschiedliche BIC (DEUTDEHH210 und DEUTDEHH214). Da wir
				// das Lookup anhand der BLZ
				// machen und der Name der Filiale aus meiner Sicht kein
				// hinreichend eindeutiges
				// Erkennungsmerkmal ist, machen wir das BIC-Update nur dann,
				// wenn sich die BIC auf
				// den ersten 8 Zeichen aendert. Die letzten 3 Stellen fuer die
				// Filialen lassen
				// wir erstmal aussen vor.
				if (bic.length() < 8 || current.length() < 8)
				{
					return;
				}

				String s1 = bic.substring(0, 8);
				String s2 = current.substring(0, 8);
				if (!s1.equals(s2))
				{
					System.out.println(blz + ": BIC \"" + current + "\" -> \"" + bic + "\"");
					this.values[2] = bic;
				}
			}
		}

		/**
		 * Speichert die neue HBCI-Version, wenn vorher keine da war oder eine
		 * andere.
		 *
		 * @param die
		 *            neue HBCI-Version.
		 */
		private void updateVersion ( String version )
		{
			// Keine neue Version
			if (version == null || version.length() == 0)
			{
				return;
			}

			version = version.toLowerCase();
			if (version.contains("3.0"))
			{
				version = "300";
			}
			else if (version.contains("2.2"))
			{
				version = "plus";
			}

			String current = this.values[7];
			current = current != null ? current.trim() : "";
			if (!current.equals(version))
			{
				System.out.println(blz + ": Version \"" + current + "\" -> \"" + version + "\"");
				this.values[7] = version;
			}
		}

		/**
		 * Wandelt die Zeile wieder zurueck in einen String.
		 *
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString ( )
		{
			StringBuffer sb = new StringBuffer();
			sb.append(this.blz);
			sb.append("=");
			for (int i = 0; i < this.values.length; ++i)
			{
				if (i > 0)
				{
					sb.append("|");
				}

				String s = this.values[i];
				if (s != null)
				{
					sb.append(s);
				}
			}

			return sb.toString();
		}
	}
}
