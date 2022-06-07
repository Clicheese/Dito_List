package com.example.dito_list

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dito_list.room.Book
import com.example.dito_list.room.BookDb
import com.example.dito_list.room.Constant
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val db by lazy { BookDb(this) }
    lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupListener()
        setupRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData(){
        CoroutineScope(Dispatchers.IO).launch {
            bookAdapter.setData(db.bookDao().getBooks())
            withContext(Dispatchers.Main) {
                bookAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun setupView (){
        supportActionBar!!.apply {
            title = "TsansBooks"
        }
    }

    private fun setupListener(){
        add_novel.setOnClickListener {
            intentEdit(Constant.TYPE_CREATE, 0)
        }
    }

    private fun setupRecyclerView () {

        bookAdapter = BookAdapter(
            arrayListOf(),
            object : BookAdapter.OnAdapterListener {
                override fun onClick(book: Book) {
                    intentEdit(Constant.TYPE_READ, book.id)
                }

                override fun onUpdate(book: Book) {
                    intentEdit(Constant.TYPE_UPDATE, book.id)
                }

                override fun onDelete(book: Book) {
                    deleteAlert(book)
                }

            })

        rv_book.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = bookAdapter
        }

    }

    private fun intentEdit(intent_type: Int, book_Id: Int) {
        startActivity(
            Intent(this, EditActivity::class.java)
                .putExtra("intent_type", intent_type)
                .putExtra("book_Id", book_Id)
        )

    }

    private fun deleteAlert(book: Book) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Confirmation")
            setMessage("Want to delete this? ${book.title}?")
            setNegativeButton("Cancel") { dialogInterface, i ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Delete") { dialogInterface, i ->
                dialogInterface.dismiss()
                CoroutineScope(Dispatchers.IO).launch{
                    db.bookDao().deleteBook(book)
                    loadData()
                }
            }
        }

        alertDialog.show()
    }
}