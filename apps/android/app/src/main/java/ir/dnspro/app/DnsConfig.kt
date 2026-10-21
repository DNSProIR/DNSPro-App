package ir.dnspro.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

data class DnsConfig(
    @SerializedName("serviceName") val serviceName: String = "DNSPro",
    @SerializedName("primaryIPv4") val primaryIPv4: String = "80.244.11.244",
    @SerializedName("secondaryIPv4") val secondaryIPv4: String = "80.244.11.244",
    @SerializedName("checkHost") val checkHost: String = "dnspro.ir",
    @SerializedName("websiteUrl") val websiteUrl: String = "https://dnspro.ir",
    @SerializedName("telegramUrl") val telegramUrl: String = "https://t.me/dnspro_ir",
    @SerializedName("email") val email: String = "we@dnspro.ir",
    @SerializedName("disclaimer") val disclaimer: String =
        "DNSPro helps you reach sanction-restricted websites and provides Safe Family filtering. It is not intended for bypassing government filtering."
) {
    companion object {
        fun load(context: Context): DnsConfig {
            return try {
                context.assets.open("dns-config.json").bufferedReader().use { reader ->
                    Gson().fromJson(reader, DnsConfig::class.java) ?: DnsConfig()
                }
            } catch (_: Exception) {
                DnsConfig()
            }
        }
    }
}
