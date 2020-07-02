package com.potalab.tomcat.embed.exam;

import java.io.File;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceSet;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class FirstNewTomcat {

  public static void main(String[] args) throws LifecycleException {
    String webappDirLocation = "webapp";
    Tomcat tomcat = new Tomcat();
    tomcat.setPort(8080);
    // reference 룰 얻기위해 get 을 하지만 실제로 이 시점에 위에서 지정한 포트로 default connector
    // 가 생성된다고 한다.
    Connector connector = tomcat.getConnector();
    connector.setURIEncoding("UTF-8");

    StandardContext ctx = (StandardContext)tomcat.
        addWebapp("testctx/", new File(webappDirLocation).getAbsolutePath());

    // Declare an alternative location for your "WEB-INF/classes" dir
    // => 이 메이븐 프로젝트의 결과물인 target/classes 하위의 컴파일 결과물을 WEB-INF/classes
    // 디렉터리에 매핑시키는 것이다.

    File additionWebInfClasses = new File("target/classes");
    WebResourceRoot resources = new StandardRoot(ctx);

    WebResourceSet resourceSet =
        new DirResourceSet(resources, "/WEB-INF/classes",
            additionWebInfClasses.getAbsolutePath(), "/");
    resources.addPostResources(resourceSet);
    ctx.setResources(resources);

    ExecutorService es = Executors.newSingleThreadExecutor();
    es.submit(()->{
      while (true) {
        String command = inputCommandFromConsole();
        System.out.println("TRACE >>> " + command);
        if ("shutdown".equalsIgnoreCase(command)) {
          try {
            tomcat.stop();
            tomcat.destroy();
          } catch (LifecycleException e) {
            e.printStackTrace();
          }
          break;
        }
      }
    });
    es.shutdown();

    tomcat.start();
    tomcat.getServer().await();

  }

  private static String inputCommandFromConsole() {
    Scanner scanner = new Scanner(System.in);
    String nextCommand = scanner.nextLine();
    if (nextCommand == null || "".equals(nextCommand)) {
      return inputCommandFromConsole();
    } else {
      return nextCommand;
    }
  }

}
