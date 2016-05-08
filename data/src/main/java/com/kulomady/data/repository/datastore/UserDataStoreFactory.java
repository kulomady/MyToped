package com.kulomady.data.repository.datastore;

import android.content.Context;

import com.kulomady.data.cache.UserCache;

import com.kulomady.data.net.UserRestApi;
import com.kulomady.data.entity.mapper.UserEntityJsonMapper;
import com.kulomady.data.net.UserRestApiImpl;

import javax.inject.Inject;

/**
 * Factory that creates different implementations of {@link UserDataStore}.
 * Created by kulomady on 5/6/16.
 */
public class UserDataStoreFactory {
    private final Context context;
    private final UserCache userCache;

    @Inject
    public UserDataStoreFactory(Context context, UserCache userCache) {
        if (context == null || userCache == null) {
            throw new IllegalArgumentException("Constructor parameters cannot be null!!!");
        }
        this.context = context;
        this.userCache = userCache;
    }

    /**
     * Create {@link UserDataStore} from a user id.
     */
    public UserDataStore create(int userId) {
        UserDataStore userDataStore;

        if (!this.userCache.isExpired() && this.userCache.isCached(userId)) {
            userDataStore = new DiskUserDataStore(this.userCache);
        } else {
            userDataStore = createCloudDataStore();
        }

        return userDataStore;
    }

    /**
     * Create {@link UserDataStore} to retrieve data from the Cloud.
     */
    public UserDataStore createCloudDataStore() {
        UserEntityJsonMapper userEntityJsonMapper = new UserEntityJsonMapper();

        UserRestApi restApi = new UserRestApiImpl(this.context, userEntityJsonMapper);

        return new CloudUserDataStore(restApi, this.userCache);
    }
}
