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
package android.example.com.squawker.following;

import android.content.SharedPreferences;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;


/**
 * Shows the list of instructors you can follow
 */
public class FollowingPreferenceFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    //private final static String LOG_TAG = FollowingPreferenceFragment.class.getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);*/
        // Add the shared preference change listener
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);*/
        // Remove the shared preference change listener
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Add visualizer preferences, defined in the XML file in res->xml->preferences_squawker
        addPreferencesFromResource(R.xml.following_squawker);
    }

    /**
     * Triggered when shared preferences changes. This will be triggered when a person is followed
     * or un-followed
     *
     * @param sharedPreferences SharedPreferences file
     * @param key               The key of the preference which was changed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null && preference instanceof SwitchPreferenceCompat) {
            // Get the current state of the switch preference
            boolean isOn = sharedPreferences.getBoolean(key, false);
            if (isOn) {
                // The preference key matches the following key for the associated instructor in
                // FCM. For example, the key for Lyla is key_lyla (as seen in
                // following_squawker.xml). The topic for Lyla's messages is /topics/key_lyla

                // Subscribe
                FirebaseMessaging.getInstance().subscribeToTopic(key);
                // Log.d(LOG_TAG, "Subscribing to " + key);
            } else {
                // Un-subscribe
                FirebaseMessaging.getInstance().unsubscribeFromTopic(key);
                // Log.d(LOG_TAG, "Un-subscribing to " + key);
            }
        }
        /*boolean following;
        if (key.equals(getString(R.string.follow_key_switch_asser)))
        {
            following = sharedPreferences.getBoolean(
                    key, getResources().getBoolean(R.bool.follow_default_message_subscription));
            //Log.d(LOG_TAG, "following is " + following);
            if (following)
            {
                FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.ASSER_KEY);
            }
            else
            {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(SquawkContract.ASSER_KEY);
            }
        }
        else if (key.equals(getString(R.string.follow_key_switch_cezanne)))
        {
            following = sharedPreferences.getBoolean(
                    key, getResources().getBoolean(R.bool.follow_default_message_subscription));
            if (following)
            {
                FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.CEZANNE_KEY);
            }
            else
            {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(SquawkContract.CEZANNE_KEY);
            }
        }
        else if (key.equals(getString(R.string.follow_key_switch_jlin)))
        {
            following = sharedPreferences.getBoolean(
                    key, getResources().getBoolean(R.bool.follow_default_message_subscription));
            if (following)
            {
                FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.JLIN_KEY);
            }
            else
            {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(SquawkContract.JLIN_KEY);
            }
        }
        else if (key.equals(getString(R.string.follow_key_switch_lyla)))
        {
            following = sharedPreferences.getBoolean(
                    key, getResources().getBoolean(R.bool.follow_default_message_subscription));
            if (following)
            {
                FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.LYLA_KEY);
            }
            else
            {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(SquawkContract.LYLA_KEY);
            }
        }
        else if (key.equals(getString(R.string.follow_key_switch_nikita)))
        {
            following = sharedPreferences.getBoolean(
                    key, getResources().getBoolean(R.bool.follow_default_message_subscription));
            if (following)
            {
                FirebaseMessaging.getInstance().subscribeToTopic(SquawkContract.NIKITA_KEY);
            }
            else
            {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(SquawkContract.NIKITA_KEY);
            }
        }*/
    }
}
