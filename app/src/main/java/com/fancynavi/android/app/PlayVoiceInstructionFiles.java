package com.fancynavi.android.app;

import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

import static com.fancynavi.android.app.DataHolder.getActivity;

class PlayVoiceInstructionFiles {

    private onPlayingVoiceInstructionListener onPlayingVoiceInstructionListener;

    void setOnPlayingVoiceInstructionListener(onPlayingVoiceInstructionListener onPlayingVoiceInstructionListener) {
        this.onPlayingVoiceInstructionListener = onPlayingVoiceInstructionListener;
    }

    private final String[] playlist;


    public PlayVoiceInstructionFiles(String[] playlist) {
        this.playlist = playlist;
    }

    private int getSoundDuration(String path) {
        return MediaPlayer.create(getActivity(), Uri.fromFile(new File(path))).getDuration();
    }

    private Integer[] loadRaw(SoundPool soundPool, String path) {
        int soundId = soundPool.load(path, 1);
        int duration = getSoundDuration(path);
        return new Integer[]{soundId, duration};
    }

    void play() {
        SoundPool spool;
        HashMap<Integer, Integer> soundIdMap = new HashMap<>();
        spool = new SoundPool.Builder()
                .setMaxStreams(15)
                .build();

        Set<Integer> soundIdSet = soundIdMap.keySet();

        for (String soundPath : playlist) {
            Integer[] sound = loadRaw(spool, soundPath);
            soundIdMap.put(sound[0], sound[1]);
        }
        for (Integer soundId : soundIdSet) {
            onPlayingVoiceInstructionListener.onStarted();
            spool.play(soundId, 1, 1, 0, 0, 1);
            try {
                Thread.sleep(soundIdMap.get(soundId));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        onPlayingVoiceInstructionListener.onCompleted();
    }

    interface onPlayingVoiceInstructionListener {
        void onCompleted();

        void onStarted();
    }

}
