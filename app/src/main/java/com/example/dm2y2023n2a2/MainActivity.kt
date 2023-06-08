package com.example.dm2y2023n2a2

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Objects

class MainActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btnNovoProduto).setOnClickListener {
            val intent = Intent(this, NovoProdutoActivity::class.java)
            startActivity(intent)
        }

        findViewById<ListView>(R.id.lstProdutos).setOnItemLongClickListener { adapterView, view, position, id ->
            val produto = adapterView.adapter.getItem(position) as String
            val nome = produto.substringAfter("Nome: ").substringBefore(" | Valor:")
            val valor = produto.substringAfter("Valor: ").toDouble()

            val builder = AlertDialog.Builder(this)
            builder
                .setTitle("Excluir este produto?")
                .setMessage("Tem certeza de que deseja excluir este produto?")
                .setPositiveButton("Tenho certeza") { dialog, wich ->
                    db.collection("produtos")
                        .whereEqualTo("nome", nome)
                        .whereEqualTo("valor", valor)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val documents = querySnapshot.documents

                            if(documents.isNotEmpty()) {
                                val documentId = documents[0].id

                                db.collection("produtos")
                                    .document(documentId)
                                    .delete()
                                    .addOnSuccessListener {
                                        updateList()
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d(TAG, "Erro ao excluir: ${exception}")
                                    }
                            }
                            Toast.makeText(this, "Produto removido!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Erro: ${exception}")
                        }
                }
                .setNegativeButton("Não") { dialog, wich ->
                    Toast.makeText(this, "Produto não foi removido",
                        Toast.LENGTH_SHORT).show()
                }
                .show()
            true
        }

    }

    override fun onResume() {
        super.onResume()

        updateList()
    }

    fun updateList() {
        db.collection("produtos")
            .get()
            .addOnSuccessListener { result ->
                if(result == null) {
                    Log.d(TAG, "Documento não encontrado!")
                } else {
                    val produtos = result.map { document ->

                        val nome = document.getString("nome")
                        val valor = document.getDouble("valor")

                        "Nome: $nome | Valor: $valor"
                    }

                    findViewById<ListView>(R.id.lstProdutos).adapter = ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        produtos
                    )
                }

            }
            .addOnFailureListener { expection ->
                Log.d(TAG, "deu o seguinte erro: ${expection}")
            }
    }


}