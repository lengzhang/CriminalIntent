package com.lengzhang.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "CrimeListFragment"

class CrimeListFragment : Fragment() {

    /**
     * Required interface for hosting activities
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeListAdapter? = CrimeListAdapter()

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView =
            view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    adapter?.submitList(crimes)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)
        private val requiresPoliceButton: Button? =
            itemView.findViewById(R.id.contact_police_button)

        init {
            itemView.setOnClickListener(this)
            requiresPoliceButton?.setOnClickListener {
                Toast.makeText(context, "Contact Police!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.format("EEEE, LLL dd, yyyy - hh:mm a", this.crime.date)
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE

        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

//    private inner class CrimeAdapter(var crimes: List<Crime>) :
//        RecyclerView.Adapter<CrimeHolder>() {
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
//                : CrimeHolder {
//
//            val view = when (viewType) {
//                1 -> layoutInflater.inflate(R.layout.list_item_crime_police, parent, false)
//                else -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
//            }
//
//            return CrimeHolder(view)
//        }
//
//        override fun getItemCount() = crimes.size
//
//        override fun getItemViewType(position: Int): Int =
//            if (crimes[position].requiresPolice) 1 else 0
//
//        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
//            val crime = crimes[position]
//            holder.bind(crime)
//        }
//    }

    private inner class CrimeListAdapter : ListAdapter<Crime, CrimeHolder>(DiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {

            val view = when (viewType) {
                1 -> layoutInflater.inflate(R.layout.list_item_crime_police, parent, false)
                else -> layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            }

            return CrimeHolder(view)
        }

        override fun getItemCount(): Int = currentList.size

        override fun getItemViewType(position: Int): Int =
            if (getItem(position).requiresPolice) 1 else 0

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = getItem(position)
            holder.bind(crime)
        }

    }

    private inner class DiffCallback : DiffUtil.ItemCallback<Crime>() {
        override fun areItemsTheSame(oldItem: Crime, newItem: Crime): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Crime, newItem: Crime): Boolean =
            Crime.isEqual(oldItem, newItem)
    }

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}