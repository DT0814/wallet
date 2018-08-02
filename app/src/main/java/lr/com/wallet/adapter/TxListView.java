package lr.com.wallet.adapter;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import lr.com.wallet.R;

public class TxListView extends ListView implements AbsListView.OnScrollListener {
    private View header;
    private int headerHeight;//顶部布局高度
    private int firstVisibleItem;//当前第一个 Item 可见位置
    private float startY;//按下时开始的Y值
    private int scrollState;//当前滚动状态
    private boolean canRefreshEnabled;//是否允许滚动

    private static int state;//当前状态
    private final static int NONE = 0;//正常状态
    private final static int PULL = 1;//下拉状态
    private final static int RELEASE = 2;//释放状态
    private final static int REFRESHING = 3;//正在刷新状态

    private IRefreshListener iRefreshListener;
    private int lastVisibleItem;//最后一个可见的Item
    private int totalItemCount;//所有Item数

    private View footer;
    private int footerHeight;//底部布局高度
    private float lastY;

    private ILoadMoreListener iLoadMoreListener;
    private boolean canLoadMoreEnabled;//是否允许加载更多

    public TxListView(Context context) {
        super(context);
        initView(context);
    }

    public TxListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TxListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        header = LayoutInflater.from(context).inflate(R.layout.header_layout, null);
        footer = LayoutInflater.from(context).inflate(R.layout.footer_layout, null);
        measureView(header);
        measureView(footer);
        //这里获取高度的时候需要先通知父布局header占用的空间
        headerHeight = header.getMeasuredHeight();
        footerHeight = footer.getMeasuredHeight();
        topPadding(-headerHeight);
        bottomPadding(-footerHeight);
        this.addHeaderView(header);
        this.addFooterView(footer);
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局 header 占用的宽高
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, lp.width);
        int height;
        int tempHeight = lp.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }

    /**
     * 设置 Header 布局的上边距
     * 以隐藏 Header
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        header.setPadding(header.getPaddingLeft(), topPadding,
                header.getPaddingRight(),
                header.getPaddingBottom());
        header.invalidate();
    }

    private void bottomPadding(int bottomPadding) {
        footer.setPadding(footer.getPaddingLeft(), footer.getPaddingTop(),
                footer.getPaddingRight(),
                bottomPadding);
        footer.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //最顶部
                if (firstVisibleItem == 0) {
                    canRefreshEnabled = true;
                    startY = ev.getY();
                } else if (lastVisibleItem == totalItemCount) {
                    canLoadMoreEnabled = true;
                    lastY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELEASE) {//如果已经释放，则可以提示刷新数据
                    state = REFRESHING;
                    if (iRefreshListener != null) {
                        iRefreshListener.onRefresh();
                    }
                    if (iLoadMoreListener != null) {
                        iLoadMoreListener.onLoadMore();
                    }
                } else if (state == PULL) {//如果是在下拉状态，不刷新数据
                    state = NONE;
                }
                if (canRefreshEnabled) {
                    refreshViewByState();
                }
                if (canLoadMoreEnabled) {
                    loadViewByState();
                }
                canLoadMoreEnabled = false;
                canRefreshEnabled = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程中的操作
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        int tempY = (int) ev.getY();
        int refreshSpace = (int) (tempY - startY);//向下移动的距离
        int topPadding = refreshSpace - headerHeight;//在移动过程中不断设置 topPadding
        int loadSpace = (int) (lastY - tempY);//向上移动的距离
        int bottomPadding = loadSpace - footerHeight;//在移动过程中不断设置 bottomPadding
        switch (state) {
            case NONE:
                //下拉移动距离大于0
                if (refreshSpace > 0) {
                    state = PULL; //状态变成下拉状态
                    refreshViewByState();
                }
                //上拉移动距离大于0
                if (loadSpace > 0) {
                    state = PULL;//状态变成下拉状态
                    loadViewByState();
                }
                break;
            case PULL:
                if (canRefreshEnabled) {
                    topPadding(topPadding);//在移动过程中不断设置 topPadding，让 Header 随着下拉动作慢慢显示
                }
                if (canLoadMoreEnabled) {
                    bottomPadding(bottomPadding);//在移动过程中不断设置 bottomPadding，让 Footer 随着上拉动作慢慢显示
                }
                //移动距离大于headerHeight并且正在滚动
                if (canRefreshEnabled && refreshSpace > (headerHeight + 30) && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELEASE;//提示释放
                    refreshViewByState();
                }
                //移动距离大于footerHeight并且正在滚动
                if (canLoadMoreEnabled && loadSpace > footerHeight + 30 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELEASE;//提示释放
                    loadViewByState();//刷新footer布局
                }
                break;
            case RELEASE:
                if (canRefreshEnabled) {
                    topPadding(topPadding);
                    //移动距离小于headerHeight
                    if (refreshSpace < headerHeight + 30) {
                        state = PULL;//提示下拉
                    } else if (refreshSpace <= 0) {
                        state = NONE;
                    }
                    refreshViewByState();//更新header
                }
                if (canLoadMoreEnabled) {
                    bottomPadding(bottomPadding);
                    //移动距离小于footerHeight
                    if (loadSpace < footerHeight + 30) {
                        state = PULL;//提示下拉
                    } else if (loadSpace <= 0) {
                        state = NONE;
                    }
                    loadViewByState();//更新footer
                }
                break;
        }
    }

    private void loadViewByState() {
        TextView tip = footer.findViewById(R.id.tv_tip);
        ImageView arrow = footer.findViewById(R.id.img_arrow);
        ProgressBar progressBar = footer.findViewById(R.id.progress);
        progressBar.setBackgroundResource(R.drawable.custom_progress_bar);
        AnimationDrawable animationDrawable = (AnimationDrawable) progressBar.getBackground();
        //给箭头设置动画
        RotateAnimation anim = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        anim.setFillAfter(true);
        anim1.setDuration(200);
        anim1.setFillAfter(true);
        switch (state) {
            case NONE://正常，footer不显示
                bottomPadding(-footerHeight);
                arrow.clearAnimation();
                break;
            case PULL://下拉状态
                arrow.setVisibility(VISIBLE);//箭头显示，进度条隐藏
                progressBar.setVisibility(GONE);
                if (animationDrawable.isRunning()) {
                    animationDrawable.stop();
                }
                tip.setText("上拉可以加载");
                arrow.clearAnimation();
                arrow.setAnimation(anim);//箭头向下
                break;
            case RELEASE://释放状态
                arrow.setVisibility(VISIBLE);//箭头显示，进度条隐藏
                progressBar.setVisibility(GONE);
                if (animationDrawable.isRunning()) {
                    //停止动画播放
                    animationDrawable.stop();
                }
                tip.setText("松开开始加载");
                arrow.clearAnimation();
                arrow.setAnimation(anim);//箭头向上
                break;
            case REFRESHING://刷新状态
                bottomPadding(50);
                arrow.setVisibility(GONE);//箭头显示，进度条隐藏
                progressBar.setVisibility(VISIBLE);
                animationDrawable.start();
                tip.setText("正在加载...");
                arrow.clearAnimation();
                break;
        }
    }

    private void refreshViewByState() {
        TextView tip = header.findViewById(R.id.tv_tip);
        ImageView arrow = header.findViewById(R.id.img_arrow);
        ProgressBar progressBar = header.findViewById(R.id.progress);
        progressBar.setBackgroundResource(R.drawable.custom_progress_bar);
        AnimationDrawable animationDrawable = (AnimationDrawable) progressBar.getBackground();
        //给箭头设置动画
        RotateAnimation anim = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(200);
        anim.setFillAfter(true);
        anim1.setDuration(200);
        anim1.setFillAfter(true);
        switch (state) {
            case NONE://正常，Header不显示
                topPadding(-headerHeight);
                arrow.clearAnimation();
                break;
            case PULL://下拉状态
                arrow.setVisibility(VISIBLE);//箭头显示，进度条隐藏
                progressBar.setVisibility(GONE);
                if (animationDrawable.isRunning()) {
                    //停止动画播放
                    animationDrawable.stop();
                }
                tip.setText("下拉可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);//箭头向上
                break;
            case RELEASE://释放状态
                arrow.setVisibility(VISIBLE);//箭头显示，进度条隐藏
                progressBar.setVisibility(GONE);
                if (animationDrawable.isRunning()) {
                    //停止动画播放
                    animationDrawable.stop();
                }
                tip.setText("松开可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim);//箭头向下
                break;
            case REFRESHING://刷新状态
                topPadding(50);
                arrow.setVisibility(GONE);//箭头显示，进度条隐藏
                progressBar.setVisibility(VISIBLE);
                animationDrawable.start();
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }
    }

    public void refreshComplete() {
        state = NONE;
        refreshViewByState();
        TextView lastUpdateTime = header.findViewById(R.id.tv_last_update_time);
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        String time = format.format(new Date(System.currentTimeMillis()));
        lastUpdateTime.setText(time);
    }

    public void loadMoreComplete() {
        state = NONE;
        loadViewByState();
        setSelection(getCount());
        TextView lastUpdateTime = footer.findViewById(R.id.tv_tip);
        lastUpdateTime.setText("加载完成");
    }

    public void setIRefreshListener(IRefreshListener iRefreshListener) {
        this.iRefreshListener = iRefreshListener;
    }

    public interface IRefreshListener {
        void onRefresh();
    }

    public void setILoadMoreListener(ILoadMoreListener iLoadMoreListener) {
        this.iLoadMoreListener = iLoadMoreListener;
    }

    public interface ILoadMoreListener {
        void onLoadMore();
    }
}
