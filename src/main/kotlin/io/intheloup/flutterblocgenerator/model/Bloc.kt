package io.intheloup.flutterblocgenerator.model

import com.google.common.base.CaseFormat

data class Bloc(
    val name: String,
    val className: String,
    val projectName: String,
    val stateFilename: String,
    val blocFilename: String,
    val actionsFilename: String,
    val screenFilename: String

) {

  companion object {
    fun build(name: String, projectName: String): Bloc {
      return Bloc(
          name,
          CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name),
          projectName,
          "${name}_state.dart",
          "${name}_bloc.dart",
          "${name}_actions.dart",
          "${name}_screen.dart"
      )
    }
  }


}