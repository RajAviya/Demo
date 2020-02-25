package com.teenpattithreecardspoker;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import utils.C;
import utils.Parameters;
import utils.PreferenceManager;

public class FirebaseMessageService extends FirebaseMessagingService {
    private static final int NOTIFICATION_ID_JOIN = 1005;
    private static final int NOTIFICATION_ID_IMAGE = 1006;
    private static final int NOTIFICATION_ID_MESSAGE = 1007;
    private boolean isHasImage = true;

    C c = C.getInstance();

    String token;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        token = s;
        System.out.println("Token---->" + token);
        Log.d(TAG, "---------------------- >>> TOKEN PREF : " + PreferenceManager.getRegistrationId());
        PreferenceManager.setRegistrationId(token);
        Log.d(TAG, "Notification >>> TOKEN RECEVIED : " + token);
    }


    public FirebaseMessageService() {
    }


    static NotificationManager notificationManager;
    private static final String TAG = "MyFirebaseMsgService";

    private boolean GameisOn(Context context) {

        try {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> services;

            boolean isActivityFound = false;
            if (activityManager != null) {
                services = activityManager
                        .getRunningTasks(Integer.MAX_VALUE);

                //		for (int i = 0; i < services.size(); i++) {
                if (services.get(0).topActivity.getPackageName()
                        .equalsIgnoreCase(context.getPackageName())) {
                    isActivityFound = true;
                }
                //		}
            }

            if (isActivityFound) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

//    private boolean isApplicationBroughtToBackground(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
//        if (!tasks.isEmpty()) {
//            ComponentName topActivity = tasks.get(0).topActivity;
//            if (!topActivity.getPackageName().equals(context.getPackageName())) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Notification >>> DATA : " + remoteMessage.getData());
        Log.d(TAG, "Notification >>> From : " + remoteMessage.getFrom());
        Log.d(TAG, "Notification >>> Notification Message Body: " + remoteMessage.getData());
        CharSequence imgUrl = "";
        CharSequence userInfo = "";
        CharSequence category = "";
        CharSequence callDetails = "";
        CharSequence cta = "";
        CharSequence tbid = "";
        CharSequence tbype = "";
        CharSequence mode = "";
        CharSequence lnm = "";
        CharSequence nid = "";
        CharSequence dsi = "";
        CharSequence reward_x = "";

        if (remoteMessage.getData().containsKey("nid")) {
            nid = remoteMessage.getData().get("nid");
        }
        if (remoteMessage.getData().containsKey("CTA")) {
            cta = remoteMessage.getData().get("CTA");
        }
        if (remoteMessage.getData().containsKey("tbid")) {
            tbid = remoteMessage.getData().get("tbid");
        }
        if (remoteMessage.getData().containsKey("tbype")) {
            tbype = remoteMessage.getData().get("tbype");
        }
        if (remoteMessage.getData().containsKey("lnm")) {
            lnm = remoteMessage.getData().get("lnm");
        }
        if (remoteMessage.getData().containsKey("mode")) {
            mode = remoteMessage.getData().get("mode");
        }
        if (remoteMessage.getData().containsKey("dsi")) {
            dsi = remoteMessage.getData().get("dsi");
        }
        if (remoteMessage.getData().containsKey("reward_x")) {
            reward_x = remoteMessage.getData().get("reward_x");
        }

        JSONObject obj = null;

        if (cta.length() > 0) {
            try {
                obj = new JSONObject();
                obj.put("cta", cta);
                obj.put("tbid", tbid);
                obj.put("tbype", tbype);
                obj.put("lnm", lnm);
                obj.put("mode", mode);
                obj.put("nid", nid);
                obj.put("dsi", dsi);
                obj.put("reward_x", reward_x);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (!GameisOn(getApplicationContext())) {
            Log.d(TAG, "Notification >>> GAME IS NOT OPEN >>");
            CharSequence message = remoteMessage.getData().get("Message");
            CharSequence OfferId = remoteMessage.getData().get("OfferId");
            CharSequence Reward = "";

            if (remoteMessage.getData().containsKey("Reward")) {
                Reward = remoteMessage.getData().get("Reward");
            }

            JSONObject obj2 = new JSONObject();
            try {
                obj2.put("Message", message);
                obj2.put("OfferId", OfferId);
                if (Reward.length() > 0) {
                    obj2.put("Reward", Reward);
                }
                PreferenceManager.setPushData(obj2.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*if(isApplicationBroughtToBackground(getApplicationContext())){
                Log.d(TAG, "Notification >>> GAME IS IN BACKGROUND >>");
                if(Dashboard.handler != null){
                    Message msg = new Message();
                    msg.what = ResponseCode.HANDLE_PN_CTA;
                    msg.obj = obj.toString();
                    Log.d(TAG, "Notification >>> DATA SENT TO DASHBOARD >>");
                    Dashboard.handler.sendMessage(msg);
                }else{
                    Log.d(TAG, "Notification >>> CAN NOT SENT DATA TO DASHBOARD >>");
                }
            }*/

        } else {
            Log.d(TAG, "Notification >>> GAME IS ALREADY OPEN >>");
            PreferenceManager.setPushData("");


        }

        if (remoteMessage.getData().containsKey("image")) {
            imgUrl = remoteMessage.getData().get("image");
        }
        if (remoteMessage.getData().containsKey("userInfo")) {
            userInfo = remoteMessage.getData().get("userInfo");
        }
        if (remoteMessage.getData().containsKey("category")) {
            category = remoteMessage.getData().get("category");
        }
        if (remoteMessage.getData().containsKey("callDetails")) {
            callDetails = remoteMessage.getData().get("callDetails");
        }
//        imgUrl = "http://artoon-teenpatti.s3.amazonaws.com/uploads/mail_template/main-image-3.png";
        //Calling method to generate notification
        if (/*BuildConfig.isCallScreen &&*/ category.length() > 0 && category.toString().equalsIgnoreCase("call_alert") && callDetails.length() > 0) {
            if (!GameisOn(this)) {
                sendNotificationForCall(remoteMessage.getData(), getApplicationContext(), obj, callDetails.toString());
            }
        } else if (category.length() > 0 && userInfo.length() > 0) {
            // This Is Method For Custome NotiFiaction with Button Click
            sendNotification2(remoteMessage.getData(), getApplicationContext(), userInfo.toString());
        } else {
            if (String.valueOf(imgUrl).length() > 0) {
                try {
                    handleMessage(remoteMessage.getData(), getApplicationContext(), String.valueOf(imgUrl), obj);
                } catch (Exception e) {
                    sendNotification(remoteMessage.getData(), getApplicationContext(), obj);
                    e.printStackTrace();
                }
            } else {
                sendNotification(remoteMessage.getData(), getApplicationContext(), obj);
            }
        }
    }

    private void handleMessage(Map<String, String> data, Context context, String imgurl, JSONObject obj) {

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.game_icon);

        CharSequence from = data.get("title");
        CharSequence message = data.get("message");

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.game_icon);
        contentView.setTextViewText(R.id.title, from);
        contentView.setTextViewText(R.id.text, message);
//        contentView.setImageViewResource(R.id.big_picture,R.drawable.game_icon );

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            PendingIntent contentIntent = getPendingIntent(context);
//            Notification notification;
//            Notification.Builder builder = new Notification.Builder(context);
//            builder.setSmallIcon(getNotificationIcon())
//                    .setLargeIcon(icon)
//                    .setAutoCancel(true)
//                    .setContent(contentView)
//                    .setContentIntent(contentIntent);
//            notification = builder.getNotification();
//            notification.defaults |= Notification.DEFAULT_SOUND;
//            notification.defaults |= Notification.DEFAULT_VIBRATE;
//
////            try {
////                String channelId = getString(R.string.default_notification_channel_id);
////                String channelName = getString(R.string.default_notification_channel_name);
////                // TODO: 8/22/2018  In Android Oreo, notification channel is needed.
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
////                    notificationManager.createNotificationChannel(channel);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//
//            notificationManager.notify(NOTIFICATION_ID_IMAGE, notification);
//        } else {
        Intent i;
        //TODO INTENT_ISSUE
        if (Dashboard.handler != null) {
            i = new Intent(this, Dashboard.class);
        } else {
            i = new Intent(this, Login.class);
        }
        //i = new Intent(this, Login.class);

        i.putExtra("isFromPushNoti", true);
        try {
            if (obj != null) {
                i.putExtra("isFromCta", obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chaal);
        Bitmap remote_picture;/* = ((BitmapDrawable) d).getBitmap();*/
        try {
            remote_picture = getBitmapfromUrl(imgurl);
            contentView.setImageViewBitmap(R.id.big_picture, remote_picture);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(getNotificationIcon())
                .setLargeIcon(icon)
                .setCustomHeadsUpContentView(contentView)
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSound(sound)
                .setContentText(message)
                .setContentTitle(from)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        try {
            String channelName = getString(R.string.default_notification_channel_name);
            // TODO: 8/22/2018  In Android Oreo, notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//            try {
//                String channelId = getString(R.string.default_notification_channel_id);
//                String channelName = getString(R.string.default_notification_channel_name);
//                // TODO: 8/22/2018  In Android Oreo, notification channel is needed.
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
//                    notificationManager.createNotificationChannel(channel);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        notificationManager.notify(NOTIFICATION_ID_IMAGE, notificationBuilder.build());

        //**Show Custome Notification Popup For All Screen**//
        Handler handler = c.conn.getHandler();
        c.sendNotificationAllScreen(handler, from, message, obj);

//        }

//        try {
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            boolean isScreenOn = pm.isScreenOn();
//
//            if (isScreenOn == false) {
//                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
//                wl.acquire(10000);
//                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
//
//                wl_cpu.acquire(10000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }

    private int getNotificationIcon() {
        boolean useSilhouette = Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.M
                || Build.VERSION.SDK_INT == Build.VERSION_CODES.N
                || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;

        return useSilhouette ? R.drawable.icon_shilhoutte : R.drawable.game_icon;
    }

    private void sendNotificationForCall(Map<String, String> data, Context context, JSONObject obj, String callDetail) {
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.inKeyguardRestrictedInputMode()) {
            //it is locked
            sendNotification(data, context, obj);
        } else {
            Intent notifyIntent = new Intent(this, Login.class);
//            notifyIntent.putExtra("data",callDetail);
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(notifyIntent);
        }
    }

    private void sendNotification(Map<String, String> data, Context context, JSONObject obj) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.game_icon);
        CharSequence from = data.get("title");
        CharSequence message = data.get("message");
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.drawable.game_icon);
        contentView.setTextViewText(R.id.title, from);
        contentView.setTextViewText(R.id.text, message);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            PendingIntent contentIntent = getPendingIntent(context);
//
//            Notification notification;
//            Notification.Builder builder = new Notification.Builder(context);
//            builder.setSmallIcon(getNotificationIcon())
//                    .setLargeIcon(icon)
//                    .setContent(contentView)
//                    .setAutoCancel(true)
//                    .setContentIntent(contentIntent);
//
//            notification = builder.getNotification();
//            notification.defaults |= Notification.DEFAULT_SOUND;
//            notification.defaults |= Notification.DEFAULT_VIBRATE;
//
////            try {
////                String channelId = getString(R.string.default_notification_channel_id);
////                String channelName = getString(R.string.default_notification_channel_name);
////                // TODO: 8/22/2018  In Android Oreo, notification channel is needed.
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
////                    notificationManager.createNotificationChannel(channel);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//
//            notificationManager.notify(NOTIFICATION_ID_MESSAGE, notification);
//        } else {

        Intent i;
        //TODO INTENT_ISSUE
        if (Dashboard.handler != null) {
            i = new Intent(this, Dashboard.class);
        } else {
            i = new Intent(this, Login.class);
        }
        //i = new Intent(this, Login.class);
        i.putExtra("isFromPushNoti", true);
        try {
            if (obj != null) {
                i.putExtra("isFromCta", obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//            i.putExtra("PushData",String.valueOf(message));
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_ONE_SHOT);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chaal);

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(getNotificationIcon())
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSound(sound)
                .setLargeIcon(icon)
                .setCustomHeadsUpContentView(contentView)
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        try {
            String channelName = getString(R.string.default_notification_channel_name);
            // TODO: 8/22/2018  In Android Oreo, notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationManager.notify(NOTIFICATION_ID_MESSAGE, notificationBuilder.build());

        //**Show Custome Notification Popup For All Screen**//
//        Handler handler = c.conn.getHandler();
//        c.sendNotificationAllScreen(handler, from, message, obj);
//        }

//        try {
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            boolean isScreenOn = pm.isScreenOn();
//
//            if (isScreenOn == false) {
//                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
//                wl.acquire(10000);
//                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
//
//                wl_cpu.acquire(10000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void sendNotification2(Map<String, String> data, Context context, String userData) {
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.game_icon);
//        CharSequence from = data.get("title");
        Log.d(TAG, "Notification >>> USER DATA : " + userData);
        CharSequence message = data.get("message");
        CharSequence userProfile = "";
        CharSequence cards = "";
        JSONArray cardArray;
        Bitmap profile_picture;
        JSONObject userInfo;


        try {
            userInfo = new JSONObject(userData);

            if (userInfo.has(Parameters.propile_pic)) {
                userProfile = userInfo.getString(Parameters.propile_pic);
            }


            if (userInfo.has(Parameters.Cards)) {
                cards = userInfo.getString(Parameters.Cards);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification_button);
//        contentView.setImageViewResource(R.id.image, R.drawable.game_icon);
//        contentView.setTextViewText(R.id.title, from);
        contentView.setTextViewText(R.id.tv_message, message);
        try {
            if (cards.length() > 0) {
                try {
                    cardArray = new JSONArray(cards.toString());
                    Log.d(TAG, "Notification >>> CARDS DATA : " + cardArray.toString());
                    contentView.setImageViewResource(R.id.card0, getCardDrawable(cardArray.getString(0)));
                    contentView.setImageViewResource(R.id.card1, getCardDrawable(cardArray.getString(1)));
                    contentView.setImageViewResource(R.id.card2, getCardDrawable(cardArray.getString(2)));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Notification >>> EXCEPTION 1 : ");
                } catch (Exception e) {
                    Log.d(TAG, "Notification >>> EXCEPTION 2 : ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (userProfile.length() > 0) {
            try {
                profile_picture = getBitmapfromUrl(String.valueOf(userProfile));
                contentView.setImageViewBitmap(R.id.iv_user, profile_picture);
            } catch (Exception e) {
                e.printStackTrace();
                contentView.setImageViewResource(R.id.iv_user, R.drawable.photo_profile);
            }
        } else {
            contentView.setImageViewResource(R.id.iv_user, R.drawable.photo_profile);
        }

        // Called When Join Button Click
        Intent join = new Intent(this, Login.class);
        join.putExtra("notification_data", userData);
        join.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingJoin = PendingIntent.getActivity(this, 1, join, PendingIntent.FLAG_ONE_SHOT);
        contentView.setOnClickPendingIntent(R.id.btn_join, pendingJoin);

        // Called When Frame is Click To Send UserInfo In SP/CUVN
        contentView.setOnClickPendingIntent(R.id.frm_main_join, pendingJoin);

//        // Remove Comment To Disable  Click On Frame
//        Intent doNothing = new Intent(this, NotificationClick.class);
//        PendingIntent pendingdoNothing = PendingIntent.getBroadcast(this, 3,
//                doNothing, 0);
//        contentView.setOnClickPendingIntent(R.id.frm_main_join, pendingdoNothing);

        // Called When Close Button is Click
        Intent close = new Intent(this, ClearNotifiacation.class);
        PendingIntent pendingclose = PendingIntent.getBroadcast(this, 2, close, 0);
        contentView.setOnClickPendingIntent(R.id.btn_close, pendingclose);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//            PendingIntent contentIntent = getPendingIntent(context);
//
//            Notification notification;
//            Notification.Builder builder = new Notification.Builder(context);
//            builder.setSmallIcon(getNotificationIcon())
//                    .setLargeIcon(icon)
//                    .setAutoCancel(true)
//                    .setContent(contentView)
//                    .setContentIntent(contentIntent);
//
//            notification = builder.getNotification();
//            notification.defaults |= Notification.DEFAULT_SOUND;
//            notification.defaults |= Notification.DEFAULT_VIBRATE;
//
////            try {
////                String channelId = getString(R.string.default_notification_channel_id);
////                String channelName = getString(R.string.default_notification_channel_name);
////                // TODO: 8/22/2018  In Android Oreo, notification channel is needed.
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
////                    notificationManager.createNotificationChannel(channel);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
//
//            notificationManager.notify(NOTIFICATION_ID_JOIN, notification);
//        } else {

        Intent i = new Intent(this, Login.class);
        join.putExtra("notification_data", userData);
        join.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 4, i, PendingIntent.FLAG_ONE_SHOT);

        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.chaal);

        String channelId = getResources().getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(getNotificationIcon())
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setSound(sound)
                .setLargeIcon(icon)
                .setCustomHeadsUpContentView(contentView)
                .setCustomContentView(contentView)
                .setCustomBigContentView(contentView)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        try {

            /*<string name="default_notification_channel_id">Super_Star_TeenPatti_96241</string>
            <string name="default_notification_channel_name">Super_Star_TeenPatti</string>*/
            String channelName = getResources().getString(R.string.default_notification_channel_name);
            // TODO: 8/22/2018  In Android Oreo, notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, channelName,
                        NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
                notificationBuilder.setChannelId(channelId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        notificationManager.notify(NOTIFICATION_ID_JOIN, notificationBuilder.build());
//        }

//        try {
//            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//            boolean isScreenOn = pm.isScreenOn();
//
//            if (isScreenOn == false) {
//                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "MyLock");
//                wl.acquire(10000);
//                PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyCpuLock");
//
//                wl_cpu.acquire(10000);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private int getCardDrawable(String card_string) {

        String p1 = card_string;
        p1 = p1.toLowerCase().replace("-", "");
        int cardValues = Integer.parseInt(p1.substring(1));
        String cardColors = p1.substring(0, 1);
//        String d1 = p1.replaceAll("\\D", "");
        return getResources().getIdentifier(cardColors + cardValues, "drawable", getApplication().getPackageName());
    }

    public PendingIntent getPendingIntent(Context context) {
        return PendingIntent.getActivity(context, 5, new Intent(context,
                Login.class).putExtra("noti_type", "local").putExtra("isFromPushNoti", true), 0);
    }

    public static class ClearNotifiacation extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("NOTIFICATION >>> ", "CLEAR");

            notificationManager.cancel(NOTIFICATION_ID_JOIN);
        }
    }

//    public static class NotificationClick extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Log.d("NOTIFICATION >>> ", "Do Nothing");
//
//        }
//    }
}
