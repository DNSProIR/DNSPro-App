using System.IO;
using System.Text.Json;
using System.Text.Json.Serialization;

namespace DNSPro;

public sealed class DnsConfig
{
    [JsonPropertyName("serviceName")]
    public string ServiceName { get; set; } = "DNSPro";

    [JsonPropertyName("primaryIPv4")]
    public string PrimaryIPv4 { get; set; } = "80.244.11.244";

    [JsonPropertyName("secondaryIPv4")]
    public string SecondaryIPv4 { get; set; } = "80.244.11.244";

    [JsonPropertyName("checkHost")]
    public string CheckHost { get; set; } = "dnspro.ir";

    [JsonPropertyName("websiteUrl")]
    public string WebsiteUrl { get; set; } = "https://dnspro.ir";

    [JsonPropertyName("telegramUrl")]
    public string TelegramUrl { get; set; } = "https://t.me/dnspro_ir";

    [JsonPropertyName("email")]
    public string Email { get; set; } = "we@dnspro.ir";

    [JsonPropertyName("disclaimer")]
    public string Disclaimer { get; set; } =
        "DNSPro helps you reach sanction-restricted websites and provides Safe Family filtering. It is not intended for bypassing government filtering.";

    public static DnsConfig Load()
    {
        var candidates = new[]
        {
            Path.Combine(AppContext.BaseDirectory, "dns-config.json"),
            Path.GetFullPath(Path.Combine(AppContext.BaseDirectory, "..", "..", "..", "..", "..", "shared", "dns-config.json")),
            Path.GetFullPath(Path.Combine(AppContext.BaseDirectory, "..", "..", "..", "..", "shared", "dns-config.json")),
        };

        foreach (var path in candidates)
        {
            if (!File.Exists(path)) continue;
            var json = File.ReadAllText(path);
            return JsonSerializer.Deserialize<DnsConfig>(json) ?? new DnsConfig();
        }

        return new DnsConfig();
    }
}
