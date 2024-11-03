package com.example.musicplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        try {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://deezerdevs-deezer.p.rapidapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)

            val call = retrofit.getData("eminem")
            call.enqueue(object : Callback<myData> {
                override fun onResponse(call: Call<myData>, response: Response<myData>) {
                    Log.d("MainActivity", "Response received: ${response.body()}")
                    if (response.isSuccessful && response.body() != null) {
                        val dataList = response.body()?.data ?: emptyList()
                        if (dataList.isEmpty()) {
                            Log.e("MainActivity", "Error: dataList is empty")
                        } else {
                            myAdapter = MyAdapter(this@MainActivity, dataList) { song ->
                                val intent = Intent(this@MainActivity, PlayMusicActivity::class.java)
                                val bundle = Bundle()
                                bundle.putParcelable("song", song)  // Use the `song` parameter
                                intent.putExtras(bundle)
                                startActivity(intent)
                            }

                            recyclerView.adapter = myAdapter
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                        }
                    } else {
                        Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<myData>, t: Throwable) {
                    Log.e("MainActivity", "OnFailure: ${t.message}", t)
                }
            })
        } catch (e: Exception) {
            Log.e("MainActivity", "Exception occurred: ${e.message}", e)
        }
    }

}
