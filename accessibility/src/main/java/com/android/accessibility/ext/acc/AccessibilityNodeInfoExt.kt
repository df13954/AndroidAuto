package com.android.accessibility.ext.acc

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.android.accessibility.ext.data.NodeWrapper
import com.android.accessibility.ext.default

/**
 * 简化findAccessibilityNodeInfosByViewId
 */
fun AccessibilityNodeInfo.findNodesById(viewId: String): List<AccessibilityNodeInfo> =
    findAccessibilityNodeInfosByViewId(viewId)

/**
 * 简化findAccessibilityNodeInfosByText
 */
fun AccessibilityNodeInfo.findNodesByText(text: String): List<AccessibilityNodeInfo> =
    findAccessibilityNodeInfosByText(text)

/**
 * 简化findAccessibilityNodeInfosByViewId(viewId).firstOrNull()
 */
fun AccessibilityNodeInfo.findNodeById(viewId: String): AccessibilityNodeInfo? =
    findAccessibilityNodeInfosByViewId(viewId).firstOrNull()

/**
 * 简化findAccessibilityNodeInfosByText(text).firstOrNull()
 */
fun AccessibilityNodeInfo.findNodeByText(text: String): AccessibilityNodeInfo? =
    findAccessibilityNodeInfosByText(text).firstOrNull { it.text.default() == text }

/**
 * 判断是否有viewId这个节点
 */
fun AccessibilityNodeInfo.contains(viewId: String): Boolean =
    findNodesById(viewId).isNotEmpty()


/**
 * 递归遍历结点的方法
 */
private fun AccessibilityNodeInfo?.findNodeWrapper(
    isPrint: Boolean = true,
    prefix: String = "",
    isLast: Boolean = false,
    compare: (NodeWrapper) -> Boolean
): NodeWrapper? {
    val node = this ?: return null
    val nodeWrapper = NodeWrapper(
        className = node.className.default(),
        text = node.text.default(),
        id = node.viewIdResourceName.default(),
        description = node.contentDescription.default(),
        isClickable = node.isClickable,
        isScrollable = node.isScrollable,
        isEditable = node.isEditable,
        nodeInfo = node
    )

    if (compare(nodeWrapper)) {
        Log.d("findNodeWrapper", nodeWrapper.toString())
        return nodeWrapper
    }
    val marker = if (isLast) """\--- """ else "+--- "
    val currentPrefix = "$prefix$marker"
    if (isPrint) {
        Log.d("printNodeInfo", currentPrefix + nodeWrapper.toString())
    }
    val size = node.childCount
    if (size > 0) {
        val childPrefix = prefix + if (isLast) "  " else "|  "
        val lastChildIndex = size - 1
        for (index in 0 until size) {
            val isLastChild = index == lastChildIndex
            val find =
                node.getChild(index).findNodeWrapper(isPrint, childPrefix, isLastChild, compare)
            if (find != null) {
                return find
            }
        }
    }
    return null
}

fun AccessibilityNodeInfo?.findNodeWrapperById(id: String): NodeWrapper? {
    return findNodeWrapper { node -> node.id == id }
}

fun AccessibilityNodeInfo?.findNodeWrapperByText(text: String): NodeWrapper? {
    return findNodeWrapper { node -> node.text == text }
}

fun AccessibilityNodeInfo?.findNodeWrapperByIdAndText(id: String, text: String): NodeWrapper? {
    return findNodeWrapper { node -> node.text == text && node.id == id }
}

fun AccessibilityNodeInfo?.findNodeWrapperByContainsText(
    isPrint: Boolean = true,
    textList: List<String>
): NodeWrapper? {
    return findNodeWrapper(isPrint) { node ->
        textList.find { node.text?.contains(it) == true } != null
    }
}

