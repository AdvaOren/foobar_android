package com.example.foobar_dt_ad;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
/**
 * Activity for deleting the user account.
 */
public class DeleteAccount extends AppCompatActivity {

    /**
     * Result code for indicating the deletion action.
     */
    public final static int FROM_DELETE_SCREEN = 101;

    /**
     * Result code for indicating that the user has decided not to delete the account.
     */
    public final static int DEAD = 55;

    /**
     * Called when the activity is first created.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        // Initialize buttons
        Button btnDelete = findViewById(R.id.btnDelete);
        Button btnRegret = findViewById(R.id.btnRegret);

        // Delete button click listener
        btnDelete.setOnClickListener(v -> {
            // Indicate deletion action
            Intent intent = new Intent();
            intent.putExtra("toDelete", true);
            setResult(FROM_DELETE_SCREEN, intent);
            finish();
        });

        // Regret button click listener
        btnRegret.setOnClickListener(v -> {
            // Indicate no deletion action
            Intent intent = new Intent();
            intent.putExtra("toDelete", false);
            setResult(FROM_DELETE_SCREEN, intent);
            finish();
        });
    }
}
