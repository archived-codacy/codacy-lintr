package codacy

import codacy.dockerApi.DockerEngine
import codacy.lintr.Lintr

object Engine extends DockerEngine(Lintr)