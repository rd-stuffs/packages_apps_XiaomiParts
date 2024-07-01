# Copyright (C) 2024 Paranoid Android
# SPDX-License-Identifier: Apache-2.0

XIAOMIPARTS_PATH := packages/apps/XiaomiParts

PRODUCT_PACKAGES += \
    XiaomiParts

PRODUCT_VENDOR_PROPERTIES += \
    persist.vendor.audio.auto.scenario=true \
    persist.vendor.audio.misound.disable=false \
    persist.vendor.audio.misoundasc=true \
    ro.vendor.audio.misound.bluetooth.enable=true

PRODUCT_SYSTEM_EXT_PROPERTIES += \
    persist.lcd.hbm_mode=0 \
    persist.lcd.cabc_mode=1

SYSTEM_EXT_PRIVATE_SEPOLICY_DIRS += \
    $(XIAOMIPARTS_PATH)/sepolicy/private

BOARD_VENDOR_SEPOLICY_DIRS += \
    $(XIAOMIPARTS_PATH)/sepolicy/vendor

PRODUCT_SOONG_NAMESPACES += \
    $(XIAOMIPARTS_PATH)
