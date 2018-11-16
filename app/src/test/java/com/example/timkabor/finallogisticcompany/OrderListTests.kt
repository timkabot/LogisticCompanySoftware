package com.example.timkabor.finallogisticcompany

import com.example.timkabor.finallogisticcompany.network.Api
import com.example.timkabor.finallogisticcompany.presenters.OrderListActivityPresenter
import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.plugins.RxJavaPlugins
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor

/**
 * Created by Java-Ai-BOT on 23.10.2018.
 */


class OrderListTests {

    @Mock
    lateinit var client: Api
    @Mock
    lateinit var view: OrderListView
    private lateinit var underTest: OrderListActivityPresenter



    private val immediateScheduler = object : Scheduler() {
        override fun createWorker(): Worker {
            return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
        }
    }

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        RxJavaPlugins.setInitIoSchedulerHandler { immediateScheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediateScheduler }
        underTest = OrderListActivityPresenter()
        underTest.attachView(view)
        underTest.apiService = client
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun viewAttached() {
        //given @Before executed
        //then
        assert(underTest.viewState == view)
    }

    @Test
    fun clientAttached() {
        //given @Before executed
        //then
        assert(underTest.apiService == client)
    }


// @Deprecated after App token became secure
//    @Test
//    fun onSuccessResponseViewUpdated() {
//        //given
//        val response = listOf(
//                DispatchOrder(),
//                DispatchOrder()
//        )
//        Mockito.`when`(app.getAuthToken)
//        Mockito.`when`(client.getDispatchOrders("Token ")).thenReturn(Observable.just(response))
//        //when
//        underTest.downloadNextOrders()
//        //then
//        Mockito.verify(view, times(1)).updateOrders(response)
//    }
}