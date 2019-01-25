package kwizzapp.com.kwizzapp.settings.menusettings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mayank.kwizzapp.settings.menusettings.SettingMenuAdapter


import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.viewmodels.SettingVm

class SettingMenuFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var recyclerView: RecyclerView
    val adapter: SettingMenuAdapter by lazy { SettingMenuAdapter() }
    lateinit var modelList: MutableList<SettingVm.SettingMenuVm>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =inflater.inflate(R.layout.fragment_setting_menu, container, false)
        recyclerView = view.findViewById(R.id.menu_setting_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        recyclerView.adapter = adapter
        modelList = mutableListOf<SettingVm.SettingMenuVm>()
        setSettingsItem()
        return view
    }

    private fun setSettingsItem() {
        modelList.clear()
        modelList.add(SettingVm.SettingMenuVm(R.drawable.ic_profile, "Profile"))
        modelList.add(SettingVm.SettingMenuVm(R.drawable.ic_security, "Policies"))
        setRecyclerViewAdapter(modelList)
    }


    private fun setRecyclerViewAdapter(list: List<SettingVm.SettingMenuVm>) {
        adapter.items = list
        adapter.notifyDataSetChanged()
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
    }
}