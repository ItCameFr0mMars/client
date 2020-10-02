package me.zeroeightsix.kami.module.modules.client

import me.zeroeightsix.kami.event.events.SafeTickEvent
import me.zeroeightsix.kami.gui.clickGui.KamiGuiClickGui
import me.zeroeightsix.kami.module.Module
import me.zeroeightsix.kami.setting.Setting
import me.zeroeightsix.kami.setting.Settings
import me.zeroeightsix.kami.util.TimerUtils
import org.lwjgl.input.Keyboard
import kotlin.math.round

@Module.Info(
        name = "ClickGUI",
        description = "Opens the Click GUI",
        showOnArray = Module.ShowOnArray.OFF,
        category = Module.Category.CLIENT,
        alwaysListening = true
)
object ClickGUI : Module() {
    private val scaleSetting = register(Settings.integerBuilder("Scale").withValue(100).withRange(10, 400).build())val customFont = register(Settings.b("CustomFont", true)) // For the sake of dumb Minecraftia simps
    val scrollSpeed = register(Settings.f("ScrollSpeed", 1f))
    val triggerSpeed = register(Settings.f("TriggerSpeed", 0.5f))

    private var prevScale = scaleSetting.value / 100.0
    private var scale = prevScale
    private val settingTimer = TimerUtils.StopTimer()

    fun resetScale() {
        scaleSetting.value = 100
        prevScale = 1.0
        scale = 1.0
    }

    fun getScaleFactor(): Double {
        return (prevScale + (scale - prevScale) * mc.renderPartialTicks) * 2.0
    }

    override fun onUpdate(event: SafeTickEvent) {
        prevScale = scale
        if (settingTimer.stop() > 500L) {
            val diff = scale - getRoundedScale()
            when {
                diff < -0.025 -> scale += 0.025
                diff > 0.025 -> scale -= 0.025
                else -> scale = getRoundedScale()
            }
        }
    }

    private fun getRoundedScale(): Double {
        return round((scaleSetting.value / 100.0) / 0.1) * 0.1
    }

    override fun onEnable() {
        if (mc.currentScreen !is KamiGuiClickGui) {
            mc.displayGuiScreen(KamiGuiClickGui)
        }
    }

    override fun onDisable() {
        if (mc.currentScreen is KamiGuiClickGui) {
            mc.displayGuiScreen(null)
        }
    }

    init {
        bind.value.key = Keyboard.KEY_Y
        scaleSetting.settingListener = Setting.SettingListeners {
            settingTimer.reset()
        }
    }
}