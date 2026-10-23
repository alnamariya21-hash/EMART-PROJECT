param([switch]$BuildOnly)
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

$driverVersion = "9.4.0"
$driver = Join-Path $PSScriptRoot ("lib\mysql-connector-j-" + $driverVersion + ".jar")
if (-not (Get-Command javac -ErrorAction SilentlyContinue) -or -not (Get-Command java -ErrorAction SilentlyContinue) -or -not (Get-Command jar -ErrorAction SilentlyContinue)) {
    throw "Java JDK 8 or newer is required. Install a JDK, restart the terminal, then run .\run.bat."
}
if (-not (Test-Path $driver)) {
    New-Item -ItemType Directory -Force (Split-Path $driver) | Out-Null
    Write-Host "Downloading MySQL Connector/J $driverVersion from Maven Central..."
    $url = "https://repo.maven.apache.org/maven2/com/mysql/mysql-connector-j/$driverVersion/mysql-connector-j-$driverVersion.jar"
    Invoke-WebRequest -Uri $url -OutFile $driver -UseBasicParsing
}
New-Item -ItemType Directory -Force "build\classes\emartproject\images", "dist" | Out-Null
& javac -encoding UTF-8 -d "build\classes" (Get-ChildItem "src\emartproject\*.java" | ForEach-Object FullName)
if ($LASTEXITCODE -ne 0) { throw "Java compilation failed." }
Copy-Item "src\emartproject\images\*.png" "build\classes\emartproject\images\" -Force
$manifest = "build\emart-manifest.mf"
[System.IO.File]::WriteAllText((Join-Path $PSScriptRoot $manifest),
    "Manifest-Version: 1.0\r\nMain-Class: emartproject.EMartProject\r\nClass-Path: ../lib/mysql-connector-j-$driverVersion.jar\r\n\r\n",
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
if (-not (Test-Path $whoFile) -or -not (Test-Path $secretFile)) {
    New-Item -ItemType Directory -Force $settings | Out-Null
    $dbUser = Read-Host "Local MySQL username [root]"
    if ([string]::IsNullOrWhiteSpace($dbUser)) { $dbUser = "root" }
    $secret = Read-Host "Local MySQL password" -AsSecureString
    Set-Content -LiteralPath $whoFile -Value $dbUser -Encoding UTF8
    $secret | ConvertFrom-SecureString | Set-Content -LiteralPath $secretFile -Encoding UTF8
}
$dbUser = (Get-Content -LiteralPath $whoFile -Raw).Trim()
$encryptedPassword = (Get-Content -LiteralPath $secretFile -Raw).Trim()
$secure = ConvertTo-SecureString -String $encryptedPassword
$ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure)
try { $dbPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr) }
finally { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr) }
$env:EMART_DB_USER = $dbUser
$env:EMART_DB_PASSWORD = $dbPassword
try {
    & java -cp ("dist\EMartProject.jar;" + $driver) emartproject.EMartProject
    if ($LASTEXITCODE -ne 0) { throw "E-Mart exited with code $LASTEXITCODE" }
} finally {
    Remove-Item Env:EMART_DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:EMART_DB_USER -ErrorAction SilentlyContinue
}
