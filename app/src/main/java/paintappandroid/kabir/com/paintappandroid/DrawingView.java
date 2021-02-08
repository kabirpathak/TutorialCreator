package paintappandroid.kabir.com.paintappandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingView extends View implements View.OnTouchListener {


    public static int MIN_DOT_SIZE = 5;
    public static int MAX_DOT_SIZE = 100;
    public static int DEFAULT_DOT_SIZE = 10;
    private int mDotSize;

    private final int DEFAULT_COLOR = Color.GREEN;
    private int mDotColor;


    private ArrayList<Path> mPaths;
    private ArrayList<Paint> mPaints;
    private Path mPath;
    private Paint mPaint;

    private float mX, mY, mOldX, mOldY;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i = 0;i < mPaths.size();i++){
            canvas.drawPath(mPaths.get(i), mPaints.get(i));


        }
    }





    public DrawingView(Context context) {
        super(context);
        this.init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    public void init(){
        mDotSize = DEFAULT_DOT_SIZE;
        mDotColor = DEFAULT_COLOR;

        //arrayLists..
        mPaths = new ArrayList<Path>();
        mPaints = new ArrayList<Paint>();

       // mPath = new Path();
        this.addPath(false);
        this.mX = this.mY = this.mOldX = this.mOldY =  (float)0.0;
        this.setOnTouchListener(this);
    }

    private void addPath(Boolean fill){
        mPath = new Path();
        mPaths.add(mPath);
        mPaint = new Paint();
        mPaints.add(mPaint);

        //mPaint = new Paint();
        mPaint.setColor(mDotColor);

        if(!fill){
            mPaint.setStyle(Paint.Style.STROKE);
        }

        mPaint.setStrokeWidth(mDotSize);

    }

    public int getDotSize(){
        return mDotSize;
    }

    public void changeDotSize(int increment){
        this.mDotSize += increment;
        this.mDotSize = Math.max(mDotSize, MIN_DOT_SIZE);
        this.mDotSize = Math.min(mDotSize, MAX_DOT_SIZE);
//the next line has to be removed cause now we're adding directly to arraylist..
        //if we don't remove then the exception occuring is :
        // when I draw the nth path.. the colour and dot size of the (n-1)th path also changes...
        //this.mPaint.setStrokeWidth(mDotSize);
    }

    public int getDotColor(){
        return mDotColor;
    }

    public void setDotColor(int penColor){
        this.mDotColor = penColor;
        //the next line has to be removed cause now we're adding directly to arraylist..
        //if we don't remove then the exception occuring is :
        // when I draw the nth path.. the colour and dot size of the (n-1)th path also changes...
        //this.mPaint.setColor(mDotColor);
    }


    public void reset() {
        this.init();
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mX = event.getX();
        mY = event.getY();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                this.addPath(true);
                this.mPath.addCircle(mX, mY, mDotSize/2, Path.Direction.CW);
                this.addPath(false);
                this.mPath.moveTo(mX, mY);
                break;

            case MotionEvent.ACTION_MOVE:
                this.mPath.lineTo(mX, mY);
                break;

            case MotionEvent.ACTION_UP:
                this.addPath(true);
                if(this.mOldX == mX && this.mOldY == mY)
                {
                    this.mPath.addCircle(mX, mY, mDotSize/2, Path.Direction.CW);
                }
                break;
        }
        this.invalidate();
        mOldX = mX;
        mOldY = mY;
        return true;
    }
}
