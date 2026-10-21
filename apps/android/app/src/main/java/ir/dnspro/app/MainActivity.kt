package ir.dnspro.app

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress

class MainActivity : ComponentActivity() {

    private var setActive: ((Boolean) -> Unit)? = null

    private val stateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val active = intent?.getBooleanExtra(DnsVpnService.EXTRA_ACTIVE, false) ?: false
            setActive?.invoke(active)
        }
    }

    private val vpnPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            startService(Intent(this, DnsVpnService::class.java))
            setActive?.invoke(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = DnsConfig.load(this)

        val filter = IntentFilter(DnsVpnService.ACTION_STATE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(stateReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(stateReceiver, filter)
        }

        setContent {
            val scope = rememberCoroutineScope()
            var active by remember { mutableStateOf(false) }
            var message by remember { mutableStateOf(config.disclaimer) }
            setActive = { active = it }

            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0B5FFF),
                    background = Color(0xFFF5F7FB),
                    onBackground = Color(0xFF1A1A1A)
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("DNSPro", fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Text(
                            getString(R.string.tagline),
                            color = Color(0xFF5F6368),
                            fontSize = 14.sp
                        )
                        Text(
                            if (active) getString(R.string.status_active) else getString(R.string.status_inactive),
                            fontWeight = FontWeight.SemiBold,
                            color = if (active) Color(0xFF0F9D58) else Color(0xFF5F6368)
                        )
                        Text(getString(R.string.vpn_hint), color = Color(0xFF5F6368), fontSize = 13.sp)

                        Button(
                            onClick = {
                                if (active) {
                                    startService(
                                        Intent(this@MainActivity, DnsVpnService::class.java)
                                            .setAction(DnsVpnService.ACTION_STOP)
                                    )
                                    active = false
                                    message = "DNSPro disabled."
                                } else {
                                    val prep = VpnService.prepare(this@MainActivity)
                                    if (prep != null) {
                                        message = "Needs permission"
                                        vpnPermission.launch(prep)
                                    } else {
                                        startService(Intent(this@MainActivity, DnsVpnService::class.java))
                                        active = true
                                        message = "DNSPro enabled."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (active) Color(0xFFD93025) else Color(0xFF0B5FFF)
                            )
                        ) {
                            Text(if (active) getString(R.string.disable) else getString(R.string.enable))
                        }

                        OutlinedButton(
                            onClick = {
                                message = "Checkingâ€¦"
                                scope.launch {
                                    val ok = withContext(Dispatchers.IO) {
                                        try {
                                            InetAddress.getAllByName(config.checkHost).isNotEmpty()
                                        } catch (_: Exception) {
                                            false
                                        }
                                    }
                                    message = if (ok) "DNSPro is reachable" else "Could not verify DNSPro"
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(getString(R.string.check_connection))
                        }

                        Text(message, color = Color(0xFF5F6368), fontSize = 13.sp)

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { open(config.websiteUrl) }) { Text("Website") }
                            TextButton(onClick = { open(config.telegramUrl) }) { Text("Telegram") }
                            TextButton(onClick = { open("mailto:${config.email}") }) { Text("Email") }
                        }
                    }
                }
            }
        }
    }

    private fun open(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    override fun onDestroy() {
        unregisterReceiver(stateReceiver)
        super.onDestroy()
    }
}
