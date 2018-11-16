package com.example.timkabor.finallogisticcompany.presenters

import android.graphics.Bitmap
import android.util.Log
import com.example.timkabor.finallogisticcompany.App
import com.example.timkabor.finallogisticcompany.network.Api
import com.example.timkabor.finallogisticcompany.SignatureDeliveryNotifiable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.net.UnknownHostException

/**
 * Created by Java-Ai-BOT on 15.11.2018.
 */
class SignatureSender {
    companion object {
        private val TAG = "SIGNATURE_SENDER"
        fun sendSignature(bitmap: Bitmap, order_id: String, viewState: SignatureDeliveryNotifiable,
                          apiService: Api, saveOnFail: Boolean = true) {
            Log.d(TAG, "Signature collected -> H: " + bitmap.height + " W: " + bitmap.width)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)

            // https://medium.com/@adinugroho/upload-image-from-android-app-using-retrofit-2-ae6f922b184c
            val mediaType = MediaType.parse("Content-Disposition: form-data; name=\"dispatch_id\"\n%s".format(order_id))
            val reqFile = RequestBody.create(mediaType, baos.toByteArray())
            val file = MultipartBody.Part.createFormData("dispatch_id", order_id)
            val id = MultipartBody.Part.createFormData("image", "ogg%s.png".format(order_id), reqFile)


            val obs = apiService.uploadSignature("Token " + App.getAuthToken(), file, id)
            Log.d(TAG, "Sending started!")
            obs.observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ result ->
                        run {
                            if (result != null) {
                                Log.d(TAG, "Received data successfully!")
                            } else {
                                Log.d(TAG, "Received null data!")
                            }
                            viewState.signatureDelivered(order_id)
                        }
                    }, { error ->
                        Log.d(TAG, error.message)
                        if (error is HttpException)
                            Log.d(TAG, error.response().raw().toString())
                        else if (error is UnknownHostException)
                            Log.d(TAG, error.message)
                        viewState.signatureSendFail()
                        if (saveOnFail)
                            saveImage(bitmap, order_id)
                    })
            baos.close()
        }

        /**
         * in case if no internet and need to save image
         */
        private fun saveImage(bitmap: Bitmap, fileName: String) {
            App.writeToFile(bitmap, fileName)
        }
    }
}