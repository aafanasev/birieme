package com.github.aafanasev.birieme

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

class EngineHandler(
        reference: MyWatchFace.Engine,
        private val messageId: Int
) : Handler() {

    private val mWeakReference: WeakReference<MyWatchFace.Engine> = WeakReference(reference)

    override fun handleMessage(msg: Message) {
        val engine = mWeakReference.get()
        if (engine != null) {
            when (msg.what) {
                messageId -> engine.handleUpdateTimeMessage()
            }
        }
    }
}