#include <jni.h>
#include <string>
#include <unistd.h>
#include <pthread.h>
#include <filesystem>
#include <sys/socket.h>
#include <sys/select.h>
#include <sys/un.h>
#include <android/log.h>
#include <signal.h>
#include <sys/wait.h>

#define LOG_TAG "DoubleGuardProcess"

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

void createChild();
void createMonitor();
void createWatcherThread();

bool test_apk_dead() {
    while (getppid() != 1) {
        LOGE("child process test apk dead......");
        sleep(1);
    }
    return getppid() == 1;
}

void child_end(int sig) {
    LOGE("child process killed");
    int status;
    wait(&status);
    createChild();
}

JavaVM* javaVm;
const char* g_userId = NULL;

void launchHome() {
    LOGE("relaunch apk process");
//    const char* target = "com.example.keepalive/com.example.keepalive.MainActivity";
//    execlp("am", "am", "start", "-n", target, (char*) NULL);

    const char* target = "com.example.keepalive/com.example.keepalive.BackgroundService";
    execlp("am", "am", "startservice",  "--user", g_userId, "-n", target, (char*) NULL);
}

const char* path = "/data/data/com.example.keepalive/sock";

extern "C"
JNIEXPORT void JNICALL
Java_com_example_keepalive_WatchDog_startChild(JNIEnv *env, jobject thiz, jstring userId) {
    g_userId = env->GetStringUTFChars(userId, NULL);
    createWatcherThread();
    createChild();
}

void createChild() {
    int pid = fork();
    if (pid == 0) { // 子进程
        LOGE("child process start");
        createMonitor();
    } else if (pid > 0) { //  父进程
        LOGE("parent process continue");
    }
}

void* connectMonitor(void* arg) {
    signal(SIGCHLD, child_end);
    JNIEnv* env;
    javaVm->AttachCurrentThread(&env, NULL);
    LOGE("apk thread start");
    int socketId = socket(AF_LOCAL, SOCK_STREAM, 0);
    sockaddr_un saddr;
    saddr.sun_family = AF_LOCAL;
    strcpy(saddr.sun_path, path);
    connect(socketId, (sockaddr*) &saddr, sizeof(sockaddr_un));
    javaVm->DetachCurrentThread();
    return 0;
}

void createWatcherThread() {
    pthread_t pid;
    pthread_create(&pid, nullptr, connectMonitor, nullptr);
}

void createMonitor() {
    int socketId;
    unlink(path);
    socketId = socket(AF_LOCAL, SOCK_STREAM, 0);
    sockaddr_un saddr;
    memset(&saddr, 0, sizeof(sockaddr_un));
    saddr.sun_family = AF_LOCAL;
    strcpy(saddr.sun_path, path);
    bind(socketId, (sockaddr*) &saddr, sizeof(sockaddr_un));
    listen(socketId, 5);

    char buf[128];

    while (1) {
        int clientId = accept(socketId, NULL, NULL);
        LOGE("apk process connected");
        int length = read(clientId, buf, sizeof(buf));
        LOGE("apk process disconnected");
        if (length < 1 && test_apk_dead()) {
            LOGE("apk process is dead");
            launchHome();
        }
        close(clientId);
    }
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
