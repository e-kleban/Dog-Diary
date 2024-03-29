package by.kleban.dogdiary.ui.editdog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.kleban.dogdiary.entities.Dog
import by.kleban.dogdiary.entities.Post
import by.kleban.dogdiary.entities.Sex
import by.kleban.dogdiary.entities.Validation
import by.kleban.dogdiary.helper.FileHelper
import by.kleban.dogdiary.repositories.DogRepository
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

    private val _ageLiveData = MutableLiveData<String>()
    val ageLiveData: LiveData<String>
        get() = _ageLiveData

    private val _dogIsSavedLiveData = MutableLiveData(false)
    val dogIsSavedLiveData: LiveData<Boolean>
        get() = _dogIsSavedLiveData

    private val _dogIsDeleteLiveData = MutableLiveData(false)
    val dogIsDeleteLiveData: LiveData<Boolean>
        get() = _dogIsDeleteLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    private val _validationNameLiveData = MutableLiveData<Validation>()
    val validationNameLiveData: LiveData<Validation>
        get() = _validationNameLiveData

    private val _validationAgeLiveData = MutableLiveData<Validation>()
    val validationAgeLiveData: LiveData<Validation>
        get() = _validationAgeLiveData

    private val _validationDescriptionLiveData = MutableLiveData<Validation>()
    val validationDescriptionLiveData: LiveData<Validation>
        get() = _validationDescriptionLiveData

    lateinit var originalDog: Dog
    private val originalPosts = mutableListOf<Post>()

    init {
        getDogWithPosts()
    }

    fun validateChangedDog() {
        _validationNameLiveData.value = validateName()
        _validationAgeLiveData.value = validateAge()
        _validationDescriptionLiveData.value = validateDescription()
        saveChangedDog()
    }

    private fun validateName(): Validation {
        return when {
            _dogLiveData.value?.name.isNullOrEmpty() -> Validation.EMPTY
            else -> Validation.VALID
        }
    }

    private fun validateAge(): Validation {
        val age = _ageLiveData.value
        Timber.e(age.toString())
            val valid = when {
                age.isNullOrBlank() -> Validation.EMPTY
                age.toInt() < 0 -> Validation.EMPTY
                else -> Validation.VALID
            }
        Timber.e(valid.toString())
        return valid
    }

    private fun validateDescription(): Validation {
        return when {
            _dogLiveData.value?.description.isNullOrEmpty() -> Validation.EMPTY
            else -> Validation.VALID
        }
    }

    fun changeName(newName: String) {
        val dog = _dogLiveData.value
        if (dog != null && dog.name != newName) {
            _dogLiveData.value = dog.copy(name = newName)
        }
    }

    fun changeAge(newAge: String) {
        val age = _ageLiveData.value
        if (age != null && age != newAge) {
            _ageLiveData.value = newAge
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

    private fun saveChangedDog() {
        val validationName = _validationNameLiveData.value
        val validationAge = _validationAgeLiveData.value
        val validationDescription = _validationDescriptionLiveData.value

        if (validationName == Validation.VALID &&
            validationAge == Validation.VALID &&
            validationDescription == Validation.VALID
        ) {
            val dog = _dogLiveData.value?.copy(age =_ageLiveData.value!!.toInt() )
            ioScope.launch {
                if (dog != originalDog ){
                    val image = fileHelper.saveFileIntoAppsDir(Uri.parse(dog?.image), "avatar")
                    val thumb = fileHelper.createThumbnail(image, "avatar_thumb")
                    val age = _ageLiveData.value!!
                    deleteOldImages()
                    try {
                        repository.updateDog(dog!!.copy(image = image.toString(), thumbnail = thumb.toString(), age =age.toInt() ))
                        _dogIsSavedLiveData.postValue(true)
                    } catch (e: Exception) {
                        Timber.e(e)
                        _errorLiveData.postValue(e.message)
                    }
                }
            }
        }
    }

    fun deleteDog() {
        ioScope.launch {
            try {
                fileHelper.deleteImages(originalDog.image)
                fileHelper.deleteImages(originalDog.thumbnail)
                originalPosts.forEach {
                    fileHelper.deleteImages(it.image)
                    fileHelper.deleteImages(it.thumbnail)
                }
                repository.deleteDogWithPosts()
                _dogIsDeleteLiveData.postValue(true)
            } catch (e: Exception) {
                Timber.e(e)
                _errorLiveData.postValue(e.message)
            }
        }
    }

    fun getDogWithPosts() {
        ioScope.launch {
            try {
                val dogWithPosts = repository.getDogWithPosts()
                val dog = dogWithPosts.dog
                _dogLiveData.postValue(dog)
                withContext(Dispatchers.Main) {
                    originalDog = dog
                    _ageLiveData.postValue(dog.age.toString())
                    originalPosts.addAll(dogWithPosts.posts)
                }
            } catch (e: Exception) {
                Timber.e(e)
                _errorLiveData.postValue(e.message)

            }
        }
    }

    private fun deleteOldImages() {
        ioScope.launch {
            try {
                fileHelper.deleteImages(originalDog.image)
                fileHelper.deleteImages(originalDog.thumbnail)
            } catch (e: Exception) {
                Timber.e(e)
                _errorLiveData.postValue(e.message)
            }
        }
    }
}