#include <jni.h>
#include <string>
#include <unistd.h>
#include <pthread.h>
#include <filesystem>
#include <sys/socket.h>
#include <sys/select.h>
#include <android/log.h>
#include <signal.h>
#include <sys/wait.h>

#define LOG_TAG "Native"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

void startServer();

void createWatchThread();

void createWorker();

JavaVM* javaVm;

void launchHome(JNIEnv *env, jobject thiz) {
    const char* target = "com.example.keepalive/com.example.keepalive.MainActivity";
    execlp("am", "am", "start", "-a", "android.intent.action.VIEW", "-d", target, (char*) NULL);
}

const char* path = "/data/data/com.example.keepalive/sock";

extern "C"
JNIEXPORT void JNICALL
Java_com_example_keepalive_WatchDog_startChild(JNIEnv *env, jobject thiz) {
    createWatchThread();
    createWorker();
}

void createWorker() {
    int pid = fork();
    if (pid == 0) { // 子进程
        startServer();
    } else if (pid > 0) { //  父进程

    }
}

void* watchMonitor(void* arg) {
    JNIEnv* env;
    javaVm->AttachCurrentThread(&env, NULL);
    LOGE("child thread start");
    int socketId = socket(AF_UNIX, SOCK_STREAM, 0);
    sockaddr_storage sockaddrStorage;
    sockaddrStorage.ss_family = AF_UNIX;
    strcpy(sockaddrStorage.__data, path);
    connect(socketId, (sockaddr*) &sockaddrStorage, sizeof(sockaddrStorage));

    javaVm->DetachCurrentThread();
    return 0;
}

void createWatchThread() {
    pthread_t pid;
    pthread_create(&pid, nullptr, watchMonitor, nullptr);
}

void startServer() {
    int socketId;
    unlink(path);
    socketId = socket(AF_UNIX, SOCK_STREAM, 0);
    sockaddr_storage sockaddrStorage;
    sockaddrStorage.ss_family = AF_UNIX;
    strcpy(sockaddrStorage.__data, path);
    bind(socketId, (sockaddr*) &sockaddrStorage, sizeof(sockaddr_storage));
    listen(socketId, 5);

    while (1) {
        int clientId = accept(socketId, NULL, NULL);
        fd_set fdSet;
        FD_ZERO(&fdSet);

    }
}

bool test_apk_dead() {
    sleep(1000);
    return getppid() == 1;
}

void child_exit_handler(void* sig) {
    int status;
    wait(&status);
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    javaVm = vm;
    JNIEnv* env;
    jint result = vm->GetEnv((void**) &env, JNI_VERSION_1_4);
    if (result != JNI_OK) {
        return JNI_EVERSION;
    }
    return JNI_VERSION_1_4;
}
