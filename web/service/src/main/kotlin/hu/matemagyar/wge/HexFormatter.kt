package hu.matemagyar.wge

import hu.matemagyar.wge.HexFormatter.Companion.format

@OptIn(ExperimentalStdlibApi::class)
fun UShort.toFormattedHexString(): String = toHexString(format)

class HexFormatter {
    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        var format: HexFormat =
            HexFormat {
                upperCase = true
                number {
                    prefix = "0x"
                    removeLeadingZeros = true
                }
            }
    }
}
