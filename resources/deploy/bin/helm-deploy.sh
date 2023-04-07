#!/usr/bin/env bash
#########################################################
#########################################################
########## 远程ssh连接kubernetes服务器执行脚本
#########################################################
#########################################################

WORKSPACE="REPLACE_WORKSPACE"
CHART_NAME="REPLACE_CHART_NAME"
DEPLOY_NAMESPACE="REPLACE_DEPLOY_NAMESPACE"

SELECT_CHART_STATUS() {
  echo "$(helm list -n "$DEPLOY_NAMESPACE" --time-format "2006-01-02" --filter "^$CHART_NAME$" | sed -n '2p' | awk '{print $5}' | sed s/[[:space:]]//g)"
}

cd $WORKSPACE

git pull || (echo "获取配置失败!" && exit 1)

if [ -d $CHART_NAME ]; then
  if [ "$(SELECT_CHART_STATUS)" == "failed" ]; then
      helm uninstall $CHART_NAME -n $DEPLOY_NAMESPACE
      WAIT_COUNT=0
      WAIT_STEP=3
      while [ "$(SELECT_CHART_STATUS)" == "failed" ]; do
        if [ $WAIT_COUNT -eq 0 ]; then
          echo "Waiting for failed chart uninstall, chart status: $(SELECT_CHART_STATUS)"
        else
          echo "Waiting $WAIT_COUNT s, chart status: $(SELECT_CHART_STATUS)"
        fi
        sleep $WAIT_STEP
        WAIT_COUNT=$(( $WAIT_COUNT + $WAIT_STEP ))
      done
  fi
  if [ "$(SELECT_CHART_STATUS)" == "deployed" ]; then
      cd $CHART_NAME && helm upgrade -f values.yaml $CHART_NAME .  -n $DEPLOY_NAMESPACE
  else
      helm install $CHART_NAME "$CHART_NAME/"  -n $DEPLOY_NAMESPACE
  fi
fi
