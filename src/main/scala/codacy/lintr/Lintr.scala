package codacy.lintr

import codacy.docker.api._
import codacy.dockerApi.utils.CommandRunner
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

object Lintr extends Tool {

  def apply(source: Source.Directory, configuration: Option[List[Pattern.Definition]], files: Option[Set[Source.File]],
            options: Map[Configuration.Key, Configuration.Value])
           (implicit specification: Tool.Specification): Try[List[Result]] = {

    val rCall = getRSysCall(source, configuration, files, options, specification)

    CommandRunner.exec(rCall) match {
      case Right(resultFromTool) =>
        val lines = resultFromTool.stdout
        Success(lines.flatMap { line => parseLine(line) })
      case Left(failure) =>
        Failure(failure)
    }
  }

  /** Returns a list of strings than when executed as a single system call
    * will call the codacy-lintr.R script. The argument to this script is a
    * single JSON blob that describes the entire contents of the .codacy.JSON
    * file.
    */
  private def getRSysCall(source: Source.Directory, configuration: Option[List[Pattern.Definition]],
                          files: Option[Set[Source.File]], options: Map[Configuration.Key, Configuration.Value],
                          specification: Tool.Specification): List[String] = {
    val configJSON = "[" + configuration.get.map(x => definitionToJSON(x)).mkString(",") + "]"
    val filesJSON = "[\"" + files.get.mkString("\",\"") + "\"]"
    val optionsJSON = Json.stringify(Json.toJson(options))
    val toolSpecJSON = toolSpecToJSON(specification)
    val arg = s"""{"source":"$source","configuration":$configJSON,"files":$filesJSON,"options":$optionsJSON,"specification":$toolSpecJSON}"""
    List("Rscript", "/lintr/codacy-lintr.R", arg)
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
        "[" + spec.parameters.get.map(x => parameterSpecToJSON(x)).mkString(",") + "]"
      } else {
        "[]"
      }
    }
    s"""{"patternId":"${spec.patternId}","level":"${spec.level}","category":"${spec.category}","languages":"${spec.languages}","parameters":$parametersJSON}"""
  }

  private def toolSpecToJSON(spec: Tool.Specification): String = {
    val patternsJSON: String = "[".concat(spec.patterns.map(x => patternSpecToJSON(x)).mkString(",")).concat("]")
    s"""{"name":"${spec.name}","patterns":$patternsJSON}"""
  }


  /** Parses a line of JSON output from the system call to R and returns an
    * issue in the form of a Result.
    */
  private def parseLine(line: String): Option[Result] = {
    val resultJSON = Json.parse(line)

    def createIssue(filename: String, lineNumber: String, message: String, patternId: String) = {
      val issueLine = if (lineNumber.nonEmpty) lineNumber.toInt else 1
      Result.Issue(Source.File(filename),
        Result.Message(message),
        Pattern.Id(patternId),
        Source.Line(issueLine))
    }

    Try {
      createIssue(
        (resultJSON \ "filename").get.toString,
        (resultJSON \ "line").get.toString,
        (resultJSON \ "message").get.toString,
        (resultJSON \ "patternId").get.toString)
    }.toOption
  }
}
