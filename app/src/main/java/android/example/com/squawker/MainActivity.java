/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package android.example.com.squawker;

import android.content.Intent;
import android.database.Cursor;
import android.example.com.squawker.following.FollowingPreferenceActivity;
import android.example.com.squawker.following.FollowingPreferenceFragment;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
//import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID_MESSAGES = 0;

    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    SquawkAdapter mAdapter;

    static final String[] MESSAGES_PROJECTION = {
            SquawkContract.COLUMN_AUTHOR,
            SquawkContract.COLUMN_MESSAGE,
            SquawkContract.COLUMN_DATE,
            SquawkContract.COLUMN_AUTHOR_KEY
    };

    static final int COL_NUM_AUTHOR = 0;
    static final int COL_NUM_MESSAGE = 1;
    static final int COL_NUM_DATE = 2;
    static final int COL_NUM_AUTHOR_KEY = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.squawks_recycler_view);

        // Use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // Use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // Add dividers
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        // Specify an adapter
        mAdapter = new SquawkAdapter();
        mRecyclerView.setAdapter(mAdapter);

        // Start the loader
        getSupportLoaderManager().initLoader(LOADER_ID_MESSAGES, null, this);

        // Get token from the ID Service you created and show it in a log
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log
                        String msg = getString(R.string.message_token_format, token);
                        Log.d(LOG_TAG, msg);
                    }
                });

        /*FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.ASSER_KEY);
        FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.CEZANNE_KEY);
        FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.JLIN_KEY);
        FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.LYLA_KEY);
        FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.NIKITA_KEY);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_following_preferences) {
            // Opens the following activity when the menu icon is pressed
            Intent startFollowingActivity = new Intent(this, FollowingPreferenceActivity.class);
            startActivity(startFollowingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Loader callbacks
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This method generates a selection off of only the current followers
        String selection = SquawkContract.createSelectionForCurrentFollowers(
                PreferenceManager.getDefaultSharedPreferences(this));
        Log.d(LOG_TAG, "Selection is " + selection);
        return new CursorLoader(this, SquawkProvider.SquawkMessages.CONTENT_URI,
                MESSAGES_PROJECTION, selection, null, SquawkContract.COLUMN_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
