package com.fancynavi.android.app;

import com.here.android.mpa.routing.Maneuver;

class TurnPresenter {
    private String turnName;
    private String turnLocalizedName;

    TurnPresenter(Maneuver.Turn turn) {
        switch (turn) {
            case UNDEFINED:
                turnName = "Follow The Road";
                turnLocalizedName = "沿著當前道路行駛";
                break;
            case NO_TURN:
                turnName = "Go Straight";
                turnLocalizedName = "直行";
                break;
            case KEEP_MIDDLE:
                turnName = "Keep Middle";
                turnLocalizedName = "沿著中間車道";
                break;
            case KEEP_RIGHT:
                turnName = "Keep Right";
                turnLocalizedName = "沿著右側車道";
                break;
            case LIGHT_RIGHT:
                turnName = "Turn Slightly Right";
                turnLocalizedName = "稍向右轉";
                break;
            case QUITE_RIGHT:
                turnName = "Turn Right";
                turnLocalizedName = "向右轉";
                break;
            case HEAVY_RIGHT:
                turnName = "Take A Sharp Right Turn";
                turnLocalizedName = "向右急轉";
                break;
            case KEEP_LEFT:
                turnName = "Keep Left";
                turnLocalizedName = "沿著左側車道";
                break;
            case LIGHT_LEFT:
                turnName = "Turn Slightly Left";
                turnLocalizedName = "稍向左轉";
                break;
            case QUITE_LEFT:
                turnName = "Turn Left";
                turnLocalizedName = "向左轉";
                break;
            case HEAVY_LEFT:
                turnName = "Take A Sharp Left Turn";
                turnLocalizedName = "向左急轉";
                break;
            case RETURN:
                turnName = "Take A U Turn";
                turnLocalizedName = "迴轉";
                break;
            case ROUNDABOUT_1:
                turnName = "Take The First Exit Of Roundabout";
                turnLocalizedName = "從圓環的第一個出口離開";
                break;
            case ROUNDABOUT_2:
                turnName = "Take The Second Exit Of Roundabout";
                turnLocalizedName = "從圓環的第二個出口離開";
                break;
            case ROUNDABOUT_3:
                turnName = "Take The Third Exit Of Roundabout";
                turnLocalizedName = "從圓環的第三個出口離開";
                break;
            case ROUNDABOUT_4:
                turnName = "Take The Fourth Exit Of Roundabout";
                turnLocalizedName = "從圓環的第四個出口離開";
                break;
            case ROUNDABOUT_5:
                turnName = "Take The Fifth Exit Of Roundabout";
                turnLocalizedName = "從圓環的第五個出口離開";
                break;
            case ROUNDABOUT_6:
                turnName = "Take The Sixth Exit Of Roundabout";
                turnLocalizedName = "從圓環的第六個出口離開";
                break;
            case ROUNDABOUT_7:
                turnName = "Take The Seventh Exit Of Roundabout";
                turnLocalizedName = "從圓環的第七個出口離開";
                break;
            case ROUNDABOUT_8:
                turnName = "Take The Eighth Exit Of Roundabout";
                turnLocalizedName = "從圓環的第八個出口離開";
                break;
            case ROUNDABOUT_9:
                turnName = "Take The Ninth Exit Of Roundabout";
                turnLocalizedName = "從圓環的第九個出口離開";
                break;
            case ROUNDABOUT_10:
                turnName = "Take The Tenth Exit Of Roundabout";
                turnLocalizedName = "從圓環的第十個出口離開";
                break;
            case ROUNDABOUT_11:
                turnName = "Take The Eleventh Exit Of Roundabout";
                turnLocalizedName = "從圓環的第十一個出口離開";
                break;
            case ROUNDABOUT_12:
                turnName = "Take The Twelfth Exit Of Roundabout";
                turnLocalizedName = "從圓環的第十二個出口離開";
                break;
        }
    }

    String getTurnName() {
        return turnName;
    }

    String getTurnLocalizedName() {
        return turnLocalizedName;
    }
}
