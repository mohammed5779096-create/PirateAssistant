package com.pirateassistant

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager

private val Navy = Color(0xFF080D18)
private val Panel = Color(0xFF121D30)
private val Blue = Color(0xFF38BDF8)
private val Muted = Color(0xFF9AAAC0)

class MainActivity : ComponentActivity() {
    private var resumeTick by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PirateApp(this, resumeTick) }
    }

    override fun onResume() {
        super.onResume()
        resumeTick++
    }
}

@Composable
fun PirateApp(context: Context, resumeTick: Int = 0) {
    var hasAccess by remember { mutableStateOf(hasUsageAccess(context)) }
    var gameState by remember { mutableStateOf("غير معروف") }
    var message by remember { mutableStateOf("امنح إذن الاستخدام لمراقبة تشغيل اللعبة.") }

    fun refresh() {
        hasAccess = hasUsageAccess(context)
        if (hasAccess) {
            val found = isFortniteLikelyForeground(context)
            gameState = if (found) "يبدو أن Fortnite مفتوحة" else "لم يتم رصد Fortnite الآن"
            message = if (found) "اللعبة مرصودة. استمتع باللعب!" else "افتح Fortnite ثم ارجع للتطبيق واضغط تحديث الحالة."
        } else {
            gameState = "الإذن غير مفعّل"
            message = "فعّل إذن الوصول إلى إحصائيات الاستخدام من إعدادات أندرويد."
        }
    }

    LaunchedEffect(resumeTick) { refresh() }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = Navy, surface = Panel, primary = Blue,
            onBackground = Color.White, onSurface = Color.White
        )
    ) {
        Column(
            Modifier.fillMaxSize().background(Navy).verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("PIRATE", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Black)
                    Text("ASSISTANT  •  v1.0", color = Blue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Surface(color = Color(0xFF12334A), shape = RoundedCornerShape(50)) {
                    Text("FREE", color = Blue, modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp), fontWeight = FontWeight.Bold)
                }
            }

            Surface(color = Panel, shape = RoundedCornerShape(22.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("FORTNITE ASSISTANT", color = Muted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(gameState, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(message, color = Muted, fontSize = 13.sp)
                    Button(
                        onClick = {
                            val launch = context.packageManager.getLaunchIntentForPackage("com.epicgames.fortnite")
                            if (launch != null) context.startActivity(launch)
                            else message = "لم أجد Fortnite مثبتة بهذا المعرّف على الجهاز."
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Blue, contentColor = Navy)
                    ) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("تشغيل Fortnite", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = {
                            if (!hasUsageAccess(context)) {
                                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                            } else refresh()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (hasAccess) "تحديث حالة اللعبة" else "تفعيل إذن مراقبة التطبيقات")
                    }
                }
            }

            Text("أدوات الأداء", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            FeatureCard(Icons.Default.Memory, "فحص الذاكرة", "افتح إعدادات الجهاز لمراجعة الذاكرة والتطبيقات المستهلكة.")
            FeatureCard(Icons.Default.Thermostat, "مراقبة الحرارة", "أندرويد لا يتيح لكل الأجهزة قراءة حرارة المعالج بدقة من تطبيق عادي.")
            FeatureCard(Icons.Default.Speed, "تحسين الأداء", "اقتراحات آمنة: أغلق التطبيقات غير المستخدمة، وخفّض الرسوم من إعدادات اللعبة.")
            FeatureCard(Icons.Default.NotificationsActive, "تذكير Ready", "تذكير يدوي بعد انتهاء المباراة؛ لا نضغط أزرار اللعبة تلقائيًا.")

            Surface(color = Panel, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("PREMIUM — مخطط للمرحلة القادمة", color = Blue, fontWeight = FontWeight.Bold)
                    Text("تقارير جلسات اللعب • سجل الأداء • تصدير الإحصائيات • تخصيص التنبيهات", color = Muted, fontSize = 13.sp)
                    Text("لن يتم تفعيل الدفع قبل اختبار الميزات وتحديد طريقة بيع مناسبة وبمساعدة ولي الأمر.", color = Muted, fontSize = 11.sp)
                }
            }
            Text("Pirate Assistant لا يعدّل ملفات Fortnite ولا يضمن منع العقوبات أو زيادة FPS.", color = Muted, fontSize = 11.sp)
        }
    }
}

@Composable
fun FeatureCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, detail: String) {
    Surface(color = Panel, shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Blue, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(detail, color = Muted, fontSize = 12.sp)
            }
        }
    }
}

fun hasUsageAccess(context: Context): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    return appOps.checkOpNoThrow(
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        Process.myUid(),
        context.packageName
    ) == AppOpsManager.MODE_ALLOWED
}

fun isFortniteLikelyForeground(context: Context): Boolean {
    val manager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    val now = System.currentTimeMillis()
    val events = manager.queryEvents(now - 10 * 60_000, now)
    val e = UsageEvents.Event()
    var last: String? = null
    while (events.hasNextEvent()) {
        events.getNextEvent(e)
        if (e.packageName == context.packageName) continue
        if (e.eventType == UsageEvents.Event.ACTIVITY_RESUMED) last = e.packageName
        else if (e.eventType == UsageEvents.Event.ACTIVITY_PAUSED && e.packageName == last) last = null
    }
    return last == "com.epicgames.fortnite"
}
