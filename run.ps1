param([switch]$BuildOnly)
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$driverVersion = "9.4.0"
$driver = Join-Path $PSScriptRoot ("lib\mysql-connector-j-" + $driverVersion + ".jar")
if (-not (Get-Command javac -ErrorAction SilentlyContinue) -or -not (Get-Command java -ErrorAction SilentlyContinue) -or -not (Get-Command jar -ErrorAction SilentlyContinue)) {
    throw "Java JDK 8 or newer is required. Install a JDK, restart the terminal, then run .\run.bat."
}
# Different java.exe and javac.exe versions are common with NetBeans/JDK installations.
# Compile compatible Java 8 bytecode even if javac is JDK 17/21/26, so Java 8+ can run it.
$compilerVersion = (& javac -version | Out-String).Trim()
if ($compilerVersion -match '^javac\s+1\.8') {
    $compatFlags = @('-source', '8', '-target', '8')
} elseif ($compilerVersion -match '^javac\s+(\d+)') {
    if ([int]$Matches[1] -lt 9) {
        throw "JDK 8 or newer is required. Found: $compilerVersion"
    }
    $compatFlags = @('--release', '8')
} else {
    throw "Unable to determine the Java compiler version: $compilerVersion"
}
Write-Host "Compiler: $compilerVersion (targeting Java 8+ compatible bytecode)"
if (-not (Test-Path $driver)) {
    New-Item -ItemType Directory -Force (Split-Path $driver) | Out-Null
    Write-Host "Downloading MySQL Connector/J $driverVersion from Maven Central..."
    $url = "https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/$driverVersion/mysql-connector-j-$driverVersion.jar"
    Invoke-WebRequest -Uri $url -OutFile $driver -UseBasicParsing
}
# Old NetBeans build artifacts include incompatible Java 26 .class files.
# Clear them before every build so only the selected JDK bytecode is packaged.
if (Test-Path "build\classes") { Remove-Item "build\classes" -Recurse -Force }
New-Item -ItemType Directory -Force "build\classes\emartproject\images", "dist" | Out-Null
& javac @compatFlags -encoding UTF-8 -d "build\classes" (Get-ChildItem "src\emartproject\*.java" | ForEach-Object FullName)
if ($LASTEXITCODE -ne 0) { throw "Java compilation failed." }
Copy-Item "src\emartproject\images\*.png" "build\classes\emartproject\images\" -Force
$manifest = "build\emart-manifest.mf"
[System.IO.File]::WriteAllText((Join-Path $PSScriptRoot $manifest),
    "Manifest-Version: 1.0`r`nMain-Class: emartproject.EMartProject`r`nClass-Path: ../lib/mysql-connector-j-$driverVersion.jar`r`n`r`n",
    (New-Object System.Text.UTF8Encoding($false)))
& jar cfm "dist\EMartProject.jar" $manifest -C "build\classes" .
if ($LASTEXITCODE -ne 0) { throw "JAR packaging failed." }
Write-Host "E-Mart built. Six PNG category icons are included."
if ($BuildOnly) { exit 0 }

# If Windows has a MySQL service, try to start it; lack of elevation is non-fatal.
$service = Get-Service -Name 'MySQL*' -ErrorAction SilentlyContinue | Select-Object -First 1
if ($service -and $service.Status -ne 'Running') {
    try { Start-Service -Name $service.Name -ErrorAction Stop }
    catch { Write-Warning "MySQL service is stopped. Start it in Windows Services if login fails." }
}

$settings = Join-Path $env:LOCALAPPDATA "EMart"
$whoFile = Join-Path $settings "mysql.user"
$secretFile = Join-Path $settings "mysql.secret"
New-Item -ItemType Directory -Force $settings | Out-Null

function Save-LocalCredentials {
    $dbUser = Read-Host "Local MySQL username [root]"
    if ([string]::IsNullOrWhiteSpace($dbUser)) { $dbUser = "root" }
    $secret = Read-Host "Local MySQL password" -AsSecureString
    Set-Content -LiteralPath $whoFile -Value $dbUser -Encoding UTF8
    $secret | ConvertFrom-SecureString | Set-Content -LiteralPath $secretFile -Encoding UTF8
}

if (-not (Test-Path $whoFile) -or -not (Test-Path $secretFile)) {
    Save-LocalCredentials
}

$verified = $false
for ($attempt = 0; $attempt -lt 2; $attempt++) {
    $dbUser = (Get-Content -LiteralPath $whoFile -Raw).Trim()
    $encryptedPassword = (Get-Content -LiteralPath $secretFile -Raw).Trim()
    $secure = ConvertTo-SecureString -String $encryptedPassword
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
    try { $dbPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
    finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }

    $env:EMART_DB_USER = $dbUser
    $env:EMART_DB_PASSWORD = $dbPassword
    & java -cp ("dist\EMartProject.jar;" + $driver) emartproject.DatabaseCheck
    if ($LASTEXITCODE -eq 0) { $verified = $true; break }

    Write-Warning "MySQL connection failed. Check the MySQL server and saved credentials."
    Remove-Item Env:EMART_DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:EMART_DB_USER -ErrorAction SilentlyContinue
    if ($attempt -eq 0) {
        Save-LocalCredentials
    }
}
if (-not $verified) {
    throw "Could not connect to MySQL. Ensure the service is running, or correct credentials in the launcher."
}
try {
    & java -cp ("dist\EMartProject.jar;" + $driver) emartproject.EMartProject
    if ($LASTEXITCODE -ne 0) { throw "E-Mart exited with code $LASTEXITCODE" }
} finally {
    Remove-Item Env:EMART_DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:EMART_DB_USER -ErrorAction SilentlyContinue
}
