package com.example.filmsearchproject


import android.R
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.filmsearchproject.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL



class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val adapter = RVAdapter()
    lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var apiData: String = ""
    var pageMain = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = resources.getColor(R.color.black);
        binding.recyclerViewMain.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMain.adapter = adapter
        if (isOnline(this)) {
            pageMain = 1
            responseThread(pageMain)
        } else {
            Toast.makeText(
                this,
                "Проверьте соединение с интернетом и попробуйте ещё раз.",
                Toast.LENGTH_SHORT
            ).show()
        }
        swipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            if (isOnline(this)) {
                adapter.listOfElements.clear()
                pageMain = 1
                responseThread(pageMain)
                Toast.makeText(this, "Страница обновилась", Toast.LENGTH_SHORT).show()
                swipeRefreshLayout.isRefreshing = false
            } else {
                Toast.makeText(
                    this,
                    "Проверьте соединение с интернетом и попробуйте ещё раз.",
                    Toast.LENGTH_SHORT
                ).show()
                swipeRefreshLayout.isRefreshing = false
            }
        }
        val editText = binding.searchingText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                filterRV(s.toString())
            }
        })
        val newContentButton: Button = binding.newContentButton
        newContentButton.setOnClickListener {
            if (isOnline(this)) {
                pageMain++
                responseThread(pageMain)
            } else {
                Toast.makeText(
                    this,
                    "Проверьте соединение с интернетом и попробуйте ещё раз.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun filterRV(searchString: String) {
        val filteredRV: ArrayList<RVElement> = ArrayList()
        for (item in adapter.oldListOfElements) {
            if (item.title.toLowerCase().contains(searchString.toLowerCase())) {
                filteredRV.add(item)
            }
        }
        adapter.filterRV(filteredRV)
    }

    private fun responseThread(page: Int) {
        val thread = Thread {
            try {
                val temp =
                    "https://api.themoviedb.org/3/discover/movie?page=$page&api_key=6ccd72a2a8fc239b13f209408fc31c33&language=ru-RU&sort_by=popularity.desc"
                val url = URL(temp)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream: InputStream = httpURLConnection.inputStream;
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
                var string: String? = ""
                while (string != null) {
                    string = bufferedReader.readLine()
                    apiData += string
                }
                if (apiData.isNotEmpty()) {
                    val jsonArray: JSONArray = JSONObject(apiData).getJSONArray("results")
                    var i = 0
                    while (i != jsonArray.length()) {
                        val element = RVElement("", "", "", "")
                        val jsonTitle: JSONObject = jsonArray.getJSONObject(i)
                        element.title = jsonTitle.getString("title")
                        val jsonDescription: JSONObject = jsonArray.getJSONObject(i)
                        element.description = jsonDescription.getString("overview")
                        val jsonDate: JSONObject = jsonArray.getJSONObject(i)
                        element.date = jsonDate.getString("release_date")
                        val jsonImage: JSONObject = jsonArray.getJSONObject(i)
                        element.image = jsonImage.getString("poster_path")
                        adapter.addElement(element)
                        i++
                    }
                }
                apiData = ""
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val allNetworks: Array<Network> = connectivityManager.allNetworks
        for (network in allNetworks) {
            val capability = connectivityManager.getNetworkCapabilities(network)
            if (capability != null) {
                when {
                    capability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        }
        return false
    }
}
