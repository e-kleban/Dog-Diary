package by.kleban.dogdiary.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_dog")
data class DbDog(
    val name: String,
    val image: String,
    val thumbnail: String,
    val age: Int,
    val sex: Int,
    val breed: String,
    val description: String,

    ) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}