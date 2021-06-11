package by.kleban.dogdairy.entities

import java.io.Serializable


data class Post(
    val dogCreatorId: Long,
    val image: String,
    val thumbnail: String,
    val description: String,
) : Serializable