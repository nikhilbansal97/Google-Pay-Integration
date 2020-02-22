package app.nikhil.googlepayintegration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.nikhil.googlepayintegration.utils.createIsReadyToPayRequest
import app.nikhil.googlepayintegration.utils.createPaymentsClient
import app.nikhil.googlepayintegration.utils.extractPaymentMethodToken
import app.nikhil.googlepayintegration.utils.paymentDataRequestJson
import app.nikhil.googlepayintegration.utils.setVisible
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import kotlinx.android.synthetic.main.activity_main.btnGooglePay
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

  companion object {
    private const val REQUEST_PAYMENT_DATA_RESOLVE = 100
  }

  private lateinit var paymentsClient: PaymentsClient

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    paymentsClient = createPaymentsClient(this)
    enablePayButtonIfIfReady()
  }

  private fun enablePayButtonIfIfReady() {
    val readyToPayRequest = createIsReadyToPayRequest()
    val task = paymentsClient.isReadyToPay(readyToPayRequest)
    task.addOnCompleteListener { isReadyTask ->
      try {
        when {
          (isReadyTask.result as Boolean) -> {
            btnGooglePay.setVisible(true)
            btnGooglePay.setOnClickListener { requestPayment() }
          }
          else -> btnGooglePay.setVisible(false)
        }
      } catch (e: Exception) {
        println(e.message)
      }
    }
  }

  private fun requestPayment() {
    val paymentDataRequest = PaymentDataRequest.fromJson(paymentDataRequestJson)

    // Open the Google Pay bottom sheet
    AutoResolveHelper.resolveTask(
      paymentsClient.loadPaymentData(paymentDataRequest),
      this,
      REQUEST_PAYMENT_DATA_RESOLVE
    )
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    when (requestCode) {
      REQUEST_PAYMENT_DATA_RESOLVE -> {
        when (resultCode) {
          Activity.RESULT_OK -> {
            data?.let { PaymentData.getFromIntent(data)?.let { handlePaymentSuccess(it) } }
          }
          Activity.RESULT_CANCELED -> {
            // User cancelled without selecting a payment method.
          }
          AutoResolveHelper.RESULT_ERROR -> {
            AutoResolveHelper.getStatusFromIntent(data)?.let { handleError(it.statusCode) }
          }
        }
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  private fun handlePaymentSuccess(paymentData: PaymentData) {
    val paymentMethodToken = extractPaymentMethodToken(paymentData)
    // TODO: Use this token to perform transaction through your payment gateway
  }

  private fun handleError(statusCode: Int) {
  }
}
