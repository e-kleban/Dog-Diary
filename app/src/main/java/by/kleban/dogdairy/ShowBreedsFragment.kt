package by.kleban.dogdairy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kleban.dogdairy.adapter.ShowBreedsAdapter

class ShowBreedsFragment : Fragment(), ShowBreedsAdapter.OnItemClickListener {

    private val showBreedAdapter = ShowBreedsAdapter(this)

    private val viewModel by lazy {
        ViewModelProvider(this).get(ShowBreedsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.show_breeds_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.show_recycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = showBreedAdapter

        setupSearchView(view)

        viewModel.breedListLiveData.observe(viewLifecycleOwner) {
            showBreedAdapter.setItems(it)
        }
        viewModel.breedListWithFilter.observe(viewLifecycleOwner) {
            showBreedAdapter.setItems(it)
        }
        viewModel.loadListBreed()
    }

    private fun setupSearchView(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.topAppBar_breed)
        val searchItem = toolbar.menu.findItem(R.id.search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filter(newText ?: "")
                return true
            }
        })
        searchView.setOnCloseListener {
            viewModel.filter("")
            false
        }
    }

    override fun onItemCLick(breed: String) {
        val bundle = Bundle().apply { putString(BUNDLE_BREED, breed) }
        findNavController().navigate(
            R.id.from_showBreedsFragment_to_registrationFragment,
            bundle
        )
    }

    companion object {
        const val BUNDLE_BREED = "bundle breed"
    }
}