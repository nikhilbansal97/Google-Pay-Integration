package app.nikhil.googlepayintegration.utils

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import com.google.gson.Gson
import org.json.JSONObject

private val transactionInfo by lazy {
  mutableMapOf<String, String>().apply {
    put("totalPrice", "10")
    put("totalPriceStatus", "FINAL")
    put("currencyCode", "USD")
  }
}

private val tokenizationSpecification by lazy {
  mutableMapOf<String, Any>().apply {
    put("type", TOKENIZATION_METHOD_PAYMENT_GATEWAY)
    put(
      "parameters", mapOf(
        "gateway" to GATEWAY_TEST,
        "gatewayMerchantId" to GATEWAY_MERCHANT_ID_TEST
      )
    )
  }
}

private val merchantInfo by lazy {
  mutableMapOf<String, String>().apply {
    put("merchantId", "01234567890")
    put("merchantName", "Example Merchant")
  }
}

val paymentDataRequestJson by lazy {
  Gson().toJson(createBaseRequestConfiguration().apply {
    put("transactionInfo", transactionInfo)
    put("merchantInfo", merchantInfo)
  })
}

fun createPaymentsClient(activity: AppCompatActivity): PaymentsClient {
  val walletOptions = Wallet.WalletOptions.Builder()
    // Change it to ENVIRONMENT_PRODUCTION when shipping to Google Play!
    .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
    .build()
  return Wallet.getPaymentsClient(activity, walletOptions)
}

fun createBasePaymentMethod() =
  mutableMapOf<String, Any>().apply {
    put("type", PAYMENT_METHOD_CARD)
    put("tokenizationSpecification", tokenizationSpecification)
    put("parameters", mutableMapOf<String, Any>().apply {
      put("allowedCardNetworks", listOf(CARD_NETWORK_VISA, CARD_NETWORK_MASTERCARD))
      put("allowedAuthMethods", listOf(AUTH_METHOD_PAN_ONLY, AUTH_METHOD_CRYPTOGRAM_3DS))
      put("billingAddressRequired", true)
      put("billingAddressParameters", mutableMapOf("format" to "FULL"))
    })
  }

fun createBaseRequestConfiguration() =
  mutableMapOf<String, Any>().apply {
    put("apiVersion", 2)
    put("apiVersionMinor", 0)
    put("allowedPaymentMethods", listOf(createBasePaymentMethod()))
  }

fun createIsReadyToPayRequest(): IsReadyToPayRequest =
  IsReadyToPayRequest.fromJson(Gson().toJson(createBaseRequestConfiguration()))

fun extractPaymentMethodToken(paymentData: PaymentData): String {
  return JSONObject(paymentData.toJson())
    .getJSONObject("paymentMethodData")
    .getJSONObject("tokenizationData")
    .getString("token")
}