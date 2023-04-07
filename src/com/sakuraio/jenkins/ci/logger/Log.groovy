package com.sakuraio.jenkins.ci.logger

import com.sakuraio.jenkins.ci.core.Initialization
import com.sakuraio.jenkins.ci.ext.jenkins.JenkinsParameter

/**
 * <p>Log</p>
 *
 * @author nekoimi 2022/11/18
 */
class Log implements Initialization, Serializable {
    def jenkins

    @Override
    void initialization(Object jenkins) {
        this.jenkins = jenkins
    }

    void debug(Object message) {
        this.jenkins.println("DEBUG: ${message}")
    }

    void info(Object message) {
        this.jenkins.println("INFO: ${message}")
    }

    void warning(Object message) {
        this.jenkins.println("WARNING: ${message}")
    }

    void fail(Object message) {
        this.jenkins.error("${message}")
    }

    void banner(JenkinsParameter jenkinsParameter) {
        this.jenkins.println("""
                                                                                                                               
                                                                                                                               
        GGGGGGGGGGGGG                                                                                   CCCCCCCCCCCCCIIIIIIIIII
     GGG::::::::::::G                                                                                CCC::::::::::::CI::::::::I
   GG:::::::::::::::G                                                                              CC:::::::::::::::CI::::::::I
  G:::::GGGGGGGG::::G                                                                             C:::::CCCCCCCC::::CII::::::II
 G:::::G       GGGGGGrrrrr   rrrrrrrrr   aaaaaaaaaaaaa  ppppp   ppppppppp       eeeeeeeeeeee     C:::::C       CCCCCC  I::::I  
G:::::G              r::::rrr:::::::::r  a::::::::::::a p::::ppp:::::::::p    ee::::::::::::ee  C:::::C                I::::I  
G:::::G              r:::::::::::::::::r aaaaaaaaa:::::ap:::::::::::::::::p  e::::::eeeee:::::eeC:::::C                I::::I  
G:::::G    GGGGGGGGGGrr::::::rrrrr::::::r         a::::app::::::ppppp::::::pe::::::e     e:::::eC:::::C                I::::I  
G:::::G    G::::::::G r:::::r     r:::::r  aaaaaaa:::::a p:::::p     p:::::pe:::::::eeeee::::::eC:::::C                I::::I  
G:::::G    GGGGG::::G r:::::r     rrrrrrraa::::::::::::a p:::::p     p:::::pe:::::::::::::::::e C:::::C                I::::I  
G:::::G        G::::G r:::::r           a::::aaaa::::::a p:::::p     p:::::pe::::::eeeeeeeeeee  C:::::C                I::::I  
 G:::::G       G::::G r:::::r          a::::a    a:::::a p:::::p    p::::::pe:::::::e            C:::::C       CCCCCC  I::::I  
  G:::::GGGGGGGG::::G r:::::r          a::::a    a:::::a p:::::ppppp:::::::pe::::::::e            C:::::CCCCCCCC::::CII::::::II
   GG:::::::::::::::G r:::::r          a:::::aaaa::::::a p::::::::::::::::p  e::::::::eeeeeeee     CC:::::::::::::::CI::::::::I
     GGG::::::GGG:::G r:::::r           a::::::::::aa:::ap::::::::::::::pp    ee:::::::::::::e       CCC::::::::::::CI::::::::I
        GGGGGG   GGGG rrrrrrr            aaaaaaaaaa  aaaap::::::pppppppp        eeeeeeeeeeeeee          CCCCCCCCCCCCCIIIIIIIIII
                                                         p:::::p                                                               
                                                         p:::::p                                                               
                                                        p:::::::p                                                              
                                                        p:::::::p                构建环境：${jenkinsParameter.getBuildEnv()}              
                                                        p:::::::p                构建分支：${jenkinsParameter.getBuildBranch()}                                 
                                                        ppppppppp                                                              
                                                                                                                               
""")
    }
}
