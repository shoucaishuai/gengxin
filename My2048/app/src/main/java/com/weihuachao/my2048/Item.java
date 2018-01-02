package com.weihuachao.my2048;


import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.TextView;

/**
 * Created by Virgo on 2016/7/17.
 */
public class Item extends TextView {
    private int num;
    private int backGroundColor;
    private Point point;
    private GridLayout.LayoutParams lp;

    public Item(Context context){
        super(context);
        lp=new GridLayout.LayoutParams();
        setGravity(Gravity.CENTER);
        setLayoutParams(lp);
    }
    public void setNum(int num){
        this.num=num;
        switch (num){
            case 0:
                backGroundColor=R.color.bg_0;
                break;
            case 2:
                backGroundColor=R.color.bg_2;
                break;
            case 4:
                backGroundColor=R.color.bg_4;
                break;
            case 8:
                backGroundColor=R.color.bg_8;
                break;
            case 16:
                backGroundColor=R.color.bg_16;
                break;
            case 32:
                backGroundColor=R.color.bg_32;
                break;
            case 64:
                backGroundColor=R.color.bg_64;
                break;
            case 128:
                backGroundColor=R.color.bg_128;
                break;
            case 256:
                backGroundColor=R.color.bg_256;
                break;
            case 512:
                backGroundColor=R.color.bg_512;
                break;
            case 1024:
                backGroundColor=R.color.bg_1024;
                break;
            case 2048:
                backGroundColor=R.color.bg_2048;
                break;
            case 4096:
                backGroundColor=R.color.bg_4096;
                break;
            default:
                backGroundColor=R.color.bg_default;
        }
        if (num != 0) setText(String.valueOf(num));
        else {
            setText("");
        }
        setBackgroundResource(backGroundColor);
    }
    public Point getPoint(){
        return this.point;
    }
    public void set(int row,int col){
        if (point == null) {
            point = new Point(row, col);
        } else {
            point.x=row;
            point.y=col;
        }
        lp.rowSpec=GridLayout.spec(row);
        lp.columnSpec=GridLayout.spec(col);
    }
    public void setSize(int size,int divider,int count){
        lp.width=size;
        lp.height=size;
        int tmp= divider/2;
        lp.leftMargin=tmp;
        lp.rightMargin=tmp;
        lp.topMargin=tmp;
        lp.bottomMargin=tmp;
        if (point.x==0) {
            lp.leftMargin=divider;
            lp.rightMargin=tmp;
        } else if (point.x == count - 1) {
            lp.rightMargin = divider;
            lp.leftMargin = tmp;
        }
        if (point.y==0) {
            lp.topMargin=divider;
            lp.bottomMargin=tmp;
        } else if (point.y==count-1) {
            lp.bottomMargin=divider;
            lp.topMargin=tmp;
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX,size/3);
    }
    public int getNum(){
        return num;
    }
}
