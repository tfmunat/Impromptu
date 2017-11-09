package com.laserscorpion.impromptu;

import com.facebook.AccessToken;
import com.facebook.Profile;

/**
 * Created by joel on 11/9/17.
 */

public class FacebookResourceLocator {
    private static AccessToken token;
    private static Profile profile;

    static void setToken(AccessToken token) {
        FacebookResourceLocator.token = token;
    }

    static void setProfile(Profile profile) {
        FacebookResourceLocator.profile = profile;
    }

    static AccessToken getToken() {
        if (token == null)
            return AccessToken.getCurrentAccessToken();
        return token;
    }

    static Profile getProfile() {
        if (profile == null)
            return Profile.getCurrentProfile();
        return profile;
    }

}
