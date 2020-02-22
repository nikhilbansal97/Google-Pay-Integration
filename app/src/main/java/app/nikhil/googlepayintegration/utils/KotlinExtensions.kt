package app.nikhil.googlepayintegration.utils

import android.view.View

fun View.setVisible(isVisible: Boolean) {
  this.visibility = when {
    isVisible -> View.VISIBLE
    else -> View.GONE
  }
}