/*
 *
 *  * Copyright © 2018 Pandabus Ltd., All Rights Reserved.
 *  * For licensing terms please contact Pandabus LTD.
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package androiduseasserver.administrator.slidingbuttonlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;



/**
 * Created by zy on 2018/3/1.
 * Describe 横向选择按钮view
 */

public class SlidingButton extends LinearLayout {
    private int backGroundIcon;
    private int normalTextColor;
    private int selTextColor;
    private int selectBackGroundIcon;
    private int buttonTextSize;
    private String[] btnTexts;
    private ButtonItemClickListener buttonItemClickListener;
    private int nowSelectIndex;//当前选中状态
    private ArrayList<TextView> allItemTextViews = new ArrayList<>();
    private ArrayList<LinearLayout> allISelBgViews = new ArrayList<>();
    LinearLayout ll_bg_all;
    LinearLayout animalLayout;
    TextView selTv;
    String selText;

    private View animalItem;//要进行动画变换的view对象
    private ArrayList<View> animalViewsList = new ArrayList<>(); //展示动画的item
    private Handler mHandler = new Handler();

    public SlidingButton(Context context) {
        this(context, null);
        init();
    }

    public SlidingButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public SlidingButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SlidingButton, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SlidingButton_backGroundIcon) {//默认背景
                backGroundIcon = a.getResourceId(attr, -1);

            } else if (attr == R.styleable.SlidingButton_normalTextColor) {// 默认颜色设置为黑色
                normalTextColor = a.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.SlidingButton_selectTextColor) {
                selTextColor = a.getColor(attr, Color.BLACK);

            } else if (attr == R.styleable.SlidingButton_selectBackGroundIcon) {//选中背景
                selectBackGroundIcon = a.getResourceId(attr, -1);

            } else if (attr == R.styleable.SlidingButton_buttonTextSize) {// 默认设置为16sp，TypeValue也可以把sp转化为px
                buttonTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));

            }

        }
        a.recycle();
        //加载布局
        init();
    }

    void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.sliding_button_layout, this, true);
        ll_bg_all = this.findViewById(R.id.ll_bg_all);
        animalLayout = this.findViewById(R.id.ll_bg_all_sel);
    }


    public void build(String[] btnTexts, int nowSelectIndex, ButtonItemClickListener buttonItemClickListener) {
        this.btnTexts = btnTexts;
        this.nowSelectIndex = nowSelectIndex;
        this.buttonItemClickListener = buttonItemClickListener;
        setUI();
    }


    private void setUI() {
        ll_bg_all.removeAllViews();
        allItemTextViews.clear();
        allISelBgViews.clear();
        addTextViews();
        addAnimalViews();
    }

    //添加个item
    private void addTextViews() {
        int i = 0;
        for (final String s : btnTexts) {
            final View btnItem = LayoutInflater.from(getContext()).inflate(R.layout.slide_button_item_view, this, false);
            //整体背景
            final LinearLayout item_bg = (LinearLayout) btnItem.findViewById(R.id.item_bg);
            //按钮内文字
            final TextView item_txt = (TextView) btnItem.findViewById(R.id.item_txt);
            if (i == nowSelectIndex) {
                selTv = item_txt;
                selText = s;
                item_txt.setTextColor(selTextColor);
            } else {
                item_txt.setTextColor(normalTextColor);
            }
            if (buttonItemClickListener != null) {
                final int finalI = i;
                item_bg.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (nowSelectIndex == finalI) {
                            return;//点击相同按键，不做任何操作
                        }
                        int removeRule = finalI - nowSelectIndex;//移动规则，当前点击的减去当前选中的
                        nowSelectIndex = finalI;
                        startAnimal(animalItem, item_txt, s, removeRule);
                        allBgEnableSet(false);
                    }
                });
            } else {
                Log.e("selecButton", "buttonItemClickListener is null!");
            }
            item_txt.setText(s);
            ll_bg_all.addView(btnItem);
            allItemTextViews.add(item_txt);
            allISelBgViews.add(item_bg);
            i++;
        }
    }

    private void addAnimalViews() {
        //  用于显示动画的层
        animalViewsList.clear();
        int index = 0;
        animalLayout.removeAllViews();
        animalLayout.setBackgroundResource(backGroundIcon);
        for (final String s : btnTexts) {
            final View btnItem = LayoutInflater.from(getContext()).inflate(R.layout.slide_button_item_view, this, false);
            //整体背景
            final LinearLayout animal_bg = (LinearLayout) btnItem.findViewById(R.id.item_bg);
            if (nowSelectIndex == index) {
                animal_bg.setBackgroundResource(selectBackGroundIcon);
                animalItem = btnItem;
            }
            animalViewsList.add(btnItem);
            index++;
            animalLayout.addView(btnItem);
        }
    }

    //设置当前的选中
    public void setNowSelectIndex(int nowSelectIndex) {
        if (this.nowSelectIndex == nowSelectIndex) {
            return;//点击相同按键，不做任何操作
        }
        int removeRule = nowSelectIndex - this.nowSelectIndex;//移动规则，当前点击的减去当前选中的
        this.nowSelectIndex = nowSelectIndex;
        startAnimal(animalItem, selTv, selText, removeRule);
        allBgEnableSet(false);
    }

    //补间动画移动
    private void startAnimal(View view, final TextView textView, final String s, int removeRule) {
        AnimationSet animationSet = new AnimationSet(true);
            /*
                    Animation还有几个方法
                    setFillAfter(boolean fillAfter)
                    如果fillAfter的值为真的话，动画结束后，控件停留在执行后的状态
                    setFillBefore(boolean fillBefore)
                    如果fillBefore的值为真的话，动画结束后，控件停留在动画开始的状态
                    setStartOffset(long startOffset)
                    设置动画控件执行动画之前等待的时间
                    setRepeatCount(int repeatCount)
                    设置动画重复执行的次数
             */
        //判断移动规则
        float translateX = removeRule;
        TranslateAnimation translateAnimation = new TranslateAnimation(
                //X轴初始位置
                Animation.RELATIVE_TO_SELF, 0.0f,
                //X轴移动的结束位置

                Animation.RELATIVE_TO_SELF, translateX,
                //y轴开始位置
                Animation.RELATIVE_TO_SELF, 0.0f,
                //y轴移动后的结束位置
                Animation.RELATIVE_TO_SELF, 0.0f);

        //3秒完成动画
        translateAnimation.setDuration(200);
        //如果fillAfter的值为真的话，动画结束后，控件停留在执行后的状态
        animationSet.setFillAfter(false);
        //将AlphaAnimation这个已经设置好的动画添加到 AnimationSet中
        animationSet.addAnimation(translateAnimation);
        //动画监听
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetItemViews();//重置所有item
                        //动画结束
                        animalItem = animalViewsList.get(nowSelectIndex);//下一次点击需要进行移动的view
                        textView.setTextColor(selTextColor);
                        allBgEnableSet(true);
                        animalItem.findViewById(R.id.item_bg).setBackgroundResource(selectBackGroundIcon);
                        buttonItemClickListener.buttonItemClickListener(s);//回调
                    }
                }, 10);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //启动动画
        view.startAnimation(animationSet);
    }


    private void resetItemViews() {
        for (TextView textView : allItemTextViews) {
            textView.setTextColor(normalTextColor);
        }

        for (View view : animalViewsList) {
            view.findViewById(R.id.item_bg).setBackgroundResource(0);
        }
    }

    private void allBgEnableSet(boolean enable){
        for (LinearLayout textView: allISelBgViews){
            textView.setEnabled(enable);
        }
    }

    public interface ButtonItemClickListener {
        void buttonItemClickListener(String text);
    }

}
