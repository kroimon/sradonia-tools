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
package net.sradonia.gui;

import java.awt.AWTException;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sradonia.time.SimpleTimer;

/**
 * Simple splash screen implementation with the ability of displaying transparent images.
 * 
 * @author Stefan Rado
 */
public class SplashScreen {
	protected final static Log log = LogFactory.getLog(SplashScreen.class);

	private JFrame window;
	private Image image;
	private BufferedImage splash;
	private boolean timeoutActive;
	private SimpleTimer timer;

	/**
	 * Creates a new splash screen.
	 * 
	 * @param image
	 *            the image to display
	 */
	public SplashScreen(String image) {
		this(getImage(image), "");
	}

	/**
	 * Creates a new splash screen.
	 * 
	 * @param image
	 *            the image to display
	 * @param title
	 *            the title of the window
	 */
	public SplashScreen(String image, String title) {
		this(getImage(image), title);
	}

	/**
	 * Creates a new splash screen.
	 * 
	 * @param image
	 *            the image to display
	 */
	public SplashScreen(Image image) {
		this(image, "");
	}

	/**
	 * Creates a new splash screen.
	 * 
	 * @param image
	 *            the image to display
	 * @param title
	 *            the title of the window
	 */
	public SplashScreen(Image image, String title) {
		log.debug("initializing splash screen");

		if (image == null)
			throw new IllegalArgumentException("null image");

		// create the frame
		window = new JFrame(title) {
			private static final long serialVersionUID = 2193620921531262633L;

			@Override
			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.drawImage(splash, 0, 0, this);
			}
		};
		window.setUndecorated(true);

		// wait for the image to load
		MediaTracker mt = new MediaTracker(window);
		mt.addImage(image, 0);
		try {
			mt.waitForID(0);
		} catch (InterruptedException e1) {
			log.debug("interrupted while waiting for image loading");
		}

		// check for loading errors
		if (mt.isErrorID(0))
			throw new IllegalArgumentException("couldn't load the image");
		if (image.getHeight(null) <= 0 || image.getWidth(null) <= 0)
			throw new IllegalArgumentException("illegal image size");

		setImage(image);

		window.addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				updateSplash();
			}

			public void windowLostFocus(WindowEvent e) {
			}
		});

		timer = new SimpleTimer(new Runnable() {
			public void run() {
				log.debug(timer.getDelay() + "ms timeout reached");
				timer.setRunning(false);
				setVisible(false);
			}
		}, 5000, false);
		timeoutActive = false;
	}

	/**
	 * @param file
	 *            the image file
	 * @return an image instance
	 */
	private static Image getImage(String file) {
		return Toolkit.getDefaultToolkit().getImage(SplashScreen.class.getClassLoader().getResource(file));
	}

	/**
	 * Re-captures the splash's background and redraws the buffered picture.
	 */
	private void updateSplash() {
		try {
			splash = new Robot(window.getGraphicsConfiguration().getDevice()).createScreenCapture(window.getBounds());
			splash.createGraphics().drawImage(image, 0, 0, window);
		} catch (AWTException e) {
		}
	}

	/**
	 * @return whether the window is visible or not
	 */
	public boolean isVisible() {
		return window.isVisible();
	}

	/**
	 * Changes the visibility of the splash screen window. The window will automatically be disposed when it's hidden.
	 * 
	 * @param visible
	 *            the visibility of the splash screen
	 */
	public void setVisible(boolean visible) {
		if (visible && !isVisible()) {
			log.info("displaying splash screen");
			updateSplash();
			window.setVisible(true);
			if (timeoutActive)
				timer.setRunning(true);
		} else if (!visible && isVisible()) {
			log.info("hiding splash screen");
			timer.setRunning(false);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					window.dispose();
				}
			});
		}
	}

	/**
	 * @return the splash screen image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the new splash screen image
	 */
	public void setImage(Image image) {
		if (image == null)
			throw new IllegalArgumentException("null image");
		this.image = image;
		window.setSize(image.getWidth(window), image.getHeight(window));
		window.setLocationRelativeTo(null);
		log.debug("image set. dimensions: " + window.getBounds());
	}

	/**
	 * @return the splash screen's title
	 */
	public String getTitle() {
		return window.getTitle();
	}

	/**
	 * @param title
	 *            the splash screen's new title
	 */
	public void setTitle(String title) {
		window.setTitle(title);
	}

	/**
	 * @return the splash screen's window icon
	 */
	public Image getIconImage() {
		return window.getIconImage();
	}

	/**
	 * @param image
	 *            the splash screen's window icon
	 */
	public void setIconImage(Image image) {
		window.setIconImage(image);
	}

	/**
	 * @param image
	 *            the splash screen's window icon
	 */
	public void setIconImage(String image) {
		setIconImage(getImage(image));
	}

	/**
	 * Gets the timeout after which the window will automatically be hidden.
	 * 
	 * @return the timeout
	 */
	public long getTimeout() {
		return timer.getDelay();
	}

	/**
	 * Sets the timeout after which the window will automatically be hidden.
	 * 
	 * @param timeout
	 *            the new timeout
	 */
	public void setTimeout(long timeout) {
		if (timeoutActive = (timeout > 0)) {
			timer.setDelay(timeout);
			if (isVisible())
				timer.setRunning(true);
		}
	}
}
