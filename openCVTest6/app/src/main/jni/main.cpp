#include <jni.h>
#include "kr_ac_kpu_opencvtest6_MainActivity.h"

#include <opencv2/opencv.hpp>

using namespace cv;

extern "C" {
    JNIEXPORT void JNICALL Java_kr_ac_kpu_opencvtest6_MainActivity_ConvertRGBtoGray(
    JNIEnv *env, jobject instance, jlong matAddrInput, jlong matAddrResult){

        Mat &matInput = *(Mat *)matAddrInput;
        Mat &matResult = *(Mat *)matAddrResult;

        cvtColor(matInput, matResult, COLOR_RGBA2GRAY);

    }
}