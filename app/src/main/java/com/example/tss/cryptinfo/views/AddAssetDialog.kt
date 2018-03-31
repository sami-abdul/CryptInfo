package com.example.tss.cryptinfo.views

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText

import com.example.tss.cryptinfo.R

import butterknife.BindView
import butterknife.ButterKnife
import com.example.tss.cryptinfo.actvities.AssetsActivity

class AddAssetDialog() : DialogFragment() {

    private lateinit var cursor: Cursor

    fun setCursor(cursor: Cursor) {
        this.cursor = cursor
    }

    internal var coin: EditText? = null
    internal var recyclerView: RecyclerView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        @SuppressLint("InflateParams") val addCoinDialogBody = inflater.inflate(R.layout.dialog_add_coin, null)

        coin = addCoinDialogBody.findViewById(R.id.dialog_add_coin_symbol)
        recyclerView = addCoinDialogBody.findViewById(R.id.add_coin_recyclerview)

        ButterKnife.bind(this, addCoinDialogBody)

        coin!!.setOnEditorActionListener { v, actionId, event ->
            addCoin()
            true
        }

        val dialog = AlertDialog.Builder(activity)
                .setView(addCoinDialogBody)
                .setMessage(getString(R.string.search_symbol))
                .setPositiveButton(getString(R.string.dialog_add)) { dialog, id -> addCoin() }
                .setNegativeButton(getString(R.string.dialog_cancel), null)
                .create()

        val dialogWindow = dialog.window
        dialogWindow?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        return dialog
    }

    override fun onResume() {
        super.onResume()

        val addButton = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        addButton.isEnabled = false

        coin!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (TextUtils.isEmpty(s)) {
                    addButton.isEnabled = false
                } else {
                    addButton.isEnabled = true
                }
            }
        })
    }

    private fun addCoin() {
        val parent = activity
        (parent as? AssetsActivity)?.addCoin(coin!!.text.toString())
        dismissAllowingStateLoss()
    }
}
