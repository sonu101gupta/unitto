package com.sadellie.unitto.core.ui.common.key.unittoicons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.sadellie.unitto.core.ui.common.key.UnittoIcons

val @receiver:Suppress("UNUSED") UnittoIcons.KeyF: ImageVector
    get() {
        if (_keyf != null) {
            return _keyf!!
        }
        _keyf = Builder(name = "KeyF", defaultWidth = 150.0.dp, defaultHeight = 150.0.dp,
                viewportWidth = 150.0f, viewportHeight = 150.0f).apply {
            path(fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                    strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                    pathFillType = NonZero) {
                moveTo(62.94f, 43.4f)
                verticalLineTo(75.464f)
                horizontalLineTo(100.637f)
                verticalLineTo(85.576f)
                horizontalLineTo(62.94f)
                verticalLineTo(125.0f)
                horizontalLineTo(50.525f)
                verticalLineTo(33.288f)
                horizontalLineTo(107.037f)
                verticalLineTo(43.4f)
                horizontalLineTo(62.94f)
                close()
            }
        }
        .build()
        return _keyf!!
    }

private var _keyf: ImageVector? = null
