package uni.project.mylocalbooking.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import uni.project.mylocalbooking.MyLocalBooking;
import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.SessionPreferences;


public class MainActivity extends AppCompatActivity {

    ViewPager mSlideViewPager;
    LinearLayout mDotLayout;
    Button nextBtn, backBtn, skipBtn;

    TextView[] dots;
    ViewPageAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!SessionPreferences.getUserPrefs().isEmpty()){
            System.out.println(SessionPreferences.getUserPrefs());
            if (MyLocalBooking.getCurrentUser() != null){
                System.out.println(MyLocalBooking.getCurrentUser());
                startActivity(new Intent(MyLocalBooking.getAppContext(),HomeActivity.class));
            }
        }
        else{
            System.out.println("Empty sessionData");
        }

        nextBtn = findViewById(R.id.next_btn_landing_page);
        backBtn = findViewById(R.id.back_btn_landing_page);
        skipBtn = findViewById(R.id.skip_btn_landing_page);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(0) < 3) {
                    mSlideViewPager.setCurrentItem(1, true);
                }
                else {
                    Intent intent = new Intent(MainActivity.this, LoginOrRegistrationActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getItem(0) > 0) {
                    mSlideViewPager.setCurrentItem(getItem(-1), true);
                }
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginOrRegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_landing_page);

        viewPagerAdapter = new ViewPageAdapter(this);

        mSlideViewPager.setAdapter(viewPagerAdapter);

        setUpIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

    }

    public void setUpIndicator(int position) {
        dots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i < 4; ++i) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dots[i].setTextColor(getResources().getColor(R.color.grey, getApplication().getTheme()));
            }
            mDotLayout.addView(dots[i]);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dots[position].setTextColor(getResources().getColor(R.color.booking_color, getApplication().getTheme()));
        }

    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setUpIndicator(position);

            if (position > 0) {
                backBtn.setVisibility(View.VISIBLE);
            }
            else {
                backBtn.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i) {
        return mSlideViewPager.getCurrentItem() + i;
    }

}