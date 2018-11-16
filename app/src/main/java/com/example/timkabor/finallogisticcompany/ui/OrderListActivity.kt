package com.example.timkabor.finallogisticcompany.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.timkabor.finallogisticcompany.App
import com.example.timkabor.finallogisticcompany.DaggerNetComponent
import com.example.timkabor.finallogisticcompany.models.DispatchOrder
import com.example.timkabor.finallogisticcompany.OrderListView
import com.example.timkabor.finallogisticcompany.presenters.OrderListActivityPresenter
import com.example.timkabor.finallogisticcompany.R
import com.example.timkabor.finallogisticcompany.ui.adapters.OrderListAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_order_list.*


class OrderListActivity : MvpAppCompatActivity(), OrderListView {


    override fun signatureDelivered(order_id: String) {
        App.removeSignature(order_id)
        Toast.makeText(this, "Signature delivered!", Toast.LENGTH_SHORT).show()
    }

    override fun signatureSendFail() {
        Toast.makeText(this, "Failed in signature delivery! Signature still in memory.", Toast.LENGTH_SHORT).show()
    }

    lateinit var recyclerView: RecyclerView
    @InjectPresenter
    lateinit var presenter: OrderListActivityPresenter
    lateinit var adapter: OrderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)

        DaggerNetComponent.create().inject(presenter)

        initRecyclerView()
        initLogoutButton()

        if (isNetworkAvailable()) {
            presenter.downloadNextOrders()
            checkSignatures()
        } else
            showOrdersFromCache()
    }

    private fun checkSignatures() {
        val a = App.readAllSignatures()
        if (a != null && a.isNotEmpty()) {
            presenter.sendAllImages(a)
        }
    }

    private fun initLogoutButton() {
        logout_button.setOnClickListener {
            run {
                App.removeAuthToken()
                val i = Intent(applicationContext, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                startActivity(i)
                this.finish()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        if (isNetworkAvailable()) {
            presenter.downloadNextOrders()
            checkSignatures()
        }
    }


    fun initRecyclerView() {
        recyclerView = findViewById(R.id.order_list)
        val layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView.layoutManager = layoutManager

        adapter = OrderListAdapter(ArrayList()) { partItem: DispatchOrder -> partItemClicked(partItem) }
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (layoutManager.findLastCompletelyVisibleItemPosition() >= layoutManager.itemCount - 1)
                    presenter.downloadNextOrders()
            }
        })
    }


    override fun updateOrders(orders: List<DispatchOrder>) {
        val position = adapter.orderList.size
        Log.d(javaClass.simpleName, "old size: " + position)
        (adapter.orderList as ArrayList).addAll(orders)
        Log.d(javaClass.simpleName, "new size: " + adapter.orderList.size)

        adapter.notifyDataSetChanged()
        saveOrdersToCache(adapter.orderList)
    }

    fun saveOrdersToCache(orders: List<DispatchOrder>) {
        val gson = Gson()
        val json: String = gson.toJson(orders)
        App.mainPrefs!!.prefs.edit().putString(App.mainPrefs!!.ORDER_LIST, json).apply()
        Log.d(javaClass.name, "Now in cache (orders): " + orders.size)
    }


    override fun clearList() {
        (adapter.orderList as ArrayList).clear()
    }

    override fun showOrdersFromCache() {
        val gson = Gson()
        val json = App.mainPrefs!!.prefs.getString(App.mainPrefs!!.ORDER_LIST, "NO_LIST")
        val type = object : TypeToken<List<DispatchOrder>>() {
        }.type

        clearList()

        val orderListFromCache = gson.fromJson<Any>(json, type) as List<DispatchOrder>
        updateOrders(orderListFromCache)
    }

    private fun partItemClicked(partItem: DispatchOrder) {
        val i = Intent(applicationContext, MapActivity::class.java)
        i.putExtra(DispatchOrder::class.java.canonicalName, partItem)
        startActivity(i)
    }

    @SuppressLint("CommitPrefEdits")
    override fun goToLoginActivity() {
        App.mainPrefs!!.prefs.edit().remove(App.mainPrefs!!.TOKEN_NAME)
        val i = Intent(applicationContext, LoginActivity::class.java)
        startActivity(i)
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = App.appContext!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}