package by.kleban.dogdairy.ui.editdog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.kleban.dogdairy.entities.Dog
import by.kleban.dogdairy.entities.Sex
import by.kleban.dogdairy.entities.file_helper.FileHelper
import by.kleban.dogdairy.repositories.DogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditDogViewModel @Inject constructor(
    private val repository: DogRepository,
    private val fileHelper: FileHelper
) : ViewModel() {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private val _dogLiveData = MutableLiveData<Dog>()
    val dogLiveData: LiveData<Dog>
        get() = _dogLiveData

    private val _dogIsSavedLiveData = MutableLiveData(false)
    val dogIsSavedLiveData: LiveData<Boolean>
        get() = _dogIsSavedLiveData

    lateinit var originalDog: Dog

    init {
        getDogWithPosts()
    }

    fun changeName(newName: String) {
        val dog = _dogLiveData.value
        if (dog != null && dog.name != newName) {
            _dogLiveData.value = dog.copy(name = newName)
        }
    }

    fun changeAge(newAge: Int) {
        val dog = _dogLiveData.value
        if (dog != null && dog.age != newAge) {
            _dogLiveData.value = dog.copy(age = newAge)
        }
    }

    fun changeDescription(newDescription: String) {
        val dog = _dogLiveData.value
        if (dog != null && dog.description != newDescription) {
            _dogLiveData.value = dog.copy(description = newDescription)
        }
    }

    fun changeSex(sex: Sex) {
        val dog = _dogLiveData.value
        if (dog != null && dog.sex != sex) {
            _dogLiveData.value = dog.copy(sex = sex)
        }
    }

    fun changeBreed(newBreed: String) {
        val dog = _dogLiveData.value
        if (dog != null && dog.breed != newBreed) {
            _dogLiveData.value = dog.copy(breed = newBreed)
        }
    }

    fun changeImage(uri: Uri) {
        val dog = _dogLiveData.value
        if (dog != null && dog.image != uri.toString() && dog.thumbnail != uri.toString()) {
            _dogLiveData.postValue(dog.copy(image = uri.toString(), thumbnail = uri.toString()))
        }
    }

    fun saveChangedDog() {
        val dog = _dogLiveData.value
        ioScope.launch {
            if (dog != originalDog) {
                val image = fileHelper.saveFileIntoAppsDir(Uri.parse(dog?.image), "avatar")
                val thumb = fileHelper.createThumbnail(image, "avatar_thumb")
                deleteOldImages()
                try {
                    repository.updateDog(dog!!.copy(image = image.toString(), thumbnail = thumb.toString()))
                    _dogIsSavedLiveData.postValue(true)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    fun getDogWithPosts() {
        ioScope.launch {
            try {
                val dog = repository.getDogWithPosts().dog
                _dogLiveData.postValue(dog)
                withContext(Dispatchers.Main) {
                    originalDog = dog
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun deleteOldImages() {
        ioScope.launch {
            fileHelper.deleteImages(originalDog.image)
            fileHelper.deleteImages(originalDog.thumbnail)
        }
    }

}