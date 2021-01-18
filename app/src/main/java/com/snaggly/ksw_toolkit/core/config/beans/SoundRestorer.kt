package com.snaggly.ksw_toolkit.core.config.beans

import com.snaggly.ksw_toolkit.core.config.IConfigBean
import com.snaggly.ksw_toolkit.core.config.custom.BooleanSetting

class SoundRestorer(
        startOnBoot: Boolean,
        initVolumesAtBoot: Boolean,
        keepOEMRadio: Boolean,
        forceSoundInOEM: Boolean,
        configMaster: IConfigBean
) {
    var startOnBoot = BooleanSetting(startOnBoot, configMaster)
    var initVolumesAtBoot = BooleanSetting(initVolumesAtBoot, configMaster)
    var keepOEMRadio = BooleanSetting(keepOEMRadio, configMaster)
    var forceSoundInOEM = BooleanSetting(forceSoundInOEM, configMaster)

    companion object {
        fun initSoundRestorer(configMaster: IConfigBean) : SoundRestorer {
            return SoundRestorer(
                    startOnBoot = true,
                    initVolumesAtBoot = true,
                    keepOEMRadio = false,
                    forceSoundInOEM = false,
                    configMaster)
        }
    }
}