package com.lx.mystalecode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.lx.mystalecode.R;

import java.util.regex.Pattern;

/**
 * Created by liuxu on 15-5-28.
 * index for contact list as a side bar.
 * import and modified by liuxu.
 */
public class IndexerBar extends View {

    private static final int BAR_PRESS_COLOR = Color.parseColor("#40000000");
    private static final int TEXT_SELECT_COLOR = Color.parseColor("#3399ff");
    private static final int TEXT_NORMAL_COLOR = Color.parseColor("#565656");
    private static final int TEXT_DISABLE_COLOR = Color.parseColor("#aaaaaa");

    private static final String PATTERN = "^[A-Za-z]+$";

    private static final String HASH_MARK = "#";

    private OnTouchingLetterChangedListener mTouchingChangedListener;

    private String[] mIndexer = {
            "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"
            , "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };

    private int mTextSize;
    private int mChoose = -1;
    private boolean mShowBkg = false;

    private Paint mPaint = new Paint();

    public IndexerBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public IndexerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public IndexerBar(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        mTextSize = getResources().getDimensionPixelOffset(R.dimen.indexer_bar_text_size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mShowBkg) {
            canvas.drawColor(BAR_PRESS_COLOR);
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / mIndexer.length;
        for (int i = 0; i < mIndexer.length; i++) {
            mPaint.setTextSize(mTextSize);
            mPaint.setAntiAlias(true);
            if (i == mChoose) {
                mPaint.setColor(TEXT_SELECT_COLOR);
                mPaint.setFakeBoldText(true);
            } else {
                mPaint.setColor(TEXT_NORMAL_COLOR);
            }
            float xPos = width / 2 - mPaint.measureText(mIndexer[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(mIndexer[i], xPos, yPos, mPaint);
            mPaint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = mChoose;
        final OnTouchingLetterChangedListener listener = mTouchingChangedListener;
        final int characterIndex = (int) (y / getHeight() * mIndexer.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mShowBkg = true;
                if (oldChoose != characterIndex && listener != null) {
                    if (characterIndex >= 0 && characterIndex < mIndexer.length) {
                        listener.onTouchingLetterChanged(mIndexer[characterIndex]);
                        mChoose = characterIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != characterIndex && listener != null) {
                    if (characterIndex >= 0 && characterIndex < mIndexer.length) {
                        listener.onTouchingLetterChanged(mIndexer[characterIndex]);
                        mChoose = characterIndex;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (listener != null) {
                    listener.onTouchUp();
                }
                mShowBkg = false;
                mChoose = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.mTouchingChangedListener = onTouchingLetterChangedListener;
    }

    public static String getCurrentLetter(String str) {
        if (str == null) {
            return HASH_MARK;
        }

        if (str.trim().length() == 0) {
            return HASH_MARK;
        }

        String firstChar = str.trim().substring(0, 1);
        Pattern pattern = Pattern.compile(PATTERN);
        if (pattern.matcher(firstChar).matches()) {
            return firstChar.toUpperCase();
        } else {
            return HASH_MARK;
        }
    }


    public interface OnTouchingLetterChangedListener {

        public void onTouchingLetterChanged(String s);

        public void onTouchUp();
    }

}
