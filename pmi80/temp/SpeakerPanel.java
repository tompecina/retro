/* SpeakerPanel.java
 *
 * Copyright (C) 2015, Tomáš Pecina <tomas@pecina.cz>
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

package cz.pecina.retro.pmi80;

import java.util.Hashtable;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import javax.swing.border.Border;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.gui.WheelSlider;

public class SpeakerPanel extends PeripheralFrame {

  public SpeakerPanel(Peripheral peripheral) {
    super(Emulator.textResources.getString("speaker.frameTitle"), peripheral);

    Border border = BorderFactory.createEmptyBorder(2, 8, 0, 8);
	
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setBorder(border);

    GridBagConstraints iconConstraints = new GridBagConstraints();
    JLabel icon = new JLabel(IconCache.get("misc/speaker.png"));
    iconConstraints.gridx = 0;
    iconConstraints.gridy = 0;
    iconConstraints.gridwidth = GridBagConstraints.REMAINDER;
    iconConstraints.insets = new Insets(10, 20, 10, 20);
    iconConstraints.anchor = GridBagConstraints.CENTER;
    iconConstraints.weightx = 0.0;
    iconConstraints.weighty = 1.0;
    panel.add(icon, iconConstraints);

    GridBagConstraints volumeLabelConstraints =
      new GridBagConstraints();
    JLabel volumeLabel =
      new JLabel(Emulator.textResources.getString("speaker.volume") + ":");
    volumeLabelConstraints.gridx = 0;
    volumeLabelConstraints.gridy = 1;
    volumeLabelConstraints.insets = new Insets(0, 10, 0, 0);
    volumeLabelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
    volumeLabelConstraints.weightx = 0.0;
    volumeLabelConstraints.weighty = 0.0;
    panel.add(volumeLabel, volumeLabelConstraints);

    GridBagConstraints volumeConstraints =
      new GridBagConstraints();
    JSlider volume =
      new WheelSlider(JSlider.HORIZONTAL, 0, 100, 50);
    volumeLabel.setLabelFor(volume);
    volume.setMajorTickSpacing(25);
    volume.setPaintTicks(true);
    final Hashtable<Integer,JLabel> volumeLabelTable = new Hashtable<>();
    volumeLabelTable.put(
      new Integer(0),
      new JLabel(Emulator.textResources.getString("speaker.mute")));
    volumeLabelTable.put(
      new Integer(100),
      new JLabel(Emulator.textResources.getString("speaker.max")));
    volume.setLabelTable(volumeLabelTable);
    volume.setPaintLabels(true);
    volume.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent event) {
	  Computer.sound.setVolume(((JSlider)(event.getSource())).getValue());
	}
      });
    volumeConstraints.gridx = 1;
    volumeConstraints.gridy = 1;
    volumeConstraints.insets = new Insets(0, 0, 10, 0);
    volumeConstraints.anchor = GridBagConstraints.LINE_START;
    volumeConstraints.weightx = 0.0;
    volumeConstraints.weighty = 0.0;
    panel.add(volume, volumeConstraints);

    add(panel);
    pack();
    postamble();
  }
}
