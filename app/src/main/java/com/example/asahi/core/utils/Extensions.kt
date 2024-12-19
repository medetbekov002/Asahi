package com.example.asahi.core.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.example.asahi.R
import com.example.asahi.core.common.Constants.APP_ACTIVITY
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.roundToInt

object Extensions {

    fun Fragment.showToast(message: String) {
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun ImageView.loadImage(url: String) {
        Glide.with(this).load(url).into(this)
    }

    fun ImageView.loadImageUri(uri: Uri) {
        Glide.with(this).load(uri).into(this)
    }

    inline fun <T> Fragment.observeData(
        flow: Flow<T>,
        lifecycleOwner: LifecycleOwner = viewLifecycleOwner,
        state: Lifecycle.State = Lifecycle.State.STARTED,
        crossinline block: (T) -> Unit,
    ) = lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(state) {
            flow.collect { data ->
                block(data)
            }
        }
    }

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    @SuppressLint("ObsoleteSdkInt")
    fun Fragment.noInternetSnackbar() {
        view?.apply {
            val snackbar = Snackbar.make(this, R.string.no_internet_text, Snackbar.LENGTH_SHORT)
            val view = snackbar.view
            val mTextView =
                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mTextView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            } else {
                mTextView.gravity = Gravity.CENTER_HORIZONTAL
            }
                .apply {
                    view.background =
                        ContextCompat.getDrawable(context, R.drawable.snackbar_background)

                    val params = view.layoutParams
                    if (params is FrameLayout.LayoutParams) {
                        params.gravity = Gravity.TOP
                        params.topMargin = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            56f,
                            context.resources.displayMetrics
                        ).roundToInt()
                    } else if (params is CoordinatorLayout.LayoutParams) {
                        params.gravity = Gravity.TOP
                        params.topMargin = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            56f,
                            context.resources.displayMetrics
                        ).roundToInt()
                    }
                    view.layoutParams = params
                }
            snackbar.show()
        }
    }

    fun hideKeyboard() {
        val imm: InputMethodManager = APP_ACTIVITY.getSystemService(Context.INPUT_METHOD_SERVICE)
                as InputMethodManager
        imm.hideSoftInputFromWindow(APP_ACTIVITY.window.decorView.windowToken, 0)
    }

    @SuppressLint("Range")
    fun getFilenameFromUri(uri: Uri): String {
        val cursor = APP_ACTIVITY.contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME)) ?: ""
            } else {
                ""
            }
        } ?: ""
    }

    fun Int.formatAsPrice(): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(this)
    }

    fun Context.getProgressBar(): CircularProgressDrawable {
        val circularProgressDrawable = CircularProgressDrawable(this).apply {
            strokeWidth = 7f
            centerRadius = 40f
            setColorSchemeColors(ContextCompat.getColor(this@getProgressBar, R.color.black))
            start()
        }
        return circularProgressDrawable
    }

    fun Fragment.snackbar(msg: String) {
        view?.apply {
            Snackbar.make(this, msg, Snackbar.LENGTH_LONG).show()
        }
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    val EditText.textFlow: Flow<String>
        get() = callbackFlow {
            val textWatcher = doAfterTextChanged {
                if (it != null) {
                    trySend(it.toString())
                }
            }
            awaitClose { removeTextChangedListener(textWatcher) }
        }

    fun showAlertDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "OK",
        negativeButtonText: String? = null,
        onPositiveButtonClick: (() -> Unit)? = null,
        onNegativeButtonClick: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                onPositiveButtonClick?.invoke()
                dialog.dismiss()
            }

        if (negativeButtonText != null) {
            builder.setNegativeButton(negativeButtonText) { dialog, _ ->
                onNegativeButtonClick?.invoke()
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    @SuppressLint("ShowToast")
    fun Snackbar.showSnack(message: String) {
        view.let {
            Snackbar.make(it, message, Snackbar.LENGTH_INDEFINITE).apply {
                setAction("Dismiss") {
                    dismiss()
                }
            }
        }
    }

    fun <T : Any, VH : RecyclerView.ViewHolder> Fragment.handleLoadStateWithProgress(
        pagingAdapter: PagingDataAdapter<T, VH>,
        onLoadingChanged: (Boolean) -> Unit
    ) {
        lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect { loadStates ->
                val state = loadStates.refresh
                onLoadingChanged(state is LoadState.Loading)
            }
        }
    }

    fun Context.sendEmail(email: String, subject: String, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
            type = "message/rfc822"
        }
        startActivity(Intent.createChooser(intent, "Choose an email client:"))
    }

}