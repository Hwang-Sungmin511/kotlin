package com.example.firebase

import android.content.Context
import android.os.Handler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object HandlerModule {

    @Provides
    fun provideHandler(@ActivityContext context: Context): Handler {
        return Handler(context.mainLooper)
    }

}