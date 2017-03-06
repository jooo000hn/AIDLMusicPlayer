package com.aidlmusicplayer.www.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aidlmusicplayer.www.App;
import com.aidlmusicplayer.www.IMusicPlayer;
import com.aidlmusicplayer.www.IMusicPlayerListener;
import com.aidlmusicplayer.www.R;
import com.aidlmusicplayer.www.bean.SongListBean;
import com.aidlmusicplayer.www.iml.AgSeekBarChangeListener;
import com.aidlmusicplayer.www.service.MusicService;
import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * author：agxxxx on 2017/3/6 13:57
 * email：agxxxx@126.com
 * blog: http://www.jianshu.com/u/c1a3c4c943e5
 * github: https://github.com/agxxxx
 * Created by Administrator on 2017/3/6.
 */

public class BottomMusicPlayer extends FrameLayout {
    @Bind(R.id.play_bar_img)
    ImageView mPlayBarImg;
    @Bind(R.id.play_bar_info)
    TextView mPlayBarInfo;
    @Bind(R.id.play_bar_singer)
    TextView mPlayBarSinger;

    @Bind(R.id.play_list)
    ImageView mPlayList;
    @Bind(R.id.control)
    ImageView mControl;
    @Bind(R.id.play_next)
    ImageView mPlayNext;

    @Bind(R.id.musics_player_seek_bar)
    SeekBar mMusicsPlayerSeekBar;
    @Bind(R.id.nav_play)
    LinearLayout mNavPlay;
    private View mRootView;


    public BottomMusicPlayer(Context context) {
        this(context, null);
    }

    public BottomMusicPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMusicPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private IMusicPlayer mMusicPlayerService;

    public void init(Context context) {

        mRootView = View.inflate(context, R.layout.view_music_bottom, null);
        ButterKnife.bind(this, mRootView);
        addView(mRootView);

    }




    public void registerListener(IMusicPlayer musicPlayerService) {
        try {
            mMusicPlayerService = musicPlayerService;
            mMusicsPlayerSeekBar.setOnSeekBarChangeListener(new AgSeekBarChangeListener(mMusicPlayerService));
            mMusicPlayerService.registerListener(mPlayerListener);
            mHandler.sendEmptyMessage(UPDATE_UI);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void unregisterListener() {
        try {
            if (mMusicPlayerService != null) {
                mMusicPlayerService.unregisterListener(mPlayerListener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    IMusicPlayerListener mPlayerListener = new IMusicPlayerListener.Stub() {
        @Override
        public void action(int action, Message msg) throws RemoteException {
            mHandler.sendMessage(msg);
        }
    };


    private void updateSeek(Message msg) {
        int currentPosition = msg.arg1;
        int totalDuration = msg.arg2;
        mMusicsPlayerSeekBar.setMax(totalDuration);
        mMusicsPlayerSeekBar.setProgress(currentPosition);
    }

    private final int UPDATE_UI =23;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MusicService.MUSIC_ACTION_SEEK_PLAY:
                    updateSeek(msg);
                    break;
                case MusicService.MUSIC_ACTION_PLAY:
                case UPDATE_UI:
                    onPlay();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    };

    private void onPlay() {
        mMusicPlayerService = App.app.getMusicPlayerService();
        try {
            SongListBean songListBean =
                    (SongListBean) mMusicPlayerService.getCurrentSongInfo().obj;

            if (songListBean == null) {
                return;
            }
            Glide.with(getContext()).load(songListBean.pic_big)
                    .into(mPlayBarImg);
            mPlayBarInfo.setText(songListBean.title);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}