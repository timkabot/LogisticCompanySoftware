package com.example.timkabor.finallogisticcompany.models

/**
 * Created by Java-Ai-BOT on 15.11.2018.
 */
data class DispatchOrderResponse(
        val next: Int?,
        val previous: Int?,
        val count: Int,
        val total_pages: Int,
        var results: MutableList<DispatchOrder>? = null
)