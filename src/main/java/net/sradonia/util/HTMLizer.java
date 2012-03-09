/*******************************************************************************
 * sradonia tools
 * Copyright (C) 2012 Stefan Rado
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************************/
package net.sradonia.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stefan Rado
 */
public class HTMLizer {

	private HTMLizer() {
	}

	public static String parse(String text) {
		text = specialChars(text, true);
		text = nl2br(text, true);
		text = parseLinks(text);
		return text;
	}

	public static String specialChars(String text) {
		return specialChars(text, true);
	}

	public static String specialChars(String text, boolean quotes) {
		text = text.replace("&", "&amp;");
		text = text.replace("<", "&lt;");
		text = text.replace(">", "&gt;");
		if (quotes) {
			text = text.replace("\"", "&quot;");
			text = text.replace("'", "&#039;");
		}
		return text;
	}

	public static String parseLinks(String text) {
		// Replace email adresses
		text = text.replaceAll("[A-Za-z0-9\\.]+\\@[A-Za-z0-9\\.-]+", "<a href='mailto:$0'>$0</a>");

		// Replace URLs
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(\\s)((?:(https?|ftp|irc|telnet)://|(www|ftp|irc)\\.)[\\S&&[^<>\"]]+)").matcher(text);
		while (m.find()) {
			String match = m.group(2);
			String url;

			// Guess protocol via subdomain if not given
			if (m.group(3) == null) {
				String subdomain = m.group(4);
				if (subdomain.equalsIgnoreCase("www"))
					subdomain = "http";
				url = subdomain + "://" + match;
			} else
				url = match;
			m.appendReplacement(sb, m.group(1) + "<a href='" + url + "'>" + match + "</a>");
		}
		m.appendTail(sb);
		text = sb.toString();

		return text;
	}

	public static String nl2br(String text) {
		return nl2br(text, true);
	}

	public static String nl2br(String text, boolean keepNL) {
		// Adjust different line-breaks
		text = text.replace("\r\n", "\n");
		text = text.replace("\r", "\n");

		// Replace
		text = text.replace("\n", (keepNL ? "<br />\n" : "<br />"));

		return text;
	}

	public static String bbCode(String text) {
		StringBuffer sb = new StringBuffer();
		Matcher m = Pattern.compile("(?i)\\[(b|i|u)\\](.*)\\[/\\1\\]").matcher(text);
		while (m.find()) {
			// recursive
			m.appendReplacement(sb, "<$1>" + bbCode(m.group(2)) + "</$1>");
		}
		m.appendTail(sb);
		text = sb.toString();
		return text;
	}
}
