package by.kleban.dogdiary.di

import by.kleban.dogdiary.helper.FileHelper
import by.kleban.dogdiary.helper.FileHelperImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class FileHelperBindingModule {

    @Binds
    abstract fun bindFileHelperImpl(fileHelperImpl: FileHelperImpl): FileHelper
}