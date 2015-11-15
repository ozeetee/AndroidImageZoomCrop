package io.togoto.imagezoomcrop.photoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * A SeekBar whose purpose is to represent a rotation 360° spectrum. Its center is 0° and its
 * extremes represent 180° rotations, and the "progress" can be directly set in degrees.
 * <p/>
 * Usage:
 * - Do NOT call {@link #setMax(int)}
 * - Use {@link #setRotationProgress(float)} instead of {@link #setProgress(int)}
 * - Use {@link OnRotationSeekBarChangeListener} instead of {@link android.widget.SeekBar.OnSeekBarChangeListener}
 *
 * @author marcosalis
 */
public class RotationSeekBar extends SeekBar {

    // degree values are multiplied by 10 to improve smoothness
    private static final int DEFAULT_MAX = 3600;
    private static final int DEFAULT_PROGRESS = 1800;

    private OnRotationSeekBarChangeListener mRotationListener;

    public RotationSeekBar(Context context) {
        super(context);
        init();
    }

    public RotationSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotationSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RotationSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setMax(DEFAULT_MAX);
        setProgress(DEFAULT_PROGRESS);
    }

    @Override
    public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
        if (l != null && !(l instanceof OnRotationSeekBarChangeListener)) {
            throw new IllegalArgumentException("Use OnRotationSeekBarChangeListener");
        }
        mRotationListener = (OnRotationSeekBarChangeListener) l;
        super.setOnSeekBarChangeListener(l);
    }

    public void setRotationProgress(float rotation) {
        if (rotation < -180f || rotation > 180f) {
            throw new IllegalArgumentException("Invalid rotation value");
        }
        if (rotation == 0f) {
            reset();
        } else {
            setProgress(fromDegreesToProgress(rotation));
        }
    }

    public float getRotationProgress() {
        return fromProgressToDegrees(getProgress());
    }

    public void reset() {
        init();
        mRotationListener.resetPreviousProgress();
    }

    private static float fromProgressToDegrees(int progress) {
        return (progress - DEFAULT_PROGRESS) / 10f;
    }

    private static int fromDegreesToProgress(float degrees) {
        return (int) ((degrees + 180f) * 10f);
    }

    public static abstract class OnRotationSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        private float mPreviousProgress;

        public OnRotationSeekBarChangeListener(@NonNull RotationSeekBar seekBar) {
            mPreviousProgress = seekBar.getProgress();
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            final float angle = fromProgressToDegrees(progress);
            final float delta = (progress - mPreviousProgress) / 10f;
            onRotationProgressChanged((RotationSeekBar) seekBar, angle, delta, fromUser);
            mPreviousProgress = progress;
        }

        void resetPreviousProgress() {
            mPreviousProgress = DEFAULT_PROGRESS;
        }

        /**
         * Notification that the rotation progress level has changed.
         *
         * @param seekBar  The SeekBar whose progress has changed
         * @param angle    The current SeekBar angle
         * @param delta    The difference in degrees from the previous call
         * @param fromUser True if the progress change was initiated by the user
         */
        public abstract void onRotationProgressChanged(
                @NonNull RotationSeekBar seekBar, float angle, float delta, boolean fromUser);

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

}