package br.edu.ifsp.scl.bes.prdm.intents

import android.Manifest.permission.CALL_PHONE
import android.app.ComponentCaller
import android.content.Intent
import android.content.Intent.ACTION_CALL
import android.content.Intent.ACTION_VIEW
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.ifsp.scl.bes.prdm.intents.Extras.PARAMETER_EXTRA
import br.edu.ifsp.scl.bes.prdm.intents.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var cppArl: ActivityResultLauncher<String>

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName

        amb.parameterBt.setOnClickListener {
            // Intent implícita porque não define a classe que será executada para tratar a Intent
            // Deixa a cargo do SO escolher a Activity com base no IntentFilter
            Intent("OPEN_PARAMETER_ACTIVITY_ACTION").let {
                // Colocando o valor na Intent que será enviada para a ParameterActivity
                it.putExtra(PARAMETER_EXTRA, amb.parameterTv.text.toString())
                parameterArl.launch(it)
            }
        }

        parameterArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    // Recebendo o valor devolvido pela ParameterActivity
                    result.data?.getStringExtra(PARAMETER_EXTRA).let {
                        amb.parameterTv.text = it
                    }
                }
            }

        cppArl =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    // Chamar o número
                    callPhone()
                } else {
                    Toast.makeText(
                        this,
                        "Permission required to call a number!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_activity_mi -> {
                Toast.makeText(this, "vc clicou no open", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.view_mi -> {
                val url = Uri.parse(amb.parameterTv.text.toString())
                val browserIntent = Intent(ACTION_VIEW, url)
                startActivity(browserIntent)
                true
            }

            R.id.call_mi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        callPhone()
                    }
                    else {
                        // Solicitar a permissão para o usuário
                        cppArl.launch(CALL_PHONE)
                    }
                }
                else {
                    callPhone()
                }

                true
            }

            R.id.dial_mi -> {
                true
            }

            R.id.pick_mi -> {
                true
            }

            R.id.chooser_mi -> {
                true
            }

            else -> {
                false
            }
        }
    }

    private fun callPhone() {
        val number = "tel: ${amb.parameterTv.text}"
        val callIntent = Intent(ACTION_CALL)
        callIntent.data = Uri.parse(number)
        startActivity(callIntent)
    }
}