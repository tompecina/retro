/* SettingsSoundPanel.java
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

package cz.pecina.retro.ondra;

import java.util.logging.Logger;

import java.util.Hashtable;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.BorderFactory;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import cz.pecina.retro.common.Application;
import cz.pecina.retro.common.Parameters;
import cz.pecina.retro.common.Sound;

import cz.pecina.retro.gui.WheelSlider;

/**
 * The Settings/Sound panel.
 *
 * @author @AUTHOR@
 * @version @VERSION@
 */
public class SettingsSoundPanel extends JPanel {

  // static logger
  private static final Logger log =
    Logger.getLogger(SettingsSoundPanel.class.getName());
    
  // components holding the new values
  private JSlider tapeRecorderVolume, speakerVolume;
  private JCheckBox tapeRecorderMute, speakerMute;

  /**
   * Creates the Settings/Sound panel.
   */
  public SettingsSoundPanel() {
    super(new GridBagLayout());
    log.fine("New Settings/Sound panel creation started");

    setBorder(BorderFactory.createEmptyBorder(15, 8, 0, 8));

    int line = 0;
    
    final GridBagConstraints tapeRecorderLabelConstraints =
      new GridBagConstraints();
    final JLabel tapeRecorderLabel =
      new JLabel(Application.getString(
        this, "settings.sound.tapeRecorder.label") + ":");
    tapeRecorderLabelConstraints.gridx = 0;
    tapeRecorderLabelConstraints.gridy = line;
    tapeRecorderLabelConstraints.insets = new Insets(5, 0, 0, 10);
    tapeRecorderLabelConstraints.anchor = GridBagConstraints.LINE_END;
    tapeRecorderLabelConstraints.weightx = 0.0;
    tapeRecorderLabelConstraints.weighty = 0.0;
    add(tapeRecorderLabel, tapeRecorderLabelConstraints);

    final GridBagConstraints tapeRecorderVolumeConstraints =
      new GridBagConstraints();
    tapeRecorderVolume =
      new WheelSlider(JSlider.HORIZONTAL, 0, 100, 0);
    tapeRecorderVolume.setMajorTickSpacing(25);
    tapeRecorderVolume.setPaintTicks(true);
    final Hashtable<Integer,JLabel> tapeRecorderVolumeLabelTable =
      new Hashtable<>();
    tapeRecorderVolumeLabelTable.put(
      Integer.valueOf(0),
      new JLabel(Application.getString(this, "settings.sound.min")));
    tapeRecorderVolumeLabelTable.put(
      Integer.valueOf(100),
      new JLabel(Application.getString(this, "settings.sound.max")));
    tapeRecorderVolume.setLabelTable(tapeRecorderVolumeLabelTable);
    tapeRecorderVolume.setPaintLabels(true);
    tapeRecorderVolumeConstraints.gridx = 1;
    tapeRecorderVolumeConstraints.gridy = line;
    tapeRecorderVolumeConstraints.fill = GridBagConstraints.HORIZONTAL;
    tapeRecorderVolumeConstraints.insets = new Insets(10, 10, 10, 10);
    tapeRecorderVolumeConstraints.anchor = GridBagConstraints.LINE_START;
    tapeRecorderVolumeConstraints.weightx = 1.0;
    tapeRecorderVolumeConstraints.weighty = 0.0;
    tapeRecorderVolume.addChangeListener(
      new VolumeChangeListener(Sound.TAPE_RECORDER_CHANNEL));
    add(tapeRecorderVolume, tapeRecorderVolumeConstraints);

    final GridBagConstraints tapeRecorderMuteConstraints =
      new GridBagConstraints();
    tapeRecorderMute =
      new JCheckBox(Application.getString(this, "settings.sound.mute"));
    tapeRecorderMuteConstraints.gridx = 2;
    tapeRecorderMuteConstraints.gridy = line;
    tapeRecorderVolumeConstraints.gridwidth = GridBagConstraints.REMAINDER;
    tapeRecorderMuteConstraints.insets = new Insets(10, 5, 10, 0);
    tapeRecorderMuteConstraints.anchor = GridBagConstraints.LINE_START;
    tapeRecorderMuteConstraints.weightx = 0.0;
    tapeRecorderMuteConstraints.weighty = 0.0;
    tapeRecorderMute.addChangeListener(
      new MuteChangeListener(Sound.TAPE_RECORDER_CHANNEL));
    add(tapeRecorderMute, tapeRecorderMuteConstraints);

    line++;

    final GridBagConstraints speakerLabelConstraints =
      new GridBagConstraints();
    final JLabel speakerLabel =
      new JLabel(Application.getString(this, "settings.sound.speaker.label") +
		 ":");
    speakerLabelConstraints.gridx = 0;
    speakerLabelConstraints.gridy = line;
    speakerLabelConstraints.insets = new Insets(5, 0, 0, 10);
    speakerLabelConstraints.anchor = GridBagConstraints.LINE_END;
    speakerLabelConstraints.weightx = 0.0;
    speakerLabelConstraints.weighty = 0.0;
    add(speakerLabel, speakerLabelConstraints);

    final GridBagConstraints speakerVolumeConstraints =
      new GridBagConstraints();
    speakerVolume =
      new WheelSlider(JSlider.HORIZONTAL, 0, 100, 0);
    speakerVolume.setMajorTickSpacing(25);
    speakerVolume.setPaintTicks(true);
    final Hashtable<Integer,JLabel> speakerVolumeLabelTable = new Hashtable<>();
    speakerVolumeLabelTable.put(
      new Integer.valueOf(0),
      new JLabel(Application.getString(this, "settings.sound.min")));
    speakerVolumeLabelTable.put(
      Integer.valueOf(100),
      new JLabel(Application.getString(this, "settings.sound.max")));
    speakerVolume.setLabelTable(speakerVolumeLabelTable);
    speakerVolume.setPaintLabels(true);
    speakerVolumeConstraints.gridx = 1;
    speakerVolumeConstraints.gridy = line;
    speakerVolumeConstraints.fill = GridBagConstraints.HORIZONTAL;
    speakerVolumeConstraints.insets = new Insets(10, 10, 10, 10);
    speakerVolumeConstraints.anchor = GridBagConstraints.LINE_START;
    speakerVolumeConstraints.weightx = 1.0;
    speakerVolumeConstraints.weighty = 0.0;
    speakerVolume.addChangeListener(
      new VolumeChangeListener(Sound.SPEAKER_CHANNEL));
    add(speakerVolume, speakerVolumeConstraints);

    final GridBagConstraints speakerMuteConstraints =
      new GridBagConstraints();
    speakerMute =
      new JCheckBox(Application.getString(this, "settings.sound.mute"));
    speakerMuteConstraints.gridx = 2;
    speakerMuteConstraints.gridy = line;
    speakerVolumeConstraints.gridwidth = GridBagConstraints.REMAINDER;
    speakerMuteConstraints.insets = new Insets(10, 5, 10, 0);
    speakerMuteConstraints.anchor = GridBagConstraints.LINE_START;
    speakerMuteConstraints.weightx = 0.0;
    speakerMuteConstraints.weighty = 0.0;
    speakerMute.addChangeListener(
      new MuteChangeListener(Sound.SPEAKER_CHANNEL));
    add(speakerMute, speakerMuteConstraints);

    log.fine("Settings/Sound panel set up");
  }

  // volume change listener
  private class VolumeChangeListener implements ChangeListener {

    private int channel;
    
    public VolumeChangeListener(final int channel) {
      assert channel >= 0;
      this.channel = channel;
    }
    
    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      Parameters.sound.setVolume(
        channel,
	((JSlider)(event.getSource())).getValue() / 100f);
    }
  }

  // mute change listener
  private class MuteChangeListener implements ChangeListener {

    private int channel;
    
    public MuteChangeListener(final int channel) {
      assert channel >= 0;
      this.channel = channel;
    }
    
    // for description see ChangeListener
    @Override
    public void stateChanged(final ChangeEvent event) {
      Parameters.sound.setMute(channel,
			       ((JCheckBox)(event.getSource())).isSelected());
    }
  }

  /**
   * Initialize widgets.
   */
  public void setUp() {
    tapeRecorderVolume.setValue(UserPreferences.getTapeRecorderVolume());
    tapeRecorderMute.setSelected(UserPreferences.isTapeRecorderMute());
    speakerVolume.setValue(UserPreferences.getSpeakerVolume());
    speakerMute.setSelected(UserPreferences.isSpeakerMute());
    log.fine("Widgets initialized");
  }

  // partial set listener
  private class SetListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Set button event detected");
      UserPreferences.setTapeRecorderVolume(tapeRecorderVolume.getValue());
      UserPreferences.setTapeRecorderMute(tapeRecorderMute.isSelected());
      UserPreferences.setSpeakerVolume(speakerVolume.getValue());
      UserPreferences.setSpeakerMute(speakerMute.isSelected());
      log.fine("Partial changes implemented");
    }
  }

  /**
   * Partial set listener factory method.
   *
   * @return new partial set listener
   */
  public SetListener createSetListener() {
    log.finer("Partial set listener created");
    return new SetListener();
  }

  // partial cancel listener
  private class CancelListener implements ActionListener {

    // for description see ActionListener
    @Override
    public void actionPerformed(final ActionEvent event) {
      log.finer("Cancel button event detected");
      Parameters.sound.setVolume(Sound.TAPE_RECORDER_CHANNEL,
        UserPreferences.getTapeRecorderVolume() / 100f);
      Parameters.sound.setMute(Sound.TAPE_RECORDER_CHANNEL,
        UserPreferences.isTapeRecorderMute());
      Parameters.sound.setVolume(Sound.SPEAKER_CHANNEL,
	UserPreferences.getSpeakerVolume() / 100f);
      Parameters.sound.setMute(Sound.SPEAKER_CHANNEL,
        UserPreferences.isSpeakerMute());
      log.fine("Partial changes cancelled");
    }
  }

  /**
   * Partial cancel listener factory method.
   *
   * @return new partial cancel listener
   */
  public CancelListener createCancelListener() {
    log.finer("Partial cancel listener created");
    return new CancelListener();
  }
}
