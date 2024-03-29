package by.kleban.dogdiary.repositories

import android.annotation.SuppressLint
import android.content.SharedPreferences
import by.kleban.dogdiary.database.DogDb
import by.kleban.dogdiary.entities.*
import by.kleban.dogdiary.networking.dogbreed.DogApi
import javax.inject.Inject


class DogRepositoryImpl @Inject constructor(
    private val dogDb: DogDb,
    private val dogApi: DogApi,
    private val sharedPreferences: SharedPreferences,
) : DogRepository {

    override suspend fun loadBreeds(): List<DogBreed> {
        return dogApi.loadBreeds()
    }

    override suspend fun getDog(): Dog? {
        val dogList = dogDb.getAllDog()
        return if (dogList.isNotEmpty()) {
            dogList.first()
        } else {
            return null
        }
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun saveDog(dog: Dog) {
        val dogId = dogDb.saveDog(dog)
        sharedPreferences.edit()
            .clear()
            .putLong(SharedConfig.SHARED_PREF_DOG_ID, dogId)
            .commit()
    }

    override suspend fun updateDog(dog: Dog) {
        val id = sharedPreferences.getLong(SharedConfig.SHARED_PREF_DOG_ID, 0)
        dogDb.updateDog(dog, id)
    }

    override suspend fun getDogWithPosts(): DogWithPosts {
        val dogId = sharedPreferences.getLong(SharedConfig.SHARED_PREF_DOG_ID, 0)
        return dogDb.getDogWithPosts(dogId)
    }

    override suspend fun savePost(post: Post) {
        dogDb.savePost(post)
    }

    override suspend fun updatePost(post: Post) {
        dogDb.updatePost(post)
    }

    override suspend fun deletePost(image: String) {
        dogDb.deletePost(image)
    }

    @SuppressLint("ApplySharedPref")
    override suspend fun deleteDogWithPosts() {
        val id = sharedPreferences.getLong(SharedConfig.SHARED_PREF_DOG_ID, 0)
        sharedPreferences.edit()
            .clear()
            .commit()
        dogDb.deleteDogWithPosts(id)
    }
}