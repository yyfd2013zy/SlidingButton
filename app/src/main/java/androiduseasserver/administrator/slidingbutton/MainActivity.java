package androiduseasserver.administrator.slidingbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import androiduseasserver.administrator.slidingbuttonlibrary.SlidingButton;


public class MainActivity extends AppCompatActivity {
    private String[] tests = new String[]{"实时","预约","全部"};
    private String[] testSecond = new String[]{"1","2","3","4","5"};
    private SlidingButton slidingBtn,slidingBtnSecond;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        slidingBtn = (SlidingButton) findViewById(R.id.slidingBtn);
        slidingBtn.build(tests, 0, new SlidingButton.ButtonItemClickListener() {
            @Override
            public void buttonItemClickListener(String text) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

        slidingBtnSecond = (SlidingButton) findViewById(R.id.slidingBtnSecond);
        slidingBtnSecond.build(testSecond, 0, new SlidingButton.ButtonItemClickListener() {
            @Override
            public void buttonItemClickListener(String text) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
