package com.ram.delivery.di

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn( SingletonComponent::class )
object GlideModule {

    @Singleton
    @Provides
    fun provideGlide(
        @ApplicationContext context: Context
    ): RequestManager = Glide.with(context)
}
