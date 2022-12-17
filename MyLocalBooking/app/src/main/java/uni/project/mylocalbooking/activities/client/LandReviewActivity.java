package uni.project.mylocalbooking.activities.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import uni.project.mylocalbooking.R;
import uni.project.mylocalbooking.fragments.FailureFragment;
import uni.project.mylocalbooking.fragments.SuccessFragment;

public class LandReviewActivity extends AppCompatActivity {

    private CheckBox anonymous;
    private Button sendButton;
    private double reviewStars;
    private EditText reviewText;
    private String reviewString;
    private RatingBar ratingBar;
    private FloatingActionButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_review);

        backButton = findViewById(R.id.backButtonReview);
        anonymous = findViewById(R.id.anonymousReviewCheckBox);
        sendButton = findViewById(R.id.send_land_review);
        reviewText = findViewById(R.id.review_land);
        reviewString = reviewText.getText().toString();
        ratingBar = findViewById(R.id.rating_land_bar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                reviewStars = ratingBar.getRating();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Goes back to previous Activity
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If rating is null or 0.0
                if(reviewStars < 0.5){
                    failedSend();
                }
                else{
                    // TODO: update DB
                    confirmSend();
                }
            }
        });

    }

    public void confirmSend() {
        DialogFragment newFragment = new SuccessFragment("Thank you!",
                "Your review was submitted successfully!\n\n" +
                        "Recap:\n" +
                        "Rating: " + reviewStars + " stars\n" +
                        "Anonymous? " + anonymous.isChecked());
        newFragment.show(getSupportFragmentManager(), "successReview");
    }

    public void failedSend(){
        DialogFragment newFragment = new FailureFragment("Error",
                "At least one of the fields is not valid, please try again with a different input, " +
                        "for example put at least 0.5 star as your rating");
        newFragment.show(getSupportFragmentManager(), "failedReview");
    }

}