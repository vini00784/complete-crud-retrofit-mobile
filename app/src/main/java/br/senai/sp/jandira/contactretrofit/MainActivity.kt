package br.senai.sp.jandira.contactretrofit

import android.os.Bundle
import android.util.Log
import android.widget.Space
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.sp.jandira.contactretrofit.api.ContactCall
import br.senai.sp.jandira.contactretrofit.api.RetrofitApi
import br.senai.sp.jandira.contactretrofit.model.Contact
import br.senai.sp.jandira.contactretrofit.ui.theme.ContactRetrofitTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactRetrofitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {
    val context = LocalContext.current

    var nameState by remember {
        mutableStateOf("")
    }

    var phoneState by remember {
        mutableStateOf("")
    }

    var emailState by remember {
        mutableStateOf("")
    }

    var activeState by remember {
        mutableStateOf(false)
    }

    val retrofit = RetrofitApi.getRetrofitConnection()
    val contactCall = retrofit.create(ContactCall::class.java)
    val getContactsCall = contactCall.getAllContacts()

    var contacts by remember {
        mutableStateOf(listOf<Contact>())
    }

    // Call the endPoint
    getContactsCall.enqueue(object: Callback<List<Contact>> {
        override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
            contacts = response.body()!!
        }

        override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
            Log.i("ds3m", t.message.toString())
        }

    })

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = "CADASTRO DE CONTATOS",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = nameState,
            onValueChange = {
                nameState = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact name")
            }
        )

        OutlinedTextField(
            value = phoneState,
            onValueChange = {
                phoneState = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact phone")
            }
        )

        OutlinedTextField(
            value = emailState,
            onValueChange = {
                emailState = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact email")
            }
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = activeState,
                onCheckedChange = {
                    activeState = it
                }
            )
            Text(text = "Enable")
        }

        Button(
            onClick = {
                val contact = Contact (
                    name = nameState,
                    email = emailState,
                    phone = phoneState,
                    active = activeState
                )
                val postContactCall = contactCall.saveContact(contact)

                postContactCall.enqueue(object: Callback<Contact> {
                    override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                        Log.i("ds3m", response.body()!!.toString())
                    }

                    override fun onFailure(call: Call<Contact>, t: Throwable) {
                        Log.i("ds3m", t.message.toString())
                    }

                })
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Save contact")
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(contacts) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            nameState = it.name
                            emailState = it.email
                            phoneState = it.phone
                            activeState = it.active
                        },
                    backgroundColor = Color(30, 150, 155, 255)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = it.name)
                        Text(text = it.email)
                        Text(text = it.phone)
                        Button(onClick = {
                            val deleteContactCall = contactCall.deleteContact(it.id)
                            deleteContactCall.enqueue(object: Callback<String> {
                                override fun onResponse(
                                    call: Call<String>,
                                    response: Response<String>
                                ) {
                                    Toast.makeText(context, response.code().toString(), Toast.LENGTH_SHORT).show()
                                }

                                override fun onFailure(call: Call<String>, t: Throwable) {

                                }

                            })
                        }) {
                            Text(text = "Delete")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    ContactRetrofitTheme {
        Greeting()
    }
}