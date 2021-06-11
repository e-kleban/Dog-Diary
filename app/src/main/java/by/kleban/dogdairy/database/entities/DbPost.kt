package by.kleban.dogdairy.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_post")
data class DbPost(
    val dogCreatorId: Long,
    val postBigImage: String,
    val postLittleImage: String,
    val postDescription: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
