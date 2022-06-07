package com.example.dito_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dito_list.room.Book
import com.example.dito_list.room.BookDb
import com.example.dito_list.room.Constant
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditActivity : AppCompatActivity() {

    val db by lazy { BookDb(this) }
    private var book_Id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        setupView()
        setupListener()

    }

    private fun setupView(){
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        when (intentType()) {
            Constant.TYPE_CREATE -> {
                supportActionBar!!.title = "New List Book"
                button_save.visibility = View.VISIBLE
                button_update.visibility = View.GONE
            }
            Constant.TYPE_READ -> {
                supportActionBar!!.title = "READ"
                button_save.visibility = View.GONE
                button_update.visibility = View.GONE
                getBook()
            }
            Constant.TYPE_UPDATE -> {
                supportActionBar!!.title = "EDIT"
                button_save.visibility = View.GONE
                button_update.visibility = View.VISIBLE
                getBook()
            }
        }
    }

    private fun setupListener(){
        button_save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.bookDao().addBook(
                    Book(
                        0,
                        edit_title.text.toString(),
                        edit_book.text.toString()
                    )
                )
                finish()
            }
        }
        button_update.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                db.bookDao().updateBook(
                    Book(
                        book_Id,
                        edit_title.text.toString(),
                        edit_book.text.toString()
                    )
                )
                finish()
            }
        }
    }

    private fun getBook(){
        book_Id = intent.getIntExtra("book_Id", 0)
        CoroutineScope(Dispatchers.IO).launch {
            val books = db.bookDao().getBook(book_Id).get(0)
            edit_title.setText( books.title )
            edit_book.setText( books.desc )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }
}