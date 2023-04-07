#!/usr/bin/env bash
#########################################################
#########################################################
########## helm部署包deploy.sh脚本
#########################################################
#########################################################

#===================================================================
#===================================================================
# 配置部分
CHART_NAME="REPLACE_CHART_NAME"
DEPLOY_NAMESPACE="REPLACE_DEPLOY_NAMESPACE"
#===================================================================
#===================================================================
DIR="$(cd "$(dirname "$0")" && pwd)"

#
# Set Colors
#

bold=$(tput bold)
underline=$(tput sgr 0 1)
reset=$(tput sgr0)

red=$(tput setaf 1)
green=$(tput setaf 76)
white=$(tput setaf 7)
tan=$(tput setaf 202)
blue=$(tput setaf 25)

#
# Headers and Logging
#

underline() { printf "${underline}${bold}%s${reset}\n" "$@"
}
h1() { printf "\n${underline}${bold}${blue}%s${reset}\n" "$@"
}
h2() { printf "\n${underline}${bold}${white}%s${reset}\n" "$@"
}
debug() { printf "${white}%s${reset}\n" "$@"
}
info() { printf "${white}➜ %s${reset}\n" "$@"
}
success() { printf "${green}✔ %s${reset}\n" "$@"
}
error() { printf "${red}✖ %s${reset}\n" "$@"
}
warn() { printf "${tan}➜ %s${reset}\n" "$@"
}
bold() { printf "${bold}%s${reset}\n" "$@"
}
note() { printf "\n${underline}${bold}${blue}Note:${reset} ${blue}%s${reset}\n" "$@"
}

LOAD_DOCKER_IMAGE() {
 h1 "Load push image"

 if [ -d "$DIR/image" ]; then
   if [ "$(ls -A "$DIR/image")" ]; then
     for image_file in $(ls -A "$DIR/image"); do
       info "Loading image file: $image_file"
       image_name=$(docker load -q -i "$DIR/image/$image_file" | sed 's/Loaded image://g' | sed s/[[:space:]]//g)
       info "Push image: $image_name"
       docker push $image_name || warn "Error in pushing image: $image_name"
       info "Clean image: $image_name"
       docker rmi $image_name
       success "Processed $image_name successfully!"
     done
   else
     info "Check the folder is empty, ignore"
   fi
 else
   info "Check folder does not exist, ignore"
 fi
}

# 读取helm chart状态
SELECT_CHART_STATUS() {
  echo "$(helm list -n "$DEPLOY_NAMESPACE" --time-format "2006-01-02" --filter "^$CHART_NAME$" | sed -n '2p' | awk '{print $5}' | sed s/[[:space:]]//g)"
}

HELM_DEPLOY() {
  h1 "Helm deploy"

  if [ ! -d "$DIR/chart" ]; then
    error "Helm chart folder does not exist!"
    exit 1
  fi

  if [ "$(SELECT_CHART_STATUS)" == 'failed' ]; then
    h2 "Helm: uninstall"
    helm uninstall $CHART_NAME -n "$DEPLOY_NAMESPACE"

    wait_failed_step=3
    wait_failed_count=0
    while [ "$(SELECT_CHART_STATUS)" == 'failed' ]; do
      if [ $wait_failed_count -eq 0 ]; then
        info "Waiting for failed chart uninstall, check chart status: $(SELECT_CHART_STATUS)"
      else
        info "Waiting $wait_failed_count s, check chart status: $(SELECT_CHART_STATUS)"
      fi
      sleep $wait_failed_step
      wait_failed_count=$(( $wait_failed_count + $wait_failed_step ))
    done
  fi

  if [ "$(SELECT_CHART_STATUS)" == 'deployed' ]; then
    h2 "Helm: upgrade"
    helm upgrade $CHART_NAME "$DIR/chart/" -f "$DIR/chart/values.yaml" -n "$DEPLOY_NAMESPACE"
    success "The $CHART_NAME service is upgrade successfully!"
  else
    h2 "Helm: install"
    helm install $CHART_NAME "$DIR/chart/" -n "$DEPLOY_NAMESPACE"
    success "The $CHART_NAME service is install successfully!"
  fi
}

HELM_TEST() {
  h1 "Helm test"

  if [ "$(SELECT_CHART_STATUS)" == 'failed' ]; then
    error "The $CHART_NAME service deployment failed!"
    exit 1
  fi

  if [ "$(SELECT_CHART_STATUS)" == 'deployed' ]; then
    helm status $CHART_NAME -n "$DEPLOY_NAMESPACE"
    success "The $CHART_NAME service is deployed successfully!"
  else
    warn "The $CHART_NAME service deployment status ($(SELECT_CHART_STATUS)) is abnormal, Please check service status!"
  fi
}

_main() {
  # 加载docker image
  LOAD_DOCKER_IMAGE
  # 部署
  HELM_DEPLOY
  # 测试部署结果
  HELM_TEST
}

# Start
_main "$@"
