package ir.dnspro.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import java.io.FileInputStream
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class DnsVpnService : VpnService() {

    private var tun: ParcelFileDescriptor? = null
    private val running = AtomicBoolean(false)
    private var worker: Thread? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopVpn()
                stopSelf()
                return START_NOT_STICKY
            }
            else -> startVpn()
        }
        return START_STICKY
    }

    private fun startVpn() {
        if (running.get()) return
        val config = DnsConfig.load(this)
        startForeground(NOTIFICATION_ID, buildNotification())

        // DNS-only VPN: set DNS servers on a local interface without routing all traffic.
        val builder = Builder()
            .setSession("DNSPro")
            .addAddress("10.10.10.2", 32)
            .addDnsServer(config.primaryIPv4)
            .setMtu(1500)
            .setBlocking(false)

        if (config.secondaryIPv4.isNotBlank()) {
            builder.addDnsServer(config.secondaryIPv4)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setMetered(false)
        }

        tun = builder.establish()
        if (tun == null) {
            stopSelf()
            return
        }

        running.set(true)
        // Keep the TUN fd open while the service runs; DNS is applied via Builder.addDnsServer.
        worker = thread(name = "dnspro-vpn", isDaemon = true) {
            holdTun(tun!!)
        }
        sendBroadcast(Intent(ACTION_STATE).setPackage(packageName).putExtra(EXTRA_ACTIVE, true))
    }

    private fun stopVpn() {
        running.set(false)
        worker?.interrupt()
        worker = null
        try {
            tun?.close()
        } catch (_: Exception) {
        }
        tun = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        sendBroadcast(Intent(ACTION_STATE).setPackage(packageName).putExtra(EXTRA_ACTIVE, false))
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    private fun holdTun(iface: ParcelFileDescriptor) {
        val input = FileInputStream(iface.fileDescriptor)
        val packet = ByteArray(32767)
        while (running.get()) {
            try {
                // Drain any unexpected packets so the TUN does not block.
                if (input.read(packet) < 0) break
            } catch (_: Exception) {
                break
            }
        }
    }

    private fun buildNotification(): Notification {
        val channelId = "dnspro_vpn"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(channelId, "DNSPro", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val launch = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentIntent(launch)
            .setOngoing(true)
            .build()
    }

    companion object {
        const val ACTION_STOP = "ir.dnspro.app.STOP"
        const val ACTION_STATE = "ir.dnspro.app.STATE"
        const val EXTRA_ACTIVE = "active"
        private const val NOTIFICATION_ID = 42
    }
}
