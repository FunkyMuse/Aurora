package com.funkymuse.aurora.navigator

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by funkymuse on 6/25/21 to long live and prosper !
 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class NavigatorModule {

    @Binds
    abstract fun navigator(navigator: NavigatorImpl): Navigator
}