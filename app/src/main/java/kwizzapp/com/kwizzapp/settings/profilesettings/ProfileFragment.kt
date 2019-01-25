package kwizzapp.com.kwizzapp.settings.profilesettings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.example.mayank.kwizzapp.profile.ProfileViewAdapter
import kotlinx.android.synthetic.main.fragment_profile.*
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.viewmodels.SettingVm
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find


class ProfileFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var playerImage : ImageView
    private lateinit var playerName : TextView
    private lateinit var totalWin : TextView
    private lateinit var totalLoose : TextView
    private lateinit var email : TextView
    private lateinit var mobileNumber : TextView
    private lateinit var accountNumber : TextView
    private lateinit var ifsc : TextView
    private lateinit var editProfile : ImageView
    private lateinit var editBankDetails : ImageView


    private lateinit var recyclerView: RecyclerView
    val adapter: ProfileViewAdapter by lazy { ProfileViewAdapter() }
    lateinit var modelList: MutableList<SettingVm.ProfileVm>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        playerImage = view.find(R.id.player_image)
        playerName = view.find(R.id.playerName)
        totalWin = view.find(R.id.totalWin)
        totalLoose = view.find(R.id.totalLoose)
        email = view.find(R.id.email)
        mobileNumber = view.find(R.id.mobileNumber)
        accountNumber = view.find(R.id.accountNumber)
        ifsc = view.find(R.id.ifsc)
        editProfile = view.find(R.id.editProfileImage)
        editBankDetails = view.find(R.id.editBankDetails)

        editBankDetails.setOnClickListener{
            toast("Edit bank details button clicked.")
        }

        editProfile.setOnClickListener{
            toast("Edit profile button clicked")
            val editProfileFrag = EditProfileFragment()
            switchToFragmentBackStack(editProfileFrag)
        }

        val imageUri = Uri.parse(activity?.getPref(SharedPrefKeys.ICON_IMAGE_URI, ""))
        if(imageUri.toString()!=""){
            playerImage.setImageURI(imageUri)
        }else{
            playerImage.setImageResource(R.drawable.achievements_4)
        }

        playerName.text = "${activity?.getPref(SharedPrefKeys.FIRST_NAME, "") } ${activity?.getPref(SharedPrefKeys.LAST_NAME, "") }"
        email.text = activity?.getPref(SharedPrefKeys.EMAIL, "")
        mobileNumber.text = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        accountNumber.text = activity?.getPref(SharedPrefKeys.ACCOUNT_NUMBER, "")
        ifsc.text = activity?.getPref(SharedPrefKeys.IFSC_CODE, "")

//        recyclerView = view.findViewById(R.id.profile_recycler_view)
//        recyclerView.layoutManager = LinearLayoutManager(activity)
//        recyclerView.setHasFixedSize(true)
//        recyclerView.addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
//        recyclerView.adapter = adapter
//        modelList = mutableListOf<SettingVm.ProfileVm>()
//        setProfileItem()
        return view
    }

    private fun setProfileItem() {
        modelList.clear()
        val firstName = activity?.getPref(SharedPrefKeys.FIRST_NAME, "")!!
        logD("First Name - $firstName")
        modelList.add(SettingVm.ProfileVm("First Name", firstName))
        modelList.add(SettingVm.ProfileVm("Last Name", activity?.getPref(SharedPrefKeys.LAST_NAME, "")!!))
        modelList.add(SettingVm.ProfileVm("Mobile Number", activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")!!))
        modelList.add(SettingVm.ProfileVm("Email", activity?.getPref(SharedPrefKeys.EMAIL, "")!!))
        setRecyclerViewAdapter(modelList)

    }

    private fun setRecyclerViewAdapter(list: List<SettingVm.ProfileVm>) {
        adapter.items = list
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        activity?.menuInflater?.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.edit -> {
                val editProfileFragment = EditProfileFragment()
                switchToFragmentBackStack(editProfileFragment)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
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