package com.graduationproject.robokidsapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.graduationproject.robokidsapp.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth, database: FirebaseFirestore): AuthRepository {
        return AuthRepositoryImpl(auth, database)
    }

    @Provides
    @Singleton
    fun provideInfoRepository(auth: FirebaseAuth, database: FirebaseFirestore): InfoRepository {
        return InfoRepositoryImpl(auth, database)
    }

    @Provides
    @Singleton
    fun provideContentRepository(database: FirebaseFirestore,storage : FirebaseStorage): ContentRepository {
        return ContentRepositoryImpl(database,storage)
    }


}