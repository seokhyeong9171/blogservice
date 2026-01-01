#!/bin/zsh

KEY_FILE="/Users/hongseoghyeong/.ssh/hong-blogservice.pem"
SERVER_IPS=(43.200.225.95 16.184.56.151)

if [ -f ./build/libs/app.jar ]; then
  echo "app.jar already exist. Deleting it"
  rm ./build/libs/app.jar
fi

./gradlew build

mv ./build/libs/*.jar ./build/libs/app.jar

for SERVER_IP in "${SERVER_IPS[@]}"; do
scp -i ${KEY_FILE} ./build/libs/app.jar ec2-user@"${SERVER_IP}":~

ssh -i ${KEY_FILE} ec2-user@"${SERVER_IP}" <<EOF
pkill java
nohup java -jar app.jar > app.log 2>&1 &
exit
EOF
done