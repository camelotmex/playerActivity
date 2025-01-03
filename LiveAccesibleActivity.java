package com.legazy.systems.main;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;

import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
//import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.felipecsl.gifimageview.library.GifImageView;
import androidx.media3.common.C;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.DefaultLivePlaybackSpeedControl;
import androidx.media3.exoplayer.DefaultLoadControl;
import androidx.media3.exoplayer.DefaultRenderersFactory;
import androidx.media3.exoplayer.ExoPlaybackException;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.LoadControl;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.Tracks;
import androidx.media3.exoplayer.RenderersFactory;
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.common.TrackGroup;
import androidx.media3.exoplayer.source.TrackGroupArray;
//import androidx.media3.common.Tracks;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.ui.CaptionStyleCompat;
import androidx.media3.ui.PlayerView;
import androidx.media3.ui.SubtitleView;
import androidx.media3.exoplayer.upstream.BandwidthMeter;
import androidx.media3.datasource.DataSource;
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter;
import androidx.media3.datasource.DefaultDataSourceFactory;
import androidx.media3.datasource.TransferListener;
import androidx.media3.common.util.Util;


import com.google.gson.Gson;
import com.legazy.systems.R;
import com.legazy.systems.adapter.ChannelMenuListAdapterAccessible;
import com.legazy.systems.adapter.MenuListAdapter;
import com.legazy.systems.http.APIConstant;
import com.legazy.systems.http.AppController;
import com.legazy.systems.http.VolleyCallback;
import com.legazy.systems.http.XtreamAPI;
import com.legazy.systems.ijkmedia.IjkVideoView;
import com.legazy.systems.ijkmedia.TableLayoutBinder;
import com.legazy.systems.model.ArrayItemTopic;
import com.legazy.systems.model.CategoryItem;
import com.legazy.systems.model.ChannelItem;
import com.legazy.systems.model.ItemTopic;
import com.legazy.systems.utils.AppConstants;
import com.legazy.systems.utils.DateUtil;
import com.legazy.systems.utils.Utils;
import com.legazy.systems.view.VerticalTextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.videolan.libvlc.interfaces.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaFormat;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkMediaFormat;

import static com.legazy.systems.utils.AppConstants.UPDATE_TIME_DELAY;

public class LiveAccessibleActivity extends BaseActivity implements IVLCVout.Callback, IVLCVout.OnNewVideoLayoutListener, org.videolan.libvlc.MediaPlayer.EventListener{
    private static final String KEY_PLAY_WHEN_READY = "play_when_ready";
    private static final String KEY_WINDOW = "window";
    private static final String KEY_POSITION = "position";
    private String Apagado;


    protected PlayerView playerView;
    protected ExoPlayer player;

    protected Timeline.Window window;
    protected DataSource.Factory mediaDataSourceFactory;
    protected boolean shouldAutoPlay;
    protected BandwidthMeter bandwidthMeter;

    protected boolean playWhenReady;
    protected int currentWindow;
    protected long playbackPosition;

    protected Handler reconnectHandler = new Handler();
    protected Runnable reconnectRunnable = new Runnable() {
        @Override
        public void run() {
            currentWindow = C.INDEX_UNSET;
            playbackPosition = C.INDEX_UNSET;
            initializePlayer();
        }
    };

    private DrawerLayout drawerLayout;
    private RelativeLayout rlVideo;
    private LinearLayout llContent, llProgressBar, llDetails, llchannelinfo;
    private LinearLayout llSideBar;
    private TextView tvCurrentTime;
    private TextView tvSelectedChannelNumber;

    private String          padUnlockedCategory;
    private ChannelItem     selectedChannelItem;
    private String          selectedChannelNumber = "";
    private Handler         selectChannelHandler = new Handler();
    private Runnable        selectChannelRunnable = new Runnable() {
        @Override
        public void run() {
            if(selectedChannelNumber.isEmpty())
                return;

            if(selectedChannelItem != null) {
                if(AppConstants.LIVE_PARENTAL.values.contains(selectedChannelItem.m_sCategory_ID)) {
                    if(padUnlockedCategory == null || !padUnlockedCategory.equals(selectedChannelItem.m_sCategory_ID)) {
                        LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
                        assert layoutInflater != null;
                        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.parental_dialog, null, false);
                        final Dialog parentalDlg = new Dialog(LiveAccessibleActivity.this, R.style.Theme_CustomDialog);
                        parentalDlg.setContentView(view);

                        TextView tvHeader = view.findViewById(R.id.ID_TEXT_HEADER);
                        EditText edtPassword = view.findViewById(R.id.ID_EDIT_PASSWORD);
                        EditText edtConfirmPassword = view.findViewById(R.id.ID_EDIT_CONFIRM_PASSWORD);
                        edtConfirmPassword.setVisibility(View.GONE);
                        TextView tvBtn = view.findViewById(R.id.ID_TEXT_BTN_SET_PASSWORD);

                        tvHeader.setText(getString(R.string.confirm_password));
                        tvBtn.setText(getString(R.string.confirm_password));

                        tvBtn.setOnClickListener(v1 -> {
                            if (!edtPassword.getText().toString().equals(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PARENTAL_PASSWORD, ""))) {
                                Toast.makeText(LiveAccessibleActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                            } else {
                                padUnlockedCategory = selectedChannelItem.m_sCategory_ID;
                                String newStreamUrl = Utils.makeStreamURL(LiveAccessibleActivity.this, selectedChannelItem.m_sStreamID);
                                if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && player == null) {
                                    currentChannelItem = selectedChannelItem;
                                    streamUrl = newStreamUrl;
                                    initializePlayer();
                                } else if (!streamUrl.equalsIgnoreCase(newStreamUrl)) {
                                    currentChannelItem = selectedChannelItem;
                                    streamUrl = newStreamUrl;
                                    releasePlayer();
                                    initializePlayer();
                                }
                                parentalDlg.dismiss();
                            }
                        });

                        parentalDlg.setOnDismissListener(dialog -> selectedChannelItem = null);

                        parentalDlg.show();
                    }
                    else {
                        String newStreamUrl = Utils.makeStreamURL(LiveAccessibleActivity.this, selectedChannelItem.m_sStreamID);
                        if (streamUrl == null) {
                            currentChannelItem = selectedChannelItem;
                            streamUrl = newStreamUrl;
                            initializePlayer();
                        } else if (!streamUrl.equalsIgnoreCase(newStreamUrl)) {
                            currentChannelItem = selectedChannelItem;
                            streamUrl = newStreamUrl;
                            releasePlayer();
                            initializePlayer();
                        }
                        selectedChannelItem = null;
                    }
                }
                else {
                    padUnlockedCategory = null;
                    String newStreamUrl = Utils.makeStreamURL(LiveAccessibleActivity.this, selectedChannelItem.m_sStreamID);
                    if(streamUrl == null) {
                        currentChannelItem = selectedChannelItem;
                        streamUrl = newStreamUrl;
                        initializePlayer();
                    }
                    else if(!streamUrl.equalsIgnoreCase(newStreamUrl)){
                        currentChannelItem = selectedChannelItem;
                        streamUrl = newStreamUrl;
                        releasePlayer();
                        initializePlayer();
                    }
                    selectedChannelItem = null;
                }
            }

            selectedChannelNumber = "";
            tvSelectedChannelNumber.setText(selectedChannelNumber);

            if(llDetails.getVisibility() == View.VISIBLE) {
                detailHandler.postDelayed(detailRunnable, 5000);
            }
        }
    };

    private Handler detailHandler = new Handler();
    private Runnable detailRunnable = new Runnable() {
        @Override
        public void run() {
            llDetails.setVisibility(View.GONE);
            //llchannelinfo.setVisibility(View.GONE);
            if(btnCloseCaption != null) {
                btnCloseCaption.setVisibility(View.GONE);
            }
            if(btnAudioTrack != null) {
                btnAudioTrack.setVisibility(View.GONE);
            }
            mVideoSurfaceFrame.requestFocus();
        }
    };

    private ListView lvChannelList;

    private Activity activity;
    private ChannelMenuListAdapterAccessible liveMenuAdapter;

    private ImageView ivChannel, ivChannelLock;
    private TextView tvTopicName, tvTopicDescription, tvTopicPlayTime;
    private TextView tvChannelNumber, tvChannelName;
    //private View vDivider;
    private ScrollView svTopicDescription;

    private LinearLayout llLoadingLayout;
    private TextView tvLoading;

    private Handler m_timeHandler = new Handler();

    private SurfaceView mVideoSurface = null;
    private LibVLC mLibVLC = null;
    private org.videolan.libvlc.MediaPlayer mMediaPlayer = null;

    private final Handler mHandler = new Handler();
    private final Runnable mRunnable = this::updateVideoSurfaces;
    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;

    private IjkVideoView mVideoView;
    private FrameLayout mVideoSurfaceFrame = null;

    private String streamUrl;

    private TextView tvStartTime, tvEpgName, tvChannelGenre, tvDuration;
    //private RatingBar ratingBar;
    private VerticalTextView vtvCategory;
    private EditText edtSearch;
    private ListView lvLiveCategoryList;
    private MenuListAdapter liveCategoryMenuAdapter;
    private ProgressBar videoProgressBar;
    //private ImageView ivFullScreenMode;
    private ChannelItem currentChannelItem;

    private ImageView ivChannelLogoCenter;

    private ImageView ivPlayBtn;
    private Boolean doubleBackToExitPressedOnce = false;

    private AlertDialog dialog;
    private Handler userInteractionHandler = new Handler();
    private Runnable userInteractionRunnable = new Runnable() {
        @Override
        public void run() {
            final AlertDialog.Builder builder = new AlertDialog.Builder(LiveAccessibleActivity.this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
            builder.setCancelable(true);
            builder.setTitle("Warning");
            builder.setMessage(getString(R.string.user_interaction_check_play));

            builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> dialogInterface.dismiss());

            dialog = builder.create();
            dialog.setOnDismissListener(dialogInterface -> continuePlayHandler.removeCallbacks(continuePlayRunnable));
            dialog.show();

            final Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setBackground(getDrawable(R.drawable.button_back));
            LinearLayout.LayoutParams positiveButtonLL = (LinearLayout.LayoutParams) positiveButton.getLayoutParams();
            positiveButtonLL.gravity = Gravity.CENTER;
            positiveButton.setLayoutParams(positiveButtonLL);

            continuePlayHandler.postDelayed(continuePlayRunnable, AppConstants.CHECK_USER_INTERACTION_TIMEOUT_CONTINUE_PLAY);
        }
    };

    private Handler continuePlayHandler = new Handler();
    private Runnable continuePlayRunnable = () -> {
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            if(player != null) {
                player.stop();
            }
        }
        else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
            if(mVideoView != null) {
                mVideoView.stopPlayback();
            }
        }
        else {
            if(mMediaPlayer != null) {
                mMediaPlayer.stop();
                Apagado = "true";
                //mMediaPlayer.pause();
            }
        }
        LayoutInflater inflater = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
        @SuppressLint("InflateParams") View recordingView = inflater.inflate(R.layout.skin_confirm_dialog, null, false);
        final Dialog confirmDlg = new Dialog(this, R.style.Theme_CustomDialog);
        confirmDlg.setContentView(recordingView);

        ((TextView)confirmDlg.findViewById(R.id.tvDescription)).setText(getString(R.string.user_interaction_check_play_continue));
        ((TextView)confirmDlg.findViewById(R.id.ID_BUTTON_OK)).setText(getString(R.string.yes));
        confirmDlg.findViewById(R.id.ID_BUTTON_OK).setOnClickListener(v1 -> {


            initializePlayer();
            userInteractionHandler.postDelayed(userInteractionRunnable, AppConstants.CHECK_USER_INTERACTION_TIMEOUT);
            confirmDlg.dismiss();
        });
        ((TextView)confirmDlg.findViewById(R.id.ID_BUTTON_CANCEL)).setText(getString(R.string.no));
        confirmDlg.findViewById(R.id.ID_BUTTON_CANCEL).setOnClickListener(v12 -> {
            finish();
            confirmDlg.dismiss();
        });

        confirmDlg.show();
    };

    protected Handler progressHandler = new Handler();
    protected Runnable progressRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            if(lvChannelList == null || liveMenuAdapter.getActiveMenuIndex() < 0)
                return;

            int duration = 0, curProgress = 0;
            if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 ? (player != null && player.getPlaybackState() == Player.STATE_READY) :
                    Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 ? (mVideoView != null && mVideoView.isPlaying()) : (mMediaPlayer != null && mMediaPlayer.isPlaying())) {
                //if(currentChannelItem != null) {
                    //tvEpgName.setText(currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex()).m_sTitle);
                    //.setText(currentChannelItem.m_sStreamType);
                    //ratingBar.setRating(5.0f);
                //}
                //tvChannelNumber.setText(currentChannelItem.m_sTvNum);
                //tvChannelName.setText(currentChannelItem.m_sTvName);
                //tvChannelName.setSelected(true);

                ItemTopic selectedTopic = currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex());
                //tvTopicName.setText(selectedTopic.m_sTitle);

                /*if (selectedTopic.m_dateTopicStart != null && selectedTopic.m_dateTopicEnd != null) {
                    String duration1 = Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicStart.getTime())
                            + " - " + Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicEnd.getTime());
                    tvTopicPlayTime.setText(duration1);
                } else {
                    tvTopicPlayTime.setText(R.string.unlimited);
                }*/

                if(currentChannelItem != null && currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex()).m_dateTopicEnd != null &&
                        currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex()).m_dateTopicStart != null) {
                    duration = (int) (currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex()).m_dateTopicEnd.getTime() -
                            currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex()).m_dateTopicStart.getTime());

                    curProgress = (int)(Utils.CurrentTime().getTime() - currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex()).m_dateTopicStart.getTime());

                    tvStartTime.setText(Utils.getShortTime(LiveAccessibleActivity.this, currentChannelItem.m_arrItemTopic.get(currentChannelItem.getCurrentTopicIndex()).m_dateTopicStart.getTime()));
                }
                else {
                    tvStartTime.setText("");
                }

                if(duration - curProgress < 30 * 1000) {
                    updateSelectedTopicInfo(true);
                }

                int endHours = (duration / (60 * 60 * 1000));
                int endMinutes = ((duration - (endHours * 60 * 60 * 1000)) / (60 * 1000));
                int endSeconds = ((duration / 1000) % 60);

                String durationStr;
                if(endHours > 0)
                    durationStr = String.format("%02d:%02d:%02d", endHours, endMinutes, endSeconds);
                else
                    durationStr = String.format("%02d:%02d", endMinutes, endSeconds);

                tvDuration.setText(durationStr);
                if(videoProgressBar != null && duration > 0) {
                    if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1)
                        videoProgressBar.setProgress((curProgress * 100 / duration));
                    else
                        videoProgressBar.setProgress((curProgress * 100 / duration));
                }
            }

            progressHandler.postDelayed(progressRunnable, 500);
        }
    };

    private ChannelItem shortEpgChannel;
    private Handler shortEpgHandler = new Handler();
    private Runnable shortEpgRunnable = new Runnable() {
        @Override
        public void run() {
            AppController.getInstance().cancelPendingRequests(APIConstant.TAG_GET_SIMPLE_DATA_TABLE);
            String userName = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.USERNAME, "");
            String password = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PASSWORD, "");
            String serverUrl = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.SERVERURL, getString(R.string.base_url));
            XtreamAPI.getSimpleDataTable(userName, password, serverUrl, shortEpgChannel.m_sStreamID, new VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    new Thread(()->{
                        Date currentDate = Utils.CurrentTime();
                        while (shortEpgChannel.m_arrItemTopic.size() > 1) {
                            shortEpgChannel.m_arrItemTopic.remove(shortEpgChannel.m_arrItemTopic.size() - 1);
                        }
                        try {
                            final JSONObject jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray(APIConstant.ITEM_EPG_LISTINGS);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                final ItemTopic itemTopic = new ItemTopic();
                                JSONObject object = jsonArray.getJSONObject(i);
                                itemTopic.m_sTitle = object.optString(APIConstant.ITEM_TITLE);
                                if(itemTopic.m_sTitle != null) {
                                    itemTopic.m_sTitle = new String(Base64.decode(itemTopic.m_sTitle, Base64.DEFAULT), StandardCharsets.UTF_8);
                                }
                                itemTopic.m_sDescription = object.optString(APIConstant.ITEM_DESCRIPTION);
                                if(itemTopic.m_sDescription != null) {
                                    itemTopic.m_sDescription = new String(Base64.decode(itemTopic.m_sDescription, Base64.DEFAULT), StandardCharsets.UTF_8);
                                }
                                itemTopic.m_sChannelID = object.optString(APIConstant.ITEM_CHANNEL_ID);

                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss Z");
                                try {
                                    String startTime = object.optString(APIConstant.ITEM_START);
                                    startTime = startTime.replace("-", "");
                                    startTime = startTime.replace(" ", "");
                                    startTime = startTime.replace(":", "");
                                    startTime += " -0500";
                                    String endTime = object.optString(APIConstant.ITEM_END);
                                    endTime = endTime.replace("-", "");
                                    endTime = endTime.replace(" ", "");
                                    endTime = endTime.replace(":", "");
                                    endTime += " -0500";
                                    itemTopic.m_dateTopicStart = format.parse(DateUtil.UTCStringToLocalString(startTime));
                                    itemTopic.m_dateTopicEnd = format.parse(DateUtil.UTCStringToLocalString(endTime));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if(itemTopic.m_dateTopicEnd.before(currentDate)) {
                                    continue;
                                }
                                else if(shortEpgChannel.m_arrItemTopic.size() > 2) {
                                    break;
                                }
                                shortEpgChannel.m_arrItemTopic.add(itemTopic);
                            }
                            runOnUiThread(() -> {
                                ItemTopic selectedTopic = shortEpgChannel.m_arrItemTopic.get(shortEpgChannel.getCurrentTopicIndex());
                                tvTopicName.setText(selectedTopic.m_sTitle);
                                /*if(selectedTopic.m_dateTopicStart != null && selectedTopic.m_dateTopicEnd != null) {
                                    String duration = Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicStart.getTime())
                                            + " - " + Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicEnd.getTime());
                                    tvTopicPlayTime.setText(duration);
                                }
                                else
                                    tvTopicPlayTime.setText(R.string.unlimited);

                                 */

                                //tvTopicDescription.setText(selectedTopic.m_sDescription);
                                String EpgConcatened = "";
                                for(int i=shortEpgChannel.getCurrentTopicIndex();i<shortEpgChannel.m_arrItemTopic.size();i++){
                                    String duration = "";

                                    if (shortEpgChannel.m_arrItemTopic.get(i).m_dateTopicStart != null && shortEpgChannel.m_arrItemTopic.get(i).m_dateTopicEnd != null) {
                                        duration = Utils.getShortTime(LiveAccessibleActivity.this, shortEpgChannel.m_arrItemTopic.get(i).m_dateTopicStart.getTime())
                                                + " - " + Utils.getShortTime(LiveAccessibleActivity.this, shortEpgChannel.m_arrItemTopic.get(i).m_dateTopicEnd.getTime());

                                    } else {
                                        duration = "unlimited";
                                    }
                                    String htmlTitle = "";
                                    if (i == shortEpgChannel.getCurrentTopicIndex()) {
                                        htmlTitle = "<span style='color: #44a600;'>" + duration + " - " + shortEpgChannel.m_arrItemTopic.get(i).m_sTitle + "</span><br>";
                                    } else {
                                        htmlTitle = "<span style='color: #66ccff;'>" + duration + " - " + shortEpgChannel.m_arrItemTopic.get(i).m_sTitle + "</span><br>";
                                    }

                                    String htmlDescription = "<span style='font-size: 15px;'>" + shortEpgChannel.m_arrItemTopic.get(i).m_sDescription + "</span>";
                                    EpgConcatened = EpgConcatened + htmlTitle + htmlDescription + "<br><br>";
                                }
                                svTopicDescription.scrollTo(0,0);
                                tvTopicDescription.setText(Html.fromHtml(EpgConcatened));

                                if(selectedChannelItem != null) {
                                    liveMenuAdapter.setActiveMenuIndex(selectedChannelItem.getCurrentTopicIndex());
                                }
                                liveMenuAdapter.notifyDataSetChanged();

                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }

                @Override
                public void onError(Object error) {
                    Log.e("error", error.toString());
                }
            });
        }
    };

    @OptIn(markerClass = UnstableApi.class) @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_accessible);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //for exo video player
        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            if (savedInstanceState == null) {
                playWhenReady = true;
                currentWindow = 0;
                playbackPosition = 0;
            } else {
                playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
                currentWindow = savedInstanceState.getInt(KEY_WINDOW);
                playbackPosition = savedInstanceState.getLong(KEY_POSITION);
            }

            // Load All Demands
            shouldAutoPlay = true;
            bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
            try {

                if(Utils.securityInfo.userAgent == null || Utils.securityInfo.userAgent.isEmpty() || Utils.securityInfo.userAgent.equals("null"))
                {
                    mediaDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"), (TransferListener) bandwidthMeter);
                }
                else
                {
                    mediaDataSourceFactory = new DefaultDataSourceFactory(this, Utils.securityInfo.userAgent, (TransferListener) bandwidthMeter);
                }
            } catch (Exception e) {
                Log.e("error", e.toString());
                mediaDataSourceFactory = new DefaultDataSourceFactory(this, "", (TransferListener) bandwidthMeter);

            }
            window = new Timeline.Window();
        }

        initControl();
        setEventListener();

        llContent.setVisibility(View.GONE);
        if(AppConstants.CHANNEL_CATEGORY_LIST.size() > 0)
            updateEpg();
        else
            getLiveCategory();

        m_timeHandler.postDelayed(runnableUpdateTime, AppConstants.UPDATE_TIME_DELAY);
        changeCategory();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawers();
            return;
        }

        if(llLoadingLayout != null && llLoadingLayout.getVisibility() == View.VISIBLE &&
                (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 ? player == null :
                        Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 ? mVideoView != null && !mVideoView.isPlaying() : mMediaPlayer != null && !mMediaPlayer.isPlaying())) {
            AppController.getInstance().cancelPendingRequests(APIConstant.TAG_GET_LIVE_STREAMS);
            AppController.getInstance().cancelPendingRequests(APIConstant.TAG_GET_ALL_EPG);
            AppController.getInstance().cancelPendingRequests(APIConstant.TAG_GET_LIVE_CATEGORY);
            AppConstants.CHANNEL_CATEGORY_LIST.clear();
            doubleBackToExitPressedOnce = true;
        }
        else if((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 ? (player != null && playerView != null && playerView.getVisibility() == View.VISIBLE) :
                Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 ? (mVideoView != null && mVideoView.isPlaying()) : (mMediaPlayer != null && mMediaPlayer.isPlaying())) &&
                llDetails != null && llDetails.getVisibility() == View.VISIBLE) {
            doubleBackToExitPressedOnce = true;
        }
        else if(!doubleBackToExitPressedOnce &&
                (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 ? (player != null && playerView != null && playerView.getVisibility() == View.VISIBLE) :
                        Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 ? (mVideoView != null && mVideoView.isPlaying()) : (mMediaPlayer != null && mMediaPlayer.isPlaying())) &&
                llDetails != null && llDetails.getVisibility() != View.VISIBLE) {

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.exit_back), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            return;
        }
        else {
            doubleBackToExitPressedOnce = true;
        }

        if(lvChannelList.getVisibility() == View.VISIBLE)
        {
            m_timeHandler.removeCallbacks(runnableUpdateTime);
            userInteractionHandler.removeCallbacks(userInteractionRunnable);
            shortEpgHandler.removeCallbacks(shortEpgRunnable);
            super.onBackPressed();

        }
        else {
            showGuide();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT > 23 && streamUrl != null && !streamUrl.isEmpty()) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < 23 && streamUrl != null && !streamUrl.isEmpty()) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT <= 23) {
            releasePlayer();
        }

        Gson gson = new Gson();
        String json = gson.toJson(AppConstants.REMINDER_TOPIC_ARRAY);
        Utils.setSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.REMINDER_ARRAY, json);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (Build.VERSION.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            updateStartPosition();

            outState.putBoolean(KEY_PLAY_WHEN_READY, playWhenReady);
            outState.putInt(KEY_WINDOW, currentWindow);
            outState.putLong(KEY_POSITION, playbackPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onUserInteraction() {
        userInteractionHandler.removeCallbacks(userInteractionRunnable);
        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            if(player != null && player.getPlayWhenReady()) {
                userInteractionHandler.postDelayed(userInteractionRunnable, AppConstants.CHECK_USER_INTERACTION_TIMEOUT);
            }
        }
        else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
            if(mVideoView != null && mVideoView.isPlaying()) {
                userInteractionHandler.postDelayed(userInteractionRunnable, AppConstants.CHECK_USER_INTERACTION_TIMEOUT);
            }
        }
        else {
            if(mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                userInteractionHandler.postDelayed(userInteractionRunnable, AppConstants.CHECK_USER_INTERACTION_TIMEOUT);
            }
        }
        super.onUserInteraction();
    }

    protected void updateStartPosition() {
        if (player == null) return;

        playbackPosition = player.getCurrentPosition();
        currentWindow = player.getCurrentWindowIndex();
        playWhenReady = player.getPlayWhenReady();
    }

    @Override
    protected void initControl() {
        super.initControl();
        drawerLayout = findViewById(R.id.ID_DRAWER_LAYOUT);
        FrameLayout flRoot = findViewById(R.id.ID_FL_ROOT);
        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_SKIN_SHACK_TV, AppConstants.IS_SKIN_SHACK_TV_DEFAULT)) == 0 ||  //String.valueOf(0) instead of "4"
                Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_SKIN_SHACK_TV, AppConstants.IS_SKIN_SHACK_TV_DEFAULT)) == 1 ||  //String.valueOf(0) instead of "4"
                Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_SKIN_SHACK_TV, AppConstants.IS_SKIN_SHACK_TV_DEFAULT)) == 2 ||  //String.valueOf(0) instead of "4"
                Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_SKIN_SHACK_TV, AppConstants.IS_SKIN_SHACK_TV_DEFAULT)) == 3) {  //String.valueOf(0) instead of "4"
            flRoot.setBackgroundResource(R.drawable.background);
        }

        if (Utils.bitmapBackground != null) {
            flRoot.setBackground(Utils.bitmapBackground);
            GifImageView gifBackView = findViewById(R.id.ID_GIF_BACKGROUND);
            gifBackView.setVisibility(View.GONE);
        }
        else if(Utils.gifBackground != null) {
            flRoot.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            GifImageView gifBackView = findViewById(R.id.ID_GIF_BACKGROUND);
            gifBackView.setVisibility(View.VISIBLE);
            gifBackView.setBytes(Utils.gifBackground);
            if(!Utils.bIsFireStick)
                gifBackView.startAnimation();
        }

        llContent = findViewById(R.id.ID_LL_CONTENT);
        llProgressBar = findViewById(R.id.ID_LL_PROGRESS_BAR);
        llSideBar = findViewById(R.id.ID_LL_SIDE_BAR);
        llDetails = findViewById(R.id.ID_LL_DETAILS);
        //llchannelinfo = findViewById(R.id.ID_LL_CHANNEL_INFO);
        tvCurrentTime = findViewById(R.id.ID_TEXT_TIME);
        tvSelectedChannelNumber = findViewById(R.id.ID_TEXT_SELECTED_CHANNEL_NUMBER);

        lvChannelList = findViewById(R.id.ID_CHANNEL_LIST);
        lvChannelList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        liveMenuAdapter = new ChannelMenuListAdapterAccessible(this, AppConstants.EPG_FILTERED_DATA, "CHANNEL_ARRAY");
        lvChannelList.setAdapter(liveMenuAdapter);

        llLoadingLayout = findViewById(R.id.ID_LL_LOADING);
        tvLoading = findViewById(R.id.ID_TEXT_LOADING);

        //ivChannel = findViewById(R.id.ID_IMG_CHANNEL);
        ivChannelLock = findViewById(R.id.ID_IMG_LOCK);
        tvTopicName = findViewById(R.id.ID_TEXT_TOPIC_NAME);
        tvTopicDescription = findViewById(R.id.ID_TEXT_TOPIC_DESCRIPTION);
        svTopicDescription = findViewById(R.id.ID_SV_TOPIC_DESCRIPTION);
        //tvTopicPlayTime = findViewById(R.id.ID_TEXT_TOPIC_PLAY_TIME);
        //tvChannelNumber = findViewById(R.id.ID_TEXT_CHANNEL_NUMBER);
        //tvChannelName = findViewById(R.id.ID_TEXT_CHANNEL_NAME);
        //vDivider = findViewById(R.id.ID_DIVIDER);
        //vDivider.setVisibility(View.INVISIBLE);

        tvStartTime = findViewById(R.id.ID_TEXT_START_TIME);
        tvStartTime.setText("");
        //tvEpgName = findViewById(R.id.ID_TEXT_EPG_NAME);
        //tvEpgName.setText("");
        //tvChannelGenre = findViewById(R.id.ID_TEXT_GENRE);
        //tvChannelGenre.setText("");
        tvDuration = findViewById(R.id.ID_TEXT_VIDEO_LENGTH);
        tvDuration.setText("");
        //ratingBar = findViewById(R.id.ID_RATING);
        //ratingBar.setRating(0.0f);
        vtvCategory = findViewById(R.id.ID_TEXT_CATEGORIES);
        //ivFullScreenMode = findViewById(R.id.ID_IMG_FULL_SCREEN);
        edtSearch = findViewById(R.id.ID_EDIT_SEARCH);
        lvLiveCategoryList = findViewById(R.id.ID_LIVE_CATEGORY_LIST);
        liveCategoryMenuAdapter = new MenuListAdapter(LiveAccessibleActivity.this, AppConstants.CHANNEL_CATEGORY_LIST);
        lvLiveCategoryList.setAdapter(liveCategoryMenuAdapter);
        videoProgressBar = findViewById(R.id.ID_PROGRESS_BAR);

        ivChannelLogoCenter = findViewById(R.id.ID_IMG_CHANNEL_LOGO_CENTER);

        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            playerView = findViewById(R.id.ID_EXO_PLAYER_VIEW);
            playerView.setVisibility(View.VISIBLE);
        }
        else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0) {
            mVideoView = findViewById(R.id.ID_VIDEO_VIEW);
            mVideoView.setVisibility(View.VISIBLE);
        }
        else {
            mVideoSurface = findViewById(R.id.video_surface);
            mVideoSurface.setVisibility(View.VISIBLE);
        }

        rlVideo = findViewById(R.id.ID_RL_VIDEO);
        rlVideo.setOnClickListener(view -> {
            if(player != null) {
                if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
                    if (lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && player.getPlayWhenReady()) {
                        hideGuide();
                    } else if (lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && player.getPlayWhenReady()) {
                        showGuide();
                    }
                } else if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0) {
                    if (lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && mVideoView.isPlaying()) {
                        hideGuide();
                    } else if (lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && mVideoView.isPlaying()) {
                        showGuide();
                    }
                } else {
                    if (lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && mMediaPlayer.isPlaying()) {
                        hideGuide();
                    } else if (lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && mMediaPlayer.isPlaying()) {
                        showGuide();
                    }
                }
            }
        });

        mVideoSurfaceFrame = findViewById(R.id.ID_FL_VIDEO_SURFACE);
        ivPlayBtn = findViewById(R.id.ID_IMG_PLAY);
    }

    private void changeCategory() {

        liveCategoryMenuAdapter.setActiveMenuIndex(AppConstants.CATEGORY_INDEX);
        liveCategoryMenuAdapter.notifyDataSetChanged();
        lvLiveCategoryList.setSelection(AppConstants.CATEGORY_INDEX);
        filterChannel(AppConstants.CATEGORY_INDEX);
    }

    private void setEventListener() {
        mVideoSurfaceFrame.setOnKeyListener((v, keyCode, event) -> {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_0:
                case KeyEvent.KEYCODE_1:
                case KeyEvent.KEYCODE_2:
                case KeyEvent.KEYCODE_3:
                case KeyEvent.KEYCODE_4:
                case KeyEvent.KEYCODE_5:
                case KeyEvent.KEYCODE_6:
                case KeyEvent.KEYCODE_7:
                case KeyEvent.KEYCODE_8:
                case KeyEvent.KEYCODE_9:
                case KeyEvent.KEYCODE_DPAD_UP:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        selectChannelByNumber(keyCode);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_FORWARD_DEL:
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        selectChannelByNumber(keyCode);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE) {
                            if ((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null && player.getPlayWhenReady()) ||
                                    (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null && mVideoView.isPlaying()) ||
                                    (mMediaPlayer != null && mMediaPlayer.isPlaying())) {

                                llDetails.setVisibility(View.VISIBLE);
                                llContent.setVisibility(View.VISIBLE);
                                //llchannelinfo.setVisibility(View.VISIBLE);
                                if ((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null) ||
                                        (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null) || mVideoSurface != null) {
                                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
                                    layoutParams.bottomToTop = -1;
                                    layoutParams.bottomToBottom = llDetails.getId();
                                    rlVideo.setLayoutParams(layoutParams);
                                }

                                if (liveMenuAdapter != null && liveMenuAdapter.getCurrentPlayingChannel() != null)
                                    showSelectedTopicInfo(liveMenuAdapter.getCurrentPlayingChannel());

                                if (btnCloseCaption != null) {
                                    btnCloseCaption.setVisibility(View.VISIBLE);
                                    btnCloseCaption.requestFocus();
                                }
                                if (btnAudioTrack != null) {
                                    btnAudioTrack.setVisibility(View.VISIBLE);
                                }
                                detailHandler.removeCallbacks(detailRunnable);
                                detailHandler.postDelayed(detailRunnable, 5000);
                            }
                        }
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE) {
                            if ((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null && player.getPlayWhenReady()) ||
                                    (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null && mVideoView.isPlaying()) ||
                                    (mMediaPlayer != null && mMediaPlayer.isPlaying())) {
                                if (Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.ENABLE_LIVE_PAUSE, String.valueOf(false)))) {
                                    if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null && player.getPlayWhenReady()) {
                                        player.stop();
                                    } else if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0) {
                                        mVideoView.stopPlayback();
                                    } else {
                                        mMediaPlayer.stop();
                                    }
                                    ivPlayBtn.setVisibility(View.VISIBLE);
                                    ivPlayBtn.requestFocus();
                                } else {
                                    showMediaInfo();
                                }
                            }
                        }
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE) {
                            if ((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null && player.getPlayWhenReady()) ||
                                    (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null && mVideoView.isPlaying()) ||
                                    (mMediaPlayer != null && mMediaPlayer.isPlaying())) {
                                showGuide();
                            }
                        }
                        return true;
                    }
                default:
                    break;
            }
            return false;
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchChannel(String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lvChannelList.setOnFocusChangeListener((view, b) -> {
            if(liveMenuAdapter.getActiveMenuIndex() > -1)
                lvChannelList.setSelection(liveMenuAdapter.getActiveMenuIndex());
        });

        lvChannelList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != liveMenuAdapter.getActiveMenuIndex()) {
                    liveMenuAdapter.setActiveMenuIndex(i);

                    liveMenuAdapter.notifyDataSetChanged();
                    updateSelectedTopicInfo(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lvChannelList.setOnItemClickListener((parent, view, position, id) -> {
            if(position != liveMenuAdapter.getActiveMenuIndex()) {
                liveMenuAdapter.setActiveMenuIndex(position);

                liveMenuAdapter.notifyDataSetChanged();
                updateSelectedTopicInfo(false);
                return;
            }

            ChannelItem epgChannel = AppConstants.EPG_FILTERED_DATA.get(position);

            if(AppConstants.LIVE_PARENTAL.values.contains(epgChannel.m_sCategory_ID))  {
                if(padUnlockedCategory == null || !padUnlockedCategory.equals(epgChannel.m_sCategory_ID)) {
                    LayoutInflater layoutInflater = ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));
                    assert layoutInflater != null;
                    @SuppressLint("InflateParams") View dlgView = layoutInflater.inflate(R.layout.parental_dialog, null, false);
                    final Dialog parentalDlg = new Dialog(LiveAccessibleActivity.this, R.style.Theme_CustomDialog);
                    parentalDlg.setContentView(dlgView);

                    TextView tvHeader = dlgView.findViewById(R.id.ID_TEXT_HEADER);
                    EditText edtPassword = dlgView.findViewById(R.id.ID_EDIT_PASSWORD);
                    EditText edtConfirmPassword = dlgView.findViewById(R.id.ID_EDIT_CONFIRM_PASSWORD);
                    edtConfirmPassword.setVisibility(View.GONE);
                    TextView tvBtn = dlgView.findViewById(R.id.ID_TEXT_BTN_SET_PASSWORD);

                    tvHeader.setText(getString(R.string.confirm_password));
                    tvBtn.setText(getString(R.string.confirm_password));

                    tvBtn.setOnClickListener(v1 -> {
                        if (!edtPassword.getText().toString().equals(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PARENTAL_PASSWORD, ""))) {
                            Toast.makeText(LiveAccessibleActivity.this, "Password is incorrect!", Toast.LENGTH_SHORT).show();
                        } else {
                            padUnlockedCategory = epgChannel.m_sCategory_ID;
                            String newStreamUrl = Utils.makeStreamURL(LiveAccessibleActivity.this, epgChannel.m_sStreamID);
                            if (streamUrl == null) {
                                currentChannelItem = epgChannel;
                                streamUrl = newStreamUrl;
                                initializePlayer();
                            } else if (!streamUrl.equalsIgnoreCase(newStreamUrl)) {
                                currentChannelItem = epgChannel;
                                streamUrl = newStreamUrl;
                                releasePlayer();
                                initializePlayer();
                            }
                            parentalDlg.dismiss();
                        }
                    });

                    parentalDlg.show();
                }
                else {
                    String newStreamUrl = Utils.makeStreamURL(LiveAccessibleActivity.this, epgChannel.m_sStreamID);
                    if (streamUrl == null) {
                        currentChannelItem = epgChannel;
                        streamUrl = newStreamUrl;
                        initializePlayer();
                    } else if (!streamUrl.equalsIgnoreCase(newStreamUrl)) {
                        currentChannelItem = epgChannel;
                        streamUrl = newStreamUrl;
                        releasePlayer();
                        initializePlayer();
                    }
                }
            }
            else {
                padUnlockedCategory = null;
                String newStreamUrl = Utils.makeStreamURL(LiveAccessibleActivity.this, epgChannel.m_sStreamID);
                if(streamUrl == null) {
                    currentChannelItem = epgChannel;
                    streamUrl = newStreamUrl;
                    initializePlayer();
                }
                else if(!streamUrl.equalsIgnoreCase(newStreamUrl)){
                    currentChannelItem = epgChannel;
                    streamUrl = newStreamUrl;
                    releasePlayer();
                    initializePlayer();
                }

                else if(streamUrl.equalsIgnoreCase(newStreamUrl)){
                    if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
                        if(lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && player != null && player.getPlayWhenReady()) {
                            hideGuide();
                        }
                        else if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && player != null && player.getPlayWhenReady()) {
                            showGuide();
                        }
                    }
                    else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
                        if(lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && mVideoView.isPlaying()) {
                            hideGuide();
                        }
                        else if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && mVideoView.isPlaying()) {
                            showGuide();
                        }
                    }
                    else {
                        if(lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && mMediaPlayer.isPlaying()) {
                            hideGuide();
                        }
                        else if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && mMediaPlayer.isPlaying()) {
                            showGuide();
                        }
                    }
                }
            }
        });

        lvChannelList.setOnItemLongClickListener((adapterView, view, position, l) -> {
            onFavoriteClicked(AppConstants.EPG_FILTERED_DATA.get(position));
            return true;
        });

        vtvCategory.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
        vtvCategory.setOnFocusChangeListener((view, b) -> {
            if(b) {
                vtvCategory.setTextColor(getResources().getColor(R.color.colorAccent));
            }
            else {
                vtvCategory.setTextColor(getResources().getColor(android.R.color.white));
            }
        });
        /*ivFullScreenMode.setOnClickListener(view -> {
            if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
                if(lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && player != null && player.getPlayWhenReady()) {
                    hideGuide();
                }
                else if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && player != null && player.getPlayWhenReady()) {
                    showGuide();
                }
            }
            else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
                if(lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && mVideoView.isPlaying()) {
                    hideGuide();
                }
                else if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && mVideoView.isPlaying()) {
                    showGuide();
                }
            }
            else {
                if(lvChannelList != null && lvChannelList.getVisibility() == View.VISIBLE && mMediaPlayer.isPlaying()) {
                    hideGuide();
                }
                else if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && mMediaPlayer.isPlaying()) {
                    showGuide();
                }
            }
        });*/

        lvLiveCategoryList.setOnFocusChangeListener((v, hasFocus) -> lvLiveCategoryList.setSelection(liveCategoryMenuAdapter.getActiveMenuIndex()));

        lvLiveCategoryList.setOnItemClickListener((parent, view, position, id) -> {
            for(int i = 0; i < lvLiveCategoryList.getChildCount(); i++)
                lvLiveCategoryList.getChildAt(i).animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
            AppConstants.CATEGORY_INDEX = position;
            liveCategoryMenuAdapter.setActiveMenuIndex(position);
            liveCategoryMenuAdapter.notifyDataSetChanged();
            lvLiveCategoryList.setSelection(position);
            view.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200);

            drawerLayout.closeDrawers();
            filterChannel(position);
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {
                lvLiveCategoryList.requestFocus();
            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                if(AppConstants.EPG_FILTERED_DATA.size() > 0)
                    lvChannelList.requestFocus();
                else
                    vtvCategory.requestFocus();
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        ivPlayBtn.setOnClickListener(view -> {
            initializePlayer();

            ivPlayBtn.setVisibility(View.GONE);
            mVideoSurfaceFrame.requestFocus();
        });

        if(btnCloseCaption != null) {
            btnCloseCaption.setOnClickListener(view -> {
                if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
                    if(subTitleTrackList.size() > 1) {
                        Context wrapper = new ContextThemeWrapper(LiveAccessibleActivity.this, R.style.PopupMenuStyle);
                        final PopupMenu popupMenu = new PopupMenu(wrapper, btnCloseCaption);
                        for (String subTitleTrackString: subTitleTrackList) {
                            if(subTitleTrackString == null || subTitleTrackString.isEmpty())
                                continue;
                            if(subTitleTrackString.equals(getString(R.string.disable))) {
                                popupMenu.getMenu().add(getString(R.string.disable));
                            }
                            else {
                                Locale loc = new Locale(subTitleTrackString);
                                String name = loc.getDisplayLanguage(loc);
                                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                popupMenu.getMenu().add(name);
                            }
                        }
                        popupMenu.setOnMenuItemClickListener(item -> {
                            if(getTrackSelector() != null) {
                                for (String subTitleTrackString: subTitleTrackList) {
                                    if(subTitleTrackString == null || subTitleTrackString.isEmpty())
                                        continue;
                                    if(!subTitleTrackString.equals(getString(R.string.disable))) {
                                        Locale loc = new Locale(subTitleTrackString);
                                        String name = loc.getDisplayLanguage(loc);
                                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                        if (name.equals(item.getTitle().toString())) {
                                            player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setPreferredTextLanguage(subTitleTrackString).build());
                                            playerView.getSubtitleView().setVisibility(View.VISIBLE);
                                            break;
                                        }
                                    }
                                    else {
                                        if(item.getTitle().toString().equals(getString(R.string.disable))) {
                                            playerView.getSubtitleView().setVisibility(View.GONE);
                                            break;
                                        }
                                    }
                                }
                            }
                            return true;
                        });

                        popupMenu.show();
                    }
                }
                else if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
                    if(mVideoView != null && mVideoView.getTrackInfo().length > 0) {
                        final ITrackInfo[] iTrackInfoList = mVideoView.getTrackInfo();
                        Context wrapper = new ContextThemeWrapper(LiveAccessibleActivity.this, R.style.PopupMenuStyle);
                        final PopupMenu popupMenu = new PopupMenu(wrapper, btnCloseCaption);
                        int trackCount = 0;
                        popupMenu.getMenu().add(R.string.disable);
                        for (ITrackInfo iTrackInfo : iTrackInfoList) {
                            if(iTrackInfo != null && (iTrackInfo.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT)) {
                                Locale loc = new Locale(iTrackInfo.getLanguage());
                                String name = loc.getDisplayLanguage(loc);
                                name = name.substring(0, 1).toUpperCase() + name.substring(1);

                                popupMenu.getMenu().add(name);
                                trackCount++;
                            }
                        }

                        if(trackCount == 0)
                            return;

                        popupMenu.setOnMenuItemClickListener(item -> {
                            if(item.getTitle().toString().equals(getString(R.string.disable))) {
                                if(currentSubTitleIndex > -1)
                                    mVideoView.deselectTrack(currentSubTitleIndex);
                                currentSubTitleIndex = -1;
                            }
                            else {
                                int index = 0;
                                for (ITrackInfo iTrackInfo : iTrackInfoList) {
                                    if(iTrackInfo != null && (iTrackInfo.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_TIMEDTEXT)) {
                                        Locale loc = new Locale(iTrackInfo.getLanguage());
                                        String name = loc.getDisplayLanguage(loc);
                                        name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                        if(name.equals(item.getTitle().toString())) {
                                            if(currentSubTitleIndex != index) {
                                                currentSubTitleIndex = index;
                                                mVideoView.selectTrack(index);
                                            }
                                            break;
                                        }
                                    }
                                    index++;
                                }
                            }
                            return true;
                        });

                        popupMenu.show();
                    }
                }
                else {
                    if(mMediaPlayer != null && mMediaPlayer.getSpuTracksCount() > 1) {
                        final org.videolan.libvlc.MediaPlayer.TrackDescription[] trackDescriptions = mMediaPlayer.getSpuTracks();
                        final PopupMenu popupMenu = new PopupMenu(LiveAccessibleActivity.this, btnCloseCaption);
                        for (org.videolan.libvlc.MediaPlayer.TrackDescription trackDescription: trackDescriptions) {
                            popupMenu.getMenu().add(trackDescription.name);
                        }
                        popupMenu.setOnMenuItemClickListener(item -> {
                            int index = 0;
                            for (org.videolan.libvlc.MediaPlayer.TrackDescription trackDescription: trackDescriptions) {
                                if(trackDescription.name.equalsIgnoreCase(item.getTitle().toString())) {
                                    break;
                                }
                                index++;
                            }
                            mMediaPlayer.setSpuTrack(trackDescriptions[index].id);
                            return true;
                        });

                        popupMenu.show();
                    }
                }
            });
        }

        if(btnAudioTrack != null) {
            btnAudioTrack.setOnClickListener(view -> {
                if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
                    if (audioTrackList.size() > 1) {
                        Context wrapper = new ContextThemeWrapper(LiveAccessibleActivity.this, R.style.PopupMenuStyle);
                        final PopupMenu popupMenu = new PopupMenu(wrapper, btnAudioTrack);
                        for (String audioTrackString : audioTrackList) {
                            if (audioTrackString == null || audioTrackString.isEmpty())
                                continue;
                            Locale loc = new Locale(audioTrackString);
                            String name = loc.getDisplayLanguage(loc);
                            name = name.substring(0, 1).toUpperCase() + name.substring(1);
                            popupMenu.getMenu().add(name);
                        }
                        popupMenu.setOnMenuItemClickListener(item -> {
                            if (getTrackSelector() != null) {
                                for (String audioTrackString : audioTrackList) {
                                    if (audioTrackString == null || audioTrackString.isEmpty())
                                        continue;
                                    Locale loc = new Locale(audioTrackString);
                                    String name = loc.getDisplayLanguage(loc);
                                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                    if (name.equals(item.getTitle().toString())) {
                                        player.setTrackSelectionParameters(player.getTrackSelectionParameters().buildUpon().setPreferredAudioLanguage(audioTrackString).build());
                                        break;
                                    }
                                }
                            }
                            return true;
                        });

                        popupMenu.show();
                    }
                }
                else if (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
                    if(mVideoView != null && mVideoView.getTrackInfo().length > 0) {
                        final ITrackInfo[] iTrackInfoList = mVideoView.getTrackInfo();
                        Context wrapper = new ContextThemeWrapper(LiveAccessibleActivity.this, R.style.PopupMenuStyle);
                        final PopupMenu popupMenu = new PopupMenu(wrapper, btnAudioTrack);
                        int trackCount = 0;
                        for (ITrackInfo iTrackInfo : iTrackInfoList) {
                            if(iTrackInfo != null && iTrackInfo.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                                Locale loc = new Locale(iTrackInfo.getLanguage());
                                String name = loc.getDisplayLanguage(loc);
                                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                popupMenu.getMenu().add(name);
                                trackCount++;
                            }
                        }

                        if(trackCount < 2)
                            return;

                        popupMenu.setOnMenuItemClickListener(item -> {
                            int index = 0;
                            for (ITrackInfo iTrackInfo : iTrackInfoList) {
                                if(iTrackInfo != null && iTrackInfo.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                                    Locale loc = new Locale(iTrackInfo.getLanguage());
                                    String name = loc.getDisplayLanguage(loc);
                                    name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                    if(name.equals(item.getTitle().toString())) {
                                        if(currentAudioTrackIndex != index) {
                                            currentAudioTrackIndex = index;
                                            mVideoView.selectTrack(index);
                                        }
                                        break;
                                    }
                                }
                                index++;
                            }
                            return true;
                        });

                        popupMenu.show();
                    }
                }
                else {
                    if(mMediaPlayer != null && mMediaPlayer.getAudioTracksCount() > 1) {
                        final org.videolan.libvlc.MediaPlayer.TrackDescription[] trackDescriptions = mMediaPlayer.getAudioTracks();
                        final PopupMenu popupMenu = new PopupMenu(LiveAccessibleActivity.this, btnAudioTrack);
                        for (org.videolan.libvlc.MediaPlayer.TrackDescription trackDescription: trackDescriptions) {
                            popupMenu.getMenu().add(trackDescription.name);
                        }
                        popupMenu.setOnMenuItemClickListener(item -> {
                            int index = 0;
                            for (org.videolan.libvlc.MediaPlayer.TrackDescription trackDescription: trackDescriptions) {
                                if(trackDescription.name.equalsIgnoreCase(item.getTitle().toString())) {
                                    break;
                                }
                                index++;
                            }
                            mMediaPlayer.setAudioTrack(trackDescriptions[index].id);
                            return true;
                        });

                        popupMenu.show();
                    }
                }
            });
        }
    }

    private void onFavoriteClicked(ChannelItem epgChannel) {
        if(AppConstants.FAVORITE_CHANNEL.values.contains(epgChannel.m_sTvNum))
            AppConstants.FAVORITE_CHANNEL.values.remove(epgChannel.m_sTvNum);
        else
            AppConstants.FAVORITE_CHANNEL.values.add(epgChannel.m_sTvNum);

        calculateCountForCategory();
        liveMenuAdapter.notifyDataSetChanged();
    }

    private void showSelectedTopicInfo(ChannelItem selectedChannel) {
        //vDivider.setVisibility(View.VISIBLE);

        if (AppConstants.CHANNEL_IMAGE_CACHE.containsKey(selectedChannel.m_sStreamIcon)) {
            Bitmap image = AppConstants.CHANNEL_IMAGE_CACHE.get(selectedChannel.m_sStreamIcon);
            Drawable drawable = new BitmapDrawable(getResources(), image);
            //ivChannel.setBackground(drawable);
        } else {
            if (!AppConstants.CHANNEL_IMAGE_CACHE.containsKey(selectedChannel.m_sStreamIcon)) {
                AppConstants.CHANNEL_IMAGE_TARGET_CACHE.put(selectedChannel.m_sStreamIcon, new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, bitmap);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        //ivChannel.setBackground(drawable);
                        AppConstants.CHANNEL_IMAGE_TARGET_CACHE.remove(selectedChannel.m_sStreamIcon);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        if(Utils.bitmapLogo == null) {
                            Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                            float scaleX = (float) (getResources().getDimensionPixelOffset(R.dimen.img_size_170dp)) / sourceBitmap.getWidth();
                            float scaleY = (float) (getResources().getDimensionPixelSize(R.dimen.img_size_45dp)) / sourceBitmap.getHeight();
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleX, scaleY);
                            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap,
                                    0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
                            AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, bitmap);
                            AppConstants.CHANNEL_IMAGE_TARGET_CACHE.remove(selectedChannel.m_sStreamIcon);
                        }
                        else {
                            AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, Utils.bitmapLogo.getBitmap());
                        }
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                if(selectedChannel.m_sStreamIcon != null &&!selectedChannel.m_sStreamIcon.isEmpty())
                    Utils.loadImageInto(LiveAccessibleActivity.this, selectedChannel.m_sStreamIcon, getResources().getDimensionPixelOffset(R.dimen.img_size_170dp),
                            getResources().getDimensionPixelSize(R.dimen.img_size_45dp), AppConstants.CHANNEL_IMAGE_TARGET_CACHE.get(selectedChannel.m_sStreamIcon));
            }

        }

        //tvChannelNumber.setText(selectedChannel.m_sTvNum);
        //tvChannelName.setText(selectedChannel.m_sTvName);
        //tvChannelName.setSelected(true);

        if(Utils.bIsFireStick) {
            shortEpgChannel = selectedChannel;
            shortEpgHandler.removeCallbacks(shortEpgRunnable);
            shortEpgHandler.postDelayed(shortEpgRunnable, 1000);
        }
        else {
            ItemTopic selectedTopic = selectedChannel.m_arrItemTopic.get(selectedChannel.getCurrentTopicIndex());
            String duration = "";
            if (selectedTopic.m_dateTopicStart != null && selectedTopic.m_dateTopicEnd != null) {
                 duration = Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicStart.getTime())
                        + " - " + Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicEnd.getTime());

            } else {
                duration = "unlimited";
            }
            //tvTopicPlayTime.setText(duration);
            tvTopicName.setText(duration + " - " + selectedTopic.m_sTitle);
            tvTopicDescription.setText(selectedTopic.m_sDescription);
        }
    }

    private void updateSelectedTopicInfo(boolean bNext) {
        if (liveMenuAdapter == null || AppConstants.EPG_FILTERED_DATA.size() == 0 || liveMenuAdapter.getActiveMenuIndex() < 0) {
            //ivChannel.setBackground(null);
            tvTopicName.setText("");
            tvTopicDescription.setText("");
            //tvTopicPlayTime.setText("");
            //tvChannelNumber.setText("");
            //tvChannelName.setText("");
            //vDivider.setVisibility(View.GONE);
            ivChannelLock.setVisibility(View.GONE);
            return;
        }

        ChannelItem selectedChannel = AppConstants.EPG_FILTERED_DATA.get(liveMenuAdapter.getActiveMenuIndex());

        if (liveMenuAdapter.getActiveMenuIndex() < 0) {
            //ivChannel.setBackground(null);
            tvTopicName.setText("");
            tvTopicDescription.setText("");
            //tvTopicPlayTime.setText("");
            //tvChannelNumber.setText("");
            //tvChannelName.setText("");
            ivChannelLock.setVisibility(View.GONE);
            //vDivider.setVisibility(View.GONE);
            return;
        }

        //vDivider.setVisibility(View.VISIBLE);

        if (AppConstants.CHANNEL_IMAGE_CACHE.containsKey(selectedChannel.m_sStreamIcon)) {
            Bitmap bitmap = AppConstants.CHANNEL_IMAGE_CACHE.get(selectedChannel.m_sStreamIcon);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            //ivChannel.setBackground(drawable);
        } else {
            if (!AppConstants.CHANNEL_IMAGE_CACHE.containsKey(selectedChannel.m_sStreamIcon)) {
                AppConstants.CHANNEL_IMAGE_TARGET_CACHE.put(selectedChannel.m_sStreamIcon, new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, bitmap);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        //ivChannel.setBackground(drawable);
                        AppConstants.CHANNEL_IMAGE_TARGET_CACHE.remove(selectedChannel.m_sStreamIcon);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        if(Utils.bitmapLogo == null) {
                            Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                            float scaleX = (float) (getResources().getDimensionPixelOffset(R.dimen.img_size_170dp)) / sourceBitmap.getWidth();
                            float scaleY = (float) (getResources().getDimensionPixelSize(R.dimen.img_size_45dp)) / sourceBitmap.getHeight();
                            Matrix matrix = new Matrix();
                            matrix.postScale(scaleX, scaleY);
                            Bitmap bitmap = Bitmap.createBitmap(sourceBitmap,
                                    0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
                            AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, bitmap);
                            AppConstants.CHANNEL_IMAGE_TARGET_CACHE.remove(selectedChannel.m_sStreamIcon);
                        }
                        else {
                            AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, Utils.bitmapLogo.getBitmap());
                        }
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                if(selectedChannel.m_sStreamIcon != null &&!selectedChannel.m_sStreamIcon.isEmpty())
                    Utils.loadImageInto(LiveAccessibleActivity.this, selectedChannel.m_sStreamIcon, getResources().getDimensionPixelOffset(R.dimen.img_size_170dp),
                            getResources().getDimensionPixelSize(R.dimen.img_size_45dp), AppConstants.CHANNEL_IMAGE_TARGET_CACHE.get(selectedChannel.m_sStreamIcon));
            }
        }

        //tvChannelNumber.setText(selectedChannel.m_sTvNum);
        //tvChannelName.setText(selectedChannel.m_sTvName);
        //tvChannelName.setSelected(true);
        if(AppConstants.LIVE_PARENTAL.values.contains(selectedChannel.m_sCategory_ID))
            ivChannelLock.setVisibility(View.VISIBLE);
        else
            ivChannelLock.setVisibility(View.GONE);

        if(Utils.bIsFireStick) {
            shortEpgChannel = selectedChannel;
            shortEpgHandler.removeCallbacks(shortEpgRunnable);
            shortEpgHandler.postDelayed(shortEpgRunnable, 1000);
        }
        else {
            shortEpgChannel = selectedChannel;
            shortEpgHandler.removeCallbacks(shortEpgRunnable);
            shortEpgHandler.postDelayed(shortEpgRunnable, 1000);

            // aqui podemos hacer un ciclo for a partir del selected channel y el current topic index para traer el resto del arreglo y ponerlo como lista scrolable

            /*ItemTopic selectedTopic;
            if (bNext && selectedChannel.getCurrentTopicIndex() < selectedChannel.m_arrItemTopic.size() - 1)
                selectedTopic = selectedChannel.m_arrItemTopic.get(selectedChannel.getCurrentTopicIndex() + 1);
            else
                selectedTopic = selectedChannel.m_arrItemTopic.get(selectedChannel.getCurrentTopicIndex());
            tvTopicName.setText(selectedTopic.m_sTitle);

            String duration = "";
            if (selectedTopic.m_dateTopicStart != null && selectedTopic.m_dateTopicEnd != null) {
                duration = Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicStart.getTime())
                        + " - " + Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicEnd.getTime());

            } else {
                duration = "unlimited";
            }
            // duration + " - " + selectedTopic.m_sTitle
            tvTopicName.setText(selectedChannel.m_sTvName);

            String EpgConcatened = "";
            for(int i=selectedChannel.getCurrentTopicIndex();i<selectedChannel.m_arrItemTopic.size();i++){

                if (selectedChannel.m_arrItemTopic.get(i).m_dateTopicStart != null && selectedChannel.m_arrItemTopic.get(i).m_dateTopicEnd != null) {
                    duration = Utils.getShortTime(LiveAccessibleActivity.this, selectedChannel.m_arrItemTopic.get(i).m_dateTopicStart.getTime())
                            + " - " + Utils.getShortTime(LiveAccessibleActivity.this, selectedChannel.m_arrItemTopic.get(i).m_dateTopicEnd.getTime());

                } else {
                    duration = "unlimited";
                }
                String htmlTitle = "";
                if (i == selectedChannel.getCurrentTopicIndex()) {
                    htmlTitle = "<span style='color: #44a600;'>" + duration + " - " + selectedChannel.m_arrItemTopic.get(i).m_sTitle + "</span><br>";
                } else {
                    htmlTitle = "<span style='color: #66ccff;'>" + duration + " - " + selectedChannel.m_arrItemTopic.get(i).m_sTitle + "</span><br>";
                }

                String htmlDescription = "<span style='font-size: 15px;'>" + selectedChannel.m_arrItemTopic.get(i).m_sDescription + "</span>";
                EpgConcatened = EpgConcatened + htmlTitle + htmlDescription + "<br><br>";
            }
            svTopicDescription.scrollTo(0,0);
            tvTopicDescription.setText(Html.fromHtml(EpgConcatened));

             */

            /*if (selectedTopic.m_dateTopicStart != null && selectedTopic.m_dateTopicEnd != null) {
                String duration = Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicStart.getTime())
                        + " - " + Utils.getShortTime(LiveAccessibleActivity.this, selectedTopic.m_dateTopicEnd.getTime());
                tvTopicPlayTime.setText(duration);
            } else {
                tvTopicPlayTime.setText(R.string.unlimited);
            }*/
        }
    }

    private void searchChannel(String searchStr) {
        AppConstants.EPG_FILTERED_DATA.clear();
        for (ChannelItem item : AppConstants.EPGDATA) {
            if(Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_ALL_CHANNELS, String.valueOf(true)))) {
                if(StringUtils.containsIgnoreCase(item.m_sTvName, searchStr) || searchStr.isEmpty())
                    AppConstants.EPG_FILTERED_DATA.add(item);
            }
            else {
                if (item.m_arrItemTopic.size() > 1) {
                    if(StringUtils.containsIgnoreCase(item.m_sTvName, searchStr) || searchStr.isEmpty())
                        AppConstants.EPG_FILTERED_DATA.add(item);
                }
            }
        }

        liveMenuAdapter.notifyDataSetChanged();

        int index = 0;
        boolean bIsExistInCategory = false;
        if(currentChannelItem != null) {
            for (ChannelItem channelItem : AppConstants.EPG_FILTERED_DATA) {
                if (channelItem.IsSameWith(currentChannelItem)) {
                    lvChannelList.setSelection(index);
                    liveMenuAdapter.setActiveMenuIndex(index);
                    bIsExistInCategory = true;
                    break;
                }
                index++;
            }
        }
        if(!bIsExistInCategory && AppConstants.EPG_FILTERED_DATA.size() > 0) {
            lvChannelList.setSelection(0);
            liveMenuAdapter.setActiveMenuIndex(-1);
        }
        else if(AppConstants.EPG_FILTERED_DATA.size() == 0) {
            liveMenuAdapter.setActiveMenuIndex(-1);
        }
    }

    public void filterChannel(int position) {
        if (lvChannelList == null || AppConstants.CHANNEL_CATEGORY_LIST == null || AppConstants.CHANNEL_CATEGORY_LIST.isEmpty()) {
            return;
        }

        if (position < 0 || position >= AppConstants.CHANNEL_CATEGORY_LIST.size()) {
            // El índice está fuera de los límites, maneja el error de forma segura
            return;
        }

        edtSearch.setText("");

        String currentCategory = AppConstants.CHANNEL_CATEGORY_LIST.get(position).category_id;
        if(currentCategory.equalsIgnoreCase("-1"))
            currentCategory = getString(R.string.favorite_category);
        else if(currentCategory.equalsIgnoreCase("0"))
            currentCategory = getString(R.string.all_channels);

        AppConstants.EPG_FILTERED_DATA.clear();
        for (ChannelItem item : AppConstants.EPGDATA) {
            if(currentCategory.equals(getString(R.string.favorite_category))) {
                if(AppConstants.FAVORITE_CHANNEL.values.contains(item.m_sTvNum)) {
                    if(Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_ALL_CHANNELS, String.valueOf(true))))
                        AppConstants.EPG_FILTERED_DATA.add(item);
                    else {
                        if(item.m_arrItemTopic.size() > 1) {
                            AppConstants.EPG_FILTERED_DATA.add(item);
                        }
                    }
                }
            }
            else {
                if ((item.m_sCategory_ID != null && item.m_sCategory_ID.equalsIgnoreCase(currentCategory)) || currentCategory.equals(getString(R.string.all_channels))) {
                    if(Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_ALL_CHANNELS, String.valueOf(true))))
                        AppConstants.EPG_FILTERED_DATA.add(item);
                    else {
                        if (item.m_arrItemTopic.size() > 1) {
                            AppConstants.EPG_FILTERED_DATA.add(item);
                        }
                    }
                }
            }
        }

        liveMenuAdapter.notifyDataSetChanged();
        int index = 0;
        boolean bIsExistInCategory = false;
        if(currentChannelItem != null) {
            for (ChannelItem channelItem : AppConstants.EPG_FILTERED_DATA) {
                if (channelItem.IsSameWith(currentChannelItem)) {
                    lvChannelList.setSelection(index);
                    liveMenuAdapter.setActiveMenuIndex(index);
                    bIsExistInCategory = true;
                    break;
                }
                index++;
            }
        }
        if(!bIsExistInCategory && AppConstants.EPG_FILTERED_DATA.size() > 0) {
            lvChannelList.setSelection(0);
            liveMenuAdapter.setActiveMenuIndex(-1);
        }
        else if(AppConstants.EPG_FILTERED_DATA.size() == 0) {
            liveMenuAdapter.setActiveMenuIndex(-1);
            vtvCategory.requestFocus();
        }
    }

    private Runnable runnableUpdateTime = new Runnable() {
        @Override
        public void run() {
            setTimeInfo();
            m_timeHandler.postDelayed(runnableUpdateTime, UPDATE_TIME_DELAY);
        }
    };

    @SuppressLint("SimpleDateFormat")
    private void setTimeInfo() {
        SimpleDateFormat dateFormat;
        final Calendar calendar = Calendar.getInstance();

        if(Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_TIME_FORMAT_24, String.valueOf(false))))
            dateFormat = new SimpleDateFormat("HH:mm a");
        else
            dateFormat = new SimpleDateFormat("h:mm a");
        String mStrTime = dateFormat.format(calendar.getTime());

        tvCurrentTime.setText(mStrTime);
    }

    public void showGuide() {
        detailHandler.removeCallbacks(detailRunnable);
        if(llContent != null) {
            llContent.setVisibility(View.VISIBLE);
            lvChannelList.setVisibility(View.VISIBLE);
            llProgressBar.setVisibility(View.VISIBLE);
            llSideBar.setVisibility(View.VISIBLE);
            llDetails.setVisibility(View.VISIBLE);
            //llchannelinfo.setVisibility(View.VISIBLE);
            vtvCategory.setVisibility(View.VISIBLE);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
            int margins = getResources().getDimensionPixelOffset(R.dimen.margin_20dp);
            layoutParams.setMargins(margins, margins, margins, margins);
            layoutParams.bottomToTop = llProgressBar.getId();
            layoutParams.bottomToBottom = -1;
            rlVideo.setLayoutParams(layoutParams);
        }

        //ivChannelLogoCenter.setVisibility(View.GONE);

        lvChannelList.requestFocus();
    }

    public void hideGuide() {
        if(llContent != null) {
            lvChannelList.setVisibility(View.GONE);
            llProgressBar.setVisibility(View.GONE);
            llSideBar.setVisibility(View.GONE);
            llDetails.setVisibility(View.GONE);
            //llchannelinfo.setVisibility(View.GONE);
            vtvCategory.setVisibility(View.GONE);

            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
            layoutParams.setMargins(0, 0, 0, 0);
            rlVideo.setLayoutParams(layoutParams);

            if(liveMenuAdapter.getCurrentPlayingChannel() != null) {
                //ivChannelLogoCenter.setVisibility(View.VISIBLE);
                //new Handler().postDelayed(() -> ivChannelLogoCenter.setVisibility(View.GONE), 3000);
                ChannelItem selectedChannel = liveMenuAdapter.getCurrentPlayingChannel();
                if (AppConstants.CHANNEL_IMAGE_CACHE.containsKey(selectedChannel.m_sStreamIcon)) {
                    Bitmap image = AppConstants.CHANNEL_IMAGE_CACHE.get(selectedChannel.m_sStreamIcon);
                    Drawable drawable = new BitmapDrawable(getResources(), image);
                    //ivChannelLogoCenter.setBackground(drawable);
                } else {
                    if (!AppConstants.CHANNEL_IMAGE_CACHE.containsKey(selectedChannel.m_sStreamIcon)) {
                        AppConstants.CHANNEL_IMAGE_TARGET_CACHE.put(selectedChannel.m_sStreamIcon, new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, bitmap);
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                //ivChannelLogoCenter.setBackground(drawable);
                                AppConstants.CHANNEL_IMAGE_TARGET_CACHE.remove(selectedChannel.m_sStreamIcon);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                if(Utils.bitmapLogo == null) {
                                    Bitmap sourceBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                                    float scaleX = (float) (getResources().getDimensionPixelOffset(R.dimen.img_size_170dp)) / sourceBitmap.getWidth();
                                    float scaleY = (float) (getResources().getDimensionPixelSize(R.dimen.img_size_45dp)) / sourceBitmap.getHeight();
                                    Matrix matrix = new Matrix();
                                    matrix.postScale(scaleX, scaleY);
                                    Bitmap bitmap = Bitmap.createBitmap(sourceBitmap,
                                            0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
                                    AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, bitmap);
                                    AppConstants.CHANNEL_IMAGE_TARGET_CACHE.remove(selectedChannel.m_sStreamIcon);
                                }
                                else {
                                    AppConstants.CHANNEL_IMAGE_CACHE.put(selectedChannel.m_sStreamIcon, Utils.bitmapLogo.getBitmap());
                                }
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });

                        if(selectedChannel.m_sStreamIcon != null &&!selectedChannel.m_sStreamIcon.isEmpty())
                            Utils.loadImageInto(LiveAccessibleActivity.this, selectedChannel.m_sStreamIcon, getResources().getDimensionPixelOffset(R.dimen.img_size_170dp),
                                    getResources().getDimensionPixelSize(R.dimen.img_size_45dp), AppConstants.CHANNEL_IMAGE_TARGET_CACHE.get(selectedChannel.m_sStreamIcon));
                    }

                }
            }
        }
    }

    private void getLiveCategory() {
        llLoadingLayout.setVisibility(View.VISIBLE);
        AppConstants.CHANNEL_CATEGORY_LIST.clear();

        String userName = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.USERNAME, "");
        String password = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PASSWORD, "");
        String serverUrl = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.SERVERURL, getString(R.string.base_url));

        XtreamAPI.getLiveCategory(userName, password, serverUrl, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CategoryItem categoryItem = new CategoryItem();
                        categoryItem.category_id = jsonObject.has(APIConstant.ITEM_CATEGORY_ID) ? jsonObject.getString(APIConstant.ITEM_CATEGORY_ID) : "";
                        categoryItem.category_name = jsonObject.has(APIConstant.ITEM_CATEGORY_NAME) ? jsonObject.getString(APIConstant.ITEM_CATEGORY_NAME) : "";
                        categoryItem.parent_id = jsonObject.has(APIConstant.ITEM_PARENT_ID) ? jsonObject.getInt(APIConstant.ITEM_PARENT_ID) : -1;

                        if(AppConstants.LIVE_PARENTAL.values.contains(categoryItem.category_id))
                            categoryItem.locked = true;

                        AppConstants.CHANNEL_CATEGORY_LIST.add(categoryItem);
                    }

                    CategoryItem favoriteCategoryItem = new CategoryItem();
                    favoriteCategoryItem.category_id = "-1";
                    favoriteCategoryItem.category_name = getString(R.string.favorite_category);
                    favoriteCategoryItem.parent_id = -1;
                    AppConstants.CHANNEL_CATEGORY_LIST.add(0, favoriteCategoryItem);

                    CategoryItem allCategoryItem = new CategoryItem();
                    allCategoryItem.category_id = "0";
                    allCategoryItem.category_name = getString(R.string.all_channels);
                    allCategoryItem.parent_id = -1;
                    AppConstants.CHANNEL_CATEGORY_LIST.add(0, allCategoryItem);

                    llLoadingLayout.setVisibility(View.GONE);

                    //getAllEpgData();
                } catch (JSONException e) {
                    Log.e("Response Error", result);
                    llLoadingLayout.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Object error) {
                Log.e("Server Error", error.toString());
                llLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getAllEpgData() {
        final String userName = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.USERNAME, "");
        final String password = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PASSWORD, "");
        String serverUrl = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.SERVERURL, getString(R.string.base_url));

        llLoadingLayout.setVisibility(View.VISIBLE);
        AppConstants.EPG_MAP.clear();

        XtreamAPI.getAllEpgData(userName, password, serverUrl, this, new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                final String resultStr = result;
                new Thread(() -> {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder;
                    try {
                        dBuilder = dbFactory.newDocumentBuilder();
                        InputStream is;
                        is = new ByteArrayInputStream(resultStr.getBytes(StandardCharsets.UTF_8));
                        Document doc = dBuilder.parse(is);
                        Element element = doc.getDocumentElement();
                        element.normalize();

                        NodeList nList = doc.getElementsByTagName("channel");

                        for (int i = 0; i < nList.getLength(); i++) {
                            Node node = nList.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element2 = (Element) node;
                                final ChannelItem channelItem = new ChannelItem();
                                channelItem.m_sEPGChannelID = element2.getAttribute("id");
                                if(!element2.hasChildNodes())
                                    continue;

                                NodeList childList = element2.getElementsByTagName("display-name");
                                if(childList != null && childList.getLength() > 0)
                                    channelItem.m_sTvName = childList.item(0).getTextContent();
                                childList = element2.getElementsByTagName("icon");
                                if(childList != null && childList.getLength() > 0) {
                                    channelItem.m_sStreamIcon = ((Element)element2.getElementsByTagName("icon").item(0)).getAttribute("src");
                                }

                                AppConstants.EPG_MAP.put(channelItem.m_sEPGChannelID, channelItem);
                            }
                        }

                        nList = doc.getElementsByTagName("programme");

                        for (int i=0; i<nList.getLength(); i++) {
                            Node node = nList.item(i);
                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element element2 = (Element) node;
                                final ItemTopic itemTopic = new ItemTopic();
                                itemTopic.m_sChannelID = element2.getAttribute("channel");
                                itemTopic.m_sTitle = element2.getElementsByTagName("title").item(0).getTextContent();
                                itemTopic.m_sDescription = element2.getElementsByTagName("desc").item(0).getTextContent();

                                @SuppressLint("SimpleDateFormat")
                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss Z");
                                try {
                                    itemTopic.m_dateTopicStart = format.parse(DateUtil.UTCStringToLocalString(element2.getAttribute("start")));
                                    itemTopic.m_dateTopicEnd = format.parse(DateUtil.UTCStringToLocalString(element2.getAttribute("stop")));

                                    if(AppConstants.EARLIEST_DATE.after(itemTopic.m_dateTopicStart)) {
                                        AppConstants.EARLIEST_DATE.setTime(itemTopic.m_dateTopicStart.getTime());
                                    }
                                    if(AppConstants.LATEST_DATE.before(itemTopic.m_dateTopicEnd)) {
                                        AppConstants.LATEST_DATE.setTime(itemTopic.m_dateTopicEnd.getTime());
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                ChannelItem channelItem = AppConstants.EPG_MAP.get(itemTopic.m_sChannelID);
                                if(channelItem != null && !itemTopic.m_dateTopicEnd.equals(itemTopic.m_dateTopicStart) && itemTopic.m_dateTopicEnd.after(itemTopic.m_dateTopicStart) && !channelItem.m_arrItemTopic.duplicatedTimeRange(itemTopic)) {
                                    channelItem.m_arrItemTopic.add(itemTopic);
                                }
                            }
                        }

                        runOnUiThread(() -> {
                            llLoadingLayout.setVisibility(View.GONE);
                            getChannelData();
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> llLoadingLayout.setVisibility(View.GONE));
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onError(Object error) {
                llLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void getChannelData() {
        final String userName = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.USERNAME, "");
        final String password = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PASSWORD, "");
        String serverUrl = Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.SERVERURL, getString(R.string.base_url));

        llLoadingLayout.setVisibility(View.VISIBLE);
        AppConstants.EPGDATA.clear();
        AppConstants.EPG_CATCH_UP_DATA.clear();

        XtreamAPI.getLiveStreams(userName, password, serverUrl,"", new VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Date currentDate = Utils.CurrentTime();
                final String resultStr = result;
                new Thread(() -> {
                    try {
                        final JSONArray jsonArray = new JSONArray(resultStr);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String epgChannelID = jsonObject.has(APIConstant.ITEM_EPG_CHANNEL_ID) ? jsonObject.getString(APIConstant.ITEM_EPG_CHANNEL_ID) : "";
                            if(epgChannelID == null || epgChannelID.isEmpty() || epgChannelID.equalsIgnoreCase("null"))
                                epgChannelID = "";

                            ChannelItem channelItem;
                            if(AppConstants.EPG_MAP.get(epgChannelID) == null) {
                                channelItem = new ChannelItem();
                            }
                            else if(AppConstants.EPG_MAP.get(epgChannelID) != null && Objects.requireNonNull(AppConstants.EPG_MAP.get(epgChannelID)).m_sTvNum != null) {
                                channelItem = new ChannelItem(Objects.requireNonNull(AppConstants.EPG_MAP.get(epgChannelID)));
                            }
                            else {
                                channelItem = AppConstants.EPG_MAP.get(epgChannelID);
                            }

                            assert channelItem != null;
                            channelItem.m_sTvName = jsonObject.has(APIConstant.ITEM_NAME) ? jsonObject.getString(APIConstant.ITEM_NAME) : "";
                            channelItem.m_sEPGChannelID = jsonObject.has(APIConstant.ITEM_EPG_CHANNEL_ID) ? jsonObject.getString(APIConstant.ITEM_EPG_CHANNEL_ID) : "";
                            channelItem.m_sTvNum = jsonObject.has(APIConstant.ITEM_STTREAM_NUM) ? jsonObject.getString(APIConstant.ITEM_STTREAM_NUM) : "";
                            channelItem.m_sStreamType = jsonObject.has(APIConstant.ITEM_STREAM_TYPE) ? jsonObject.getString(APIConstant.ITEM_STREAM_TYPE) : "";
                            channelItem.m_sStreamID = jsonObject.has(APIConstant.ITEM_STREAM_ID) ? jsonObject.getString(APIConstant.ITEM_STREAM_ID) : "";
                            channelItem.m_sStreamIcon = jsonObject.has(APIConstant.ITEM_STREAM_ICON) ? jsonObject.getString(APIConstant.ITEM_STREAM_ICON) : "";
                            channelItem.m_sAdded = jsonObject.has(APIConstant.ITEM_ADDED) ? jsonObject.getString(APIConstant.ITEM_ADDED) : "";
                            channelItem.m_sCategory_ID = jsonObject.has(APIConstant.ITEM_CATEGORY_ID) ? jsonObject.getString(APIConstant.ITEM_CATEGORY_ID) : "";
                            if(channelItem.m_sCategory_ID.isEmpty() || channelItem.m_sCategory_ID.equalsIgnoreCase("null"))
                                continue;
                            channelItem.m_sCustomSID = jsonObject.has(APIConstant.ITEM_CUSTOM_SID) ? jsonObject.getString(APIConstant.ITEM_CUSTOM_SID) : "";
                            channelItem.m_sTVArchive = jsonObject.has(APIConstant.ITEM_TV_ARCHIVE) ? jsonObject.getString(APIConstant.ITEM_TV_ARCHIVE) : "";
                            channelItem.m_sDirectSource = jsonObject.has(APIConstant.ITEM_DIRECT_SOURCE) ? jsonObject.getString(APIConstant.ITEM_DIRECT_SOURCE) : "";
                            channelItem.m_sTVArchiveDuration = jsonObject.has(APIConstant.ITEM_TV_ARCHIVE_DURATION) ? jsonObject.getString(APIConstant.ITEM_TV_ARCHIVE_DURATION) : "";

                            AppConstants.EPGDATA.add(channelItem);

                            for(ItemTopic topic : channelItem.m_arrItemTopic)
                                topic.m_sChannelNumber = channelItem.m_sTvNum;

                            if(channelItem.m_sTVArchive.equals("1") && channelItem.m_arrItemTopic.size() > 1 && channelItem.m_arrItemTopic.get(0).m_dateTopicStart != null && !channelItem.m_arrItemTopic.get(0).m_dateTopicStart.after(currentDate))
                                AppConstants.EPG_CATCH_UP_DATA.add(channelItem);

                            //epg sort
                            if(channelItem.m_arrItemTopic.size() == 0) {
                                ItemTopic itemTopic = new ItemTopic();
                                itemTopic.m_sTitle = "Program information not available";
                                itemTopic.m_dateTopicStart = null;
                                itemTopic.m_dateTopicEnd = null;
                                itemTopic.m_sChannelNumber = channelItem.m_sTvNum;
                                channelItem.m_arrItemTopic.add(itemTopic);
                            }
                            else {
                                Collections.sort(channelItem.m_arrItemTopic, new TopicComparator());

                                ArrayItemTopic arrayItemTopic = channelItem.m_arrItemTopic;
                                for (int index = 1; index < arrayItemTopic.size(); index++) {
                                    if (!arrayItemTopic.get(index - 1).m_dateTopicEnd.equals(arrayItemTopic.get(index).m_dateTopicStart) && arrayItemTopic.get(index - 1).m_dateTopicEnd.before(arrayItemTopic.get(index).m_dateTopicStart)) {
                                        ItemTopic itemTopic = new ItemTopic();
                                        itemTopic.m_sTitle = "Program information not available";
                                        itemTopic.m_dateTopicStart = new Date(arrayItemTopic.get(index - 1).m_dateTopicEnd.getTime());
                                        itemTopic.m_dateTopicEnd = new Date(arrayItemTopic.get(index).m_dateTopicStart.getTime());
                                        itemTopic.m_sChannelNumber = channelItem.m_sTvNum;
                                        arrayItemTopic.add(index, itemTopic);
                                        index++;
                                    }
                                }

                                Date startDate = new Date(AppConstants.EARLIEST_DATE.getTime());
                                if (arrayItemTopic.get(0).m_dateTopicStart != null && arrayItemTopic.get(0).m_dateTopicStart.after(startDate)) {
                                    ItemTopic itemTopic = new ItemTopic();
                                    itemTopic.m_sTitle = "Program information not available";
                                    itemTopic.m_dateTopicStart = new Date(startDate.getTime());
                                    itemTopic.m_dateTopicEnd = new Date(arrayItemTopic.get(0).m_dateTopicStart.getTime());
                                    itemTopic.m_sChannelNumber = channelItem.m_sTvNum;
                                    arrayItemTopic.add(0, itemTopic);
                                }

                                Date endDate = new Date(AppConstants.LATEST_DATE.getTime());
                                if (arrayItemTopic.get(0).m_dateTopicEnd != null && arrayItemTopic.get(arrayItemTopic.size() - 1).m_dateTopicEnd.before(endDate)) {
                                    ItemTopic itemTopic = new ItemTopic();
                                    itemTopic.m_sTitle = "Program information not available";
                                    itemTopic.m_dateTopicStart = new Date(arrayItemTopic.get(arrayItemTopic.size() - 1).m_dateTopicEnd.getTime());
                                    itemTopic.m_dateTopicEnd = new Date(endDate.getTime());
                                    itemTopic.m_sChannelNumber = channelItem.m_sTvNum;
                                    arrayItemTopic.add(itemTopic);
                                }
                            }
                        }

                        Collections.sort(AppConstants.EPGDATA, new ChannelComparator());

                        runOnUiThread(() -> {
                            llLoadingLayout.setVisibility(View.GONE);
                            updateEpg();
                        });

                    } catch (JSONException e) {
                        runOnUiThread(() -> llLoadingLayout.setVisibility(View.GONE));
                        e.printStackTrace();
                    }
                }).start();
            }

            @Override
            public void onError(Object error) {
                llLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    private void calculateCountForCategory() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        for(ChannelItem channelItem : AppConstants.EPGDATA) {
            if(hashMap.get(channelItem.m_sCategory_ID) == null) {
                hashMap.put(channelItem.m_sCategory_ID, 0);
            }

            if(Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_ALL_CHANNELS, String.valueOf(true))))
                hashMap.put(channelItem.m_sCategory_ID, Objects.requireNonNull(hashMap.get(channelItem.m_sCategory_ID)) + 1);
            else {
                if(channelItem.m_arrItemTopic.size() > 1)
                    hashMap.put(channelItem.m_sCategory_ID, Objects.requireNonNull(hashMap.get(channelItem.m_sCategory_ID)) + 1);
            }
        }

        for(CategoryItem categoryItem : AppConstants.CHANNEL_CATEGORY_LIST) {
            if(categoryItem.category_id.equalsIgnoreCase("0")) { //for all
                if(Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_ALL_CHANNELS, String.valueOf(true)))) {
                    categoryItem.channelCount = AppConstants.EPGDATA.size();
                }
                else {
                    categoryItem.channelCount = 0;
                    for (ChannelItem channelItem: AppConstants.EPGDATA) {
                        if(channelItem.m_arrItemTopic.size() > 1)
                            categoryItem.channelCount++;
                    }
                }
            }
            else if(categoryItem.category_id.equalsIgnoreCase("-1")) {//for favorite
                if (Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_ALL_CHANNELS, String.valueOf(true)))) {
                    categoryItem.channelCount = 0;
                    for (ChannelItem channelItem: AppConstants.EPGDATA) {
                        if(AppConstants.FAVORITE_CHANNEL.values.contains(channelItem.m_sTvNum))
                            categoryItem.channelCount++;
                    }
                }
                else {
                    categoryItem.channelCount = 0;
                    for (ChannelItem channelItem: AppConstants.EPGDATA) {
                        if(AppConstants.FAVORITE_CHANNEL.values.contains(channelItem.m_sTvNum) && channelItem.m_arrItemTopic.size() > 1)
                            categoryItem.channelCount++;
                    }
                }
            }
            else if(hashMap.get(categoryItem.category_id) != null)
                categoryItem.channelCount = Objects.requireNonNull(hashMap.get(categoryItem.category_id));
        }
    }

    private void updateEpg() {
        liveCategoryMenuAdapter.notifyDataSetChanged();
        liveMenuAdapter.notifyDataSetChanged();
        AppConstants.EPG_FILTERED_DATA.clear();

        if(Boolean.parseBoolean(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_ALL_CHANNELS, String.valueOf(true)))) {
            AppConstants.EPG_FILTERED_DATA.addAll(AppConstants.EPGDATA);
        }
        else {
            for (ChannelItem channelItem: AppConstants.EPGDATA) {
                if(channelItem.m_arrItemTopic.size() > 1)
                    AppConstants.EPG_FILTERED_DATA.add(channelItem);
            }
        }

        calculateCountForCategory();
        liveMenuAdapter.notifyDataSetChanged();

        if(AppConstants.EPG_FILTERED_DATA.size() > 0) {
            lvChannelList.setSelection(0);
            liveMenuAdapter.setActiveMenuIndex(0);
            updateSelectedTopicInfo(false);
        }

        llContent.setVisibility(View.VISIBLE);

        lvChannelList.requestFocus();

        if(getIntent().getStringExtra(AppConstants.REMINDER_CHANNEL_NUMBER) != null && !getIntent().getStringExtra(AppConstants.REMINDER_CHANNEL_NUMBER).isEmpty()) {
            goToChannel(getIntent().getStringExtra(AppConstants.REMINDER_CHANNEL_NUMBER));
        }
    }

    private void selectChannelByNumber(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                selectedChannelItem = null;
                selectChannelHandler.removeCallbacks(selectChannelRunnable);
                selectedChannelNumber += String.valueOf(keyCode - KeyEvent.KEYCODE_0);

                if(selectedChannelNumber.isEmpty())
                    return;

                int categoryIndex = AppConstants.CATEGORY_INDEX;
                for(ChannelItem item : AppConstants.EPGDATA) {
                    if(item.m_sTvNum.equals(selectedChannelNumber)) {
                        for(int j = 0; j < AppConstants.CHANNEL_CATEGORY_LIST.size(); j++) {
                            if(AppConstants.CHANNEL_CATEGORY_LIST.get(j).category_id.equalsIgnoreCase(item.m_sCategory_ID)) {
                                categoryIndex = j;
                                AppConstants.CATEGORY_INDEX = categoryIndex;
                                selectedChannelItem = item;
                                break;
                            }
                        }
                        if(categoryIndex != 0)
                            break;
                    }
                }

                if(selectedChannelItem != null) {
                    liveMenuAdapter.setActiveMenuIndex(categoryIndex);
                    liveMenuAdapter.notifyDataSetChanged();
                    filterChannel(categoryIndex);

                    int index = 0;
                    for (ChannelItem item : AppConstants.EPG_FILTERED_DATA) {
                        if (item.m_sTvNum.equals(selectedChannelNumber)) {
                            if (lvChannelList != null)
                                lvChannelList.setSelection(index);
                            break;
                        }
                        index++;
                    }
                    assert lvChannelList != null;
                    if (lvChannelList.getVisibility() != View.VISIBLE) {
                        llDetails.setVisibility(View.VISIBLE);
                        //llchannelinfo.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.VISIBLE);
                        if ((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null) ||
                                (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null) ||
                                mVideoSurface != null) {
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
                            layoutParams.bottomToTop = -1;
                            layoutParams.bottomToBottom = llDetails.getId();
                            rlVideo.setLayoutParams(layoutParams);
                        }
                    }
                    showSelectedTopicInfo(selectedChannelItem);
                }
                else {
                    llDetails.setVisibility(View.GONE);
                    //llchannelinfo.setVisibility(View.GONE);
                }

                tvSelectedChannelNumber.setText(selectedChannelNumber);
                selectChannelHandler.postDelayed(selectChannelRunnable, 2000);
                break ;
            case KeyEvent.KEYCODE_FORWARD_DEL:
                selectChannelHandler.removeCallbacks(selectChannelRunnable);
                if (selectedChannelNumber.isEmpty())
                    return ;
                if (selectedChannelNumber.length() == 1)
                    selectedChannelNumber = "";
                else {
                    selectedChannelNumber = selectedChannelNumber.substring(0, selectedChannelNumber.length() - 2);
                }

                if(selectedChannelNumber.isEmpty())
                    return;

                categoryIndex = AppConstants.CATEGORY_INDEX;
                selectedChannelItem = null;
                for(ChannelItem item : AppConstants.EPGDATA) {
                    if(item.m_sTvNum.equals(selectedChannelNumber)) {
                        for(int j = 0; j < AppConstants.CHANNEL_CATEGORY_LIST.size(); j++) {
                            if(AppConstants.CHANNEL_CATEGORY_LIST.get(j).category_id.equalsIgnoreCase(item.m_sCategory_ID)) {
                                categoryIndex = j;
                                AppConstants.CATEGORY_INDEX = categoryIndex;
                                selectedChannelItem = item;
                                break;
                            }
                        }
                        if(categoryIndex != 0)
                            break;
                    }
                }

                if(selectedChannelItem != null) {
                    liveMenuAdapter.setActiveMenuIndex(categoryIndex);
                    liveMenuAdapter.notifyDataSetChanged();
                    filterChannel(categoryIndex);

                    int index = 0;
                    for (ChannelItem item : AppConstants.EPG_FILTERED_DATA) {
                        if (item.m_sTvNum.equals(selectedChannelNumber)) {
                            if (lvChannelList != null)
                                lvChannelList.setSelection(index);
                            break;
                        }
                        index++;
                    }
                    assert lvChannelList != null;
                    if (lvChannelList.getVisibility() != View.VISIBLE) {
                        llDetails.setVisibility(View.VISIBLE);
                        //llchannelinfo.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.VISIBLE);
                        if ((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null) ||
                                (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null) ||
                                mVideoSurface != null) {
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
                            layoutParams.bottomToTop = -1;
                            layoutParams.bottomToBottom = llDetails.getId();
                            rlVideo.setLayoutParams(layoutParams);
                        }
                    }

                    showSelectedTopicInfo(selectedChannelItem);
                }
                else {
                    llDetails.setVisibility(View.GONE);
                    //llchannelinfo.setVisibility(View.GONE);
                }

                tvSelectedChannelNumber.setText(selectedChannelNumber);
                selectChannelHandler.postDelayed(selectChannelRunnable, 2000);
                break ;
            case KeyEvent.KEYCODE_DPAD_UP:
                selectedChannelItem = null;
                selectChannelHandler.removeCallbacks(selectChannelRunnable);
                if(liveMenuAdapter.getActiveMenuIndex() > 0) {
                    lvChannelList.setSelection(liveMenuAdapter.getActiveMenuIndex() - 1);
                    liveMenuAdapter.setActiveMenuIndex(liveMenuAdapter.getActiveMenuIndex() - 1);
                    if(lvChannelList.getVisibility() != View.VISIBLE) {
                        llDetails.setVisibility(View.VISIBLE);
                        //llchannelinfo.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.VISIBLE);
                        if((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null) ||
                                (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null) ||
                                mVideoSurface != null){
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
                            layoutParams.bottomToTop = -1;
                            layoutParams.bottomToBottom = llDetails.getId();
                            rlVideo.setLayoutParams(layoutParams);
                        }
                    }

                    updateSelectedTopicInfo(false);
                    selectedChannelNumber = AppConstants.EPG_FILTERED_DATA.get(liveMenuAdapter.getActiveMenuIndex()).m_sTvNum;
                    selectedChannelItem = AppConstants.EPG_FILTERED_DATA.get(liveMenuAdapter.getActiveMenuIndex());
                    tvSelectedChannelNumber.setText(selectedChannelNumber);
                    selectChannelHandler.postDelayed(selectChannelRunnable, 2000);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                selectedChannelItem = null;
                selectChannelHandler.removeCallbacks(selectChannelRunnable);
                if(liveMenuAdapter.getActiveMenuIndex() < AppConstants.EPG_FILTERED_DATA.size() - 1) {
                    lvChannelList.setSelection(liveMenuAdapter.getActiveMenuIndex() + 1);
                    liveMenuAdapter.setActiveMenuIndex(liveMenuAdapter.getActiveMenuIndex() + 1);
                    if(lvChannelList.getVisibility() != View.VISIBLE) {
                        llDetails.setVisibility(View.VISIBLE);
                        //llchannelinfo.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.VISIBLE);
                        if((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null) ||
                                (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null) ||
                                mVideoSurface != null){
                            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
                            layoutParams.bottomToTop = -1;
                            layoutParams.bottomToBottom = llDetails.getId();
                            rlVideo.setLayoutParams(layoutParams);
                        }
                    }

                    updateSelectedTopicInfo(false);
                    selectedChannelNumber = AppConstants.EPG_FILTERED_DATA.get(liveMenuAdapter.getActiveMenuIndex()).m_sTvNum;
                    selectedChannelItem = AppConstants.EPG_FILTERED_DATA.get(liveMenuAdapter.getActiveMenuIndex());
                    tvSelectedChannelNumber.setText(selectedChannelNumber);
                    selectChannelHandler.postDelayed(selectChannelRunnable, 2000);
                }
                break;

            default:
                break;
        }
    }
    private int reconnectionAttempts = 0;
    private final int MAX_RECONNECTION_ATTEMPTS = 3; // Número máximo de intentos de reconexión manual
    private final long RECONNECTION_DELAY = 10000; // 10 segundos de retraso antes de intentar reconectar manualmente
    private boolean isManualReconnectScheduled = false; // Para evitar múltiples intentos de reconexión manual



    @OptIn(markerClass = UnstableApi.class) @SuppressLint("DefaultLocale")
    private void initializePlayer() {
//        mVideoSurfaceFrame.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    Log.e("onTouchEvent", "passed");
//                    showGuide();
//                }
//                return false;
//            }
//        });
//        mVideoSurfaceFrame.setOnClickListener(v -> {
//            if(Boolean.valueOf(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, String.valueOf(true)))) {
//                if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && player != null && player.getPlayWhenReady()) {
//                    showGuide();
//                }
//            }
//            else {
//                if(lvChannelList != null && lvChannelList.getVisibility() != View.VISIBLE && mVideoView.isPlaying()) {
//                    showGuide();
//                }
//            }
//        });
        liveMenuAdapter.setCurrentPlayingChannel(currentChannelItem);
        liveMenuAdapter.notifyDataSetChanged();

        Log.e("Stream URL : ", streamUrl);

        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            playerView.setVisibility(View.VISIBLE);

            if (player == null) {
                // Configuración de LoadControl (Buffering)
                LoadControl loadControl = new DefaultLoadControl.Builder()
                        .setBufferDurationsMs(
                                2000,  // MIN_BUFFER_MS: Reduce el buffer mínimo para iniciar rápidamente
                                10000, // MAX_BUFFER_MS: Buffer máximo para evitar uso excesivo de memoria
                                1000,  // BUFFER_FOR_PLAYBACK_MS: Menor tiempo para iniciar reproducción
                                2000   // BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS: Menor tiempo para reanudar tras un rebuffer
                        )
                        .setTargetBufferBytes(C.LENGTH_UNSET) // Deja que ExoPlayer determine el tamaño del buffer
                        .setPrioritizeTimeOverSizeThresholds(true) // Prioriza el tiempo sobre el tamaño del buffer
                        .build();

                // Configuración de ExoPlayer
                RenderersFactory renderersFactory = new DefaultRenderersFactory(this)
                        .setEnableDecoderFallback(true);
                player = new ExoPlayer.Builder(this)
                        .setMediaSourceFactory(new DefaultMediaSourceFactory(this)
                                .setLiveTargetOffsetMs(3000)) // Offset de 3 segundos para baja latencia
                        .setLoadControl(loadControl)
                        .setLivePlaybackSpeedControl(
                                new DefaultLivePlaybackSpeedControl.Builder()
                                        .setFallbackMaxPlaybackSpeed(1.03f) // Ajuste de velocidad para evitar desincronización
                                        .setMinUpdateIntervalMs(500)       // Intervalo mínimo de actualización
                                        .setProportionalControlFactor(0.1f) // Control proporcional para ajustar la velocidad
                                        .build()
                        )
                        .build();

                // Selección de pistas para Adaptive Bitrate
                player.setTrackSelectionParameters(
                        player.getTrackSelectionParameters()
                                .buildUpon()
                                .setMaxVideoBitrate(5_000_000) // Limita el bitrate máximo (5 Mbps)
                                .setForceLowestBitrate(false)  // Permite cambiar dinámicamente el bitrate
                                .build()
                );

                // Listener para eventos, incluyendo errores
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerError(PlaybackException error) {
                        Log.e("ExoPlayer", "Error detected: " + error.getMessage());
                        retryStream(); // Llama al método de reconexión
                    }
                });

                // Configuración de PlayerView
                playerView.setPlayer(player);
                playerView.hideController();
                playerView.setControllerShowTimeoutMs(2000);
                player.setPlayWhenReady(shouldAutoPlay);

                // Configuración de subtítulos
                SubtitleView subtitleView = playerView.getSubtitleView();
                if (subtitleView != null) {
                    int fontSize = Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PREFERRED_FONT_SIZE, String.valueOf(12)));
                    subtitleView.setStyle(new CaptionStyleCompat(
                            Color.WHITE,
                            Color.TRANSPARENT,
                            Color.TRANSPARENT,
                            CaptionStyleCompat.EDGE_TYPE_DROP_SHADOW,
                            Color.BLACK, // Sombra negra para mejor contraste
                            null
                    ));
                    subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
                    subtitleView.setVisibility(View.VISIBLE);
                }
            }

            if (streamUrl == null || streamUrl.isEmpty()) {
                return;
            }

// Configuración de MediaSource
            MediaSource mediaSource = createMediaSource(streamUrl);

// Restaurar posición de reproducción si aplica
            boolean haveStartPosition = currentWindow != C.INDEX_UNSET;
            if (haveStartPosition) {
                player.seekTo(currentWindow, playbackPosition);
            }

// Preparar el player
            player.prepare(mediaSource, false, false);



        }
        else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
            mVideoView.setVisibility(View.VISIBLE);
            mVideoView.setOnErrorListener((iMediaPlayer, i, i1) -> {
                llLoadingLayout.setVisibility(View.GONE);
                tvLoading.setVisibility(View.VISIBLE);

                reconnectHandler.postDelayed(reconnectRunnable, AppConstants.RECONNECT_NEXT_TIME_OUT);
                return true;
            });

            mVideoView.setOnPreparedListener(iMediaPlayer -> {
                llLoadingLayout.setVisibility(View.GONE);
                tvLoading.setVisibility(View.VISIBLE);

                progressHandler.removeCallbacks(progressRunnable);
                progressHandler.postDelayed(progressRunnable, 500);
            });

            // init player
            IjkMediaPlayer.loadLibrariesOnce(null);
//            IjkMediaPlayer.native_profileBegin("libijkplayer.so");

            // prefer mVideoPath
            if (streamUrl != null) {
                mVideoView.setVideoPath(streamUrl);
                mVideoView.start();

                tvLoading.setVisibility(View.GONE);
                llLoadingLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            mVideoSurface.setVisibility(View.VISIBLE);

            ArrayList<String> args = new ArrayList<>();
            int fontSize = Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.PREFERRED_FONT_SIZE, String.valueOf(12)));
            fontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, fontSize, getResources().getDisplayMetrics());
            args.add("-vvv"); // Verbose mode for debugging
            args.add("--network-caching=5000"); // Aumentar el caché de red a 5000ms (5 segundos)
            args.add("--live-caching=3000"); // Aumentar el caché en vivo a 3000ms (3 segundos)
            args.add("--clock-jitter=0"); // Disable clock jitter to handle timestamp issues
            args.add("--drop-late-frames"); // Drop late frames to prevent stutter
            args.add("--skip-frames"); // Skip frames if necessary
            args.add("--http-reconnect"); // Enable automatic reconnection for HTTP streams
            args.add("--file-caching=5000"); // Adjust caching for MPEG-TS streams
            args.add("--avcodec-hw=mediacodec"); // Hardware acceleration
            args.add("--ts-seek-percent"); // Seek percentage for MPEG-TS streams
            args.add("--sout-keep"); // Keep the stream output alive

            mLibVLC = new LibVLC(this, args);
            if(Utils.securityInfo.userAgent == null || Utils.securityInfo.userAgent.isEmpty())
                mLibVLC.setUserAgent(Utils.securityInfo.userAgent, Util.getUserAgent(this, "mediaPlayerSample"));
            else
                mLibVLC.setUserAgent(Utils.securityInfo.userAgent, Utils.securityInfo.userAgent);

            mMediaPlayer = new org.videolan.libvlc.MediaPlayer(mLibVLC);

            final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
            vlcVout.setVideoView(mVideoSurface);
            vlcVout.attachViews(this);
            mMediaPlayer.getVLCVout().addCallback(this);

            mMediaPlayer.setEventListener(event -> {
                switch (event.type) {
                    case org.videolan.libvlc.MediaPlayer.Event.Buffering:
                        Log.d("VLC_Event", "Buffering...");
                        break;

                    case org.videolan.libvlc.MediaPlayer.Event.Playing:
                        // Restablecer el conteo de intentos de reconexión
                        reconnectionAttempts = 0;
                        Log.d("VLC_Event", "Playing...");

                        // Intentar cambiar a la pista de audio predeterminada
                        try {
                            int audioTrackCount = mMediaPlayer.getAudioTracksCount();
                            if (audioTrackCount > 0) {
                                Log.d("VLC_Event", "Intentando seleccionar la pista de audio...");
                                mMediaPlayer.setAudioTrack(0); // Seleccionar la primera pista
                            }
                        } catch (Exception e) {
                            Log.e("VLC_Event", "Error al cambiar la pista de audio: " + e.getMessage());
                        }
                        break;

                    case org.videolan.libvlc.MediaPlayer.Event.Stopped:
                        Log.d("VLC_Event", "Intentando reconexión manual...");
                    case org.videolan.libvlc.MediaPlayer.Event.EndReached:
                        Log.d("VLC_Event", "Intentando reconexión manual...");
                    case org.videolan.libvlc.MediaPlayer.Event.EncounteredError:
                        Log.d("VLC_Event", "Intentando reconexión manual...");
                        if(Apagado.equals("true")) {
                            Apagado = "false";
                            return;
                        }
                        else {
                            Apagado = "false";
                            retryConnectionWithDelay();
                        }

                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.Paused:
                        Log.d("VLC_Event", "Paused...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.Opening:
                        Log.d("VLC_Event", "Opening...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.PositionChanged:
                        //Log.d("VLC_Event", "PositionChanged...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.TimeChanged:
                        //Log.d("VLC_Event", "TimeChanged...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.Vout:
                        Log.d("VLC_Event", "Vout...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.ESAdded:
                        Log.d("VLC_Event", "ESAdded...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.ESDeleted:
                        Log.d("VLC_Event", "ESDeleted...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.ESSelected:
                        Log.d("VLC_Event", "ESSelected...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.MediaChanged:
                        Log.d("VLC_Event", "MediaChanged...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.LengthChanged:
                        Log.d("VLC_Event", "LengthChanged...");
                        break;
                    case org.videolan.libvlc.MediaPlayer.Event.SeekableChanged:
                        Log.d("VLC_Event", "SeekableChanged...");
                        break;

                    default:
                        break;
                }
            });

            Media media = new Media(mLibVLC, Uri.parse(Utils.makeAvaiableUrl(streamUrl)));
            media.addOption(":fill-screen");
            mMediaPlayer.setMedia(media);

            int sw = rlVideo.getWidth();
            int sh = rlVideo.getHeight();
            mMediaPlayer.setAspectRatio("" + sw + ":" + sh);
            mMediaPlayer.play();

            if (mOnLayoutChangeListener == null) {
                mOnLayoutChangeListener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.post(mRunnable);
                    }
                };
            }

            rlVideo.addOnLayoutChangeListener(mOnLayoutChangeListener);
            progressHandler.removeCallbacks(progressRunnable);
            progressHandler.postDelayed(progressRunnable, 500);
        }
    }

    private void retryConnectionWithDelay() {
        mHandler.postDelayed(() -> {
            // Verificamos que el mMediaPlayer esté inicializado y no haya sido liberado
            if (mMediaPlayer != null) {
                try {
                    if (!mMediaPlayer.isPlaying()) {
                        Log.d("VLC_Event", "Reintentando la conexión al stream...");
                        mMediaPlayer.stop();
                        mMediaPlayer.play();
                    }
                } catch (IllegalStateException e) {
                    // Manejo del error si el estado de mMediaPlayer es inválido
                    Log.e("VLC_Event", "No se puede obtener el estado de mMediaPlayer: ", e);
                }
            } else {
                Log.e("VLC_Event", "mMediaPlayer es null o ha sido liberado");
            }
        }, 2000); // Retraso de 2 segundos para intentar la reconexión
    }

    // Método para crear un MediaSource
    @OptIn(markerClass = UnstableApi.class) private MediaSource createMediaSource(String streamUrl) {
        String userAgent = "";
        if(Utils.securityInfo.userAgent == null || Utils.securityInfo.userAgent.isEmpty())
            userAgent = Util.getUserAgent(this, "mediaPlayerSample");
        else
            userAgent = Utils.securityInfo.userAgent;
        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(streamUrl)
                .setLiveConfiguration(
                        new MediaItem.LiveConfiguration.Builder()
                                .setMaxPlaybackSpeed(1.02f) // Incremento leve de velocidad para reducir latencia
                                .build()
                )
                .build();

        if (streamUrl.endsWith(".m3u8")) {
            // HLS
            return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(mediaItem);
        } else if (streamUrl.endsWith(".ts")) {
            // MPEG-TS
            return new ProgressiveMediaSource.Factory(
                    new DefaultDataSource.Factory(this, new DefaultHttpDataSource.Factory()
                            .setUserAgent(Util.getUserAgent(this, userAgent))
                            .setAllowCrossProtocolRedirects(true)
                            .setConnectTimeoutMs(5000)
                            .setReadTimeoutMs(5000))
            ).createMediaSource(mediaItem);
        } else {
            // Otros formatos progresivos
            return new ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(mediaItem);
        }
    }

    private void forceReconnect() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop(); // Detener la reproducción actual
        }
        reconnectHandler.postDelayed(() -> {
            Media media = new Media(mLibVLC, Uri.parse(Utils.makeAvaiableUrl(streamUrl)));
            media.addOption(":fill-screen");
            mMediaPlayer.setMedia(media);

            // Pequeña pausa antes de reproducir nuevamente
            reconnectHandler.postDelayed(() -> mMediaPlayer.play(), 1000);
        }, RECONNECTION_DELAY);
    }

    // Método para reintentar la conexión
    @OptIn(markerClass = UnstableApi.class) private void retryStream() {
        // Asegúrate de que el streamUrl no esté vacío
        if (streamUrl == null || streamUrl.isEmpty()) {
            Log.e("ExoPlayer", "Stream URL is invalid, cannot retry.");
            return;
        }

        // Detén el reproductor antes de reintentar
        if (player != null) {
            player.stop();
        }

        // Esperar un intervalo antes de reintentar
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Log.i("ExoPlayer", "Retrying connection...");
            MediaSource mediaSource = createMediaSource(streamUrl); // Crea el MediaSource nuevamente
            player.setMediaSource(mediaSource);
            player.prepare();
            player.setPlayWhenReady(true);
        }, 3000); // Retraso de 3 segundos antes de reintentar
    }

    private void releasePlayer() {
        progressHandler.removeCallbacks(progressRunnable);
        reconnectHandler.removeCallbacks(reconnectRunnable);
        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            if (player != null) {
                updateStartPosition();
                shouldAutoPlay = player.getPlayWhenReady();
                player.release();
                player = null;
            }
        }
        else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
            mVideoView.stopPlayback();
            mVideoView.release(true);
            mVideoView.stopBackgroundPlay();

//            IjkMediaPlayer.native_profileEnd();
        }
        else {
            updateStartPosition();

            if (mOnLayoutChangeListener != null) {
                rlVideo.removeOnLayoutChangeListener(mOnLayoutChangeListener);
                mOnLayoutChangeListener = null;
            }

            mHandler.removeCallbacks(mRunnable);

            if(mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mLibVLC.release();
                mMediaPlayer.getVLCVout().detachViews();
                mMediaPlayer.getVLCVout().removeCallback(this);
            }
        }
    }

    private void changeMediaPlayerLayout(int displayW, int displayH) {
        /* Change the video placement using the MediaPlayer API */
        Media.VideoTrack vtrack = mMediaPlayer.getCurrentVideoTrack();
        if (vtrack == null) {
            return;
        }

        final boolean videoSwapped = vtrack.orientation == Media.VideoTrack.Orientation.LeftBottom
                || vtrack.orientation == Media.VideoTrack.Orientation.RightTop;

        int videoW = vtrack.width;
        int videoH = vtrack.height;

        if (videoSwapped) {
            int swap = videoW;
            videoW = videoH;
            videoH = swap;
        }
        if (vtrack.sarNum != vtrack.sarDen)
            videoW = videoW * vtrack.sarNum / vtrack.sarDen;

        float ar = videoW / (float) videoH;
        float dar = displayW / (float) displayH;

        float scale;
        if (dar >= ar) {
            scale = displayW / (float) videoW; /* horizontal */
            if(scale * videoH > displayH) {
                scale = (float)displayH / videoH;
            }
        }
        else {
            scale = displayH / (float) videoH; /* vertical */
            if(scale * videoW > displayW) {
                scale = (float)displayW / videoW;
            }
        }

        if(videoW != 0 && videoH != 0)
            mMediaPlayer.setScale(scale);
        mMediaPlayer.setAspectRatio("" + displayW + ":" + displayH);

        mVideoSurface.invalidate();
    }

    private void updateVideoSurfaces() {
        int sw = rlVideo.getWidth();
        int sh = rlVideo.getHeight();

        // sanity check
        if (sw * sh == 0) {
            return;
        }

        mMediaPlayer.getVLCVout().setWindowSize(sw, sh);
        changeMediaPlayerLayout(sw, sh);
    }

    @Override
    public void onSurfacesCreated(IVLCVout ivlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout ivlcVout) {

    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        updateVideoSurfaces();
    }

    @Override
    public void onEvent(org.videolan.libvlc.MediaPlayer.Event event) {
        switch (event.type){
            case org.videolan.libvlc.MediaPlayer.Event.Buffering:
                //Log.e("aaa", "Buffering");
                onBuffering();
                break;
            case org.videolan.libvlc.MediaPlayer.Event.EncounteredError:
                //Log.e("aaa", "EncounteredError");
                onEncounteredError();
                break;
            case org.videolan.libvlc.MediaPlayer.Event.PositionChanged:
                //Log.e("aaa", "PositionChanged");
                onPositionChanged();
                break;
            case org.videolan.libvlc.MediaPlayer.Event.TimeChanged:
                //Log.e("aaa", "TimeChanged");
                onTimeChanged();
                break;
        }
    }

    private void onBuffering() {
        tvLoading.setVisibility(View.GONE);
        llLoadingLayout.setVisibility(View.VISIBLE);
    }

    private void onEncounteredError() {
        tvLoading.setVisibility(View.VISIBLE);
        llLoadingLayout.setVisibility(View.GONE);
        Toast.makeText(LiveAccessibleActivity.this, "There is no found real media.", Toast.LENGTH_SHORT).show();
    }

    private void onPositionChanged() {
        tvLoading.setVisibility(View.VISIBLE);
        llLoadingLayout.setVisibility(View.GONE);

        long timestream = mMediaPlayer.getTime();
        if(timestream == 0)
        {
            try {
                TimeUnit.SECONDS.sleep(2);
                //Log.e("tiemporeproducido", timestream + "");
                if (mMediaPlayer.getTime()==0)
                {
                    mMediaPlayer.stop();
                    mMediaPlayer.play();
                    TimeUnit.SECONDS.sleep(3);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

        //Log.e("audiotracks", timestream + "");

    }

    private void onTimeChanged() {
        tvLoading.setVisibility(View.VISIBLE);
        llLoadingLayout.setVisibility(View.GONE);
    }

    public Tracks getTrackSelector() {
        return player.getCurrentTracks();
    }

    public static class ChannelComparator implements Comparator<ChannelItem>
    {
        public int compare(ChannelItem left, ChannelItem right) {
            if(left.m_sTvNum.isEmpty())
                return -1;
            else if(right.m_sTvNum.isEmpty())
                return 1;
            else return Integer.compare(Integer.parseInt(left.m_sTvNum), Integer.parseInt(right.m_sTvNum));
        }
    }

    public static class TopicComparator implements Comparator<ItemTopic>
    {
        public int compare(ItemTopic left, ItemTopic right) {
            return Long.compare(left.m_dateTopicStart.getTime(), right.m_dateTopicStart.getTime());
        }
    }

    //for exo player
    protected class PlayerEventListener implements Player.Listener {
        boolean playWhenReady_;
        int playbackState_;

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            playbackState_ = playbackState;
            onPlayerStateChanged();
        }

        @Override
        public void onPlayWhenReadyChanged(boolean playWhenReady, int reason) {
            playWhenReady_ = playWhenReady;
            onPlayerStateChanged();
        }

        void onPlayerStateChanged() {
            boolean playWhenReady = playWhenReady_;
            int playbackState = playbackState_;
            if (playbackState == Player.STATE_BUFFERING) {
                tvLoading.setVisibility(View.GONE);
                llLoadingLayout.setVisibility(View.VISIBLE);
            }
            else if (playbackState == Player.STATE_READY) {
                llLoadingLayout.setVisibility(View.GONE);
                tvLoading.setVisibility(View.VISIBLE);

                progressHandler.removeCallbacks(progressRunnable);
                progressHandler.postDelayed(progressRunnable, 500);
            }
            else if (playbackState == Player.STATE_ENDED || !playWhenReady) {

                playerView.setKeepScreenOn(false);

                runOnUiThread(() -> {
                    llLoadingLayout.setVisibility(View.GONE);
                    tvLoading.setVisibility(View.VISIBLE);
                });

                reconnectHandler.postDelayed(reconnectRunnable, AppConstants.RECONNECT_NEXT_TIME_OUT);
            }
            else { // State playing
                // This prevents the screen from getting dim/lock
                playerView.setKeepScreenOn(true);
            }
        }

        @Override
        public void onTracksChanged(Tracks tracks) {
            if(audioTrackList.size() == 0) {
                subTitleTrackList.add(getString(R.string.disable));

                for (Tracks.Group tsg: tracks.getGroups()) {
                    final TrackGroup trackGroup = tsg.getMediaTrackGroup();
                    if (trackGroup != null && trackGroup.length > 0) {
                        if (trackGroup.getFormat(0).sampleMimeType != null) {
                            if(Objects.requireNonNull(trackGroup.getFormat(0).sampleMimeType).startsWith("audio")) {
                                if(trackGroup.getFormat(0).language != null && !Objects.requireNonNull(trackGroup.getFormat(0).language).isEmpty())
                                    audioTrackList.add(trackGroup.getFormat(0).language);
                            }
                            if(Objects.requireNonNull(trackGroup.getFormat(0).sampleMimeType).startsWith("application")) {
                                if(!subTitleTrackList.contains(trackGroup.getFormat(0).language) && trackGroup.getFormat(0).language != null && !Objects.requireNonNull(trackGroup.getFormat(0).language).isEmpty())
                                    subTitleTrackList.add(trackGroup.getFormat(0).language);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onPlayerError(PlaybackException e) {
            new Thread(() -> {
                if (Utils.getConnectivityStatus(LiveAccessibleActivity.this) != Utils.TYPE_NOT_CONNECTED) {
                    runOnUiThread(() -> {
                        llLoadingLayout.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.VISIBLE);
                    });
                } else {
                    // Mueve playerView.setVisibility dentro de runOnUiThread
                    runOnUiThread(() -> {
                        playerView.setVisibility(View.INVISIBLE);
                        if (player != null) {
                            player.stop();
                        }
                        llLoadingLayout.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.VISIBLE);
                    });
                }
            }).start();

            reconnectHandler.postDelayed(reconnectRunnable, AppConstants.RECONNECT_NEXT_TIME_OUT);
        }
    }

    public void goToChannel(String channelNumber) {
        selectedChannelItem = null;
        selectChannelHandler.removeCallbacks(selectChannelRunnable);
        selectedChannelNumber = channelNumber;

        if(selectedChannelNumber.isEmpty())
            return;

        int categoryIndex = AppConstants.CATEGORY_INDEX;
        for(ChannelItem item : AppConstants.EPGDATA) {
            if(item.m_sTvNum.equals(selectedChannelNumber)) {
                for(int j = 0; j < AppConstants.CHANNEL_CATEGORY_LIST.size(); j++) {
                    if(AppConstants.CHANNEL_CATEGORY_LIST.get(j).category_id.equalsIgnoreCase(item.m_sCategory_ID)) {
                        categoryIndex = j;
                        AppConstants.CATEGORY_INDEX = categoryIndex;
                        selectedChannelItem = item;
                        break;
                    }
                }
                if(categoryIndex != 0)
                    break;
            }
        }

        if(selectedChannelItem != null) {
            liveMenuAdapter.setActiveMenuIndex(categoryIndex);
            liveMenuAdapter.notifyDataSetChanged();
            filterChannel(categoryIndex);

            int index = 0;
            for (ChannelItem item : AppConstants.EPG_FILTERED_DATA) {
                if (item.m_sTvNum.equals(selectedChannelNumber)) {
                    if (lvChannelList != null)
                        lvChannelList.setSelection(index);
                    break;
                }
                index++;
            }

            assert lvChannelList != null;
            if (lvChannelList.getVisibility() != View.VISIBLE) {
                llDetails.setVisibility(View.VISIBLE);
                //llchannelinfo.setVisibility(View.VISIBLE);
                llContent.setVisibility(View.VISIBLE);
                if ((Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1 && playerView != null) ||
                        (Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0 && mVideoView != null) || mVideoSurface != null) {
                    ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) rlVideo.getLayoutParams();
                    layoutParams.bottomToTop = -1;
                    layoutParams.bottomToBottom = llDetails.getId();
                    rlVideo.setLayoutParams(layoutParams);
                }
            }
            showSelectedTopicInfo(selectedChannelItem);
        }
        else {
            llDetails.setVisibility(View.GONE);
            //llchannelinfo.setVisibility(View.GONE);
        }

        tvSelectedChannelNumber.setText(selectedChannelNumber);
        selectChannelHandler.postDelayed(selectChannelRunnable, 2000);
    }

    //for video and network info
    @UnstableApi public void showMediaInfo() {
        TableLayoutBinder builder = new TableLayoutBinder(LiveAccessibleActivity.this);
        if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 1) {
            builder.appendSection(R.string.mi_player);
            builder.appendRow2(R.string.mi_player, getString(R.string.exo_player_name));
            builder.appendSection(R.string.mi_media);
            builder.appendRow2(R.string.mi_resolution, Objects.requireNonNull(player.getVideoFormat()).width + "*" + player.getVideoFormat().height);
            builder.appendRow2(R.string.mi_length, "--:--");

            TrackGroupArray trackGroups = player.getCurrentTrackGroups();

            for (int i = 0; i < trackGroups.length; i++) {
                if (trackGroups.get(i) != null && trackGroups.get(i).length > 0) {
                    if (trackGroups.get(i).getFormat(0).sampleMimeType != null) {
                        if(Objects.requireNonNull(trackGroups.get(i).getFormat(0).sampleMimeType).startsWith("audio")) {
                            builder.appendSection(getString(R.string.mi_stream_fmt1, i) + " " + getString(R.string.mi__selected_audio_track));
                            builder.appendRow2(R.string.mi_type, trackGroups.get(i).getFormat(0).sampleMimeType);
                            builder.appendRow2(R.string.mi_language, trackGroups.get(i).getFormat(0).language);

                            builder.appendRow2(R.string.mi_codec, trackGroups.get(i).getFormat(0).codecs);
                            builder.appendRow2(R.string.mi_profile_level, trackGroups.get(i).getFormat(0).containerMimeType);
                            builder.appendRow2(R.string.mi_sample_rate, trackGroups.get(i).getFormat(0).sampleRate + "Hz");
                            builder.appendRow2(R.string.mi_channels, String.valueOf(trackGroups.get(i).getFormat(0).channelCount));
                            builder.appendRow2(R.string.mi_bit_rate, trackGroups.get(i).getFormat(0).bitrate + "kb/s");
                        }
                        if(Objects.requireNonNull(trackGroups.get(i).getFormat(0).sampleMimeType).startsWith("video")) {
                            builder.appendSection(getString(R.string.mi_stream_fmt1, i) + " " + getString(R.string.mi__selected_video_track));
                            builder.appendRow2(R.string.mi_type, trackGroups.get(i).getFormat(0).sampleMimeType);
                            builder.appendRow2(R.string.mi_language, trackGroups.get(i).getFormat(0).language);

                            builder.appendRow2(R.string.mi_codec, trackGroups.get(i).getFormat(0).codecs);
                            builder.appendRow2(R.string.mi_profile_level, trackGroups.get(i).getFormat(0).containerMimeType);
                            builder.appendRow2(R.string.mi_sample_rate, trackGroups.get(i).getFormat(0).sampleRate + "Hz");
                            builder.appendRow2(R.string.mi_channels, String.valueOf(trackGroups.get(i).getFormat(0).channelCount));
                            builder.appendRow2(R.string.mi_bit_rate, trackGroups.get(i).getFormat(0).bitrate + "kb/s");
                        }
                    }
                }
            }
        }
        else if(Integer.parseInt(Utils.getSharePreferenceValue(LiveAccessibleActivity.this, AppConstants.IS_EXO_PLAYER, AppConstants.IS_EXO_PLAYER_DEFAULT)) == 0){
            builder.appendSection(R.string.mi_player);
            builder.appendRow2(R.string.mi_player, mVideoView.getPlayerName());
            builder.appendSection(R.string.mi_media);
            builder.appendRow2(R.string.mi_resolution, mVideoView.getResolution());
            builder.appendRow2(R.string.mi_length, mVideoView.getDurationWithString());

            ITrackInfo[] trackInfos = mVideoView.getTrackInfo();
            if (trackInfos != null) {
                int index = -1;
                for (ITrackInfo trackInfo : trackInfos) {
                    index++;

                    int trackType = trackInfo.getTrackType();
                    if (index == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                        builder.appendSection(getString(R.string.mi_stream_fmt1, index) + " " + getString(R.string.mi__selected_video_track));
                    } else if (index == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                        builder.appendSection(getString(R.string.mi_stream_fmt1, index) + " " + getString(R.string.mi__selected_audio_track));
                    } else if (index == MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_SUBTITLE) {
                        builder.appendSection(getString(R.string.mi_stream_fmt1, index) + " " + getString(R.string.mi__selected_subtitle_track));
                    } else {
                        builder.appendSection(getString(R.string.mi_stream_fmt1, index));
                    }
                    builder.appendRow2(R.string.mi_type, mVideoView.buildTrackType(trackType));
                    builder.appendRow2(R.string.mi_language, mVideoView.buildLanguage(trackInfo.getLanguage()));

                    IMediaFormat mediaFormat = trackInfo.getFormat();
                    if (mediaFormat instanceof IjkMediaFormat) {
                        switch (trackType) {
                            case ITrackInfo.MEDIA_TRACK_TYPE_VIDEO:
                                builder.appendRow2(R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                                builder.appendRow2(R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                                builder.appendRow2(R.string.mi_pixel_format, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PIXEL_FORMAT_UI));
                                builder.appendRow2(R.string.mi_resolution, mediaFormat.getString(IjkMediaFormat.KEY_IJK_RESOLUTION_UI));
                                builder.appendRow2(R.string.mi_frame_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_FRAME_RATE_UI));
                                builder.appendRow2(R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                                break;
                            case ITrackInfo.MEDIA_TRACK_TYPE_AUDIO:
                                builder.appendRow2(R.string.mi_codec, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_LONG_NAME_UI));
                                builder.appendRow2(R.string.mi_profile_level, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CODEC_PROFILE_LEVEL_UI));
                                builder.appendRow2(R.string.mi_sample_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_SAMPLE_RATE_UI));
                                builder.appendRow2(R.string.mi_channels, mediaFormat.getString(IjkMediaFormat.KEY_IJK_CHANNEL_UI));
                                builder.appendRow2(R.string.mi_bit_rate, mediaFormat.getString(IjkMediaFormat.KEY_IJK_BIT_RATE_UI));
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        else {
            builder.appendSection(R.string.mi_player);
            builder.appendRow2(R.string.mi_player, getString(R.string.vlc_player_name));
            builder.appendSection(R.string.mi_media);
            builder.appendRow2(R.string.mi_resolution, mMediaPlayer.getCurrentVideoTrack().width + " * " + mMediaPlayer.getCurrentVideoTrack().height);
            builder.appendRow2(R.string.mi_length, String.valueOf(mMediaPlayer.getLength()));

            for (int index = 0; index < Objects.requireNonNull(mMediaPlayer.getMedia()).getTrackCount(); index++) {
                if(mMediaPlayer.getMedia().getTrack(index).type == -1) {
                    continue;
                }
                if(mMediaPlayer.getMedia().getTrack(index).type == 0) {
                    builder.appendSection(getString(R.string.mi_stream_fmt1, index) + " " + getString(R.string.mi__selected_audio_track));

                    Media.AudioTrack track = (Media.AudioTrack) mMediaPlayer.getMedia().getTrack(index);
                    if(track != null) {
                        builder.appendRow2(R.string.mi_type, track.codec != null ? track.codec : "");
                        builder.appendRow2(R.string.mi_language, track.language != null ? track.language : "");

                        builder.appendRow2(R.string.mi_codec, track.codec);
                        builder.appendRow2(R.string.mi_profile_level, "" + track.level);
                        builder.appendRow2(R.string.mi_bit_rate, track.bitrate + "");
                    }
                }
                else if(mMediaPlayer.getMedia().getTrack(index).type == 1) {
                    builder.appendSection(getString(R.string.mi_stream_fmt1, index) + " " + getString(R.string.mi__selected_video_track));

                    Media.VideoTrack track = (Media.VideoTrack) mMediaPlayer.getMedia().getTrack(index);

                    if(track != null) {
                        builder.appendRow2(R.string.mi_type, track.codec != null ? track.codec : "");
                        builder.appendRow2(R.string.mi_language, track.language != null ? track.language : "");

                        builder.appendRow2(R.string.mi_codec, track.codec);
                        builder.appendRow2(R.string.mi_profile_level, "" + track.level);
                        builder.appendRow2(R.string.mi_resolution, track.width + " * " + track.height);
                        builder.appendRow2(R.string.mi_frame_rate, "" + track.frameRateNum);
                        builder.appendRow2(R.string.mi_bit_rate, track.bitrate + "");
                    }
                }
                else {
                    builder.appendSection(getString(R.string.mi_stream_fmt1, index) + " " + getString(R.string.mi__selected_subtitle_track));

                    Media.Track track = mMediaPlayer.getMedia().getTrack(index);
                    if(track != null) {
                        builder.appendRow2(R.string.mi_type, track.codec != null ? track.codec : "");
                        builder.appendRow2(R.string.mi_language, track.language != null ? track.language : "");
                    }
                }
            }
        }

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        Log.e("netInfo", netInfo.toString());

        builder.appendSection(R.string.network_information);
        builder.appendRow2(R.string.network_type, netInfo.getTypeName());
        builder.appendRow2(R.string.network_state, netInfo.getDetailedState().toString());
        builder.appendRow2(R.string.network_extra, netInfo.getExtraInfo());

        if(netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            builder.appendRow2(R.string.network_ip, ip);
        }
        else if(netInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
            String ipAddress = getLocalIpAddress();
            if(ipAddress != null)
                builder.appendRow2(R.string.network_ip, ipAddress);
        }
        //AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        TableLayoutBinder builder2 = new TableLayoutBinder(LiveAccessibleActivity.this);
        builder2.appendSection(R.string.speed_test_loading);

        AlertDialog.Builder dialog2 = builder2.buildAlertDialogBuilder();

        AlertDialog alertDialog = dialog2.create();

        alertDialog.show();

        if(Build.VERSION.SDK_INT > 23) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            int downSpeed = nc.getLinkDownstreamBandwidthKbps();
            int speedtest = 0;

            testDownloadSpeed(streamUrl, new DownloadSpeedCallback() {
                @Override
                public void onDownloadSpeedTestCompleted(int result) {
                    builder.appendRow2(R.string.network_speed, result + " MB/s");
                    Log.d(TAG, "Downloaded " + result + " MB in one minute");
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                    AlertDialog.Builder adBuilder = builder.buildAlertDialogBuilder();
                    adBuilder.setTitle(R.string.media_network_status);
                    adBuilder.setNegativeButton(R.string.close, null);
                    adBuilder.show();
                }
            });

            //builder.appendRow2(R.string.network_speed, downSpeed + " kb/s");
        }


    }
    private static final String TAG = "DownloadSpeedTest";

    public static void testDownloadSpeed(String urlString, DownloadSpeedCallback callback) {
        new DownloadSpeedTask(urlString, callback).execute();
    }

    private static class DownloadSpeedTask extends AsyncTask<Void, Void, Integer> {
        private String urlString;
        private DownloadSpeedCallback callback;

        public DownloadSpeedTask(String urlString, DownloadSpeedCallback callback) {
            this.urlString = urlString;
            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int totalBytesDownloaded = 0;
            long startTime = System.currentTimeMillis();
            long endTime = startTime + 10 * 1000; // 60 seconds

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                URL url = new URL(urlString);
                Log.d(TAG, "Opening connection to " + urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(10000); // 10 seconds
                urlConnection.setReadTimeout(10000); // 10 seconds
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);

                try {
                    Log.d(TAG, "Getting response code");
                    int responseCode = urlConnection.getResponseCode();
                    Log.d(TAG, "Response code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        inputStream = urlConnection.getInputStream();
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            totalBytesDownloaded += bytesRead;

                            // Check if one minute has passed
                            if (System.currentTimeMillis() >= endTime) {
                                break;
                            }
                        }
                    } else {
                        Log.e(TAG, "Server returned HTTP response code: " + responseCode);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error getting response code: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during connection: " + e.toString(), e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        Log.e(TAG, "Error closing InputStream: " + e.getMessage(), e);
                    }
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            // Convert bytes to MB
            int totalMBDownloaded = totalBytesDownloaded / (1024 * 1024);
            return totalMBDownloaded;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (callback != null) {
                callback.onDownloadSpeedTestCompleted(result);
            }
        }
    }
    public interface DownloadSpeedCallback {
        void onDownloadSpeedTestCompleted(int result);
    }

    public static float bandwidth( Context context, String stream) {
        //make a speed test to stream url to get the bandwidth for a minute
        int totalBytesDownloaded = 0;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + 60 * 1000; // 60 seconds

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            URL url = new URL(stream);
            Log.d(TAG, "Opening connection to " + stream);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000); // 10 seconds
            urlConnection.setReadTimeout(10000); // 10 seconds
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            try {
                Log.d(TAG, "Getting response code");
                int responseCode = urlConnection.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        totalBytesDownloaded += bytesRead;

                        // Check if one minute has passed
                        if (System.currentTimeMillis() >= endTime) {
                            break;
                        }
                    }
                } else {
                    Log.e(TAG, "Server returned HTTP response code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error getting response code: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during connection: " + e.toString(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing InputStream: " + e.getMessage(), e);
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        // Convert bytes to MB
        int totalMBDownloaded = totalBytesDownloaded / (1024 * 1024);
        return totalMBDownloaded;
    }

    public String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }
    private static class MenuItemViewHolder {
        ImageView    ivChannelLogoView;
        ImageView    ivChannelFavoriteView;
        TextView     tvChannelNumberView;
        TextView     tvChannelNameView;
        TextView     tvActualEPG;
    }
}
