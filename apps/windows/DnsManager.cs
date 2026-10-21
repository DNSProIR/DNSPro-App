using System.Diagnostics;
using System.IO;
using System.Net;
using System.Net.NetworkInformation;
using System.Text;

namespace DNSPro;

public sealed class DnsManager
{
    private readonly DnsConfig _config;

    public DnsManager(DnsConfig config) => _config = config;

    public bool IsDnsProActive()
    {
        foreach (var nic in NetworkInterface.GetAllNetworkInterfaces())
        {
            if (nic.OperationalStatus != OperationalStatus.Up) continue;
            if (nic.NetworkInterfaceType is NetworkInterfaceType.Loopback or NetworkInterfaceType.Tunnel)
                continue;

            var props = nic.GetIPProperties();
            foreach (var dns in props.DnsAddresses)
            {
                if (dns.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork &&
                    dns.ToString() == _config.PrimaryIPv4)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public Task EnableAsync() => RunElevatedPowerShellAsync(BuildEnableScript());

    public Task DisableAsync() => RunElevatedPowerShellAsync(BuildDisableScript());

    public async Task<bool> CheckConnectionAsync()
    {
        try
        {
            using var cts = new CancellationTokenSource(TimeSpan.FromSeconds(8));
            var addresses = await Dns.GetHostAddressesAsync(_config.CheckHost, cts.Token);
            return addresses.Length > 0;
        }
        catch
        {
            return false;
        }
    }

    private string BuildEnableScript()
    {
        var primary = Escape(_config.PrimaryIPv4);
        var secondary = Escape(string.IsNullOrWhiteSpace(_config.SecondaryIPv4)
            ? _config.PrimaryIPv4
            : _config.SecondaryIPv4);

        return $$"""
            $ErrorActionPreference = 'Stop'
            $servers = @('{{primary}}','{{secondary}}') | Select-Object -Unique
            Get-NetAdapter | Where-Object { $_.Status -eq 'Up' -and $_.HardwareInterface -eq $true } | ForEach-Object {
              Set-DnsClientServerAddress -InterfaceIndex $_.ifIndex -ServerAddresses $servers
            }
            Clear-DnsClientCache
            """;
    }

    private static string BuildDisableScript() => """
        $ErrorActionPreference = 'Stop'
        Get-NetAdapter | Where-Object { $_.Status -eq 'Up' -and $_.HardwareInterface -eq $true } | ForEach-Object {
          Set-DnsClientServerAddress -InterfaceIndex $_.ifIndex -ResetServerAddresses
        }
        Clear-DnsClientCache
        """;

    private static string Escape(string value) => value.Replace("'", "''");

    private static async Task RunElevatedPowerShellAsync(string script)
    {
        var temp = Path.Combine(Path.GetTempPath(), $"dnspro-{Guid.NewGuid():N}.ps1");
        await File.WriteAllTextAsync(temp, script, Encoding.UTF8);

        try
        {
            var psi = new ProcessStartInfo
            {
                FileName = "powershell.exe",
                Arguments = $"-NoProfile -ExecutionPolicy Bypass -File \"{temp}\"",
                UseShellExecute = true,
                Verb = "runas",
                WindowStyle = ProcessWindowStyle.Hidden,
            };

            using var process = Process.Start(psi)
                ?? throw new InvalidOperationException("Could not start elevated PowerShell.");

            await process.WaitForExitAsync();
            if (process.ExitCode != 0)
            {
                throw new InvalidOperationException(
                    "DNS change failed or was cancelled. Administrator permission is required.");
            }
        }
        finally
        {
            try { File.Delete(temp); } catch { /* ignore */ }
        }
    }
}
