# Escenario 13 (RN-01 con solicitudes simultaneas).
# Lanza 6 reservas IGUALES (misma sala y horario) al mismo tiempo. Debe responder exactamente un 201 y cinco 409.
# Uso (con la app corriendo):  powershell -ExecutionPolicy Bypass -File scripts\prueba-concurrencia.ps1
$base = 'http://localhost:8080'
$token = (Invoke-RestMethod -Method POST "$base/auth/login" -ContentType 'application/json' `
    -Body '{"usuario":"recepcion","contrasena":"clave123"}').token
$dia = (Get-Date).AddDays(3).ToString('yyyy-MM-dd')
$cuerpo = "{`"salaId`":5,`"miembroId`":5,`"inicio`":`"${dia}T15:00:00`",`"fin`":`"${dia}T16:00:00`",`"asistentes`":2}"
$trabajos = 1..6 | ForEach-Object {
    Start-Job -ArgumentList $base, $token, $cuerpo -ScriptBlock {
        param($base, $token, $cuerpo)
        try {
            $r = Invoke-WebRequest -Method POST "$base/api/reservas" -ContentType 'application/json' -Body $cuerpo `
                -Headers @{ Authorization = "Bearer $token" } -UseBasicParsing
            [int]$r.StatusCode
        } catch { [int]$_.Exception.Response.StatusCode }
    }
}
$codigos = $trabajos | Wait-Job | Receive-Job
$trabajos | Remove-Job
"Respuestas: $($codigos -join ', ')"
$creadas = ($codigos | Where-Object { $_ -eq 201 }).Count
if ($creadas -eq 1) { "OK: solo una reserva creada; las demas recibieron 409 (RN-01)" } else { "FALLO: se crearon $creadas reservas" }
