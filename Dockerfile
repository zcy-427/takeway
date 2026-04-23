# 1. 绕过封锁，直接指定华为云的高速节点拉取 Java 17 环境
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk-alpine

# 2. 声明作者
LABEL maintainer="zcy"

# 3. 把战车复制进去
COPY target/first-spring-boot-0.0.1-SNAPSHOT.jar /app.jar

# 4. 暴露战车端口
EXPOSE 8080

# 5. 启动命令
ENTRYPOINT ["java", "-jar", "/app.jar"]