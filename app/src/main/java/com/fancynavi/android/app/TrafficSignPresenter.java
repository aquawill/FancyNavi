package com.fancynavi.android.app;

import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;

import com.here.android.mpa.common.RoadElement;
import com.here.android.mpa.common.TrafficSign;

import java.util.List;

import static com.fancynavi.android.app.DataHolder.isSignShowing;
import static com.fancynavi.android.app.MainActivity.textToSpeech;

class TrafficSignPresenter {

    private static ImageView imageView1;
    private static ImageView imageView2;
    private static ImageView imageView3;
    private static int imageResourceName;

    static int getTrafficSignImageResourceName(int trafficSignType) {
        switch (trafficSignType) {
            case 1:
                imageResourceName = R.drawable.traffic_sign_type_1;
                break;
            case 6:
                imageResourceName = R.drawable.traffic_sign_type_6;
                break;
            case 7:
                imageResourceName = R.drawable.traffic_sign_type_7;
                break;
            case 9:
                imageResourceName = R.drawable.traffic_sign_type_9;
                break;
            case 10:
                imageResourceName = R.drawable.traffic_sign_type_10;
                break;
            case 11:
                imageResourceName = R.drawable.traffic_sign_type_11;
                break;
            case 12:
                imageResourceName = R.drawable.traffic_sign_type_12;
                break;
            case 13:
                imageResourceName = R.drawable.traffic_sign_type_13;
                break;
            case 14:
                imageResourceName = R.drawable.traffic_sign_type_14;
                break;
            case 15:
                imageResourceName = R.drawable.traffic_sign_type_15;
                break;
            case 18:
                imageResourceName = R.drawable.traffic_sign_type_18;
                break;
            case 19:
                imageResourceName = R.drawable.traffic_sign_type_19;
                break;
            case 20:
                imageResourceName = R.drawable.traffic_sign_type_20;
                break;
            case 21:
                imageResourceName = R.drawable.traffic_sign_type_21;
                break;
            case 22:
                imageResourceName = R.drawable.traffic_sign_type_22;
                break;
            case 23:
                imageResourceName = R.drawable.traffic_sign_type_23;
                break;
            case 27:
                imageResourceName = R.drawable.traffic_sign_type_27;
                break;
            case 29:
                imageResourceName = R.drawable.traffic_sign_type_29;
                break;
            case 30:
                imageResourceName = R.drawable.traffic_sign_type_30;
                break;
            case 31:
                imageResourceName = R.drawable.traffic_sign_type_31;
                break;
            case 32:
                imageResourceName = R.drawable.traffic_sign_type_32;
                break;
            case 34:
                imageResourceName = R.drawable.traffic_sign_alert;
                break;
            case 36:
                imageResourceName = R.drawable.traffic_sign_type_36;
                break;
            case 41:
                imageResourceName = R.drawable.traffic_sign_type_41;
                break;
            case 42:
                imageResourceName = R.drawable.traffic_sign_type_42;
                break;
            case 59:
                imageResourceName = R.drawable.traffic_sign_type_59;
                break;
        }
        return imageResourceName;
    }

    static void setSignImageViews(ImageView signImageView1, ImageView signImageView2, ImageView signImageView3) {
        imageView1 = signImageView1;
        imageView2 = signImageView2;
        imageView3 = signImageView3;
    }

    static private void showTrafficSignImageView(ImageView imageView, RoadElement roadElement) {
        if (DataHolder.getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            imageView.setVisibility(View.VISIBLE);
        }
        int showSignTimePeriod;
        if (roadElement.getFormOfWay() == RoadElement.FormOfWay.MOTORWAY) {
            showSignTimePeriod = 5000;
        } else {
            showSignTimePeriod = 3000;
        }

        new CountDownTimer(showSignTimePeriod, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                imageView.setVisibility(View.GONE);
            }
        }.start();
    }

    static void showTrafficSigns(List<TrafficSign> trafficSigns, RoadElement roadElement, Context context) {

        int numberOfTrafficSigns = trafficSigns.size();
        int i = 0;
        String signName = "注意。";
        ImageView targetSignImageView = null;
        while (i < numberOfTrafficSigns) {
            TrafficSign trafficSign = trafficSigns.get(i);
            switch (i) {
                case 0:
                    targetSignImageView = imageView1;
                    break;
                case 1:
                    targetSignImageView = imageView2;
                    break;
                case 2:
                    targetSignImageView = imageView3;
                    break;
            }
            if (targetSignImageView != null) {
                switch (trafficSign.type) {
                    case 1:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_1);
                        signName = signName.concat("禁止超車。");
                        break;
                    case 6:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_6);
                        signName = signName.concat("右側來車。");
                        break;
                    case 7:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_7);
                        signName = signName.concat("左側來車。");
                        break;
                    case 9:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_9);
                        signName = signName.concat("有柵門鐵路平交道。");
                        break;
                    case 10:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_10);
                        signName = signName.concat("無柵門鐵路平交道。");
                        break;
                    case 11:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_11);
                        signName = signName.concat("路寬縮減。");
                        break;
                    case 12:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_12);
                        signName = signName.concat("左彎。");
                        break;
                    case 13:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_13);
                        signName = signName.concat("右彎。");
                        break;
                    case 14:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_14);
                        signName = signName.concat("連續彎路先向左。");
                        break;
                    case 15:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_15);
                        signName = signName.concat("連續彎路先向右。");
                        break;
                    case 18:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_18);
                        signName = signName.concat("險升坡。");
                        break;
                    case 19:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_19);
                        signName = signName.concat("險降坡。");
                        break;
                    case 20:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_20);
                        signName = signName.concat("停車再開。");
                        break;
                    case 21:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_21);
                        signName = signName.concat("注意強風。");
                        break;
                    case 22:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_22);
                        signName = signName.concat("危險。");
                        break;
                    case 23:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_23);
                        signName = signName.concat("路面高突。");
                        break;
                    case 27:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_27);
                        signName = signName.concat("當心動物。");
                        break;
                    case 29:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_29);
                        signName = signName.concat("路滑。");
                        break;
                    case 30:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_30);
                        signName = signName.concat("注意落石。");
                        break;
                    case 31:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_31);
                        signName = signName.concat("當心兒童。");
                        break;
                    case 32:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_32);
                        signName = signName.concat("當心輕軌。");
                        break;
                    case 34:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_34);
                        signName = signName.concat("易肇事路段。");
                        break;
                    case 36:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_36);
                        signName = signName.concat("禁止會車。");
                        break;
                    case 41:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_41);
                        signName = signName.concat("當心行人。");
                        break;
                    case 42:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_42);
                        signName = signName.concat("讓路。");
                        break;
                    case 59:
                        targetSignImageView.setImageResource(R.drawable.traffic_sign_type_59);
                        signName = signName.concat("當心腳踏車。");
                        break;
                }
                if (!isSignShowing) {
                    showTrafficSignImageView(targetSignImageView, roadElement);
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.beep_short);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                    textToSpeech.speak(signName, TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                }
            }
            i++;
        }
        isSignShowing = true;
    }
}
