#!/bin/zsh
#
#KEY_FILE="/Users/hongseoghyeong/.ssh/hong-blogservice.pem"
#SERVERS=(
#  "i-084de23e8f4f83c93:43.200.225.95"
#  "i-041427d67f363fb6b:16.184.56.151"
#)
#
##if [ -f ./build/libs/app.jar ]; then
##  echo "app.jar already exist. Deleting it"
##  rm ./build/libs/app.jar
##fi
##
##./gradlew build
##
##mv ./build/libs/*.jar ./build/libs/app.jar
#
#for SERVER in "${SERVERS[@]}"; do
#  IFS=':' read -r INSTANCE_ID IP <<< "$SERVER"
#
#  echo "INSTANCE_ID: $INSTANCE_ID, IP: $IP"
#
#  aws elbv2 deregister-targets \
#    --target-group-arn arn:aws:elasticloadbalancing:ap-northeast-2:447648296206:targetgroup/blogservice-target-group/9e5f064a1251ef33 \
#    --targets Id="${INSTANCE_ID}"
#
#  echo  "sleep 5 seconds to allow deregister"
#  sleep 5
#
#  scp -i ${KEY_FILE} ./build/libs/app.jar ec2-user@"${IP}":~
#
#  ssh -i ${KEY_FILE} ec2-user@"${IP}" <<EOF
#  pkill java
#  echo "starting server"
#  nohup java -jar app.jar > app.log 2>&1 &
#
#  sleep 5
#
#  for i in {1..10}; do
#    STATUS=\$(curl -s http://localhost/actuator/health | grep '"status": "UP"')
#    if [[ "$STATUS" != "" ]]; then
#      echo "Health check passed!"
#      exit 0
#    fi
#    echo "Health check failed... retrying in 5s ("$i"/10)"
#    sleep 5
#  done
#
#  echo "X Health check failed after 10 tries."
#  exit 1
#EOF
#done

set -euo pipefail

KEY_FILE="/Users/hongseoghyeong/.ssh/hong-blogservice.pem"
TG_ARN="arn:aws:elasticloadbalancing:ap-northeast-2:447648296206:targetgroup/blogservice-target-group/9e5f064a1251ef33"

SERVERS=(
  "i-084de23e8f4f83c93:43.200.225.95"
  "i-041427d67f363fb6b:16.184.56.151"
)

# 빌드/패키징
# ./gradlew build
# mv ./build/libs/*.jar ./build/libs/app.jar

for SERVER in "${SERVERS[@]}"; do
  IFS=':' read -r INSTANCE_ID IP <<< "$SERVER"
  echo "INSTANCE_ID: $INSTANCE_ID, IP: $IP"

  echo "1) Deregister from target group"
  aws elbv2 deregister-targets --target-group-arn "$TG_ARN" --targets Id="$INSTANCE_ID"

  echo "sleep 5 seconds to allow deregister"
  sleep 5

  echo "2) Upload jar"
  scp -i "$KEY_FILE" ./build/libs/app.jar "ec2-user@${IP}:~/app.jar"

  echo "3) Restart + health check on instance"
  ssh -i "$KEY_FILE" "ec2-user@${IP}" <<'EOF'
  set -e

  pkill -f 'java -jar app.jar' || true
  echo "starting server"
  nohup java -jar app.jar > app.log 2>&1 &

  sleep 5

  for i in {1..10}; do
    if curl -fsS "http://localhost:8080/actuator/health" | grep -q '"status":"UP"'; then
      echo "Health check passed!"
      exit 0
    fi
    echo "Health check failed... retrying in 5s (${i}/10)"
    sleep 5
  done

  echo "X Health check failed after 10 tries."
  exit 1
EOF

  echo "4) Register back to target group"
  aws elbv2 register-targets --target-group-arn "$TG_ARN" --targets Id="$INSTANCE_ID"

  echo "sleep 5 seconds to allow register"
  sleep 5
done