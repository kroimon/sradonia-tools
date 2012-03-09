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
package net.sradonia.log4j;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.*;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/**
 * An Appender for the Apache log4j logging system. It displays a modal (blocking) swing window.
 * 
 * @author Stefan Rado
 */
public class DialogAppender extends AppenderSkeleton {

	protected static final String LINE = "--------------------";

	protected Level level = Level.ERROR;
	protected String title = "Log4j DialogAppender - Error";
	protected String caption = "Message:";

	protected LogDialog dialog;

	public DialogAppender() {
		// no-op
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	@Override
	protected void append(LoggingEvent event) {
		if (level != null && event.getLevel().isGreaterOrEqual(level)) {
			if (layout == null) {
				errorHandler.error("No layout set for the appender named [" + name + "].");
			}

			if (dialog == null) {
				dialog = new LogDialog(caption);
			}
			dialog.showEvent(event);
		}
	}

	@Override
	public void close() {
		dialog = null;
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * Private class to display a log message
	 */
	private class LogDialog {

		private JDialog dialog;
		private JTextArea textArea;

		/**
		 * Creates a new LogDialog ready for displaying a message via {@link #showEvent(LoggingEvent)}.
		 */
		public LogDialog(final String header) {
			try {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						JPanel panel = new JPanel(new BorderLayout());

						textArea = new JTextArea();
						textArea.setEditable(false);

						JScrollPane scrollPane = new JScrollPane(textArea);

						panel.add(new JLabel(header), BorderLayout.NORTH);
						panel.add(scrollPane, BorderLayout.CENTER);

						JOptionPane optionPane = new JOptionPane();
						optionPane.setMessage(panel);
						optionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
						optionPane.setOptionType(JOptionPane.DEFAULT_OPTION);

						dialog = optionPane.createDialog(null, title);

						dialog.setSize(400, 250);
						dialog.setLocationRelativeTo(null);
					}
				});
			} catch (Exception e) {
				LogLog.error("couldn't create DialogAppender window", e);
			}
		}

		/**
		 * Formats the given event using the set layout of this appender instance.
		 * 
		 * @param event
		 *            the event to format
		 * @return the formatted event string
		 */
		private String formatEvent(LoggingEvent event) {
			StringBuilder sb = new StringBuilder();
			if (layout.getHeader() != null)
				sb.append(layout.getHeader());
			sb.append(layout.format(event));
			if (layout.getFooter() != null)
				sb.append(layout.getFooter());

			if (layout.ignoresThrowable()) {
				String[] s = event.getThrowableStrRep();
				if (s != null) {
					sb.append(Layout.LINE_SEP).append(LINE).append(Layout.LINE_SEP);
					for (String line : s) {
						sb.append(line).append(Layout.LINE_SEP);
					}
				}
			}

			return sb.toString();
		}

		/**
		 * <p>
		 * Displays the dialog with the given event.
		 * </p>
		 * <p>
		 * This method will wait for the dialog to be closed and therefore block until user interaction.
		 * </p>
		 * 
		 * @param event
		 *            the event to display
		 */
		public void showEvent(final LoggingEvent event) {
			Runnable runnable = new Runnable() {
				public void run() {
					textArea.setText(formatEvent(event));
					textArea.setCaretPosition(0);

					// show and block until user presses OK
					dialog.setVisible(true);
				}
			};
			if (!EventQueue.isDispatchThread()) {
				try {
					EventQueue.invokeAndWait(runnable);
				} catch (Exception e) {
					LogLog.error("couldn't show DialogAppender", e);
				}
			} else {
				runnable.run();
			}
		}

	}

}
