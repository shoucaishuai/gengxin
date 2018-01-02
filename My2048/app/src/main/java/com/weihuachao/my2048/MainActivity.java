package com.weihuachao.my2048;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private GridLayout borad;
    private int columcount = 4;
    private Item[][] items;
    private int itemwidth;
    private int divider;
    private int margin;
    private List<Point> points;
    private float downX;
    private float downY;
    private int maxScore = 0;
    private TextView cur_textview;
    private TextView max_textview;
    private Button button_restart;
    private Button button_col;
    private final String level_4 = "level_4";
    private final String level_5 = "level_5";
    private final String level_6 = "level_6";
    private String level;
    private Animation animScale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        borad = (GridLayout) findViewById(R.id.main_game_board);
        cur_textview = (TextView) findViewById(R.id.main_curr_max);
        max_textview = (TextView) findViewById(R.id.main_history_max);
        animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        button_restart = (Button) findViewById(R.id.main_restart);
        button_restart.setOnClickListener(clickListenerFirst);
        button_col = (Button) findViewById(R.id.main_select);
        button_col.setOnClickListener(clickListenerFirst);
        initnum();
        selcetitem();
    }

    protected void onDestroy() {
        super.onDestroy();
        saveScore();
    }

    //初始化数值
    private void initnum() {
        divider = getResources().getDimensionPixelSize(R.dimen.divider);
        margin = getResources().getDimensionPixelSize(R.dimen.activity_margin);
        points = new LinkedList<>();
    }

    //重新开始游戏，选择难度按钮事件
    private View.OnClickListener clickListenerFirst = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_restart:
                    restart();
                    break;
                case R.id.main_select:
                    selcetitem();
                    restart();
                    break;
            }
        }
    };

    //选择难度
    private void selcetitem() {
        new AlertDialog.Builder(this).setItems(new String[]{"4X4", "5X5", "6X6"}, dialogClickLis).show().setCanceledOnTouchOutside(false);
    }

    //初始化游戏
    private void initGame() {
        points.clear();
        borad.removeAllViews();
        int screenwidth = getResources().getDisplayMetrics().widthPixels;
        itemwidth = (screenwidth - 2 * margin - (columcount + 1) * divider) / columcount;
        max_textview.setText("最高分：" + String.valueOf(getScore()));
        borad.setColumnCount(columcount);
        items = new Item[columcount][columcount];
        for (int i = 0; i < columcount; ++i)
            for (int j = 0; j < columcount; ++j) {
                items[i][j] = new Item(this);
                items[i][j].setNum(0);
                items[i][j].set(i, j);
                items[i][j].setSize(itemwidth, divider, columcount);
                borad.addView(items[i][j]);
            }
        addNum(2);
    }

    //检查是否有空格
    private void checkNull() {
        points.clear();
        for (int i = 0; i < columcount; ++i) {
            for (int j = 0; j < columcount; ++j) {
                if (items[i][j].getNum() == 0) {
                    points.add(items[i][j].getPoint());
                }
            }
        }
    }

    //创建随机数
    private void createNum() {
        int index = (int) (Math.random() * points.size());
        Point point = points.get(index);
        int num = Math.random() > 0.2 ? 2 : 4;
        items[point.x][point.y].setNum(num);
        items[point.x][point.y].startAnimation(animScale);
        points.remove(index);
    }

    //添加随机数方格
    private void addNum(int count) {
        checkNull();
        for (int i = 0; i < count; ++i) {
            if (points.size() > 0) {
                createNum();
            }
        }
    }

    //进行方向移动
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_UP:
                float dx = x - downX;
                float dy = y - downY;
                if (Math.abs(dx) < 10 && Math.abs(dy) < 10) {
                    return super.onTouchEvent(event);
                }
                if (canMove()) {
                    Log.d("Orientation", String.valueOf(getMoveOrientation(dx, dy)));
                    move(getMoveOrientation(dx, dy));
                    addNum(1);
                } else {
                    gameOver();
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    //判断移动方法
    private char getMoveOrientation(float x, float y) {
        if (Math.abs(x) > Math.abs(y)) {
            if (x > 0) {
                return 'r';
            } else {
                return 'l';
            }
        } else {
            if (y > 0) {
                return 'b';
            } else {
                return 'u';
            }
        }
    }

    //是否可以移动
    private boolean canMove() {
        for (int i = 0; i < columcount - 1; ++i) {
            for (int j = 0; j < columcount - 1; ++j) {
                int tmp = items[i][j].getNum();
                int tmp1 = items[i + 1][j].getNum();
                int tmp2 = items[i][j + 1].getNum();
                if (tmp == 0 || tmp1 == 0 || tmp2 == 0 || tmp == tmp1 || tmp == tmp2) {
                    return true;
                }
            }
            if (items[i + 1][columcount - 1].getNum() == 0 || items[i][columcount - 1].getNum() == items[i + 1][columcount - 1].getNum()) {
                return true;
            }
        }
        return false;
    }

    //调用移动方法
    private boolean move(char ori) {
        switch (ori) {
            case 'l':
                return moveToLeft();
            case 'r':
                return moveToRight();
            case 'u':
                return moveToUp();
            case 'b':
                return moveToBottom();
            default:
                return false;
        }
    }

    //向下移
    private boolean moveToBottom() {
        List<Integer> its = new LinkedList<>();
        List<Integer> tmp = new LinkedList<>();
        for (int i = 0; i < columcount; ++i) {
            for (int j = columcount - 1; j >= 0; j--) {
                if (items[j][i].getNum() != 0) {
                    its.add(items[j][i].getNum());
                }
            }
            for (int j = 0; j < its.size(); ++j) {
                if (j + 1 < its.size() && its.get(j).compareTo(its.get(j + 1)) == 0) {
                    tmp.add(its.get(j) + its.get(j + 1));
                    ++j;
                } else {
                    tmp.add(its.get(j));
                }
            }
            int cur = 0;
            for (; cur < columcount - tmp.size(); ++cur) {
                items[cur][i].setNum(0);
            }
            for (int j = cur; j < columcount; ++j) {
                items[j][i].setNum(tmp.get(columcount - 1 - j));
                if (maxScore < tmp.get(columcount - 1 - j)) {
                    maxScore = tmp.get(columcount - 1 - j);
                    cur_textview.setText("当前最高分数" + String.valueOf(maxScore));
                }
            }
            its.clear();
            tmp.clear();
        }
        return false;
    }

    //向上移
    private boolean moveToUp() {
        List<Integer> its = new LinkedList<>();
        List<Integer> tmp = new LinkedList<>();
        for (int i = 0; i < columcount; ++i) {
            for (int j = 0; j < columcount; j++) {
                if (items[j][i].getNum() != 0) {
                    its.add(items[j][i].getNum());
                }
            }
            for (int j = 0; j < its.size(); ++j) {
                if (j + 1 < its.size() && its.get(j).compareTo(its.get(j + 1)) == 0) {
                    tmp.add(its.get(j) + its.get(j + 1));
                    ++j;
                } else {
                    tmp.add(its.get(j));
                }
            }
            for (int j = 0; j < tmp.size(); ++j) {
                items[j][i].setNum(tmp.get(j));
                if (maxScore < tmp.get(j)) {
                    maxScore = tmp.get(j);
                    cur_textview.setText("当前最高分数" + String.valueOf(maxScore));
                }
            }
            for (int j = tmp.size(); j < columcount; ++j) {
                items[j][i].setNum(0);
            }
            its.clear();
            tmp.clear();
        }
        return false;
    }

    //向右移
    private boolean moveToRight() {
        List<Integer> its = new LinkedList<>();
        List<Integer> tmp = new LinkedList<>();
        for (int i = 0; i < columcount; ++i) {
            for (int j = columcount - 1; j >= 0; j--) {
                if (items[i][j].getNum() != 0) {
                    its.add(items[i][j].getNum());
                }
            }
            for (int j = 0; j < its.size(); ++j) {
                if (j + 1 < its.size() && its.get(j).compareTo(its.get(j + 1)) == 0) {
                    tmp.add(its.get(j) + its.get(j + 1));
                    ++j;
                } else {
                    tmp.add(its.get(j));
                }
            }
            for (int j = 0; j < tmp.size(); ++j) {
                items[i][columcount - 1 - j].setNum(tmp.get(j));
                if (maxScore < tmp.get(j)) {
                    maxScore = tmp.get(j);
                    cur_textview.setText("当前最高分数" + String.valueOf(maxScore));
                }
            }
            for (int j = columcount - 1 - tmp.size(); j >= 0; --j) {
                items[i][j].setNum(0);
            }
            its.clear();
            tmp.clear();
        }
        return false;
    }

    //向左移
    private boolean moveToLeft() {
        List<Integer> its = new LinkedList<>();
        List<Integer> tmp = new LinkedList<>();
        for (int i = 0; i < columcount; ++i) {
            for (int j = 0; j < columcount; ++j) {
                if (items[i][j].getNum() != 0) {
                    its.add(items[i][j].getNum());
                }
            }
            for (int j = 0; j < its.size(); ++j) {
                if (j + 1 < its.size() && its.get(j).compareTo(its.get(j + 1)) == 0) {
                    tmp.add(its.get(j) + its.get(j + 1));
                    ++j;
                } else {
                    tmp.add(its.get(j));
                }
            }
            for (int j = 0; j < tmp.size(); ++j) {
                items[i][j].setNum(tmp.get(j));
                if (maxScore < tmp.get(j)) {
                    maxScore = tmp.get(j);
                    cur_textview.setText("当前最高分数" + String.valueOf(maxScore));
                }
            }
            for (int j = tmp.size(); j < columcount; ++j) {
                items[i][j].setNum(0);
            }
            its.clear();
            tmp.clear();
        }
        return false;
    }

    //判断游戏结束
    private void gameOver() {
        String msg = "";
        if (maxScore < 257) {
            msg = "你弱爆了";
        } else if (maxScore < 1025) {
            msg = "";
        } else {
            msg = "你很棒";
        }
        new AlertDialog.Builder(this).setMessage(msg).setNegativeButton("重新开始", dialogClickLis).setNeutralButton("退出游戏", dialogClickLis).show();
        saveScore();
    }

    //重新开始游戏
    private void restart() {
        saveScore();
        max_textview.setText("最高分：" + String.valueOf(getScore()));
        borad.setColumnCount(columcount);
        for (int i = 0; i < columcount; ++i)
            for (int j = 0; j < columcount; ++j) {
                items[i][j].setNum(0);
            }
        addNum(2);
    }

    //选择难度时Click响应事件
    private DialogInterface.OnClickListener dialogClickLis = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case DialogInterface.BUTTON_NEGATIVE:
                    restart();
                    break;
                case DialogInterface.BUTTON_NEUTRAL:
                    break;
                case 0:
                    columcount = 4;
                    level = level_4;
                    initGame();
                    break;
                case 1:
                    columcount = 5;
                    level = level_5;
                    initGame();
                    break;
                case 2:
                    columcount = 6;
                    level = level_6;
                    initGame();
                    break;

            }
        }
    };

    //存分数
    private void saveScore() {
        if (getScore() < maxScore) {
            SharedPreferences.Editor editor = getSharedPreferences("level", MODE_PRIVATE).edit();
            editor.putInt(level, maxScore);
            editor.commit();
        }

    }

    //取分数
    private int getScore() {
        return getSharedPreferences("level", MODE_PRIVATE).getInt(level, 0);
    }
}
