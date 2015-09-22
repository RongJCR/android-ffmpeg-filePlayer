#include <stdio.h>
#include <assert.h>
#include <android/log.h>
#include "ffmpeg/include/libavcodec/avcodec.h"
#include "ffmpeg/include/libavformat/avformat.h"
#include "ffmpeg/include/libavutil/pixfmt.h"
#include "ffmpeg/include/libswscale/swscale.h"
#include "ffmpeg/include/libswresample/swresample.h"
#include "com_h264_decode2_FFmpegNative.h"


#ifdef __cplusplus
extern "C" {
#endif
AVFormatContext *pFormatCtx = NULL; //保存需要读入的文件的格式信息，比如流的个数以及流数据等
int				i,videoStream;
AVCodecContext	*pCodecCtx;			//保存了相应流的详细编码信息，比如视频的宽、高，编码类型等
AVCodec			*pCodec;			//真正的编解码器，其中有编解码需要调用的函数
AVFrame			*pFrame;			//用于保存数据帧的数据结构，这里两个帧分别保存颜色转换前后的两帧图像
AVFrame			*pFrameRGB;
AVPacket		packet;				//解析文件时会将音/视频帧读入到packet中
int				frameFinished;
int             numBytes;
uint8_t   *buffer;

JNIEXPORT jint JNICALL Init(JNIEnv *env, jobject obj, jstring fileName)
{
    const char* local_title = (*env)->GetStringUTFChars(env,fileName,NULL); //取得java字符串的C版本
    frameFinished = 0;
    av_register_all();
    avformat_network_init();
    //if(avformat_open_input(&pFormatCtx,local_title,NULL,NULL) != 0)
    //return 1;
    int tmp = 0;
    tmp = avformat_open_input(&pFormatCtx,local_title,NULL,NULL);
    if(tmp != 0)
    return tmp;
    int tmp2 = -100;
    tmp2 = avformat_find_stream_info(pFormatCtx,NULL);
    //if(avformat_find_stream_info(pFormatCtx,NULL) < 0)
    //return 2;
    if(tmp2 < 0)
        return tmp2;
    av_dump_format(pFormatCtx,-1,local_title,0); //帮助函数，输出文件的信息
    videoStream = -1;
    for(i = 0; i < pFormatCtx->nb_streams; i++)
    {
        if(pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO)
        {
            videoStream = i;
            break;
        }
    }
    if(videoStream == -1)
    return 3;
    // 通过ideoStream的编解码信息打开相应的解码器
    pCodecCtx = pFormatCtx->streams[videoStream]->codec;
    pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    if(pCodec == NULL)
    return 4;
    /*pCodec = avcodec_find_decoder(CODEC_ID_H264);
    if(!pCodec){
        return 2;
    }
    pCodecCtx = avcodec_alloc_context3(pCodec);
    if(pCodec->capabilities & CODEC_CAP_TRUNCATED)
        pCodecCtx->flags |= CODEC_FLAG_TRUNCATED;*/

    if(avcodec_open2(pCodecCtx,pCodec,NULL) < 0)
    return 5;
    //分配图像缓存，pFrame用于存储解码后的数据，pFrameRGB用于存储转换后的数据
    pFrame = av_frame_alloc();
    if(pFrame == NULL)
    return 6;
    pFrameRGB = av_frame_alloc();
    if(pFrameRGB == NULL)
    return 7;
    // 根据pCodexCtx中原始图像的宽、高计算RGB565格式的图像需要占用的空间大小，
    //这是为了给之后的pFrameRGB分配空间
    numBytes=avpicture_get_size(PIX_FMT_RGB565,pCodecCtx->width,pCodecCtx->height);

    buffer = (uint8_t*)av_malloc(numBytes);
    // 使用能够avpicture_fill将pFrameRGB跟buffer指向的内存关联起来
    avpicture_fill((AVPicture*)pFrameRGB,buffer,PIX_FMT_RGB565,pCodecCtx->width,pCodecCtx->height);
    // 一切准备就绪，可以开始从文件中读取视频帧并解码得到图像了
    return 0;

}
JNIEXPORT jint JNICALL Decode2RGB
(JNIEnv *env, jobject obj,jbyteArray out)
{
    /*if(pFormatCtx == NULL)
    return 0;
    else
    return -2;*/
    //unsigned char *Pixel = (unsigned char*)(*env)->GetDirectBufferAddress(env,buffer);
    jbyte *outdata = (jbyte*)(*env)->GetByteArrayElements(env,out,0);
    int readFlag = av_read_frame(pFormatCtx,&packet); //如果正常读入则这里的readFlag = 0;
    if(readFlag >= 0)//读取一个packet，一个packet包含一帧数据
    {
        //return 1;
        if(packet.stream_index == videoStream)
        {
            //return 1;
            avcodec_decode_video2(pCodecCtx,pFrame,&frameFinished,&packet); //解码视频帧
            if(frameFinished)
            {
                //return 1;
                struct SwsContext *img_convert_ctx = NULL;
                img_convert_ctx = sws_getCachedContext(img_convert_ctx,pCodecCtx->width,
                pCodecCtx->height,pCodecCtx->pix_fmt,pCodecCtx->width,pCodecCtx->height,
                PIX_FMT_RGB565,SWS_BICUBIC,NULL,NULL,NULL);
                if(!img_convert_ctx) // Cannot initialize sws conversion context
                    return 2;
                sws_scale(img_convert_ctx,(const uint8_t* const*)pFrame->data,pFrame->linesize,
                            0,pCodecCtx->height,pFrameRGB->data,pFrameRGB->linesize);

                //out = pFrameRGB->data[0];
                memcpy(outdata,pFrameRGB->data[0],pCodecCtx->height * pFrameRGB->linesize[0]);
                (*env)->ReleaseByteArrayElements(env,out,outdata,0);
                return 0;
            }
            else
                return 3;

        }
        else
            return 4;
    }
    return 1;


}
static JNINativeMethod methods[] = {
        {"Init","(Ljava/lang/String;)I",(void *)Init},
        {"Decode2RGB","([B)I",(void *)Decode2RGB}
};
static int registerNativeMethods(JNIEnv* env, const char* className, JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;
    clazz = (*env)->FindClass(env,className);
    if(clazz == NULL)
    {
        return JNI_FALSE;
    }
    if((*env)->RegisterNatives(env,clazz,gMethods,numMethods) < 0)
    {
        return JNI_FALSE;
    }
    return JNI_TRUE;

}

static int registerNatives(JNIEnv* env)
{
    const char* kClassName = "com/h264/decode2/FFmpegNative"; // 指定要注册的类
    return  registerNativeMethods(env,kClassName,methods, sizeof(methods) / sizeof(methods[0]));

}




JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
	JNIEnv *env = NULL;
	jint result = -1;
	if((*vm)->GetEnv(vm,(void**)&env,JNI_VERSION_1_6) != JNI_OK)
	{
		return result;
	}
    assert(env != NULL);
    if(!registerNatives(env))
    {
        return result;
    }
    //return JNI_VERSION_1_4;
	return JNI_VERSION_1_6;
}






#ifdef __cplusplus
}
#endif
