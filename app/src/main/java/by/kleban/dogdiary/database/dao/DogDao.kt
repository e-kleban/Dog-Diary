package by.kleban.dogdiary.database.dao

import androidx.room.*
import by.kleban.dogdiary.database.entities.DbDog
import by.kleban.dogdiary.database.entities.DbDogWithPosts
import by.kleban.dogdiary.database.entities.DbPost

@Dao
interface DogDao {

    @Query("SELECT * FROM table_dog")
    suspend fun getAllDog(): List<DbDog>

    @Insert
    suspend fun saveDog(dogDb: DbDog): Long

    @Update
    suspend fun updateDog(dogDb: DbDog)

    @Transaction
    @Query("SELECT * FROM table_dog WHERE id= :id")
    suspend fun getDogWithPosts(id: Long): DbDogWithPosts

    @Insert
    suspend fun savePost(dbPost: DbPost)

    @Query("DELETE FROM table_dog WHERE id=:id")
    suspend fun deleteDog(id: Long)

    @Query("DELETE FROM table_post WHERE dogCreatorId=:id")
    suspend fun deletePost(id: Long)

    @Transaction
    suspend fun deleteDogWithPosts(id: Long) {
        deleteDog(id)
        deletePost(id)
    }

    @Query("SELECT * FROM table_post")
    suspend fun getAllPosts(): List<DbPost>

    @Query("UPDATE table_post SET description=:desc WHERE image=:image")
    suspend fun updatePost(desc: String, image: String)

    @Query("DELETE FROM table_post WHERE image=:image")
    suspend fun deletePost(image: String)
}