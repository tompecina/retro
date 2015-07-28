/* WheelSlider.java
 *
 * Copyright (C) 2014-2015, Tomáš Pecina <tomas@pecina.cz>
 *
 * This file is part of cz.pecina.retro, retro 8-bit computer emulators.
 *
 * This application is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This application is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.         
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cz.pecina.retro.gui;

import java.util.logging.Logger;
import javax.swing.JSlider;
import javax.swing.BoundedRangeModel;
import java.awt.event.MouseWheelListener;
import java.awt.event.MouseWheelEvent;
import cz.pecina.retro.common.Util;

/**
 * <code>JSlider</code> responding to mouse wheel movement.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class WheelSlider extends JSlider {

  // static logger
  private static final Logger log =
    Logger.getLogger(WheelSlider.class.getName());

  /**
   * Creates a horizontal slider with the range 0 to 100 and
   * an initial value of 50.
   */
  public WheelSlider() {
    super();
    addMouseWheelListener(new WheelListener());
    log.fine("New WheelSlider created");
  }

  /**
   * Creates a slider using the specified orientation with the
   * range {@code 0} to {@code 100} and an initial value of {@code 50}.
   * The orientation can be either <code>SwingConstants.VERTICAL</code>
   * or <code>SwingConstants.HORIZONTAL</code>.
   *
   * @param     orientation              the orientation of the slider
   * @exception IllegalArgumentException if orientation is not one of
   *                                     {@code VERTICAL}, {@code HORIZONTAL}
   */
  public WheelSlider(final int orientation) {
    super(orientation);
    addMouseWheelListener(new WheelListener());
    log.fine("New WheelSlider created");
  }

  /**
   * Creates a horizontal slider using the specified min and max
   * with an initial value equal to the average of the min plus max.
   * <p>
   * The <code>BoundedRangeModel</code> that holds the slider's data
   * handles any issues that may arise from improperly setting the
   * minimum and maximum values on the slider.  See the
   * {@code BoundedRangeModel} documentation for details.
   *
   * @param min the minimum value of the slider
   * @param max the maximum value of the slider
   */
  public WheelSlider(final int min, final int max) {
    super(min, max);
    addMouseWheelListener(new WheelListener());
    log.fine("New WheelSlider created");
  }

  /**
   * Creates a horizontal slider using the specified min, max and value.
   * <p>
   * The <code>BoundedRangeModel</code> that holds the slider's data
   * handles any issues that may arise from improperly setting the
   * minimum, initial, and maximum values on the slider.  See the
   * {@code BoundedRangeModel} documentation for details.
   *
   * @param min   the minimum value of the slider
   * @param max   the maximum value of the slider
   * @param value the initial value of the slider
   */
  public WheelSlider(final int min, final int max, final int value) {
    super(min, max, value);
    addMouseWheelListener(new WheelListener());
    log.fine("New WheelSlider created");
  }

  /**
   * Creates a slider with the specified orientation and the
   * specified minimum, maximum, and initial values.
   * The orientation can be either <code>SwingConstants.VERTICAL</code>
   * or <code>SwingConstants.HORIZONTAL</code>.
   * <p>
   * The <code>BoundedRangeModel</code> that holds the slider's data
   * handles any issues that may arise from improperly setting the
   * minimum, initial, and maximum values on the slider.  See the
   * {@code BoundedRangeModel} documentation for details.
   *
   * @param     orientation              the orientation of the slider
   * @param     min                      the minimum value of the slider
   * @param     max                      the maximum value of the slider
   * @param     value                    the initial value of the slider
   * @exception IllegalArgumentException if orientation is not one of
   *                                     {@code VERTICAL}, {@code HORIZONTAL}
   */
  public WheelSlider(final int orientation,
		     final int min,
		     final int max,
		     final int value) {
    super(orientation, min, max, value);
    addMouseWheelListener(new WheelListener());
    log.fine("New WheelSlider created");
  }

  /**
   * Creates a horizontal slider using the specified
   * <code>BoundedRangeModel</code>.
   * <p>
   * The <code>BoundedRangeModel</code> that holds the slider's data
   * handles any issues that may arise from improperly setting the
   * minimum, initial, and maximum values on the slider.  See the
   * {@code BoundedRangeModel} documentation for details.
   *
   * @param brm the <code>BoundedRangeModel</code> for the slider
   */
  public WheelSlider(final BoundedRangeModel brm) {
    super(brm);
    addMouseWheelListener(new WheelListener());
    log.fine("New WheelSlider created");
  }

  // mouse wheel listener
  private class WheelListener implements MouseWheelListener {

    // for decription see MouseWheelListener
    @Override
    public void mouseWheelMoved(final MouseWheelEvent event) {
      final int rotation = event.getWheelRotation();
      log.finest("WheelListener mouse wheel event detected, rotation: " +
		 rotation);
      final int newValue = Util.limit(getValue() - rotation,
				      getMinimum(),
				      getMaximum());
      setValue(newValue);
      log.finer("WheelListener set to: " + newValue);
    }
  }
}
