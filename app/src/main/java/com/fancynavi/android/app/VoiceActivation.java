package com.fancynavi.android.app;

import static com.fancynavi.android.app.DataHolder.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.guidance.VoiceSkin;

import java.util.List;

/**
 * Created by aquawill on 2019-05-01.
 */


class VoiceActivation {
    private final AppCompatActivity activity;
    private Context context;
    private final VoiceCatalog voiceCatalog = VoiceCatalog.getInstance();
    private String desiredLangCode;
    private Snackbar voiceDownloadSnackbar;
    private int progress;

    public void setDesiredVoiceId(long desiredVoiceId) {
        this.desiredVoiceId = desiredVoiceId;
    }

    private long desiredVoiceId;

    VoiceActivation(AppCompatActivity activity) {
        this.activity = activity;
    }

    void setContext(Context context) {
        this.context = context;
    }

    void setDesiredLangCode(String desiredLangCode) {
        this.desiredLangCode = desiredLangCode;
    }

    long getDesiredVoiceId() {
        return desiredVoiceId;
    }

    VoiceCatalog getVoiceCatalog() {
        return voiceCatalog;
    }

    private void retryVoiceDownload(Context context, final long id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(R.string.voice_download_failed);
        alertDialogBuilder.setMessage("ID: " + id);
        alertDialogBuilder.setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                downloadVoice(context, id);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.green));
        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.red));
    }

    private void downloadVoice(Context context, final long voiceSkinId) {
        Log.d(TAG, "Downloading voice skin ID: " + voiceSkinId);
        VoiceCatalog.getInstance().setOnProgressEventListener(new VoiceCatalog.OnProgressListener() {
            @Override
            public void onProgress(int i) {
                Log.d(TAG, "OnProgressListener onProgress:" + i);
                voiceDownloadSnackbar.setText(String.format("%s%d - %d%s", context.getString(R.string.downloading_voice_skin_id), voiceSkinId, i, "%"));
            }
        });
        VoiceCatalog.getInstance().setUncompressProgressEventListener(new VoiceCatalog.UncompressProgressListener() {
            @Override
            public void onProgress(int i) {
                Log.d(TAG, "UncompressProgressListener onProgress:" + i);
                voiceDownloadSnackbar.setText(String.format("%s%d - %d%s", context.getString(R.string.extracting_voice_skin_id), voiceSkinId, i, "%"));
            }
        });
        voiceDownloadSnackbar = Snackbar.make(activity.findViewById(R.id.mapFragmentView), context.getString(R.string.downloading_voice_skin_id) + voiceSkinId, Snackbar.LENGTH_INDEFINITE);
        voiceDownloadSnackbar.show();
        voiceCatalog.downloadVoice(voiceSkinId, new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
                if (error != VoiceCatalog.Error.NONE) {
                    retryVoiceDownload(context, voiceSkinId);
                    Log.e(TAG, "voiceSkinId " + voiceSkinId + " download error: " + error);
                    Snackbar.make(activity.findViewById(R.id.mapFragmentView), context.getString(R.string.failed_downloading_voice_skin) + voiceSkinId, Snackbar.LENGTH_SHORT).show();
                } else {
                    VoiceSkin localVoiceSkin = voiceCatalog.getLocalVoiceSkin(voiceSkinId);
                    NavigationManager.getInstance().getVoiceGuidanceOptions().setVoiceSkin(localVoiceSkin);
                    Log.d(TAG, "Voice skin " + voiceSkinId + " downloaded and activated (" + localVoiceSkin.getOutputType() + ").");
                    Snackbar.make(activity.findViewById(R.id.mapFragmentView), activity.getString(R.string.voice_activated) + "ID: " + localVoiceSkin.getId() + " / " + localVoiceSkin.getLanguage() + " / " + localVoiceSkin.getOutputType().name(), Snackbar.LENGTH_SHORT).show();
                    VoiceCatalog.getInstance().setUncompressProgressEventListener(null);
                    VoiceCatalog.getInstance().setOnProgressEventListener(null);
                }
            }
        });
    }

    void downloadCatalogAndSkin() {
        final Boolean[] localVoiceSkinExisted = {false};
        Log.d(TAG, "getDesiredVoiceId(): " + getDesiredVoiceId());
        VoiceCatalog.getInstance().downloadCatalog(new VoiceCatalog.OnDownloadDoneListener() {

            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
                if (error != VoiceCatalog.Error.NONE) {
                    Log.d(TAG, "Failed to download catalog: " + error.name());
                } else {
                    List<VoicePackage> voicePackages = VoiceCatalog.getInstance().getCatalogList();
                    Log.d(TAG, "# of available voicePackages: " + voicePackages.size());
                    voicePackages.forEach(voicePackage -> {
                        Log.d(TAG, "\tId: " + voicePackage.getId() + "\tLanguage name: " + voicePackage.getLocalizedLanguage() + "\tLanguage code: " + voicePackage.getMarcCode() + "\tTTS: " + voicePackage.isTts());
                    });
                    for (VoicePackage voicePackage : voicePackages) {
                        if (desiredLangCode != null) {
                            if (voicePackage.getMarcCode().compareToIgnoreCase(desiredLangCode) == 0) {
                                if (voicePackage.isTts()) {
                                    desiredVoiceId = voicePackage.getId();
                                    break;
                                }
                            }
                        }
                    }

                    List<VoiceSkin> localInstalledSkins = VoiceCatalog.getInstance().getLocalVoiceSkins();
//                    localInstalledSkins.clear();
                    Log.d(TAG, "# of local skins: " + localInstalledSkins.size());
                    String languageName = "";
                    long voiceId = 0L;
                    for (VoiceSkin voice : localInstalledSkins) {
                        Log.d(TAG, "ID: " + voice.getId() + " Language: " + voice.getLanguage());
                        languageName = voice.getLanguage();
                        voiceId = voice.getId();
                        if (voice.getId() == desiredVoiceId) {
                            localVoiceSkinExisted[0] = true;
                            Snackbar.make(activity.findViewById(R.id.mapFragmentView), activity.getString(R.string.voice_activated) + "ID: " + voiceId + " / " + languageName, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    if (!localVoiceSkinExisted[0]) {
                        downloadVoice(context, desiredVoiceId);
                    }
                }
            }
        });
    }
}
