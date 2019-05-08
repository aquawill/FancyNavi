package com.fancynavi.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.guidance.VoiceSkin;

import java.util.List;

/**
 * Created by aquawill on 2019-05-01.
 */


class VoiceActivation {
    private AppCompatActivity m_activity;
    private Context context;
    private VoiceCatalog voiceCatalog = VoiceCatalog.getInstance();
    private String desiredLangCode;
    private long desiredVoiceId;

    public VoiceActivation(AppCompatActivity m_activity) {
        this.m_activity = m_activity;
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
        alertDialogBuilder.setTitle("Voice Download Failed");
        alertDialogBuilder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                downloadVoice(context, id);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void downloadVoice(Context context, final long voiceSkinId) {
        Log.d("Test", "Downloading voice skin ID: " + voiceSkinId);
        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Downloading voice skin ID: " + voiceSkinId, Snackbar.LENGTH_LONG).show();

        voiceCatalog.downloadVoice(voiceSkinId, new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
                if (error != VoiceCatalog.Error.NONE) {
                    retryVoiceDownload(context, voiceSkinId);
                    Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Failed downloading voice skin " + voiceSkinId, Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Voice skin " + voiceSkinId + " downloaded and activated.", Snackbar.LENGTH_LONG).show();
                    //NavigationManager.getInstance().setVoiceSkin(VoiceCatalog.getInstance().getLocalVoiceSkin(voiceSkinId)); //Deprecated in SDK 3.7
                    VoiceSkin localVoiceSkin = voiceCatalog.getLocalVoiceSkin(voiceSkinId);
                    NavigationManager.getInstance().getVoiceGuidanceOptions().setVoiceSkin(localVoiceSkin);
                    Log.d("Test", "Voice skin " + voiceSkinId + " downloaded and activated.");
                }
            }
        });
    }

    void downloadCatalogAndSkin() {
        final Boolean[] localVoiceSkinExisted = {false};
        VoiceCatalog.getInstance().downloadCatalog(new VoiceCatalog.OnDownloadDoneListener() {

            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {

                if (error != VoiceCatalog.Error.NONE) {
                    Log.d("Test", "Failed to download catalog.");
                } else {
                    List<VoicePackage> voicePackages = VoiceCatalog.getInstance().getCatalogList();
                    Log.d("Test", "# of available voicePackages: " + voicePackages.size());
                    for (VoicePackage lang : voicePackages) {
                        if (lang.getMarcCode().compareToIgnoreCase(desiredLangCode) == 0) {
                            if (lang.isTts()) {
                                desiredVoiceId = lang.getId();
                                break;
                            }
                        }
                        Log.d("Test", "\tLanguage name: " + lang.getLocalizedLanguage() + "\tGender: " + lang.getGender() + "\tis TTS: " + lang.isTts() + "\tID: " + lang.getId());
                    }
                    List<VoiceSkin> localInstalledSkins = VoiceCatalog.getInstance().getLocalVoiceSkins();
                    localInstalledSkins.clear();
//                        Log.d("Test", "# of local skins: " + localInstalledSkins.size());
                    for (VoiceSkin voice : localInstalledSkins) {
                        Log.d("Test", "ID: " + voice.getId() + " Language: " + voice.getLanguage());
                        if (voice.getId() == desiredVoiceId) {
                            localVoiceSkinExisted[0] = true;
                        }
                    }
                    Log.d("Test", "" + voiceCatalog.getLocalVoiceSkin(desiredVoiceId));
                    if (!localVoiceSkinExisted[0]) {
                        downloadVoice(context, desiredVoiceId);
                    } else {
                        Snackbar.make(m_activity.findViewById(R.id.mapFragmentView), "Voice skin " + desiredVoiceId + " downloaded and activated", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
