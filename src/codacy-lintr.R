args <- commandArgs(trailingOnly = TRUE)
print(args[1])





# Only .R files should be passed to this function
lint_files <- function(files) {
  violations <- list()
  for (f in files) {
    violations <- c(violations, lintr::lint(f))
  }
  violations
}
