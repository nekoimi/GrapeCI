package com.sakuraio.jenkins.ci.utils

/**
 * <p>SortUtils</p>
 *
 * @author nekoimi 2023/03/30
 */
class SortUtils {
    private SortUtils() {}

    static void sort(list, Closure closure) {
        quickSort(list, 0, list.size() - 1, closure)
    }

    private static def quickSort(arr, left, right, Closure closure) {
        if (left >= right) {
            return
        }

        def pivot = partition(arr, left, right, closure)
        quickSort(arr, left, pivot - 1, closure)
        quickSort(arr, pivot + 1, right, closure)
    }

    private static def partition(arr, left, right, Closure closure) {
        def pivot = arr[right]
        def i = left - 1

        for (def j = left; j < right; j++) {
            if (closure.call(arr[j], pivot)) {
                i++
                def temp = arr[i]
                arr[i] = arr[j]
                arr[j] = temp
            }
        }

        def temp = arr[i + 1]
        arr[i + 1] = arr[right]
        arr[right] = temp

        return i + 1
    }
}
