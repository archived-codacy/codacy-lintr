package codacy.lintr

import java.io.File
import java.nio.file.{Files, Path}

import codacy.docker.api._
import codacy.docker.api.utils.ToolHelper
import codacy.dockerApi.utils.{CommandRunner, FileHelper}
import play.api.libs.json._

import scala.sys.process._
import scala.util.{Properties, Success, Try}

object Lintr extends Tool {

  def apply(source: Source.Directory, configuration: Option[List[Pattern.Definition]], files: Option[Set[Source.File]],
            options: Map[Configuration.Key, Configuration.Value])
           (implicit specification: Tool.Specification): Try[List[Result]] = {

      // println("before getRSysCall")
      val rCall = getRSysCall(source, configuration, files, options, specification)
      // println(rCall)
      // Try {
      CommandRunner.exec(rCall) match {
        case Right(resultFromTool) =>
          // println("RIGHT")
          // println(resultFromTool.stderr)
          // println(resultFromTool.stdout.length)
          println(resultFromTool.stdout)
        case Left(failure) =>
          // println("LEFT")
          throw failure
      // }
    }

    Try {
      val dummy: List[Result] = Nil
      dummy
    }
  }

  private def getRSysCall(source: Source.Directory, configuration: Option[List[Pattern.Definition]],
                         files: Option[Set[Source.File]], options: Map[Configuration.Key, Configuration.Value],
                         specification: Tool.Specification): List[String] = {
    val configJSON = "[" + configuration.get.map(x => definitionToJSON(x)).mkString(",") + "]"
    val filesJSON = "[\"" + files.get.mkString("\",\"") + "\"]"
    val optionsJSON = Json.stringify(Json.toJson(options))
    val toolSpecJSON = toolSpecToJSON(specification)
    val arg = s"""{"source":"$source","configuration":$configJSON,"files":$filesJSON,"options":$optionsJSON,"specification":$toolSpecJSON}"""
    return List("Rscript", "/src/codacy-lintr.R", arg)
  }

  private def definitionToJSON(definition: Pattern.Definition): String = {
    s"""{"patternId":"${definition.patternId}","parameters":"${definition.parameters}"}"""
  }

  private def parameterValueToString(value: Parameter.Value): String = {
    val s = value.toString
    s.stripPrefix("ParamValue(").stripSuffix(")").trim
  }

  private def parameterSpecToJSON(spec: Parameter.Specification): String = {
    val default = parameterValueToString(spec.default)
    s"""{"name":"${spec.name}","default":$default}"""
  }

  private def patternSpecToJSON(spec: Pattern.Specification): String = {
    val parametersJSON: String = {
      if (spec.parameters.isDefined) {
        "[" + spec.parameters.get.map( x => parameterSpecToJSON(x) ).mkString(",")  + "]"
      } else {
        "[]"
      }
    }
    s"""{"patternId":"${spec.patternId}","level":"${spec.level}","category":"${spec.category}","languages":"${spec.languages}","parameters":$parametersJSON}"""
  }

  private def toolSpecToJSON(spec: Tool.Specification): String = {
    val patternsJSON: String = "[".concat(spec.patterns.map( x => patternSpecToJSON(x)).mkString(",")).concat("]")
    s"""{"name":"${spec.name}","patterns":$patternsJSON}"""
  }

}
