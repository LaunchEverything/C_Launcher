package com.example.carlauncher.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import com.example.carlauncher.R;

/**
 * Created by Administrator on 2017/4/23.
 */

public class ReflectView extends AppCompatImageView {

    private View mCarView;

    private int mReflectWidth;
    private int mReflectHeight;

    private Bitmap mReflectBitmap;
    private Canvas mReflectCanvas;

    public ReflectView(Context context) {
        this(context, null, 0);
    }

    public ReflectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReflectView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mReflectWidth = getResources().getDimensionPixelSize(R.dimen.item_width);
        mReflectHeight = getResources().getDimensionPixelSize(R.dimen.car_height);
        mReflectBitmap = Bitmap.createBitmap(mReflectWidth, mReflectHeight, Bitmap.Config.ARGB_8888);
        mReflectCanvas = new Canvas();
        mReflectCanvas.setBitmap(mReflectBitmap);
        mReflectCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
    }

    public void setCarView(View view) {
        mCarView = view;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCarView.draw(mReflectCanvas);
        Bitmap bitmap = getReflectBitmap(mReflectBitmap, true);
        canvas.drawBitmap(bitmap, 0, 0, null);
        super.onDraw(canvas);
    }

    private Bitmap getReflectBitmap(Bitmap bitmap, boolean reflected) {
        if (reflected) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // This will not scale but will flip on the Y axis
            Matrix matrix = new Matrix();
            matrix.preScale(1, -1);

            // Create a Bitmap with the flip matrix applied to it.
            // We only want the bottom half of the image
            Bitmap reflectionBitmap = Bitmap.createBitmap(bitmap, 0,
                    height / 2, width, height / 2, matrix, false);

            // Create a new Canvas with the bitmap that's big enough for
            // the image plus gap plus reflection
            Canvas canvas = new Canvas(reflectionBitmap);
//            // Draw in the original image
//            canvas.drawBitmap(originalBitmap, 0, 0, null);

            // Create a shader that is a linear gradient that covers the
            // reflection
            Paint paint = new Paint();
            LinearGradient shader = new LinearGradient(0, 0,
                    0, reflectionBitmap.getHeight(),
                    0x70ffffff, 0x00ffffff, Shader.TileMode.CLAMP);
            // Set the paint to use this shader (linear gradient)
            paint.setShader(shader);
            // Set the Transfer mode to be porter duff and destination in
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            // Draw a rectangle using the paint with our linear gradient
            canvas.drawRect(0, 0, width,
                    reflectionBitmap.getHeight(), paint);

            bitmap = reflectionBitmap;
        }

        return bitmap;
    }
}
