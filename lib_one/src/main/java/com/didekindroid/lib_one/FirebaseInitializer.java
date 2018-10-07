package com.didekindroid.lib_one;

import com.didekinlib.BeanBuilder;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.lib_one.util.CommonAssertionMsg.firebaseInitializer_wrong_build_data;
import static io.reactivex.Single.error;
import static io.reactivex.Single.just;

public final class FirebaseInitializer {

    public static final AtomicReference<FirebaseInitializer> firebaseInitializer = new AtomicReference<>();
    private final String firebaseProjectId;
    private final String scopeAppIdToken;

    private FirebaseInitializer(FirebaseInitializerBuilder firebaseInitializerBuilder)
    {
        firebaseProjectId = firebaseInitializerBuilder.projectId;
        scopeAppIdToken = firebaseInitializerBuilder.scopeToken;
    }

    public Single<String> getSingleToken()
    {
        try {
            return just(FirebaseInstanceId.getInstance().getToken(firebaseProjectId, scopeAppIdToken));
        } catch (IOException e) {
            return error(e);
        }
    }

    //    ==================== BUILDER ====================

    public static class FirebaseInitializerBuilder implements BeanBuilder<FirebaseInitializer> {

        private final String projectId;
        private final String scopeToken;

        public FirebaseInitializerBuilder(String projectId, String scopeToken)
        {
            this.projectId = projectId;
            this.scopeToken = scopeToken;
        }

        @Override
        public FirebaseInitializer build()
        {
            FirebaseInitializer initializer = new FirebaseInitializer(this);
            if (initializer.firebaseProjectId == null || initializer.scopeAppIdToken == null) {
                throw new IllegalStateException(firebaseInitializer_wrong_build_data + " firebaseProjectId: "
                        + initializer.firebaseProjectId + " scopeAppIdToken: " + initializer.scopeAppIdToken
                );
            }
            return initializer;
        }
    }
}
