package by.kleban.dogdiary.ui.showbreeds.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import by.kleban.dogdiary.R
import by.kleban.dogdiary.entities.DogBreed
import com.squareup.picasso.Picasso
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class ShowBreedsAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
) : RecyclerView.Adapter<ShowBreedsAdapter.ShowBreedsViewHolder>() {

    private val breedList = mutableListOf<DogBreed>()
    var breedClickListener: OnBreedClickListener? = null

    fun setItems(list: List<DogBreed>) {
        breedList.clear()
        breedList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowBreedsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_breed, parent, false)
        return ShowBreedsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShowBreedsViewHolder, position: Int) {
        val breed = breedList[position]

        holder.breedText.text = breed.breed
        holder.groupText.text = breed.breedGroup
        if (breed.height.centimeter.isNotEmpty()) {
            holder.heightText.visibility = View.VISIBLE
            holder.heightText.text = context.getString(R.string.breed_height, breed.height.centimeter)
        } else {
            holder.heightText.visibility = View.INVISIBLE
        }
        if (breed.weight.kilogram.isNotEmpty()) {
            holder.weightText.visibility = View.VISIBLE
            holder.weightText.text = context.getString(R.string.breed_weight, breed.weight.kilogram)
        } else {
            holder.weightText.visibility = View.INVISIBLE
        }
        Picasso.get()
            .load(breed.image.url)
            .error(R.drawable.error_image)
            .into(holder.breedImage)

    }

    override fun getItemCount(): Int {
        return breedList.size
    }

    fun interface OnBreedClickListener {
        fun onItemCLick(breed: String)
    }

    inner class ShowBreedsViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        init {
            view.setOnClickListener(this)
        }

        val breedImage: ImageView = view.findViewById(R.id.item_image)
        val breedText: TextView = view.findViewById(R.id.item_breed)
        val groupText: TextView = view.findViewById(R.id.item_group)
        val heightText: TextView = view.findViewById(R.id.item_height)
        val weightText: TextView = view.findViewById(R.id.item_weight)

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                breedClickListener?.onItemCLick(
                    breedList[position].breed
                )
            }
        }
    }
}