package kasper.poc.meterapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class MeterView  extends View {

    private LabelConverter labelConverter;
    private double majorTickStep = 25;
    private int minorTicks = 0;
    private Paint paint = new Paint();
    private int temperature_progress1;
    private String temp_unit = "°C/m";

    Paint paintBackgroundMeter = new Paint();
    Paint paintProgressMeter = new Paint();
    Paint paintInnerMeter = new Paint();
    Paint paintTextMeter = new Paint();
    Paint paintProgress = new Paint();

    public MeterView(Context context) {
        super(context);
        init(null, 0);
    }

    public MeterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public MeterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyle) {

        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT);

        paint.setAntiAlias(true);
        paintBackgroundMeter.setAntiAlias(true);
        paintInnerMeter.setAntiAlias(true);
        paintProgressMeter.setAntiAlias(true);
        paintTextMeter.setAntiAlias(true);
        paintProgress.setAntiAlias(true);

        drawTicks(canvas);

        paintBackgroundMeter.setColor(Color.parseColor("#C8CCD2"));
        RectF rectF = getOval(canvas, 1);
        canvas.drawArc(rectF, 180, 180, true, paintBackgroundMeter);

        paintProgressMeter.setAntiAlias(true);
        paintProgressMeter.setStyle(Paint.Style.FILL);
        RectF progress = getOval(canvas, 1f);
        paintProgressMeter.setShader(new LinearGradient(20 ,0 ,progress.width(), progress.height(), Color.parseColor("#1F6FC2"), Color.parseColor("#519EED"), Shader.TileMode.CLAMP));
        canvas.drawArc(progress, 180, (temperature_progress1) * 1.8f, true, paintProgressMeter);

        paintInnerMeter.setColor(Color.parseColor("#232E42"));
        RectF rectF2 = getOval(canvas, .8f);
        canvas.drawArc(rectF2, 180, 180, true, paintInnerMeter);

        drawProgressVal(canvas);

    }

    private void drawProgressVal(Canvas canvas_text) {

        final int canvasWidth = canvas_text.getWidth() - getPaddingLeft() - getPaddingRight();
        final int canvasHeight = canvas_text.getHeight() - getPaddingTop() - getPaddingBottom();
        RectF oval = getOval(canvas_text, 1f);
        paintProgress.setColor(Color.WHITE);
        paintProgress.setTextSize(30);
        paintProgress.setFakeBoldText(true);
        paintProgress.setAntiAlias(true);
        paintProgress.setTextAlign(Paint.Align.CENTER);
        canvas_text.drawText(temperature_progress1 + temp_unit, (canvasWidth /2), oval.centerX() - 30, paintProgress);
    }

    private RectF getOval(Canvas canvas, float factor) {

        RectF oval;
        final int canvasWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        final int canvasHeight = canvas.getHeight() - getPaddingTop() - getPaddingBottom();

        if (canvasHeight*2 >= canvasWidth) {
            oval = new RectF(0, 0, canvasWidth*factor - 200, canvasWidth*factor - 200);
        } else {
            oval = new RectF(0, 0, canvasHeight*2*factor - 200, canvasHeight*2*factor - 200);
        }

        oval.offset((canvasWidth-oval.width())/2 + getPaddingLeft(), (canvasHeight*2-oval.height())/2 + getPaddingTop() - 80);

        return oval;
    }


    private void drawTicks(Canvas canvas) {
        float availableAngle = 160;
        float majorStep = (float) (18);
        float minorStep = majorStep / (1 + minorTicks);

        float majorTicksLength = 20;
        float minorTicksLength = majorTicksLength / 1.5f;

        RectF oval = getOval(canvas, 1f);
        Log.d("TAG", "drawTicks: oval" + oval.width());
        float radius = (oval.width() /2f);

        paintTextMeter.setStrokeWidth(3);
        paintTextMeter.setColor(Color.WHITE);
        paintTextMeter.setAntiAlias(true);

        float currentAngle = 0;
        double curProgress = 0;
        int progress_lable= 0;
        while (currentAngle <= 180) {

            canvas.drawLine(
                    (float) (oval.centerX() + Math.cos((180 - currentAngle) / 180 * Math.PI) * (radius - majorTicksLength / 2)),
                    (float) (oval.centerY() - Math.sin(currentAngle / 180 * Math.PI) * (radius - majorTicksLength / 2)),
                    (float) (oval.centerX() + Math.cos((180 - currentAngle) / 180 * Math.PI) * (radius + majorTicksLength / 2)),
                    (float) (oval.centerY() - Math.sin(currentAngle / 180 * Math.PI) * (radius + majorTicksLength / 2)),
                    paintTextMeter);

            paintTextMeter.setColor(Color.WHITE);
            paintTextMeter.setTextSize(20);
            paintTextMeter.setTextAlign(Paint.Align.CENTER);

                canvas.save();
                canvas.rotate(180 + currentAngle, oval.centerX(), oval.centerY());
                float txtX = oval.centerX() + radius + majorTicksLength / 2 + 7;
                float txtY = oval.centerY();
                canvas.rotate(+90, txtX, txtY);
                canvas.drawText(String.valueOf(progress_lable), txtX, txtY - 10, paintTextMeter);
                canvas.restore();

            currentAngle += majorStep;
            curProgress += majorTickStep;

            progress_lable = progress_lable+10;
        }

    }

    public static interface LabelConverter {

        String getLabelFor(double progress, double maxProgress);

    }

    public LabelConverter getLabelConverter() {
        return labelConverter;
    }

    public void setLabelConverter(LabelConverter labelConverter) {
        this.labelConverter = labelConverter;
        invalidate();
    }


    public void setProgressVal(int temperature_progress){
       // this.temperature_progress =temperature_progress;
        startAnimatingArc(temperature_progress);
    }

    public void startAnimatingArc(final float maxAngle) {

        if (maxAngle <= 100 && maxAngle > 0) {

            // temperature_progress1 = 0;

            if (temperature_progress1 > maxAngle) {

                new Thread(new Runnable() {
                    public void run() {
                        while (temperature_progress1 >= maxAngle) {
                            invalidate();
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            temperature_progress1--;
                        }
                    }
                }).start();
            } else {
                new Thread(new Runnable() {
                    public void run() {
                        while (temperature_progress1 <= maxAngle) {
                            invalidate();
                            try {
                                Thread.sleep(80);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            temperature_progress1++;
                        }
                    }
                }).start();
            }
        }
        else {

        }

    }

    public void setTempUnit(String temp_unit){
        this.temp_unit = temp_unit;
    }

}
