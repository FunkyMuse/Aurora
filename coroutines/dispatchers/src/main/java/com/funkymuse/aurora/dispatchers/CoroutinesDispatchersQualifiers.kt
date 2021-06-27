package com.funkymuse.aurora.dispatchers

import javax.inject.Qualifier

/**
 * Created by funkymuse on 6/27/21 to long live and prosper !
 */

@Retention
@Qualifier
annotation class DefaultDispatcher

@Retention
@Qualifier
annotation class IoDispatcher

@Retention
@Qualifier
annotation class MainDispatcher

@Retention
@Qualifier
annotation class MainImmediateDispatcher