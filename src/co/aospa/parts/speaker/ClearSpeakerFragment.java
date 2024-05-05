// Copyright (C) 2024 Paranoid Android
// SPDX-License-Identifier: Apache-2.0

package co.aospa.parts.speaker;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.preference.PreferenceFragment;
import android.widget.Switch;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import co.aospa.parts.R;

import java.io.IOException;

public class ClearSpeakerFragment extends PreferenceFragment implements OnMainSwitchChangeListener {

    private static final String TAG = "ClearSpeakerFragment";
    private static final String PREF_CLEAR_SPEAKER = "clear_speaker_pref";
    private static final int PLAY_DURATION_MS = 30000;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private MediaPlayer mMediaPlayer;
    private MainSwitchPreference mClearSpeakerPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.clear_speaker_settings);

        mClearSpeakerPref = findPreference(PREF_CLEAR_SPEAKER);
        mClearSpeakerPref.addOnSwitchChangeListener(this);
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        mClearSpeakerPref.setChecked(isChecked);

        if (isChecked && startPlaying()) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.postDelayed(this::stopPlaying, PLAY_DURATION_MS);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlaying();
    }

    public boolean startPlaying() {
        getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        mMediaPlayer.setLooping(true);
        try (AssetFileDescriptor afd = getResources().openRawResourceFd(
                R.raw.clear_speaker_sound)) {
            mMediaPlayer.setDataSource(afd);
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mClearSpeakerPref.setEnabled(false);
        } catch (IOException | IllegalArgumentException e) {
            Log.e(TAG, "Failed to play speaker clean sound!", e);
            return false;
        }
        return true;
    }

    public void stopPlaying() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            try {
                mMediaPlayer.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, "Failed to stop media player!", e);
            } finally {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
        mClearSpeakerPref.setEnabled(true);
        mClearSpeakerPref.setChecked(false);
    }
}
