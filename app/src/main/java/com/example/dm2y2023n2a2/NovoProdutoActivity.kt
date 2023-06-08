package com.example.dm2y2023n2a2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NovoProdutoActivity : AppCompatActivity() {
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.novo_produto_activity)

        findViewById<Button>(R.id.btnGravar).setOnClickListener {

            val nome = findViewById<EditText>(R.id.edtNomeProduto).text.toString()
            val valor = findViewById<EditText>(R.id.edtValorProduto).text.toString().toDouble()

            val dados = hashMapOf(
                "nome" to nome,
                "valor" to valor,
            )

            db.collection("produtos").add(dados)
                .addOnSuccessListener {
                Toast.makeText(this, "Sucesso!", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao gravar os dados!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}