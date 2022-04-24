/*
package com.ll.sparksql.practice

import org.apache.hadoop.security.UserGroupInformation

/**
 * @Author lin_li
 * @Date 2022/3/20 14:08
 */
case class initKerberos() {
  //kerberos权限认证
  try {
    //等同于把krb5.conf放在$JAVA_HOME\jre\lib\security，一般写代码即可
    System.setProperty("java.security.krb5.conf", "src/main/resources/krb5.conf")

    //下面的conf可以注释掉是因为在core-site.xml里有相关的配置，如果没有相关的配置，则下面的代码是必须的
    //      val conf = new Configuration
    //      conf.set("hadoop.security.authentication", "kerberos")
    //      UserGroupInformation.setConfiguration(conf)
    UserGroupInformation.loginUserFromKeytab("supergroup", "src/main/resources/supergroup.keytab")
    println(UserGroupInformation.getCurrentUser, UserGroupInformation.getLoginUser)
  } catch {
    case e: Exception =>
      e.printStackTrace()
  }
}
*/
