package com.example.timkabor.finallogisticcompany

/**
 * Created by Java-Ai-BOT on 15.11.2018.
 */
interface SignatureDeliveryNotifiable {
    fun signatureDelivered(order_id: String = "None")
    fun signatureSendFail()
}