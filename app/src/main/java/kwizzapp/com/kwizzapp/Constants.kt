package kwizzapp.com.kwizzapp

import com.google.firebase.analytics.FirebaseAnalytics

object Constants {

    // Api
    const val API_BASE_URL = "http://www.alchemyeducation.org/"

    const val CONNECTION_TIMEOUT: Long = 60
    const val READ_TIMEOUT: Long = 60

    const val RC_SIGN_IN = 100
    const val RC_LEADERBOARD_UI = 9004
    val RC_ACHIEVEMENT_UI = 9003

    const val RIGHT_ANSWERS = "RightAnswers"
    const val WRONG_ANSWERS = "WrongAnswers"
    const val DROP_QUESTIONS = "DropQuestions"

    // PayuMoney
    val MERCHANT_ID = "4873218"
    val MERCHANT_KEY = "HqRplS"
    val surl = "https://www.payumoney.com/mobileapp/payumoney/success.php"
    val furl = "https://www.payumoney.com/mobileapp/payumoney/failure.php"
    val URL = "http://alchemyeducation.org/payu/getHashCode.php"

    // Transaction Type
    const val TRANSACTION_TYPE_DEBITED = "Debited"
    const val TRANSACTION_TYPE_CREDITED = "Credited"

    lateinit var firebaseAnalytics: FirebaseAnalytics

    const val APP_VERSION_CODE = "version_code"
    const val APP_VERSION_NAME = "version_name"
}