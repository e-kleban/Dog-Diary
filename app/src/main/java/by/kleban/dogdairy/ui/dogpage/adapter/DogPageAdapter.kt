package by.kleban.dogdairy.ui.dogpage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import by.kleban.dogdairy.R
import by.kleban.dogdairy.core.Mapper
import by.kleban.dogdairy.core.picasso.transformation.CircleTransform
import by.kleban.dogdairy.entities.Dog
import by.kleban.dogdairy.entities.Post
import by.kleban.dogdairy.entities.Sex
import com.squareup.picasso.Picasso
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class DogPageAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val headerMapper: Mapper<Dog, Item.Header>,
    private val postMapper: Mapper<Post, Item.DogPost>,
    private val dogPostMapper: Mapper<Item.DogPost, Post>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Item>()
    var postClickListener: OnPostClickListener? = null

    fun setHeader(dog: Dog) {
        items.removeIf { it is Item.Header }
        val header = headerMapper.map(dog)
        items.add(POSITION_HEADER, header)
        notifyItemChanged(POSITION_HEADER)
    }

    fun setPosts(posts: List<Post>) {
        items.removeIf { it is Item.DogPost }
        val listDogPost = posts.map { postMapper.map(it) }
        items.addAll(listDogPost)
        notifyDataSetChanged()
    }

//    fun addPost(newPost:Post) {
//        val newDogPost = postMapper.map(newPost)
//        items.add(newDogPost)
//        val position = items.indexOf(newDogPost)
//       notifyItemChanged(position)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val view = layoutInflater.inflate(R.layout.item_dog_page_header, parent, false)
                HeaderViewHolder(view)
            }
            TYPE_POST -> {
                val view = layoutInflater.inflate(R.layout.item_dog_page_post, parent, false)
                PostViewHolder(view)
            }
            else -> throw Exception()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(position)
            is PostViewHolder -> holder.bind(position)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Item.Header -> TYPE_HEADER
            is Item.DogPost -> TYPE_POST
        }
    }

    fun interface OnPostClickListener {
        fun onItemCLick(post: Post)
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val dogImage: ImageView = view.findViewById(R.id.page_dog_image)
        private val dogName: TextView = view.findViewById(R.id.page_dog_name)
        private val dogAge: TextView = view.findViewById(R.id.page_dog_age)
        private val dogSex: ImageView = view.findViewById(R.id.page_dog_sex)
        private val dogBreed: TextView = view.findViewById(R.id.page_dog_breed)
        private val dogDescription: TextView = view.findViewById(R.id.page_dog_description)

        fun bind(position: Int) {
            val recyclerViewModel = items[position] as Item.Header
            dogName.text = recyclerViewModel.name
            dogAge.text = context.getString(R.string.dog_age, recyclerViewModel.age)
            dogBreed.text = recyclerViewModel.breed
            dogDescription.text = recyclerViewModel.description
            if (recyclerViewModel.sex == Sex.FEMALE) {
                dogSex.setImageResource(R.drawable.ic_sex_female)
            } else if (recyclerViewModel.sex == Sex.MALE) {
                dogSex.setImageResource(R.drawable.ic_sex_male)
            }
            Picasso.get().cancelRequest(dogImage)
            Picasso.get()
                .load(recyclerViewModel.image)
                .error(R.drawable.error_image)
                .transform(CircleTransform())
                .into(dogImage)
        }
    }

    inner class PostViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private val postImage: ImageView = view.findViewById(R.id.item_post_image)

        init {
            view.setOnClickListener(this)
        }

        fun bind(position: Int) {
            val recyclerViewModel = items[position] as Item.DogPost
            Picasso.get()
                .load(recyclerViewModel.postImage)
                .resize(560, 560)
                .onlyScaleDown()
                .centerCrop()
                .error(R.drawable.error_image)
                .into(postImage)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val dogPost = items[position] as Item.DogPost
                val post = dogPostMapper.map(dogPost)
                postClickListener?.onItemCLick(post)
            }
        }
    }

    sealed class Item {
        class Header(
            val name: String,
            val age: Int,
            val breed: String,
            val image: String,
            val description: String,
            val sex: Sex
        ) : Item()

        class DogPost(
            val postImage: String,
            val postDescription: String,
            val creatorId: Long
        ) : Item()
    }

    companion object {
        const val POSITION_HEADER = 0
        const val TYPE_HEADER = 0
        const val TYPE_POST = 1
    }
}