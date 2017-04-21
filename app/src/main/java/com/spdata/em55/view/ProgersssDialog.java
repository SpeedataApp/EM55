package com.spdata.em55.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.spdata.em55.R;

public class ProgersssDialog extends Dialog {
    private Context context;
    private ImageView img;
    private TextView txt;

    public ProgersssDialog(Context context) {
        super(context, R.style.progress_dialog);
        this.context = context;
        //加载布局文件
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progress_dialog, null);
        img = (ImageView) view.findViewById(R.id.progress_dialog_img);
        txt = (TextView) view.findViewById(R.id.progress_dialog_txt);
        //给图片添加动态效果
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.loading_dialog_progressbar);
        img.setAnimation(anim);
        txt.setText("初始化…");
        //dialog添加视图
        setContentView(view);
    }

    public void setMsg(String msg) {
        txt.setText(msg);
    }

    public void setMsg(int msgId) {
        txt.setText(msgId);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            if (isShowing()) {
                super.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}