package com.aliyouyouzi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import com.yus.taobaoui.R;
import com.yus.taobaoui.util.UIUtils;

import java.util.List;

/**
 * Created by recker on 16/4/17.
 */
public class SmoothViewPager extends RelativeLayout {

    private static final int RMP = LayoutParams.MATCH_PARENT;
    private static final int RWP = LayoutParams.WRAP_CONTENT;
    private static final int LWC = 20;
    private static final int WHAT_AUTO_PLAY = 1000;
    //Pointλ��
    public static final int CENTER = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    /**
     * ָʾ���������������ˮƽ�����Բ���
     */
    private LinearLayout mPointRealContainerLl;

    private ViewPager mViewPager;
    //����ͼƬ��Դ
    private List<Integer> mImages;
    //����ͼƬ��Դ
    private List<String> mImageUrls;
    //�Ƿ�������ͼƬ
    private boolean mIsImageUrl = false;
    //�Ƿ�ֻ��һ��ͼƬ
    private boolean mIsOneImg = false;
    //�Ƿ�����Զ�����
    private boolean mAutoPlayAble = true;
    //�Ƿ����ڲ���
    private boolean mIsAutoPlaying = false;
    //�Զ�����ʱ��
    private int mAutoPalyTime = 5000;
    //��ǰҳ��λ��
    private int mCurrentPositon;
    //ָʾ��λ��
    private int mPointPosition = CENTER;
    //ָʾ����Դ
    private int mPointDrawableResId = R.drawable.selector_bgabanner_point; //��Ҫ�Լ���
    //ָʾ��������
    private Drawable mPointContainerBackgroundDrawable;
    //ָʾ�������ֹ���
    private LayoutParams mPointRealContainerLp;
    //��ʾ��
    private TextView mTips;

    private List<String> mTipsDatas;
    //ָʾ���Ƿ�ɼ�
    private boolean mPointsIsVisible = true;


    private Handler mAutoPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mCurrentPositon++;
            mViewPager.setCurrentItem(mCurrentPositon);
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPalyTime);
        }
    };

    public FlyBanner(Context context) {
        this(context, null);
    }

    public FlyBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FlyBanner);

        mPointsIsVisible = a.getBoolean(R.styleable.FlyBanner_points_visibility, true);
        mPointPosition = a.getInt(R.styleable.FlyBanner_points_position, CENTER);
        mPointContainerBackgroundDrawable
                = a.getDrawable(R.styleable.FlyBanner_points_container_background);

        a.recycle();

        setLayout(context);
    }

    private void setLayout(Context context) {
        //�ر�view��OverScroll
        setOverScrollMode(OVER_SCROLL_NEVER);
        //����ָʾ������
        if (mPointContainerBackgroundDrawable == null) {
            mPointContainerBackgroundDrawable = new ColorDrawable(Color.parseColor("#00aaaaaa"));
        }
        //���ViewPager
        mViewPager = new ViewPager(context);
        addView(mViewPager, new LayoutParams(RMP, RMP));

        //����ָʾ����������
        RelativeLayout pointContainerRl = new RelativeLayout(context);
        if (Build.VERSION.SDK_INT >= 16) {
            pointContainerRl.setBackground(mPointContainerBackgroundDrawable);
        } else {
            pointContainerRl.setBackgroundDrawable(mPointContainerBackgroundDrawable);
        }
        //�����ڱ߾�
        pointContainerRl.setPadding(20, 10, 40, 10);
        //�趨ָʾ���������ּ�λ��
        LayoutParams pointContainerLp = new LayoutParams(RMP, RWP);
        pointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(pointContainerRl, pointContainerLp);
        //����ָʾ������
        mPointRealContainerLl = new LinearLayout(context);
        mPointRealContainerLl.setOrientation(LinearLayout.HORIZONTAL);
        mPointRealContainerLp = new LayoutParams(RWP, RWP);
        mPointRealContainerLp.bottomMargin = UIUtils.dip2px(7);
        pointContainerRl.addView(mPointRealContainerLl, mPointRealContainerLp);
        //����ָʾ�������Ƿ�ɼ�
        if (mPointRealContainerLl != null) {
            if (mPointsIsVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
        //����ָʾ������λ��
        if (mPointPosition == CENTER) {
            mPointRealContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (mPointPosition == LEFT) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (mPointPosition == RIGHT) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    /**
     * ���ñ���ͼƬ
     *
     * @param images
     */
    public void setImages(List<Integer> images) {
        //���ر���ͼƬ
        mIsImageUrl = false;
        this.mImages = images;
        if (images.size() <= 1)
            mIsOneImg = true;
        //��ʼ��ViewPager
        initViewPager();
    }

    /**
     * ��������ͼƬ
     *
     * @param urls
     */
    public void setImagesUrl(List<String> urls) {
        //��������ͼƬ
        mIsImageUrl = true;
        this.mImageUrls = urls;
        if (urls.size() <= 1)
            mIsOneImg = true;
        //��ʼ��ViewPager
        initViewPager();
    }

    /**
     * ����ָʾ���Ƿ�ɼ�
     *
     * @param isVisible
     */
    public void setPointsIsVisible(boolean isVisible) {
        if (mPointRealContainerLl != null) {
            if (isVisible) {
                mPointRealContainerLl.setVisibility(View.VISIBLE);
            } else {
                mPointRealContainerLl.setVisibility(View.GONE);
            }
        }
    }

    /**
     * ��Ӧ����λ�� CENTER,RIGHT,LEFT
     *
     * @param position
     */
    public void setPoinstPosition(int position) {
        //����ָʾ������λ��
        if (position == CENTER) {
            mPointRealContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (position == LEFT) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (position == RIGHT) {
            mPointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    private void initViewPager() {
        //��ͼƬ����1��ʱ���ָʾ��
        if (!mIsOneImg) {
            addPoints();
        }
        //����ViewPager
        FlyPageAdapter adapter = new FlyPageAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        //��ת����ҳ
        mViewPager.setCurrentItem(1, false);
        //��ͼƬ����1��ʱ��ʼ�ֲ�
        if (!mIsOneImg) {
            startAutoPlay();
        }
    }


    /**
     * ������ʵ��λ��
     *
     * @param position
     * @return
     */
    private int toRealPosition(int position) {
        int realPosition;
        if (mIsImageUrl) {
            realPosition = (position - 1) % mImageUrls.size();
            if (realPosition < 0)
                realPosition += mImageUrls.size();
        } else {
            realPosition = (position - 1) % mImages.size();
            if (realPosition < 0)
                realPosition += mImages.size();
        }

        return realPosition;
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mIsImageUrl) {
                mCurrentPositon = position % (mImageUrls.size() + 2);
            } else {
                mCurrentPositon = position % (mImages.size() + 2);
            }
            switchToPoint(toRealPosition(mCurrentPositon));
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                int current = mViewPager.getCurrentItem();
                int lastReal = mViewPager.getAdapter().getCount() - 2;
                if (current == 0) {
                    mViewPager.setCurrentItem(lastReal, false);
                } else if (current == lastReal + 1) {
                    mViewPager.setCurrentItem(1, false);
                }
            }
        }
    };

    private class FlyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //��ֻ��һ��ͼƬʱ����1
            if (mIsOneImg) {
                return 1;
            }
            //��Ϊ����ͼƬ��������ҳͼƬ����
            if (mIsImageUrl)
                return mImageUrls.size() + 2;
            //��Ϊ����ͼƬ�����ر���ͼƬ����
            return mImages.size() + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(toRealPosition(position));
                    }
                }
            });
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (mIsImageUrl) {
				//���Ի�������ͼƬ���ص������ؼ�
                Picasso.with(getContext())
                        .load(mImageUrls.get(toRealPosition(position)))
                        .into(imageView); 
            } else {
                imageView.setImageResource(mImages.get(toRealPosition(position)));
            }
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            if (object != null)
                object = null;
        }
    }


    /**
     * ���ָʾ��
     */
    private void addPoints() {
        mPointRealContainerLl.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
        lp.setMargins(10, 10, 10, 10);
        ImageView imageView;
        int length = mIsImageUrl ? mImageUrls.size() : mImages.size();
        for (int i = 0; i < length; i++) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(lp);
            imageView.setImageResource(mPointDrawableResId);
            mPointRealContainerLl.addView(imageView);
        }

        switchToPoint(0);
    }

    /**
     * �л�ָʾ��
     *
     * @param currentPoint
     */
    private void switchToPoint(final int currentPoint) {
        for (int i = 0; i < mPointRealContainerLl.getChildCount(); i++) {
            mPointRealContainerLl.getChildAt(i).setEnabled(false);
        }
        mPointRealContainerLl.getChildAt(currentPoint).setEnabled(true);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mAutoPlayAble && !mIsOneImg) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoPlay();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    startAutoPlay();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * ��ʼ����
     */
    public void startAutoPlay() {
        if (mAutoPlayAble && !mIsAutoPlaying) {
            mIsAutoPlaying = true;
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPalyTime);
        }
    }

    /**
     * ֹͣ����
     */
    public void stopAutoPlay() {
        if (mAutoPlayAble && mIsAutoPlaying) {
            mIsAutoPlaying = false;
            mAutoPlayHandler.removeMessages(WHAT_AUTO_PLAY);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
