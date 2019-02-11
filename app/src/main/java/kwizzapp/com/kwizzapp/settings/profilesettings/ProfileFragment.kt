package kwizzapp.com.kwizzapp.settings.profilesettings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mayank.kwizzapp.dependency.components.DaggerInjectFragmentComponent
import com.google.android.gms.common.images.ImageManager
import io.reactivex.disposables.CompositeDisposable
import kwizzapp.com.kwizzapp.KwizzApp
import kwizzapp.com.kwizzapp.R
import kwizzapp.com.kwizzapp.helper.Global
import kwizzapp.com.kwizzapp.helper.processRequest
import kwizzapp.com.kwizzapp.models.Users
import kwizzapp.com.kwizzapp.services.IUser
import kwizzapp.com.kwizzapp.settings.bankdetails.EditBankDetailsFragment
import net.rmitsolutions.mfexpert.lms.helpers.*
import org.jetbrains.anko.find
import java.lang.Exception
import javax.inject.Inject


class ProfileFragment : Fragment() {

    @Inject
    lateinit var userService: IUser
    private lateinit var compositeDisposable: CompositeDisposable
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var playerImage: ImageView
    private lateinit var playerName: TextView
    private lateinit var totalWin: TextView
    private lateinit var totalLoose: TextView
    private lateinit var email: TextView
    private lateinit var mobileNumber: TextView
    private lateinit var accountNumber: TextView
    private lateinit var ifsc: TextView
    private lateinit var editProfile: ImageView
    private lateinit var editBankDetails: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val depComponent = DaggerInjectFragmentComponent.builder()
                .applicationComponent(KwizzApp.applicationComponent)
                .build()
        depComponent.injectProfileFragment(this)

        compositeDisposable = CompositeDisposable()
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

        editBankDetails.setOnClickListener {
            val editBankDetails = EditBankDetailsFragment()
            switchToFragmentBackStack(editBankDetails)
        }

        editProfile.setOnClickListener {
            val editProfileFrag = EditProfileFragment()
            switchToFragmentBackStack(editProfileFrag)
        }

        val imageUri = Uri.parse(activity?.getPref(SharedPrefKeys.ICON_IMAGE_URI, ""))
        if (imageUri.toString() != "") {
            val mgr = ImageManager.create(context)
            mgr.loadImage(playerImage, imageUri)
        } else {
            Glide.with(this)
                    .load(R.mipmap.ic_profile)
                    .apply(RequestOptions.circleCropTransform())
                    .into(playerImage);
        }

        playerName.text = "${activity?.getPref(SharedPrefKeys.FIRST_NAME, "")} ${activity?.getPref(SharedPrefKeys.LAST_NAME, "")}"
        email.text = activity?.getPref(SharedPrefKeys.EMAIL, "")
        mobileNumber.text = activity?.getPref(SharedPrefKeys.MOBILE_NUMBER, "")
        accountNumber.text = activity?.getPref(SharedPrefKeys.ACCOUNT_NUMBER, "")
        ifsc.text = activity?.getPref(SharedPrefKeys.IFSC_CODE, "")

        getWinLoosePoints()

        return view
    }

    private fun getWinLoosePoints() {
        val profile = Users.Profile()
        profile.playerId = activity?.getPref(SharedPrefKeys.PLAYER_ID, "")
        if (profile.playerId != "") {
            showProgress()
            compositeDisposable.add(userService.getProfileData(profile).processRequest(
                    { profileData ->
                        hideProgress()
                        if (profileData.isSuccess){
                            totalLoose.text = profileData.totalLoose.toString()
                            totalWin.text = profileData.totalWin.toString()
                        }else{
                            toast(profileData.message)
                            Global.updateCrashlyticsMessage(profile.playerId!!, "Error on getting profile data from server", "GetProfileData", Exception(profileData.message))
                        }
                    },
                    { err ->
                        hideProgress()
                        toast(err)
                        logD(err)
                        Global.updateCrashlyticsMessage(profile.playerId!!, "Error on getting profile data from server", "GetProfileData", Exception(err))
                    }
            ))
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
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

}