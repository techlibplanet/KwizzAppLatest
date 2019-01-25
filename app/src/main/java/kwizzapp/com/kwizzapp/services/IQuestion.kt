package kwizzapp.com.kwizzapp.services


import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

import io.reactivex.Observable
import kwizzapp.com.kwizzapp.viewmodels.Questions
import retrofit2.http.Body

interface IQuestion {

    @FormUrlEncoded
    @POST("payu/fetchNumberOfRows.php")
    fun getNumberOfRows(@Field("tableName") tableName: String): Observable<Questions.CountRows>

    @POST("payu/getQuestions.php")
    fun getQuestions(@Body question: Questions.GetQuestion) : Observable<Questions.Question>
}