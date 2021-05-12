package com.example.drive.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.drive.R;
import com.example.drive.databinding.ProgressButtonBinding;

/*
public class ProgressButton extends RelativeLayout {

    private ProgrssButtonBinding binding;

    private Context mContext;

    private RelativeLayout parent;
    private ImageButton btn_Upload;
    private TextView tv_Image_Counter;
    private View pg_Uploading;

    int count = 0;

    private static final int STATE_NORMAL = 0;
    private static final int STATE_PROGRESS = 1;

    private int currentState = STATE_NORMAL;

    public ProgressButton(Context context) {
        super(context);
        initView(context);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        binding = ProgrssButtonBinding.inflate(inflater, null, false);

        View root = LayoutInflater.from(mContext).inflate(R.layout.progrss_button, this, false);

        parent = root.findViewById(R.id.parent);
        btn_Upload = root.findViewById(R.id.button);
        tv_Image_Counter = root.findViewById(R.id.tv_counter);
        pg_Uploading = root.findViewById(R.id.pg_uploading);
    }

    public ProgressButton onStateNormal() {
        this.currentState = STATE_NORMAL;
//        binding.button.setVisibility(VISIBLE);
//        binding.tvCounter.setVisibility(VISIBLE);
//        binding.tvCounter.setText("" + count);
//        binding.pgUploading.setVisibility(GONE);

        btn_Upload.setVisibility(VISIBLE);
        tv_Image_Counter.setVisibility(View.VISIBLE);
        tv_Image_Counter.setText("" + count);
        pg_Uploading.setVisibility(View.GONE);
        return this;
    }

    public ProgressButton onStateProgress() {
        this.currentState = STATE_PROGRESS;

//        binding.button.setVisibility(View.GONE);
//        binding.tvCounter.setVisibility(View.GONE);
//        binding.pgUploading.setVisibility(View.VISIBLE);

        btn_Upload.setVisibility(GONE);
        tv_Image_Counter.setVisibility(View.GONE);
        pg_Uploading.setVisibility(View.VISIBLE);
        return this;
    }

    public ProgressButton setCounter(int count) {
        this.count = count;
//        binding.tvCounter.setText("" + count);
        tv_Image_Counter.setText("" + count);

        return this;
    }
}
 */

public class ProgressButton implements View.OnClickListener {

    private TextView tv_Counter;
    private ImageButton btn_Upload;
    private ProgressBar pg_Upload;

    private OnButtonClickListener mListener;

    public ProgressButton(Context context, View view) {
        this.tv_Counter = view.findViewById(R.id.tv_counter);
        this.btn_Upload = view.findViewById(R.id.button);
        this.pg_Upload = view.findViewById(R.id.progressbar);

        Drawable drawable = context.getResources().getDrawable(R.drawable.bg_progressbar);
        pg_Upload.setProgressDrawable(drawable);

        btn_Upload.setOnClickListener(this);
    }

    public void onLoading() {
        btn_Upload.setVisibility(View.GONE);
        tv_Counter.setVisibility(View.GONE);
        pg_Upload.setVisibility(View.VISIBLE);
    }

    public void setCounter(int count) {
        tv_Counter.setText("" + count);
    }

    public void setListener(OnButtonClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                onLoading();
                mListener.onClick();
        }
    }

    public interface OnButtonClickListener {
        void onClick();
    }
}