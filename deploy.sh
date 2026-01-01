#!/bin/zsh

KEY_FILE="/Users/hongseoghyeong/.ssh/hong-blogservice.pem"
SERVER_IP="43.200.225.95"

#./gradlew build
#
#mv ./build/libs/*.jar ./build/libs/app.jar
#
#scp -i ${KEY_FILE} ./build/libs/app.jar ec2-user@${SERVER_IP}:~

ssh -i ${KEY_FILE} ec2-user@${SERVER_IP} \
  "nohup java -jar app.jar > app.log 2>&1 & exit"