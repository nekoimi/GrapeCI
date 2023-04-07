#!/usr/bin/env bash
#########################################################
#########################################################
########## 远程ssh连接kubernetes服务器执行脚本
#########################################################
#########################################################

CHART_NAME="REPLACE_CHART_NAME"
DEPLOY_NAMESPACE="REPLACE_DEPLOY_NAMESPACE"

SELECT_CHART_STATUS() {
  echo "$(helm list -n "$DEPLOY_NAMESPACE" --time-format "2006-01-02" --filter "^$CHART_NAME$" | sed -n '2p' | awk '{print $5}' | sed s/[[:space:]]//g)"
}

if [ "$(SELECT_CHART_STATUS)" == "failed" ]; then
    echo 'Deploy To Kubernetes failed!' && exit 1
fi

if [ "$(SELECT_CHART_STATUS)" == "deployed" ]; then
    helm test $CHART_NAME -n $DEPLOY_NAMESPACE
fi
