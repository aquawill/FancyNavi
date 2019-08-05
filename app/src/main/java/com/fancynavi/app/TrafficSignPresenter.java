package com.fancynavi.app;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageView;

import com.here.android.mpa.common.TrafficSign;

import java.util.List;

import static com.fancynavi.app.MainActivity.textToSpeech;
import static com.fancynavi.app.MapFragmentView.isSignShowing;

public class TrafficSignPresenter {

    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;

    public void setSignImageViews(ImageView imageView1, ImageView imageView2, ImageView imageView3) {
        this.imageView1 = imageView1;
        this.imageView2 = imageView2;
        this.imageView3 = imageView3;
    }

    private void showTrafficSignImageView(ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                imageView.setVisibility(View.GONE);
            }
        }.start();
    }

    public void showTrafficSigns(List<TrafficSign> trafficSigns, Context context) {

        int numberOfTrafficSigns = trafficSigns.size();
        int i = 0;
        String signName = "";
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
                        signName = signName.concat("狹路。");
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
                    this.showTrafficSignImageView(targetSignImageView);
                    MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.beep_short);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                    textToSpeech.speak(signName, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            i++;
        }
        isSignShowing = true;
    }
}
