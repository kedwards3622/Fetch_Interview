package com.example.fetch_interview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fetch_interview.databinding.ActivityMainBinding
import okhttp3.*
import kotlinx.coroutines.runBlocking
import com.fasterxml.jackson.module.kotlin.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataHolder(name: String?, id: String?, listId: String, isRowHeader: Boolean = false) {
    private var rowName = name
    private var rowId = id
    private var rowListId = listId
    private var isHeader = isRowHeader

     fun getPopulatedName(): String {
         return if (this.rowName == null) {
             ""
         } else this.rowName!!
    }

    fun getId(): String {
        return if (this.rowId == null) {
            ""
        } else this.rowId!!
    }

    fun getListId(): String {
        return this.rowListId
    }

    fun isRowHeader(): Boolean{
        return this.isHeader
    }
}

data class dataRow(val name: String?, val id: String, val listId: String) {
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var adapter: ListRecyclerViewAdapter? = null

    fun asynchronousGetRequest(url: String, callback: (ArrayList<DataHolder>) -> Unit)  {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        var returnVal = ArrayList<DataHolder>()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                // do nothing
            }

            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string() ?: ""
                val mapper = jacksonObjectMapper()
                var dataList : List<dataRow> = mapper.readValue(result)
                var listIds  = LinkedHashMap<String, ArrayList<DataHolder>>()
                dataList.forEach {
                    if (listIds.contains(it.listId)) {
                        listIds[it.listId]!!.add(DataHolder(it.name,it.id,it.listId))
                    } else {
                        var addArray = ArrayList<DataHolder>()
                        addArray.add(DataHolder(it.name?.padEnd(10,' '),it.id.padEnd(5,' '),it.listId.padEnd(2,' ')))
                        listIds[it.listId] = addArray
                    }
                }
                val sortedListIds = listIds.toSortedMap()
                for (set in sortedListIds) {
                    var unsorted = set.value.sortedBy { it.getPopulatedName() }
                    returnVal.add(DataHolder(null,null,set.key,true))
                    returnVal.addAll(unsorted)
                }
                returnVal = ArrayList(returnVal.filter { it.getPopulatedName().isNotBlank() || it.isRowHeader()})
                callback(returnVal)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = "https://fetch-hiring.s3.amazonaws.com/hiring.json"

        val data = ArrayList<DataHolder>()

        asynchronousGetRequest(url) { list ->
            CoroutineScope(Dispatchers.Main).launch {
                data.addAll(list)
                adapter?.mData?.addAll(list)
                adapter?.notifyDataSetChanged()
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.list_row_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ListRecyclerViewAdapter(this, data)
        recyclerView.adapter = adapter

        setSupportActionBar(binding.toolbar)

    }

}