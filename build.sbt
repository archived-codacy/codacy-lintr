import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

name := """codacy-lintr"""

version := "1.0.0-SNAPSHOT"

val languageVersion = "2.11.7"

scalaVersion := languageVersion

resolvers ++= Seq(
  "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.8",
  "com.codacy" %% "codacy-engine-scala-seed" % "2.7.8"
)

enablePlugins(JavaAppPackaging)

enablePlugins(DockerPlugin)

version in Docker := "1.0.0"

organization := "com.codacy"

val installAll =
  s"""apk update
  && apk add build-base gfortran readline-dev 
       icu-dev bzip2-dev xz-dev pcre-dev 
      libjpeg-turbo-dev libpng-dev tiff-dev  
      curl-dev zip file coreutils bash 
  && apk add build-base 
  && apk add ca-certificates curl
  && curl --silent 
    --location https://github.com/sgerrand/alpine-pkg-R/releases/download/3.3.1-r0/R-3.3.1-r0.apk --output /var/cache/apk/R-3.3.1-r0.apk 
    --location https://github.com/sgerrand/alpine-pkg-R/releases/download/3.3.1-r0/R-dev-3.3.1-r0.apk --output /var/cache/apk/R-dev-3.3.1-r0.apk 
    --location https://github.com/sgerrand/alpine-pkg-R/releases/download/3.3.1-r0/R-doc-3.3.1-r0.apk --output /var/cache/apk/R-doc-3.3.1-r0.apk 
  && apk add --allow-untrusted
    /var/cache/apk/R-3.3.1-r0.apk 
    /var/cache/apk/R-dev-3.3.1-r0.apk 
    /var/cache/apk/R-doc-3.3.1-r0.apk 
  && R -e "install.packages('igraph', repos='http://cran.rstudio.com/')"
  && rm -fr /var/cache/apk/*
  && echo "http://dl-cdn.alpinelinux.org/alpine/edge/main" >> /etc/apk/repositories 
  && R -e "install.packages('devtools', repos='http://cran.rstudio.com/')"
  && R -e "library(devtools); install_github('igraph/rigraph')"
  && R -e "install.packages('lintr', repos='http://cran.rstudio.com/')"
  && apk --no-cache add bash""".stripMargin.replaceAll(System.lineSeparator(), " ")

mappings in Universal <++= (resourceDirectory in Compile) map { (resourceDir: File) =>
  val src = resourceDir / "docs"
  val dest = "/docs"

  val resFiles = for {
    path <- (src ***).get
    if !path.isDirectory
  } yield (path, path.toString.replaceFirst(src.toString, dest))

  val scriptSrc = resourceDir / "codacy-lintr.R"
  val scriptDest = "/lintr"

  val scriptFile = scriptSrc.toString.replaceFirst(resourceDir.toString, scriptDest)

  resFiles :+ (scriptSrc, scriptFile)
}

val dockerUser = "docker"
val dockerGroup = "docker"

daemonUser in Docker := dockerUser

daemonGroup in Docker := dockerGroup

dockerBaseImage := "develar/java"

dockerCommands := dockerCommands.value.flatMap {
  case cmd@Cmd("WORKDIR", _) => List(cmd,
    Cmd("RUN", installAll)
  )
  case cmd@(Cmd("ADD", "opt /opt")) => List(cmd,
    Cmd("RUN", "mv /opt/docker/docs /docs"),
    Cmd("RUN", "mv /opt/docker/lintr /lintr"),
    Cmd("RUN", "chmod +x /lintr/codacy-lintr.R"),
    Cmd("RUN", s"adduser -u 2004 -D $dockerUser"),
    ExecCmd("RUN", Seq("chown", "-R", s"$dockerUser:$dockerGroup", "/docs"): _*)
  )
  case other => List(other)
}
