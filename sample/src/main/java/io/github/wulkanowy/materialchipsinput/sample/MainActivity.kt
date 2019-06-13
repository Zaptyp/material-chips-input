package io.github.wulkanowy.materialchipsinput.sample

import android.Manifest.permission.READ_CONTACTS
import android.os.Bundle
import android.provider.ContactsContract.Contacts.CONTENT_URI
import android.provider.ContactsContract.Contacts.DISPLAY_NAME
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentResolverCompat
import com.google.android.material.chip.Chip
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var contactList: MutableList<Chip>? = null

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contactList = mutableListOf()

        disposable.add(RxPermissions(this)
                .request(READ_CONTACTS)
                .subscribeBy {
                    if (!it || contactList?.isNotEmpty() == true) return@subscribeBy

                    val contactsQuery = ContentResolverCompat.query(contentResolver, CONTENT_URI, null, null, null, null, null)
                            ?: return@subscribeBy

                    while (contactsQuery.moveToNext()) {
                        contactList?.add(Chip(this).apply {
                            text = contactsQuery.run { getString(getColumnIndex(DISPLAY_NAME)) }
                        })
                    }

                    contactsQuery.close()
                    mainChipsInput.itemList = contactList
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}