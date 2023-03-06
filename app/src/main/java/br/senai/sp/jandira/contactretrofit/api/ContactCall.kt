package br.senai.sp.jandira.contactretrofit.api

import br.senai.sp.jandira.contactretrofit.model.Contact
import retrofit2.Call
import retrofit2.http.*

interface ContactCall {
    @GET("contacts")
    fun getAllContacts(): Call<List<Contact>>

    @POST("contacts")
    fun saveContact(@Body contact: Contact): Call<Contact> // A anotação serve para o retrofit saber que essa requisição necessita de um body

    @DELETE("contacts/{id}")
    fun deleteContact(@Path("id") id: Long): Call<String>
}