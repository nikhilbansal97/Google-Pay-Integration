package app.nikhil.googlepayintegration.models

data class RequestConfiguration(
  val apiVersion: Int,
  val apiVersionMinor: Int,
  val allowedPaymentMethods: List<MutableMap<String, Any>>
)