package com.yourcompany.arnav.ar

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet

class AROverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {
    
    init {
        setEGLContextClientVersion(2)
        // TODO: Set renderer with ARRouteRenderer
    }
}
